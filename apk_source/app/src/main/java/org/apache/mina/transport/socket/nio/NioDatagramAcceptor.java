package org.apache.mina.transport.socket.nio;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.AbstractIoAcceptor;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.ExpiringSessionRecycler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.IoSessionRecycler;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;
import org.apache.mina.transport.socket.DatagramAcceptor;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.DefaultDatagramSessionConfig;
import org.apache.mina.util.ExceptionMonitor;

/* loaded from: classes.dex */
public final class NioDatagramAcceptor extends AbstractIoAcceptor implements DatagramAcceptor, IoProcessor<NioSession> {
    private static final IoSessionRecycler DEFAULT_RECYCLER = new ExpiringSessionRecycler();
    private static final long SELECT_TIMEOUT = 1000;
    private Acceptor acceptor;
    private final Map<SocketAddress, DatagramChannel> boundHandles;
    private final Queue<AbstractIoAcceptor.AcceptorOperationFuture> cancelQueue;
    private final AbstractIoService.ServiceOperationFuture disposalFuture;
    private final Queue<NioSession> flushingSessions;
    private long lastIdleCheckTime;
    private final Semaphore lock;
    private final Queue<AbstractIoAcceptor.AcceptorOperationFuture> registerQueue;
    private volatile boolean selectable;
    private volatile Selector selector;
    private IoSessionRecycler sessionRecycler;

    public NioDatagramAcceptor() {
        this(new DefaultDatagramSessionConfig(), null);
    }

    public NioDatagramAcceptor(Executor executor) {
        this(new DefaultDatagramSessionConfig(), executor);
    }

    private NioDatagramAcceptor(IoSessionConfig sessionConfig, Executor executor) {
        super(sessionConfig, executor);
        this.lock = new Semaphore(1);
        this.registerQueue = new ConcurrentLinkedQueue();
        this.cancelQueue = new ConcurrentLinkedQueue();
        this.flushingSessions = new ConcurrentLinkedQueue();
        this.boundHandles = Collections.synchronizedMap(new HashMap());
        this.sessionRecycler = DEFAULT_RECYCLER;
        this.disposalFuture = new AbstractIoService.ServiceOperationFuture();
        try {
            try {
                init();
                this.selectable = true;
                if (!this.selectable) {
                    try {
                        destroy();
                    } catch (Exception e) {
                        ExceptionMonitor.getInstance().exceptionCaught(e);
                    }
                }
            } catch (RuntimeException e2) {
                throw e2;
            } catch (Exception e3) {
                throw new RuntimeIoException("Failed to initialize.", e3);
            }
        } catch (Throwable th) {
            if (!this.selectable) {
                try {
                    destroy();
                } catch (Exception e4) {
                    ExceptionMonitor.getInstance().exceptionCaught(e4);
                }
            }
            throw th;
        }
    }

    private class Acceptor implements Runnable {
        private Acceptor() {
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException {
            int nHandles = 0;
            NioDatagramAcceptor.this.lastIdleCheckTime = System.currentTimeMillis();
            while (true) {
                if (!NioDatagramAcceptor.this.selectable) {
                    break;
                }
                try {
                    int selected = NioDatagramAcceptor.this.select(1000L);
                    nHandles += NioDatagramAcceptor.this.registerHandles();
                    if (nHandles == 0) {
                        try {
                            NioDatagramAcceptor.this.lock.acquire();
                            if (NioDatagramAcceptor.this.registerQueue.isEmpty() && NioDatagramAcceptor.this.cancelQueue.isEmpty()) {
                                NioDatagramAcceptor.this.acceptor = null;
                                break;
                            }
                        } finally {
                            NioDatagramAcceptor.this.lock.release();
                        }
                    }
                    if (selected > 0) {
                        NioDatagramAcceptor.this.processReadySessions(NioDatagramAcceptor.this.selectedHandles());
                    }
                    long currentTime = System.currentTimeMillis();
                    NioDatagramAcceptor.this.flushSessions(currentTime);
                    nHandles -= NioDatagramAcceptor.this.unregisterHandles();
                    NioDatagramAcceptor.this.notifyIdleSessions(currentTime);
                } catch (ClosedSelectorException cse) {
                    ExceptionMonitor.getInstance().exceptionCaught(cse);
                } catch (Exception e) {
                    ExceptionMonitor.getInstance().exceptionCaught(e);
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e2) {
                    }
                }
            }
            if (NioDatagramAcceptor.this.selectable && NioDatagramAcceptor.this.isDisposing()) {
                NioDatagramAcceptor.this.selectable = false;
                try {
                    NioDatagramAcceptor.this.destroy();
                } catch (Exception e3) {
                    ExceptionMonitor.getInstance().exceptionCaught(e3);
                } finally {
                    NioDatagramAcceptor.this.disposalFuture.setValue(true);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int registerHandles() {
        while (true) {
            AbstractIoAcceptor.AcceptorOperationFuture req = this.registerQueue.poll();
            if (req != null) {
                Map<SocketAddress, DatagramChannel> newHandles = new HashMap<>();
                List<SocketAddress> localAddresses = req.getLocalAddresses();
                try {
                    for (SocketAddress socketAddress : localAddresses) {
                        DatagramChannel handle = open(socketAddress);
                        newHandles.put(localAddress(handle), handle);
                    }
                    this.boundHandles.putAll(newHandles);
                    getListeners().fireServiceActivated();
                    req.setDone();
                    int size = newHandles.size();
                } catch (Exception e) {
                    try {
                        req.setException(e);
                        if (req.getException() != null) {
                            for (DatagramChannel handle2 : newHandles.values()) {
                                try {
                                    close(handle2);
                                } catch (Exception e2) {
                                    ExceptionMonitor.getInstance().exceptionCaught(e2);
                                }
                            }
                            wakeup();
                        }
                    } finally {
                        if (req.getException() != null) {
                            for (DatagramChannel handle3 : newHandles.values()) {
                                try {
                                    close(handle3);
                                } catch (Exception e3) {
                                    ExceptionMonitor.getInstance().exceptionCaught(e3);
                                }
                            }
                            wakeup();
                        }
                    }
                }
            } else {
                return 0;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processReadySessions(Set<SelectionKey> handles) {
        Iterator<SelectionKey> iterator = handles.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            DatagramChannel handle = (DatagramChannel) key.channel();
            iterator.remove();
            if (key != null) {
                try {
                    if (key.isValid() && key.isReadable()) {
                        readHandle(handle);
                    }
                } catch (Exception e) {
                    ExceptionMonitor.getInstance().exceptionCaught(e);
                }
            }
            if (key != null && key.isValid() && key.isWritable()) {
                for (IoSession session : getManagedSessions().values()) {
                    scheduleFlush((NioSession) session);
                }
            }
        }
    }

    private boolean scheduleFlush(NioSession session) {
        if (!session.setScheduledForFlush(true)) {
            return false;
        }
        this.flushingSessions.add(session);
        return true;
    }

    private void readHandle(DatagramChannel handle) throws Exception {
        IoBuffer readBuf = IoBuffer.allocate(getSessionConfig().getReadBufferSize());
        SocketAddress remoteAddress = receive(handle, readBuf);
        if (remoteAddress != null) {
            IoSession session = newSessionWithoutLock(remoteAddress, localAddress(handle));
            readBuf.flip();
            session.getFilterChain().fireMessageReceived(readBuf);
        }
    }

    private IoSession newSessionWithoutLock(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        DatagramChannel handle = this.boundHandles.get(localAddress);
        if (handle == null) {
            throw new IllegalArgumentException("Unknown local address: " + localAddress);
        }
        synchronized (this.sessionRecycler) {
            IoSession session = this.sessionRecycler.recycle(remoteAddress);
            if (session != null) {
                return session;
            }
            NioSession newSession = newSession(this, handle, remoteAddress);
            getSessionRecycler().put(newSession);
            initSession(newSession, null, null);
            try {
                getFilterChainBuilder().buildFilterChain(newSession.getFilterChain());
                getListeners().fireSessionCreated(newSession);
            } catch (Exception e) {
                ExceptionMonitor.getInstance().exceptionCaught(e);
            }
            return newSession;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void flushSessions(long currentTime) {
        while (true) {
            NioSession session = this.flushingSessions.poll();
            if (session != null) {
                session.unscheduledForFlush();
                try {
                    boolean flushedAll = flush(session, currentTime);
                    if (flushedAll && !session.getWriteRequestQueue().isEmpty(session) && !session.isScheduledForFlush()) {
                        scheduleFlush(session);
                    }
                } catch (Exception e) {
                    session.getFilterChain().fireExceptionCaught(e);
                }
            } else {
                return;
            }
        }
    }

    private boolean flush(NioSession session, long currentTime) throws Exception {
        WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
        int maxWrittenBytes = session.getConfig().getMaxReadBufferSize() + (session.getConfig().getMaxReadBufferSize() >>> 1);
        int writtenBytes = 0;
        while (true) {
            try {
                WriteRequest req = session.getCurrentWriteRequest();
                if (req == null) {
                    req = writeRequestQueue.poll(session);
                    if (req == null) {
                        setInterestedInWrite(session, false);
                        return true;
                    }
                    session.setCurrentWriteRequest(req);
                }
                IoBuffer buf = (IoBuffer) req.getMessage();
                if (buf.remaining() == 0) {
                    session.setCurrentWriteRequest(null);
                    buf.reset();
                    session.getFilterChain().fireMessageSent(req);
                } else {
                    SocketAddress destination = req.getDestination();
                    if (destination == null) {
                        destination = session.getRemoteAddress();
                    }
                    int localWrittenBytes = send(session, buf, destination);
                    if (localWrittenBytes == 0 || writtenBytes >= maxWrittenBytes) {
                        break;
                    }
                    setInterestedInWrite(session, false);
                    session.setCurrentWriteRequest(null);
                    writtenBytes += localWrittenBytes;
                    buf.reset();
                    session.getFilterChain().fireMessageSent(req);
                }
            } finally {
                session.increaseWrittenBytes(writtenBytes, currentTime);
            }
        }
        setInterestedInWrite(session, true);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int unregisterHandles() {
        int nHandles = 0;
        while (true) {
            AbstractIoAcceptor.AcceptorOperationFuture request = this.cancelQueue.poll();
            if (request != null) {
                for (SocketAddress socketAddress : request.getLocalAddresses()) {
                    DatagramChannel handle = this.boundHandles.remove(socketAddress);
                    if (handle != null) {
                        try {
                            close(handle);
                            wakeup();
                        } catch (Exception e) {
                            ExceptionMonitor.getInstance().exceptionCaught(e);
                        } finally {
                            int nHandles2 = nHandles + 1;
                        }
                    }
                }
                request.setDone();
            } else {
                return nHandles;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyIdleSessions(long currentTime) {
        if (currentTime - this.lastIdleCheckTime >= 1000) {
            this.lastIdleCheckTime = currentTime;
            AbstractIoSession.notifyIdleness(getListeners().getManagedSessions().values().iterator(), currentTime);
        }
    }

    private void startupAcceptor() throws InterruptedException {
        if (!this.selectable) {
            this.registerQueue.clear();
            this.cancelQueue.clear();
            this.flushingSessions.clear();
        }
        this.lock.acquire();
        if (this.acceptor == null) {
            this.acceptor = new Acceptor();
            executeWorker(this.acceptor);
        } else {
            this.lock.release();
        }
    }

    protected void init() throws Exception {
        this.selector = Selector.open();
    }

    @Override // org.apache.mina.core.service.IoProcessor
    public void add(NioSession session) {
    }

    @Override // org.apache.mina.core.service.AbstractIoAcceptor
    protected final Set<SocketAddress> bindInternal(List<? extends SocketAddress> localAddresses) throws Exception {
        AbstractIoAcceptor.AcceptorOperationFuture request = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
        this.registerQueue.add(request);
        startupAcceptor();
        try {
            this.lock.acquire();
            Thread.sleep(10L);
            wakeup();
            this.lock.release();
            request.awaitUninterruptibly();
            if (request.getException() != null) {
                throw request.getException();
            }
            Set<SocketAddress> newLocalAddresses = new HashSet<>();
            for (DatagramChannel handle : this.boundHandles.values()) {
                newLocalAddresses.add(localAddress(handle));
            }
            return newLocalAddresses;
        } catch (Throwable th) {
            this.lock.release();
            throw th;
        }
    }

    protected void close(DatagramChannel handle) throws Exception {
        SelectionKey key = handle.keyFor(this.selector);
        if (key != null) {
            key.cancel();
        }
        handle.disconnect();
        handle.close();
    }

    protected void destroy() throws Exception {
        if (this.selector != null) {
            this.selector.close();
        }
    }

    @Override // org.apache.mina.core.service.AbstractIoService
    protected void dispose0() throws Exception {
        unbind();
        startupAcceptor();
        wakeup();
    }

    @Override // org.apache.mina.core.service.IoProcessor
    public void flush(NioSession session) {
        if (scheduleFlush(session)) {
            wakeup();
        }
    }

    @Override // org.apache.mina.core.service.AbstractIoAcceptor, org.apache.mina.core.service.IoAcceptor
    public InetSocketAddress getDefaultLocalAddress() {
        return (InetSocketAddress) super.getDefaultLocalAddress();
    }

    @Override // org.apache.mina.core.service.AbstractIoAcceptor, org.apache.mina.core.service.IoAcceptor
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) super.getLocalAddress();
    }

    @Override // org.apache.mina.core.service.IoService
    public DatagramSessionConfig getSessionConfig() {
        return (DatagramSessionConfig) this.sessionConfig;
    }

    @Override // org.apache.mina.transport.socket.DatagramAcceptor
    public final IoSessionRecycler getSessionRecycler() {
        return this.sessionRecycler;
    }

    @Override // org.apache.mina.core.service.IoService
    public TransportMetadata getTransportMetadata() {
        return NioDatagramSession.METADATA;
    }

    protected boolean isReadable(DatagramChannel handle) {
        SelectionKey key = handle.keyFor(this.selector);
        if (key == null || !key.isValid()) {
            return false;
        }
        return key.isReadable();
    }

    protected boolean isWritable(DatagramChannel handle) {
        SelectionKey key = handle.keyFor(this.selector);
        if (key == null || !key.isValid()) {
            return false;
        }
        return key.isWritable();
    }

    protected SocketAddress localAddress(DatagramChannel handle) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) handle.socket().getLocalSocketAddress();
        InetAddress inetAddress = inetSocketAddress.getAddress();
        if ((inetAddress instanceof Inet6Address) && ((Inet6Address) inetAddress).isIPv4CompatibleAddress()) {
            byte[] ipV6Address = ((Inet6Address) inetAddress).getAddress();
            byte[] ipV4Address = new byte[4];
            for (int i = 0; i < 4; i++) {
                ipV4Address[i] = ipV6Address[i + 12];
            }
            InetAddress inet4Adress = Inet4Address.getByAddress(ipV4Address);
            return new InetSocketAddress(inet4Adress, inetSocketAddress.getPort());
        }
        return inetSocketAddress;
    }

    protected NioSession newSession(IoProcessor<NioSession> processor, DatagramChannel handle, SocketAddress remoteAddress) {
        SelectionKey key = handle.keyFor(this.selector);
        if (key == null || !key.isValid()) {
            return null;
        }
        NioDatagramSession newSession = new NioDatagramSession(this, handle, processor, remoteAddress);
        newSession.setSelectionKey(key);
        return newSession;
    }

    @Override // org.apache.mina.core.service.IoAcceptor
    public final IoSession newSession(SocketAddress remoteAddress, SocketAddress localAddress) {
        IoSession ioSessionNewSessionWithoutLock;
        if (isDisposing()) {
            throw new IllegalStateException("Already disposed.");
        }
        if (remoteAddress == null) {
            throw new IllegalArgumentException("remoteAddress");
        }
        synchronized (this.bindLock) {
            if (!isActive()) {
                throw new IllegalStateException("Can't create a session from a unbound service.");
            }
            try {
                try {
                    ioSessionNewSessionWithoutLock = newSessionWithoutLock(remoteAddress, localAddress);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e2) {
                    throw new RuntimeIoException("Failed to create a session.", e2);
                }
            } catch (Error e3) {
                throw e3;
            }
        }
        return ioSessionNewSessionWithoutLock;
    }

    protected DatagramChannel open(SocketAddress localAddress) throws Exception {
        DatagramChannel ch = DatagramChannel.open();
        boolean success = false;
        try {
            new NioDatagramSessionConfig(ch).setAll(getSessionConfig());
            ch.configureBlocking(false);
            try {
                ch.socket().bind(localAddress);
                ch.register(this.selector, 1);
                success = true;
                return ch;
            } catch (IOException ioe) {
                String newMessage = "Error while binding on " + localAddress + "\noriginal message : " + ioe.getMessage();
                Exception e = new IOException(newMessage);
                e.initCause(ioe.getCause());
                ch.close();
                throw e;
            }
        } finally {
            if (!success) {
                close(ch);
            }
        }
    }

    protected SocketAddress receive(DatagramChannel handle, IoBuffer buffer) throws Exception {
        return handle.receive(buffer.buf());
    }

    @Override // org.apache.mina.core.service.IoProcessor
    public void remove(NioSession session) {
        getSessionRecycler().remove(session);
        getListeners().fireSessionDestroyed(session);
    }

    protected int select() throws Exception {
        return this.selector.select();
    }

    protected int select(long timeout) throws Exception {
        return this.selector.select(timeout);
    }

    protected Set<SelectionKey> selectedHandles() {
        return this.selector.selectedKeys();
    }

    protected int send(NioSession session, IoBuffer buffer, SocketAddress remoteAddress) throws Exception {
        return ((DatagramChannel) session.getChannel()).send(buffer.buf(), remoteAddress);
    }

    @Override // org.apache.mina.transport.socket.DatagramAcceptor
    public void setDefaultLocalAddress(InetSocketAddress localAddress) {
        setDefaultLocalAddress((SocketAddress) localAddress);
    }

    protected void setInterestedInWrite(NioSession session, boolean isInterested) throws Exception {
        int newInterestOps;
        SelectionKey key = session.getSelectionKey();
        if (key != null) {
            int newInterestOps2 = key.interestOps();
            if (isInterested) {
                newInterestOps = newInterestOps2 | 4;
            } else {
                newInterestOps = newInterestOps2 & (-5);
            }
            key.interestOps(newInterestOps);
        }
    }

    @Override // org.apache.mina.transport.socket.DatagramAcceptor
    public final void setSessionRecycler(IoSessionRecycler sessionRecycler) {
        synchronized (this.bindLock) {
            if (isActive()) {
                throw new IllegalStateException("sessionRecycler can't be set while the acceptor is bound.");
            }
            if (sessionRecycler == null) {
                sessionRecycler = DEFAULT_RECYCLER;
            }
            this.sessionRecycler = sessionRecycler;
        }
    }

    @Override // org.apache.mina.core.service.AbstractIoAcceptor
    protected final void unbind0(List<? extends SocketAddress> localAddresses) throws Exception {
        AbstractIoAcceptor.AcceptorOperationFuture request = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
        this.cancelQueue.add(request);
        startupAcceptor();
        wakeup();
        request.awaitUninterruptibly();
        if (request.getException() != null) {
            throw request.getException();
        }
    }

    @Override // org.apache.mina.core.service.IoProcessor
    public void updateTrafficControl(NioSession session) {
        throw new UnsupportedOperationException();
    }

    protected void wakeup() {
        this.selector.wakeup();
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x004b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x006b A[SYNTHETIC] */
    @Override // org.apache.mina.core.service.IoProcessor
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void write(org.apache.mina.transport.socket.nio.NioSession r15, org.apache.mina.core.write.WriteRequest r16) {
        /*
            r14 = this;
            r13 = 0
            long r4 = java.lang.System.currentTimeMillis()
            org.apache.mina.core.write.WriteRequestQueue r9 = r15.getWriteRequestQueue()
            org.apache.mina.core.session.IoSessionConfig r11 = r15.getConfig()
            int r11 = r11.getMaxReadBufferSize()
            org.apache.mina.core.session.IoSessionConfig r12 = r15.getConfig()
            int r12 = r12.getMaxReadBufferSize()
            int r12 = r12 >>> 1
            int r8 = r11 + r12
            r10 = 0
            java.lang.Object r2 = r16.getMessage()
            org.apache.mina.core.buffer.IoBuffer r2 = (org.apache.mina.core.buffer.IoBuffer) r2
            int r11 = r2.remaining()
            if (r11 != 0) goto L5b
            r15.setCurrentWriteRequest(r13)
            r2.reset()
            org.apache.mina.core.filterchain.IoFilterChain r11 = r15.getFilterChain()
            r0 = r16
            r11.fireMessageSent(r0)
        L39:
            return
        L3a:
            r15.setCurrentWriteRequest(r16)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
        L3d:
            java.lang.Object r11 = r16.getMessage()     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            r0 = r11
            org.apache.mina.core.buffer.IoBuffer r0 = (org.apache.mina.core.buffer.IoBuffer) r0     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            r2 = r0
            int r11 = r2.remaining()     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            if (r11 != 0) goto L6b
            r11 = 0
            r15.setCurrentWriteRequest(r11)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            r2.reset()     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            org.apache.mina.core.filterchain.IoFilterChain r11 = r15.getFilterChain()     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            r0 = r16
            r11.fireMessageSent(r0)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
        L5b:
            if (r16 != 0) goto L3d
            org.apache.mina.core.write.WriteRequest r16 = r9.poll(r15)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            if (r16 != 0) goto L3a
            r11 = 0
            r14.setInterestedInWrite(r15, r11)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
        L67:
            r15.increaseWrittenBytes(r10, r4)
            goto L39
        L6b:
            java.net.SocketAddress r3 = r16.getDestination()     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            if (r3 != 0) goto L75
            java.net.SocketAddress r3 = r15.getRemoteAddress()     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
        L75:
            int r7 = r14.send(r15, r2, r3)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            if (r7 == 0) goto L7d
            if (r10 < r8) goto L9a
        L7d:
            r11 = 1
            r14.setInterestedInWrite(r15, r11)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            org.apache.mina.core.write.WriteRequestQueue r11 = r15.getWriteRequestQueue()     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            r0 = r16
            r11.offer(r15, r0)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            r14.scheduleFlush(r15)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            goto L5b
        L8e:
            r6 = move-exception
            org.apache.mina.core.filterchain.IoFilterChain r11 = r15.getFilterChain()     // Catch: java.lang.Throwable -> Lb0
            r11.fireExceptionCaught(r6)     // Catch: java.lang.Throwable -> Lb0
            r15.increaseWrittenBytes(r10, r4)
            goto L39
        L9a:
            r11 = 0
            r14.setInterestedInWrite(r15, r11)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            r11 = 0
            r15.setCurrentWriteRequest(r11)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            int r10 = r10 + r7
            r2.reset()     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            org.apache.mina.core.filterchain.IoFilterChain r11 = r15.getFilterChain()     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            r0 = r16
            r11.fireMessageSent(r0)     // Catch: java.lang.Exception -> L8e java.lang.Throwable -> Lb0
            goto L67
        Lb0:
            r11 = move-exception
            r15.increaseWrittenBytes(r10, r4)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.mina.transport.socket.nio.NioDatagramAcceptor.write(org.apache.mina.transport.socket.nio.NioSession, org.apache.mina.core.write.WriteRequest):void");
    }
}
