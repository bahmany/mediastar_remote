package org.apache.mina.transport.socket.nio;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.FileRegion;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.service.DefaultTransportMetadata;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.AbstractSocketSessionConfig;
import org.apache.mina.transport.socket.SocketSessionConfig;

/* loaded from: classes.dex */
class NioSocketSession extends NioSession {
    static final TransportMetadata METADATA = new DefaultTransportMetadata("nio", "socket", false, true, InetSocketAddress.class, SocketSessionConfig.class, IoBuffer.class, FileRegion.class);

    /* JADX INFO: Access modifiers changed from: private */
    public Socket getSocket() {
        return ((SocketChannel) this.channel).socket();
    }

    public NioSocketSession(IoService service, IoProcessor<NioSession> processor, SocketChannel channel) {
        super(processor, service, channel);
        this.config = new SessionConfigImpl();
        this.config.setAll(service.getSessionConfig());
    }

    @Override // org.apache.mina.core.session.IoSession
    public TransportMetadata getTransportMetadata() {
        return METADATA;
    }

    @Override // org.apache.mina.core.session.AbstractIoSession, org.apache.mina.core.session.IoSession
    public SocketSessionConfig getConfig() {
        return (SocketSessionConfig) this.config;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.mina.transport.socket.nio.NioSession
    public SocketChannel getChannel() {
        return (SocketChannel) this.channel;
    }

    @Override // org.apache.mina.core.session.IoSession
    public InetSocketAddress getRemoteAddress() {
        Socket socket;
        if (this.channel == null || (socket = getSocket()) == null) {
            return null;
        }
        return (InetSocketAddress) socket.getRemoteSocketAddress();
    }

    @Override // org.apache.mina.core.session.IoSession
    public InetSocketAddress getLocalAddress() {
        Socket socket;
        if (this.channel == null || (socket = getSocket()) == null) {
            return null;
        }
        return (InetSocketAddress) socket.getLocalSocketAddress();
    }

    @Override // org.apache.mina.core.session.AbstractIoSession, org.apache.mina.core.session.IoSession
    public InetSocketAddress getServiceAddress() {
        return (InetSocketAddress) super.getServiceAddress();
    }

    private class SessionConfigImpl extends AbstractSocketSessionConfig {
        private SessionConfigImpl() {
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public boolean isKeepAlive() {
            try {
                return NioSocketSession.this.getSocket().getKeepAlive();
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public void setKeepAlive(boolean on) throws SocketException {
            try {
                NioSocketSession.this.getSocket().setKeepAlive(on);
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public boolean isOobInline() {
            try {
                return NioSocketSession.this.getSocket().getOOBInline();
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public void setOobInline(boolean on) throws SocketException {
            try {
                NioSocketSession.this.getSocket().setOOBInline(on);
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public boolean isReuseAddress() {
            try {
                return NioSocketSession.this.getSocket().getReuseAddress();
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public void setReuseAddress(boolean on) throws SocketException {
            try {
                NioSocketSession.this.getSocket().setReuseAddress(on);
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public int getSoLinger() {
            try {
                return NioSocketSession.this.getSocket().getSoLinger();
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public void setSoLinger(int linger) throws SocketException {
            try {
                if (linger < 0) {
                    NioSocketSession.this.getSocket().setSoLinger(false, 0);
                } else {
                    NioSocketSession.this.getSocket().setSoLinger(true, linger);
                }
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public boolean isTcpNoDelay() {
            if (NioSocketSession.this.isConnected()) {
                try {
                    return NioSocketSession.this.getSocket().getTcpNoDelay();
                } catch (SocketException e) {
                    throw new RuntimeIoException(e);
                }
            }
            return false;
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public void setTcpNoDelay(boolean on) throws SocketException {
            try {
                NioSocketSession.this.getSocket().setTcpNoDelay(on);
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public int getTrafficClass() {
            try {
                return NioSocketSession.this.getSocket().getTrafficClass();
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public void setTrafficClass(int tc) throws SocketException {
            try {
                NioSocketSession.this.getSocket().setTrafficClass(tc);
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public int getSendBufferSize() {
            try {
                return NioSocketSession.this.getSocket().getSendBufferSize();
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public void setSendBufferSize(int size) throws SocketException {
            try {
                NioSocketSession.this.getSocket().setSendBufferSize(size);
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public int getReceiveBufferSize() {
            try {
                return NioSocketSession.this.getSocket().getReceiveBufferSize();
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }

        @Override // org.apache.mina.transport.socket.SocketSessionConfig
        public void setReceiveBufferSize(int size) throws SocketException {
            try {
                NioSocketSession.this.getSocket().setReceiveBufferSize(size);
            } catch (SocketException e) {
                throw new RuntimeIoException(e);
            }
        }
    }

    @Override // org.apache.mina.core.session.AbstractIoSession, org.apache.mina.core.session.IoSession
    public final boolean isSecured() {
        IoFilterChain chain = getFilterChain();
        IoFilter sslFilter = chain.get(SslFilter.class);
        if (sslFilter != null) {
            return ((SslFilter) sslFilter).isSslStarted(this);
        }
        return false;
    }
}
