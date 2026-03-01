from __future__ import annotations

import asyncio
import json
import socket
import struct
import time
import zlib
import logging
from dataclasses import dataclass, field
from typing import Callable, Optional, Any

from app.stb.compression import gs_decompress

logger = logging.getLogger('stb.websocket')

GCDH_HEADER = b"GCDH"
HEADER_LEN = 16


@dataclass
class ReceivedMessage:
    command_type: int
    response_state: int
    data: bytes


MessageCallback = Callable[[ReceivedMessage], None]


class STBWebSocketBridge:
    """Bridges STB TCP socket to WebSocket"""
    
    def __init__(self, websocket):
        self.websocket = websocket
        self.stb_socket: Optional[socket.socket] = None
        self.on_message: Optional[MessageCallback] = None
        self.on_disconnect: Optional[Callable[[], None]] = None
        self._stop_event = asyncio.Event()
        self._receiver_task: Optional[asyncio.Task] = None
        self._lock = asyncio.Lock()
        
    async def connect_to_stb(self, stb_host: str = "192.168.1.100", stb_port: int = 5450) -> bool:
        """Connect to STB via TCP socket"""
        try:
            # Create TCP socket
            self.stb_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.stb_socket.settimeout(5.0)
            
            # Connect to STB
            await asyncio.get_event_loop().run_in_executor(
                None, 
                lambda: self.stb_socket.connect((stb_host, stb_port))
            )
            
            logger.info(f"Connected to STB at {stb_host}:{stb_port}")
            
            # Start receiver task
            self._stop_event.clear()
            self._receiver_task = asyncio.create_task(self._receive_loop())
            
            # Send login
            await self._login()
            
            return True
            
        except Exception as e:
            logger.error(f"Failed to connect to STB: {e}")
            if self.stb_socket:
                self.stb_socket.close()
                self.stb_socket = None
            return False
            
    async def disconnect(self) -> None:
        """Disconnect from STB"""
        self._stop_event.set()
        
        if self._receiver_task:
            try:
                await self._receiver_task
            except asyncio.CancelledError:
                pass
                
        if self.stb_socket:
            self.stb_socket.close()
            self.stb_socket = None
            
        logger.info("Disconnected from STB")
        
    async def send_frame(self, frame_data: bytes) -> None:
        """Send raw STB protocol frame to STB"""
        if not self.stb_socket:
            raise ConnectionError("Not connected to STB")
            
        try:
            await asyncio.get_event_loop().run_in_executor(
                None,
                lambda: self.stb_socket.sendall(frame_data)
            )
            logger.debug(f"Sent frame to STB: {len(frame_data)} bytes")
        except Exception as e:
            logger.error(f"Failed to send frame to STB: {e}")
            raise
            
    async def send_command(self, command_type: int, params: Optional[list] = None) -> None:
        """Send command to STB"""
        # Build XML command
        if params:
            param_str = json.dumps(params, separators=(',', ':'))
            xml = f'{{"request":"{command_type}","array":{param_str}}}'
        else:
            xml = f'{{"request":"{command_type}"}}'
            
        # Build frame
        payload = xml.encode('utf-8')
        length = len(payload)
        frame = (
            GCDH_HEADER +
            struct.pack('<I', length) +
            struct.pack('<I', command_type) +
            struct.pack('<I', 0) +  # state
            payload
        )
        
        await self.send_frame(frame)
        logger.info(f"Sent command to STB: 0x{command_type:X} ({command_type})")
        
    async def _login(self) -> None:
        """Send login command to STB"""
        # Login with default credentials
        await self.send_command(0x3E6, [{
            "LoginName": "admin",
            "LoginPass": "admin"
        }])
        
        # Wait for login response
        await asyncio.sleep(0.5)
        
        # Request STB info
        await self.send_command(0x26)
        
    async def _receive_loop(self) -> None:
        """Receive frames from STB and forward to WebSocket"""
        try:
            logger.info("STB receiver started")
            while not self._stop_event.is_set():
                try:
                    # Receive header
                    header = await asyncio.get_event_loop().run_in_executor(
                        None,
                        lambda: self._recv_exact(HEADER_LEN)
                    )
                    
                    if not header:
                        break
                        
                    # Parse header
                    if header[0:4] != GCDH_HEADER:
                        logger.warning(f"Invalid header: {header[0:4]}")
                        continue
                        
                    data_length = struct.unpack('<I', header[4:8])[0]
                    command_type = struct.unpack('<I', header[8:12])[0]
                    response_state = struct.unpack('<I', header[12:16])[0]
                    
                    # Receive data
                    raw_data = await asyncio.get_event_loop().run_in_executor(
                        None,
                        lambda: self._recv_exact(data_length)
                    )
                    
                    if not raw_data:
                        break
                        
                    # Process frame
                    await self._process_frame(command_type, response_state, raw_data)
                    
                except socket.timeout:
                    continue
                except Exception as e:
                    logger.error(f"Error in receive loop: {e}")
                    break
                    
        finally:
            if self.on_disconnect:
                try:
                    self.on_disconnect()
                except Exception:
                    pass
                    
    async def _process_frame(self, command_type: int, response_state: int, raw_data: bytes) -> None:
        """Process received frame from STB"""
        try:
            # Decompress data
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
                    
            data = data.rstrip(b"\x00")
            
            # Log received data
            preview_hex = data[:64].hex()
            preview_txt = data[:200].decode("utf-8", errors="ignore")
            logger.info("STB RECV hdr cmd=0x%X (%d) state=%d raw_len=%d data_len=%d", 
                       command_type, command_type, response_state, len(raw_data), len(data))
            logger.debug("STB RECV raw_hex=%s", raw_data.hex())
            logger.debug("STB RECV preview_hex=%s", preview_hex)
            logger.debug("STB RECV preview_txt=%s", preview_txt)
            
            try:
                logger.debug("STB RECV decoded=%s", data.decode("utf-8", errors="ignore"))
            except Exception:
                logger.debug("STB RECV decoded_hex=%s", data.hex())
                
        except Exception as e:
            logger.error(f"Error processing frame data: {e}")
            data = raw_data
            
        # Create message and call callback
        msg = ReceivedMessage(
            command_type=command_type,
            response_state=response_state,
            data=data,
        )
        
        if self.on_message:
            try:
                self.on_message(msg)
            except Exception as e:
                logger.error(f"Error in message callback: {e}")
                
        # Forward to WebSocket
        try:
            await self.websocket.send_bytes(
                GCDH_HEADER +
                struct.pack('<I', len(data)) +
                struct.pack('<I', command_type) +
                struct.pack('<I', response_state) +
                data
            )
        except Exception as e:
            logger.error(f"Failed to forward frame to WebSocket: {e}")
            
    def _recv_exact(self, n: int) -> Optional[bytes]:
        """Receive exactly n bytes from STB socket"""
        if not self.stb_socket:
            return None
            
        chunks = []
        remaining = n
        while remaining > 0:
            try:
                chunk = self.stb_socket.recv(remaining)
                if not chunk:
                    return None
                chunks.append(chunk)
                remaining -= len(chunk)
            except socket.timeout:
                continue
            except Exception:
                return None
        return b"".join(chunks)
