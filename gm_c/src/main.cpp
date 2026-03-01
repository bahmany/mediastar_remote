#include <zlib.h>

#include <algorithm>
#include <array>
#include <cerrno>
#include <cstdint>
#include <cstring>
#include <iomanip>
#include <iostream>
#include <optional>
#include <random>
#include <sstream>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#ifdef _WIN32
#ifndef NOMINMAX
#define NOMINMAX
#endif
#include <winsock2.h>
#include <ws2tcpip.h>
using socket_t = SOCKET;
static constexpr socket_t kInvalidSocket = INVALID_SOCKET;
#else
#include <arpa/inet.h>
#include <fcntl.h>
#include <netdb.h>
#include <sys/select.h>
#include <sys/socket.h>
#include <unistd.h>
using socket_t = int;
static constexpr socket_t kInvalidSocket = -1;
#endif

static void socket_close(socket_t s) {
#ifdef _WIN32
  closesocket(s);
#else
  close(s);
#endif
}

struct SocketInit {
#ifdef _WIN32
  SocketInit() {
    WSADATA wsa;
    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0) {
      throw std::runtime_error("WSAStartup failed");
    }
  }
  ~SocketInit() { WSACleanup(); }
#else
  SocketInit() = default;
  ~SocketInit() = default;
#endif
};

static void set_nonblocking(socket_t s, bool on) {
#ifdef _WIN32
  u_long mode = on ? 1 : 0;
  if (ioctlsocket(s, FIONBIO, &mode) != 0) {
    throw std::runtime_error("ioctlsocket(FIONBIO) failed");
  }
#else
  int flags = fcntl(s, F_GETFL, 0);
  if (flags < 0) {
    throw std::runtime_error("fcntl(F_GETFL) failed");
  }
  if (on) {
    flags |= O_NONBLOCK;
  } else {
    flags &= ~O_NONBLOCK;
  }
  if (fcntl(s, F_SETFL, flags) != 0) {
    throw std::runtime_error("fcntl(F_SETFL) failed");
  }
#endif
}

static socket_t connect_tcp_with_timeout(const std::string& host, uint16_t port, int timeout_ms) {
  addrinfo hints{};
  hints.ai_socktype = SOCK_STREAM;
  hints.ai_family = AF_UNSPEC;

  addrinfo* res = nullptr;
  const std::string port_str = std::to_string(port);
  if (getaddrinfo(host.c_str(), port_str.c_str(), &hints, &res) != 0 || !res) {
    throw std::runtime_error("getaddrinfo failed");
  }

  socket_t sock = kInvalidSocket;
  for (addrinfo* p = res; p; p = p->ai_next) {
    sock = static_cast<socket_t>(::socket(p->ai_family, p->ai_socktype, p->ai_protocol));
    if (sock == kInvalidSocket) {
      continue;
    }

    try {
      set_nonblocking(sock, true);

      int rc = ::connect(sock, p->ai_addr, static_cast<int>(p->ai_addrlen));
#ifdef _WIN32
      const int err = (rc == 0) ? 0 : WSAGetLastError();
      const bool in_progress = (rc != 0) && (err == WSAEWOULDBLOCK || err == WSAEINPROGRESS);
#else
      const int err = (rc == 0) ? 0 : errno;
      const bool in_progress = (rc != 0) && (err == EINPROGRESS);
#endif

      if (rc == 0) {
        set_nonblocking(sock, false);
        break;
      }

      if (!in_progress) {
        socket_close(sock);
        sock = kInvalidSocket;
        continue;
      }

      fd_set wfds;
      FD_ZERO(&wfds);
      FD_SET(sock, &wfds);

      timeval tv;
      tv.tv_sec = timeout_ms / 1000;
      tv.tv_usec = (timeout_ms % 1000) * 1000;

      rc = select(static_cast<int>(sock + 1), nullptr, &wfds, nullptr, &tv);
      if (rc <= 0) {
        socket_close(sock);
        sock = kInvalidSocket;
        continue;
      }

      int so_error = 0;
#ifdef _WIN32
      int slen = sizeof(so_error);
#else
      socklen_t slen = sizeof(so_error);
#endif
      if (getsockopt(sock, SOL_SOCKET, SO_ERROR, reinterpret_cast<char*>(&so_error), &slen) != 0 || so_error != 0) {
        socket_close(sock);
        sock = kInvalidSocket;
        continue;
      }

      set_nonblocking(sock, false);
      break;
    } catch (...) {
      socket_close(sock);
      sock = kInvalidSocket;
    }
  }

  freeaddrinfo(res);

  if (sock == kInvalidSocket) {
    throw std::runtime_error("connect failed");
  }

  return sock;
}

static void send_all(socket_t sock, const uint8_t* data, size_t len) {
  size_t sent = 0;
  while (sent < len) {
#ifdef _WIN32
    int rc = ::send(sock, reinterpret_cast<const char*>(data + sent), static_cast<int>(len - sent), 0);
#else
    ssize_t rc = ::send(sock, data + sent, len - sent, 0);
#endif
    if (rc <= 0) {
      throw std::runtime_error("send failed");
    }
    sent += static_cast<size_t>(rc);
  }
}

static std::vector<uint8_t> recv_exact(socket_t sock, size_t n, int timeout_ms = 5000) {
  std::vector<uint8_t> out(n);
  size_t got = 0;
  while (got < n) {
    fd_set fds;
    FD_ZERO(&fds);
    FD_SET(sock, &fds);
#ifdef _WIN32
    struct timeval tv;
    tv.tv_sec = timeout_ms / 1000;
    tv.tv_usec = (timeout_ms % 1000) * 1000;
    int sr = select(0, &fds, nullptr, nullptr, &tv);
#else
    struct timeval tv;
    tv.tv_sec = timeout_ms / 1000;
    tv.tv_usec = (timeout_ms % 1000) * 1000;
    int sr = select(sock + 1, &fds, nullptr, nullptr, &tv);
#endif
    if (sr <= 0) {
      throw std::runtime_error("recv timeout or select error");
    }
#ifdef _WIN32
    int rc = ::recv(sock, reinterpret_cast<char*>(out.data() + got), static_cast<int>(n - got), 0);
#else
    ssize_t rc = ::recv(sock, out.data() + got, n - got, 0);
#endif
    if (rc <= 0) {
      throw std::runtime_error("recv failed");
    }
    got += static_cast<size_t>(rc);
  }
  return out;
}

static std::vector<uint8_t> build_socket_frame(const std::vector<uint8_t>& payload) {
  std::ostringstream ss;
  ss << std::setw(7) << std::setfill('0') << payload.size();
  const std::string len7 = ss.str();

  std::vector<uint8_t> frame;
  frame.insert(frame.end(), {'S', 't', 'a', 'r', 't'});
  frame.insert(frame.end(), len7.begin(), len7.end());
  frame.insert(frame.end(), {'E', 'n', 'd'});
  frame.insert(frame.end(), payload.begin(), payload.end());
  return frame;
}

static std::string xml_escape(const std::string& s) {
  std::string out;
  out.reserve(s.size() + 16);
  for (char c : s) {
    switch (c) {
      case '&': out += "&amp;"; break;
      case '<': out += "&lt;"; break;
      case '>': out += "&gt;"; break;
      case '"': out += "&quot;"; break;
      case '\'': out += "&apos;"; break;
      default: out.push_back(c); break;
    }
  }
  return out;
}

static std::string serialize_login_xml(int request_type, const std::string& model, const std::string& uuid) {
  std::ostringstream ss;
  ss << "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>";
  ss << "<Command request=\"" << request_type << "\">";
  ss << "<data>" << xml_escape(model) << "</data>";
  ss << "<uuid>" << xml_escape(uuid) << "</uuid>";
  ss << "</Command>";
  return ss.str();
}

static std::string serialize_channel_request_xml(int request_type, int from_index, int to_index) {
  std::ostringstream ss;
  ss << "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>";
  ss << "<Command request=\"" << request_type << "\">";
  ss << "<parm>";
  ss << "<FromIndex>" << from_index << "</FromIndex>";
  ss << "<ToIndex>" << to_index << "</ToIndex>";
  ss << "</parm>";
  ss << "</Command>";
  return ss.str();
}

struct LoginInfo {
  std::string magic_code;
  std::string stb_sn_disp;
  std::string model_name;
  std::string stb_ip_disp;
  uint8_t send_data_type = 0;
};

static std::string serial_to_disp(const std::array<uint8_t, 8>& sn) {
  uint32_t i_date = (static_cast<uint32_t>(sn[0]) << 16) | (static_cast<uint32_t>(sn[1]) << 8) |
                    static_cast<uint32_t>(sn[2]);
  uint32_t i_serial = (static_cast<uint32_t>(sn[3]) << 16) | (static_cast<uint32_t>(sn[4]) << 8) |
                      static_cast<uint32_t>(sn[5]);
  std::ostringstream ss;
  ss << std::setw(6) << std::setfill('0') << i_date << std::setw(6) << std::setfill('0') << i_serial;
  return ss.str();
}

static LoginInfo parse_login_info(const std::vector<uint8_t>& b) {
  if (b.size() < 108) {
    throw std::runtime_error("login info must be 108 bytes");
  }

  LoginInfo info;
  info.magic_code.assign(reinterpret_cast<const char*>(b.data()), 12);

  std::array<uint8_t, 8> sn{};
  std::memcpy(sn.data(), b.data() + 12, 8);
  info.stb_sn_disp = serial_to_disp(sn);

  const uint8_t* model_raw = b.data() + 20;
  const size_t model_len_max = 32;
  size_t model_len = 0;
  while (model_len < model_len_max && model_raw[model_len] != 0) {
    model_len++;
  }
  info.model_name.assign(reinterpret_cast<const char*>(model_raw), model_len);

  const uint8_t* ip = b.data() + 68;
  std::ostringstream ipss;
  ipss << static_cast<int>(ip[3]) << "." << static_cast<int>(ip[2]) << "." << static_cast<int>(ip[1]) << "."
       << static_cast<int>(ip[0]);
  info.stb_ip_disp = ipss.str();

  uint8_t flags = b[84];
  info.send_data_type = (flags & 0x40) ? 1 : 0;

  return info;
}

static uint32_t read_le_u32(const uint8_t* p) {
  return (static_cast<uint32_t>(p[0])) | (static_cast<uint32_t>(p[1]) << 8) | (static_cast<uint32_t>(p[2]) << 16) |
         (static_cast<uint32_t>(p[3]) << 24);
}

struct GcdhHeader {
  uint32_t data_length = 0;
  uint32_t command_type = 0;
  uint32_t response_state = 0;
};

static std::optional<GcdhHeader> parse_gcdh_header(const std::vector<uint8_t>& hdr) {
  if (hdr.size() != 16) {
    return std::nullopt;
  }
  if (!(hdr[0] == 'G' && hdr[1] == 'C' && hdr[2] == 'D' && hdr[3] == 'H')) {
    return std::nullopt;
  }

  GcdhHeader out;
  out.data_length = read_le_u32(hdr.data() + 4);
  out.command_type = read_le_u32(hdr.data() + 8);
  out.response_state = read_le_u32(hdr.data() + 12);
  return out;
}

static std::optional<std::vector<uint8_t>> inflate_all(const std::vector<uint8_t>& input, int window_bits) {
  z_stream strm{};
  strm.next_in = const_cast<Bytef*>(reinterpret_cast<const Bytef*>(input.data()));
  strm.avail_in = static_cast<uInt>(input.size());

  if (inflateInit2(&strm, window_bits) != Z_OK) {
    return std::nullopt;
  }

  std::vector<uint8_t> out;
  out.reserve(input.size() * 2);

  std::array<uint8_t, 4096> buf{};
  int rc = Z_OK;
  while (rc == Z_OK) {
    strm.next_out = reinterpret_cast<Bytef*>(buf.data());
    strm.avail_out = static_cast<uInt>(buf.size());

    rc = inflate(&strm, Z_NO_FLUSH);
    if (rc == Z_STREAM_END || rc == Z_OK) {
      const size_t produced = buf.size() - strm.avail_out;
      out.insert(out.end(), buf.data(), buf.data() + produced);
      if (rc == Z_STREAM_END) {
        break;
      }
      continue;
    }

    inflateEnd(&strm);
    return std::nullopt;
  }

  inflateEnd(&strm);
  return out;
}

static std::vector<uint8_t> gs_uncompress_like_apk(std::vector<uint8_t> raw) {
  auto try_modes = [&](std::vector<uint8_t> in) -> std::optional<std::vector<uint8_t>> {
    if (auto out = inflate_all(in, MAX_WBITS)) {
      return out;
    }
    if (auto out = inflate_all(in, -MAX_WBITS)) {
      return out;
    }
    return std::nullopt;
  };

  if (auto out = try_modes(raw)) {
    return *out;
  }

  raw.insert(raw.end(), 8, 0x00);
  if (auto out = try_modes(raw)) {
    return *out;
  }

  return raw;
}

static std::string random_uuid_hex() {
  std::array<uint8_t, 16> b{};
  std::random_device rd;
  for (auto& x : b) {
    x = static_cast<uint8_t>(rd());
  }
  b[6] = static_cast<uint8_t>((b[6] & 0x0F) | 0x40);
  b[8] = static_cast<uint8_t>((b[8] & 0x3F) | 0x80);

  auto hex2 = [](uint8_t v) {
    const char* d = "0123456789abcdef";
    std::string s;
    s.push_back(d[(v >> 4) & 0xF]);
    s.push_back(d[v & 0xF]);
    return s;
  };

  std::ostringstream ss;
  for (size_t i = 0; i < b.size(); i++) {
    ss << hex2(b[i]);
    if (i == 3 || i == 5 || i == 7 || i == 9) {
      ss << "-";
    }
  }
  return ss.str();
}

int main(int argc, char** argv) {
  std::cout << "Program starting...\n";
  try {
    SocketInit _init;
    std::cout << "Socket initialized\n";

    std::string ip;
    uint16_t port = 20000;
    std::string uuid;
    std::string model = "PC";
    bool read_one_gcdh = false;

    for (int i = 1; i < argc; i++) {
      const std::string a = argv[i];
      if (a == "--ip" && i + 1 < argc) {
        ip = argv[++i];
      } else if (a == "--port" && i + 1 < argc) {
        port = static_cast<uint16_t>(std::stoi(argv[++i]));
      } else if (a == "--uuid" && i + 1 < argc) {
        uuid = argv[++i];
      } else if (a == "--model" && i + 1 < argc) {
        model = argv[++i];
      } else if (a == "--read-one-gcdh") {
        read_one_gcdh = true;
      } else {
        std::cerr << "Unknown argument: " << a << "\n";
        return 2;
      }
    }

    if (ip.empty()) {
      std::cerr << "Usage: gmscreen_cli --ip <STB_IP> [--port 20000] [--uuid <uuid>] [--model <string>] [--read-one-gcdh]\n";
      return 2;
    }

    if (uuid.empty()) {
      uuid = random_uuid_hex();
    }

    std::cout << "Connecting to " << ip << ":" << port << "...\n";
    socket_t sock = connect_tcp_with_timeout(ip, port, 3000);

    try {
      const int kLoginRequest = 0x3e6;
      const std::string xml = serialize_login_xml(kLoginRequest, model, uuid);
      std::vector<uint8_t> payload(xml.begin(), xml.end());
      const auto frame = build_socket_frame(payload);

      send_all(sock, frame.data(), frame.size());
      std::cout << "Login request sent. Waiting for 108-byte login response...\n";

      auto login_raw = recv_exact(sock, 108);
      const auto info = parse_login_info(login_raw);

      std::cout << "Login OK\n";
      std::cout << "  magic_code: " << info.magic_code << "\n";
      std::cout << "  stb_sn:     " << info.stb_sn_disp << "\n";
      std::cout << "  model:      " << info.model_name << "\n";
      std::cout << "  stb_ip:     " << info.stb_ip_disp << "\n";
      std::cout << "  send_type:  " << static_cast<int>(info.send_data_type) << "\n";

      if (read_one_gcdh) {
        std::cout << "Sending channel list request (0..99) to trigger a GCDH response...\n";
        const int kChannelListRequest = 0x3e8;
        const std::string chan_xml = serialize_channel_request_xml(kChannelListRequest, 0, 99);
        std::vector<uint8_t> chan_payload(chan_xml.begin(), chan_xml.end());
        const auto chan_frame = build_socket_frame(chan_payload);
        send_all(sock, chan_frame.data(), chan_frame.size());

        std::cout << "Reading one GCDH message...\n";
        auto hdr = recv_exact(sock, 16);
        auto parsed = parse_gcdh_header(hdr);
        if (!parsed) {
          throw std::runtime_error("Invalid GCDH header");
        }

        std::cout << "  data_length: " << parsed->data_length << "\n";
        std::cout << "  command:     0x" << std::hex << parsed->command_type << std::dec << "\n";
        std::cout << "  state:       " << parsed->response_state << "\n";

        auto raw = recv_exact(sock, parsed->data_length);
        auto data = gs_uncompress_like_apk(raw);

        std::cout << "  raw_len:     " << raw.size() << "\n";
        std::cout << "  data_len:    " << data.size() << "\n";

        std::string preview(reinterpret_cast<const char*>(data.data()), std::min<size_t>(data.size(), 200));
        for (char& c : preview) {
          if (static_cast<unsigned char>(c) < 0x20 && c != '\n' && c != '\r' && c != '\t') {
            c = '.';
          }
        }
        std::cout << "  preview:     " << preview << "\n";
      }

      socket_close(sock);
      return 0;
    } catch (...) {
      socket_close(sock);
      throw;
    }

  } catch (const std::exception& e) {
    std::cerr << "Error: " << e.what() << "\n";
    return 1;
  }
}
