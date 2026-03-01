package org.apache.mina.transport.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Executor;
import org.apache.mina.core.polling.AbstractPollingIoConnector;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.SocketSessionConfig;

/* loaded from: classes.dex */
public final class NioSocketConnector extends AbstractPollingIoConnector<NioSession, SocketChannel> implements SocketConnector {
    private volatile Selector selector;

    @Override // org.apache.mina.core.polling.AbstractPollingIoConnector
    protected /* bridge */ /* synthetic */ AbstractIoSession newSession(IoProcessor x0, SocketChannel socketChannel) throws Exception {
        return newSession2((IoProcessor<NioSession>) x0, socketChannel);
    }

    public NioSocketConnector() {
        super(new DefaultSocketSessionConfig(), NioProcessor.class);
        ((DefaultSocketSessionConfig) getSessionConfig()).init(this);
    }

    public NioSocketConnector(int processorCount) {
        super(new DefaultSocketSessionConfig(), NioProcessor.class, processorCount);
        ((DefaultSocketSessionConfig) getSessionConfig()).init(this);
    }

    public NioSocketConnector(IoProcessor<NioSession> processor) {
        super(new DefaultSocketSessionConfig(), processor);
        ((DefaultSocketSessionConfig) getSessionConfig()).init(this);
    }

    public NioSocketConnector(Executor executor, IoProcessor<NioSession> processor) {
        super(new DefaultSocketSessionConfig(), executor, processor);
        ((DefaultSocketSessionConfig) getSessionConfig()).init(this);
    }

    public NioSocketConnector(Class<? extends IoProcessor<NioSession>> processorClass, int processorCount) {
        super(new DefaultSocketSessionConfig(), processorClass, processorCount);
    }

    public NioSocketConnector(Class<? extends IoProcessor<NioSession>> processorClass) {
        super(new DefaultSocketSessionConfig(), processorClass);
    }

    @Override // org.apache.mina.core.polling.AbstractPollingIoConnector
    protected void init() throws Exception {
        this.selector = Selector.open();
    }

    @Override // org.apache.mina.core.polling.AbstractPollingIoConnector
    protected void destroy() throws Exception {
        if (this.selector != null) {
            this.selector.close();
        }
    }

    @Override // org.apache.mina.core.service.IoService
    public TransportMetadata getTransportMetadata() {
        return NioSocketSession.METADATA;
    }

    @Override // org.apache.mina.core.service.IoService
    public SocketSessionConfig getSessionConfig() {
        return (SocketSessionConfig) this.sessionConfig;
    }

    @Override // org.apache.mina.core.service.AbstractIoConnector, org.apache.mina.core.service.IoConnector
    public InetSocketAddress getDefaultRemoteAddress() {
        return (InetSocketAddress) super.getDefaultRemoteAddress();
    }

    @Override // org.apache.mina.transport.socket.SocketConnector
    public void setDefaultRemoteAddress(InetSocketAddress defaultRemoteAddress) {
        super.setDefaultRemoteAddress((SocketAddress) defaultRemoteAddress);
    }

    @Override // org.apache.mina.core.polling.AbstractPollingIoConnector
    protected Iterator<SocketChannel> allHandles() {
        return new SocketChannelIterator(this.selector.keys());
    }

    @Override // org.apache.mina.core.polling.AbstractPollingIoConnector
    public boolean connect(SocketChannel handle, SocketAddress remoteAddress) throws Exception {
        return handle.connect(remoteAddress);
    }

    @Override // org.apache.mina.core.polling.AbstractPollingIoConnector
    public AbstractPollingIoConnector<NioSession, SocketChannel>.ConnectionRequest getConnectionRequest(SocketChannel handle) {
        SelectionKey key = handle.keyFor(this.selector);
        if (key == null || !key.isValid()) {
            return null;
        }
        return (AbstractPollingIoConnector.ConnectionRequest) key.attachment();
    }

    @Override // org.apache.mina.core.polling.AbstractPollingIoConnector
    public void close(SocketChannel handle) throws Exception {
        SelectionKey key = handle.keyFor(this.selector);
        if (key != null) {
            key.cancel();
        }
        handle.close();
    }

    @Override // org.apache.mina.core.polling.AbstractPollingIoConnector
    public boolean finishConnect(SocketChannel handle) throws Exception {
        if (!handle.finishConnect()) {
            return false;
        }
        SelectionKey key = handle.keyFor(this.selector);
        if (key != null) {
            key.cancel();
        }
        return true;
    }

    @Override // org.apache.mina.core.polling.AbstractPollingIoConnector
    public SocketChannel newHandle(SocketAddress localAddress) throws Exception {
        SocketChannel ch = SocketChannel.open();
        int receiveBufferSize = getSessionConfig().getReceiveBufferSize();
        if (receiveBufferSize > 65535) {
            ch.socket().setReceiveBufferSize(receiveBufferSize);
        }
        if (localAddress != null) {
            try {
                ch.socket().bind(localAddress);
            } catch (IOException ioe) {
                String newMessage = "Error while binding on " + localAddress + "\noriginal message : " + ioe.getMessage();
                Exception e = new IOException(newMessage);
                e.initCause(ioe.getCause());
                ch.close();
                throw ioe;
            }
        }
        ch.configureBlocking(false);
        return ch;
    }

    /* renamed from: newSession */
    protected NioSession newSession2(IoProcessor<NioSession> processor, SocketChannel handle) {
        return new NioSocketSession(this, processor, handle);
    }

    @Override // org.apache.mina.core.polling.AbstractPollingIoConnector
    public void register(SocketChannel handle, AbstractPollingIoConnector<NioSession, SocketChannel>.ConnectionRequest request) throws Exception {
        handle.register(this.selector, 8, request);
    }

    @Override // org.apache.mina.core.polling.AbstractPollingIoConnector
    protected int select(int timeout) throws Exception {
        return this.selector.select(timeout);
    }

    @Override // org.apache.mina.core.polling.AbstractPollingIoConnector
    protected Iterator<SocketChannel> selectedHandles() {
        return new SocketChannelIterator(this.selector.selectedKeys());
    }

    @Override // org.apache.mina.core.polling.AbstractPollingIoConnector
    protected void wakeup() {
        this.selector.wakeup();
    }

    private static class SocketChannelIterator implements Iterator<SocketChannel> {
        private final Iterator<SelectionKey> i;

        /* synthetic */ SocketChannelIterator(Collection x0, AnonymousClass1 x1) {
            this(x0);
        }

        private SocketChannelIterator(Collection<SelectionKey> selectedKeys) {
            this.i = selectedKeys.iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override // java.util.Iterator
        public SocketChannel next() {
            SelectionKey key = this.i.next();
            return (SocketChannel) key.channel();
        }

        @Override // java.util.Iterator
        public void remove() {
            this.i.remove();
        }
    }
}
