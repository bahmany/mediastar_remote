from __future__ import annotations

import socket
import struct
import threading
import time
import zlib
import logging
from dataclasses import dataclass, field
from typing import Callable, Optional

from app.stb.compression import gs_decompress
from app.stb.serializers import build_socket_frame, serialize_json_command, serialize_xml_command

logger = logging.getLogger('stb.wire')
GCDH_HEADER = b"GCDH"
HEADER_LEN = 16
KEEP_ALIVE_INTERVAL_S = 25
KEEP_ALIVE_TIMEOUT_S = 30
KEEP_ALIVE_CMD = 0x1A


@dataclass
class ReceivedMessage:
    command_type: int
    response_state: int
    data: bytes


MessageCallback = Callable[[ReceivedMessage], None]


@dataclass
class SocketReceiveThread:
    sock: socket.socket
    on_message: Optional[MessageCallback] = None
    on_disconnect: Optional[Callable[[], None]] = None
    use_json: bool = False
    send_lock: Optional[threading.Lock] = None

    _stop_event: threading.Event = field(default_factory=threading.Event)
    _thread: Optional[threading.Thread] = None
    _keep_alive_thread: Optional[threading.Thread] = None
    _last_recv_time: float = field(default_factory=time.time)
    _last_keep_alive_sent_time: float = 0.0
    _keep_alive_pending: bool = False
    _lock: threading.Lock = field(default_factory=threading.Lock)

    def start(self) -> None:
        self._stop_event.clear()
        with self._lock:
            self._last_recv_time = time.time()
            self._last_keep_alive_sent_time = 0.0
            self._keep_alive_pending = False

        self._thread = threading.Thread(target=self._receive_loop, daemon=True)
        self._thread.start()

        self._keep_alive_thread = threading.Thread(target=self._keep_alive_loop, daemon=True)
        self._keep_alive_thread.start()

    def stop(self) -> None:
        self._stop_event.set()
        cur = threading.current_thread()
        if self._thread:
            try:
                if self._thread is not cur:
                    self._thread.join(timeout=2)
            except Exception:
                pass
        if self._keep_alive_thread:
            try:
                if self._keep_alive_thread is not cur:
                    self._keep_alive_thread.join(timeout=2)
            except Exception:
                pass

    def _receive_loop(self) -> None:
        try:
            print("STB receiver thread started, waiting for messages...")
            while not self._stop_event.is_set():
                self.sock.settimeout(1.0)
                try:
                    header = self._recv_exact(HEADER_LEN)
                except socket.timeout:
                    continue
                except OSError as e:
                    print(f"STB receiver OSError: {e}")
                    break

                if header is None or len(header) < HEADER_LEN:
                    print(f"STB receiver: incomplete header received, len={len(header) if header else 0}")
                    break

                if header[0:4] != GCDH_HEADER:
                    print(f"STB receiver: invalid header magic, got {header[0:4]}")
                    continue
                
                print(f"STB receiver: got valid header, parsing...")

                data_length = struct.unpack("<I", header[4:8])[0]
                command_type = struct.unpack("<I", header[8:12])[0]
                response_state = struct.unpack("<I", header[12:16])[0]
                print(f"STB receiver: data_length={data_length}, cmd={command_type}, state={response_state}")

                with self._lock:
                    self._last_recv_time = time.time()
                    self._keep_alive_pending = False

                if data_length > 0:
                    try:
                        raw_data = self._recv_exact(data_length)
                    except (socket.timeout, OSError):
                        break

                    if raw_data is None:
                        break

                    data = raw_data
                    try:
                        d = zlib.decompressobj()
                        data = d.decompress(raw_data + (b"\x00" * 8))
                        data += d.flush()
                    except Exception:
                        try:
                            data = gs_decompress(raw_data)
                        except Exception:
                            data = raw_data

                    try:
                        data = data.rstrip(b"\x00")
                    except Exception:
                        pass
                    try:
                        preview_hex = data[:64].hex()
                        preview_txt = data[:200].decode("utf-8", errors="ignore")
                        logger.info("STB RECV hdr cmd=0x%X (%d) state=%d raw_len=%d data_len=%d", command_type, command_type, response_state, len(raw_data), len(data))
                        logger.debug("STB RECV raw_hex=%s", raw_data.hex())
                        logger.debug("STB RECV preview_hex=%s", preview_hex)
                        logger.debug("STB RECV preview_txt=%s", preview_txt)
                        try:
                            logger.debug("STB RECV decoded=%s", data.decode("utf-8", errors="ignore"))
                        except Exception:
                            logger.debug("STB RECV decoded_hex=%s", data.hex())
                    except Exception:
                        pass
                else:
                    data = b""

                msg = ReceivedMessage(
                    command_type=command_type,
                    response_state=response_state,
                    data=data,
                )

                try:
                    print(
                        f"STB RECV cmd=0x{command_type:X} ({command_type}) state={response_state} data_len={len(data)}"
                    )
                except Exception:
                    pass

                if self.on_message:
                    try:
                        self.on_message(msg)
                    except Exception:
                        pass

        finally:
            try:
                self._stop_event.set()
            except Exception:
                pass
            if self.on_disconnect:
                try:
                    self.on_disconnect()
                except Exception:
                    pass

    def _keep_alive_loop(self) -> None:
        while not self._stop_event.is_set():
            time.sleep(1)
            now = time.time()
            with self._lock:
                last_recv = self._last_recv_time
                pending = self._keep_alive_pending
                last_sent = self._last_keep_alive_sent_time

            if pending and (now - last_sent) >= KEEP_ALIVE_TIMEOUT_S and (now - last_recv) >= KEEP_ALIVE_TIMEOUT_S:
                try:
                    self.sock.shutdown(socket.SHUT_RDWR)
                except Exception:
                    pass
                self._stop_event.set()
                break

            if (not pending) and (now - last_recv) >= KEEP_ALIVE_INTERVAL_S:
                try:
                    if self.use_json:
                        payload = serialize_json_command(KEEP_ALIVE_CMD, None).encode("utf-8")
                    else:
                        payload = serialize_xml_command(KEEP_ALIVE_CMD, None).encode("utf-8")
                    if self.send_lock is None:
                        self.sock.sendall(build_socket_frame(payload))
                    else:
                        with self.send_lock:
                            self.sock.sendall(build_socket_frame(payload))
                    with self._lock:
                        self._last_keep_alive_sent_time = now
                        self._keep_alive_pending = True
                except OSError:
                    try:
                        self.sock.shutdown(socket.SHUT_RDWR)
                    except Exception:
                        pass
                    break

    def _recv_exact(self, n: int) -> Optional[bytes]:
        chunks: list[bytes] = []
        remaining = n
        while remaining > 0:
            chunk = self.sock.recv(remaining)
            if not chunk:
                return None
            chunks.append(chunk)
            remaining -= len(chunk)
        return b"".join(chunks)



