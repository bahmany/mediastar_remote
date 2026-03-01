package org.apache.mina.core.polling;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.channels.ClosedSelectorException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.FileRegion;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.future.DefaultIoFuture;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.IoServiceListenerSupport;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.SessionState;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;
import org.apache.mina.core.write.WriteToClosedSessionException;
import org.apache.mina.transport.socket.AbstractDatagramSessionConfig;
import org.apache.mina.util.ExceptionMonitor;
import org.apache.mina.util.NamePreservingRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: classes.dex */
public abstract class AbstractPollingIoProcessor<S extends AbstractIoSession> implements IoProcessor<S> {
    private static final long SELECT_TIMEOUT = 1000;
    private static final int WRITE_SPIN_COUNT = 256;
    private volatile boolean disposed;
    private volatile boolean disposing;
    private final Executor executor;
    private long lastIdleCheckTime;
    private final String threadName;
    private static final Logger LOG = LoggerFactory.getLogger(IoProcessor.class);
    private static final ConcurrentHashMap<Class<?>, AtomicInteger> threadIds = new ConcurrentHashMap<>();
    private final Queue<S> newSessions = new ConcurrentLinkedQueue();
    private final Queue<S> removingSessions = new ConcurrentLinkedQueue();
    private final Queue<S> flushingSessions = new ConcurrentLinkedQueue();
    private final Queue<S> trafficControllingSessions = new ConcurrentLinkedQueue();
    private final AtomicReference<AbstractPollingIoProcessor<S>.Processor> processorRef = new AtomicReference<>();
    private final Object disposalLock = new Object();
    private final DefaultIoFuture disposalFuture = new DefaultIoFuture(null);
    protected AtomicBoolean wakeupCalled = new AtomicBoolean(false);

    protected abstract Iterator<S> allSessions();

    protected abstract void destroy(S s) throws Exception;

    protected abstract void doDispose() throws Exception;

    protected abstract SessionState getState(S s);

    protected abstract void init(S s) throws Exception;

    protected abstract boolean isBrokenConnection() throws IOException;

    protected abstract boolean isInterestedInRead(S s);

    protected abstract boolean isInterestedInWrite(S s);

    protected abstract boolean isReadable(S s);

    protected abstract boolean isSelectorEmpty();

    protected abstract boolean isWritable(S s);

    protected abstract int read(S s, IoBuffer ioBuffer) throws Exception;

    protected abstract void registerNewSelector() throws IOException;

    protected abstract int select() throws Exception;

    protected abstract int select(long j) throws Exception;

    protected abstract Iterator<S> selectedSessions();

    protected abstract void setInterestedInRead(S s, boolean z) throws Exception;

    protected abstract void setInterestedInWrite(S s, boolean z) throws Exception;

    protected abstract int transferFile(S s, FileRegion fileRegion, int i) throws Exception;

    protected abstract void wakeup();

    protected abstract int write(S s, IoBuffer ioBuffer, int i) throws Exception;

    protected AbstractPollingIoProcessor(Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("executor");
        }
        this.threadName = nextThreadName();
        this.executor = executor;
    }

    private String nextThreadName() {
        int newThreadId;
        Class<?> cls = getClass();
        AtomicInteger threadId = threadIds.putIfAbsent(cls, new AtomicInteger(1));
        if (threadId == null) {
            newThreadId = 1;
        } else {
            newThreadId = threadId.incrementAndGet();
        }
        return cls.getSimpleName() + '-' + newThreadId;
    }

    @Override // org.apache.mina.core.service.IoProcessor
    public final boolean isDisposing() {
        return this.disposing;
    }

    @Override // org.apache.mina.core.service.IoProcessor
    public final boolean isDisposed() {
        return this.disposed;
    }

    @Override // org.apache.mina.core.service.IoProcessor
    public final void dispose() {
        if (!this.disposed && !this.disposing) {
            synchronized (this.disposalLock) {
                this.disposing = true;
                startupProcessor();
            }
            this.disposalFuture.awaitUninterruptibly();
            this.disposed = true;
        }
    }

    @Override // org.apache.mina.core.service.IoProcessor
    public final void add(S session) {
        if (this.disposed || this.disposing) {
            throw new IllegalStateException("Already disposed.");
        }
        this.newSessions.add(session);
        startupProcessor();
    }

    @Override // org.apache.mina.core.service.IoProcessor
    public final void remove(S session) {
        scheduleRemove(session);
        startupProcessor();
    }

    public void scheduleRemove(S session) {
        this.removingSessions.add(session);
    }

    @Override // org.apache.mina.core.service.IoProcessor
    public void write(S session, WriteRequest writeRequest) {
        WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
        writeRequestQueue.offer(session, writeRequest);
        if (!session.isWriteSuspended()) {
            flush((AbstractPollingIoProcessor<S>) session);
        }
    }

    @Override // org.apache.mina.core.service.IoProcessor
    public final void flush(S session) {
        if (session.setScheduledForFlush(true)) {
            this.flushingSessions.add(session);
            wakeup();
        }
    }

    private void scheduleFlush(S session) {
        if (session.setScheduledForFlush(true)) {
            this.flushingSessions.add(session);
        }
    }

    public final void updateTrafficMask(S session) {
        this.trafficControllingSessions.add(session);
        wakeup();
    }

    private void startupProcessor() {
        AbstractPollingIoProcessor<S>.Processor processor = this.processorRef.get();
        if (processor == null) {
            AbstractPollingIoProcessor<S>.Processor processor2 = new Processor();
            if (this.processorRef.compareAndSet(null, processor2)) {
                this.executor.execute(new NamePreservingRunnable(processor2, this.threadName));
            }
        }
        wakeup();
    }

    public int handleNewSessions() {
        int addedSessions = 0;
        S session = this.newSessions.poll();
        while (session != null) {
            if (addNow(session)) {
                addedSessions++;
            }
            S session2 = this.newSessions.poll();
            session = session2;
        }
        return addedSessions;
    }

    private boolean addNow(S session) {
        try {
            init(session);
            IoFilterChainBuilder chainBuilder = session.getService().getFilterChainBuilder();
            chainBuilder.buildFilterChain(session.getFilterChain());
            IoServiceListenerSupport listeners = ((AbstractIoService) session.getService()).getListeners();
            listeners.fireSessionCreated(session);
            return true;
        } catch (Exception e) {
            ExceptionMonitor.getInstance().exceptionCaught(e);
            try {
                try {
                    destroy(session);
                    return false;
                } catch (Exception e1) {
                    ExceptionMonitor.getInstance().exceptionCaught(e1);
                    return false;
                }
            } finally {
            }
        }
    }

    public int removeSessions() {
        int removedSessions = 0;
        S session = this.removingSessions.poll();
        while (session != null) {
            SessionState state = getState(session);
            switch (state) {
                case OPENED:
                    if (!removeNow(session)) {
                        break;
                    } else {
                        removedSessions++;
                        break;
                    }
                case CLOSING:
                    break;
                case OPENING:
                    this.newSessions.remove(session);
                    if (!removeNow(session)) {
                        break;
                    } else {
                        removedSessions++;
                        break;
                    }
                default:
                    throw new IllegalStateException(String.valueOf(state));
            }
            S session2 = this.removingSessions.poll();
            session = session2;
        }
        return removedSessions;
    }

    /* JADX WARN: Finally extract failed */
    private boolean removeNow(S session) {
        boolean z;
        clearWriteRequestQueue(session);
        try {
            try {
                destroy(session);
                clearWriteRequestQueue(session);
                ((AbstractIoService) session.getService()).getListeners().fireSessionDestroyed(session);
                z = true;
            } catch (Exception e) {
                IoFilterChain filterChain = session.getFilterChain();
                filterChain.fireExceptionCaught(e);
                clearWriteRequestQueue(session);
                ((AbstractIoService) session.getService()).getListeners().fireSessionDestroyed(session);
                z = false;
            }
            return z;
        } catch (Throwable th) {
            clearWriteRequestQueue(session);
            ((AbstractIoService) session.getService()).getListeners().fireSessionDestroyed(session);
            throw th;
        }
    }

    private void clearWriteRequestQueue(S session) {
        WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
        List<WriteRequest> failedRequests = new ArrayList<>();
        WriteRequest req = writeRequestQueue.poll(session);
        if (req != null) {
            Object message = req.getMessage();
            if (message instanceof IoBuffer) {
                IoBuffer buf = (IoBuffer) message;
                if (buf.hasRemaining()) {
                    buf.reset();
                    failedRequests.add(req);
                } else {
                    IoFilterChain filterChain = session.getFilterChain();
                    filterChain.fireMessageSent(req);
                }
            } else {
                failedRequests.add(req);
            }
            while (true) {
                WriteRequest req2 = writeRequestQueue.poll(session);
                if (req2 == null) {
                    break;
                } else {
                    failedRequests.add(req2);
                }
            }
        }
        if (!failedRequests.isEmpty()) {
            WriteToClosedSessionException cause = new WriteToClosedSessionException(failedRequests);
            for (WriteRequest r : failedRequests) {
                session.decreaseScheduledBytesAndMessages(r);
                r.getFuture().setException(cause);
            }
            IoFilterChain filterChain2 = session.getFilterChain();
            filterChain2.fireExceptionCaught(cause);
        }
    }

    public void process() throws Exception {
        Iterator<S> i = selectedSessions();
        while (i.hasNext()) {
            S session = i.next();
            process(session);
            i.remove();
        }
    }

    private void process(S session) {
        if (isReadable(session) && !session.isReadSuspended()) {
            read(session);
        }
        if (isWritable(session) && !session.isWriteSuspended() && session.setScheduledForFlush(true)) {
            this.flushingSessions.add(session);
        }
    }

    private void read(S session) {
        int ret;
        IoSessionConfig config = session.getConfig();
        int bufferSize = config.getReadBufferSize();
        IoBuffer buf = IoBuffer.allocate(bufferSize);
        boolean hasFragmentation = session.getTransportMetadata().hasFragmentation();
        int readBytes = 0;
        try {
            try {
                if (hasFragmentation) {
                    do {
                        ret = read(session, buf);
                        if (ret <= 0) {
                            break;
                        } else {
                            readBytes += ret;
                        }
                    } while (buf.hasRemaining());
                } else {
                    ret = read(session, buf);
                    if (ret > 0) {
                        readBytes = ret;
                    }
                }
                if (readBytes > 0) {
                    IoFilterChain filterChain = session.getFilterChain();
                    filterChain.fireMessageReceived(buf);
                    if (hasFragmentation) {
                        if ((readBytes << 1) < config.getReadBufferSize()) {
                            session.decreaseReadBufferSize();
                        } else if (readBytes == config.getReadBufferSize()) {
                            session.increaseReadBufferSize();
                        }
                    }
                }
                if (ret < 0) {
                    IoFilterChain filterChain2 = session.getFilterChain();
                    filterChain2.fireInputClosed();
                }
            } finally {
                buf.flip();
            }
        } catch (Exception e) {
            if ((e instanceof IOException) && (!(e instanceof PortUnreachableException) || !AbstractDatagramSessionConfig.class.isAssignableFrom(config.getClass()) || ((AbstractDatagramSessionConfig) config).isCloseOnPortUnreachable())) {
                scheduleRemove(session);
            }
            IoFilterChain filterChain3 = session.getFilterChain();
            filterChain3.fireExceptionCaught(e);
        }
    }

    public void notifyIdleSessions(long currentTime) throws Exception {
        if (currentTime - this.lastIdleCheckTime >= 1000) {
            this.lastIdleCheckTime = currentTime;
            AbstractIoSession.notifyIdleness(allSessions(), currentTime);
        }
    }

    public void flush(long currentTime) {
        if (!this.flushingSessions.isEmpty()) {
            do {
                S session = this.flushingSessions.poll();
                if (session != null) {
                    session.unscheduledForFlush();
                    SessionState state = getState(session);
                    switch (state) {
                        case OPENED:
                            try {
                                boolean flushedAll = flushNow(session, currentTime);
                                if (flushedAll && !session.getWriteRequestQueue().isEmpty(session) && !session.isScheduledForFlush()) {
                                    scheduleFlush(session);
                                    break;
                                }
                            } catch (Exception e) {
                                scheduleRemove(session);
                                IoFilterChain filterChain = session.getFilterChain();
                                filterChain.fireExceptionCaught(e);
                                break;
                            }
                            break;
                        case CLOSING:
                            break;
                        case OPENING:
                            scheduleFlush(session);
                            return;
                        default:
                            throw new IllegalStateException(String.valueOf(state));
                    }
                } else {
                    return;
                }
            } while (!this.flushingSessions.isEmpty());
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:108:0x00df A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:78:0x0053 A[Catch: Exception -> 0x00c9, TryCatch #0 {Exception -> 0x00c9, blocks: (B:69:0x002e, B:70:0x0035, B:72:0x003b, B:75:0x0045, B:76:0x004a, B:78:0x0053, B:80:0x0061, B:82:0x006b, B:98:0x00e0, B:99:0x00ea, B:101:0x00ed, B:102:0x00f3, B:104:0x00f7, B:83:0x0076, B:85:0x007a, B:87:0x0088, B:89:0x0096, B:90:0x00a2, B:91:0x00c8), top: B:107:0x002e }] */
    /* JADX WARN: Removed duplicated region for block: B:83:0x0076 A[Catch: Exception -> 0x00c9, TryCatch #0 {Exception -> 0x00c9, blocks: (B:69:0x002e, B:70:0x0035, B:72:0x003b, B:75:0x0045, B:76:0x004a, B:78:0x0053, B:80:0x0061, B:82:0x006b, B:98:0x00e0, B:99:0x00ea, B:101:0x00ed, B:102:0x00f3, B:104:0x00f7, B:83:0x0076, B:85:0x007a, B:87:0x0088, B:89:0x0096, B:90:0x00a2, B:91:0x00c8), top: B:107:0x002e }] */
    /* JADX WARN: Removed duplicated region for block: B:99:0x00ea A[Catch: Exception -> 0x00c9, TryCatch #0 {Exception -> 0x00c9, blocks: (B:69:0x002e, B:70:0x0035, B:72:0x003b, B:75:0x0045, B:76:0x004a, B:78:0x0053, B:80:0x0061, B:82:0x006b, B:98:0x00e0, B:99:0x00ea, B:101:0x00ed, B:102:0x00f3, B:104:0x00f7, B:83:0x0076, B:85:0x007a, B:87:0x0088, B:89:0x0096, B:90:0x00a2, B:91:0x00c8), top: B:107:0x002e }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean flushNow(S r19, long r20) {
        /*
            Method dump skipped, instructions count: 256
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.mina.core.polling.AbstractPollingIoProcessor.flushNow(org.apache.mina.core.session.AbstractIoSession, long):boolean");
    }

    private int writeBuffer(S session, WriteRequest req, boolean hasFragmentation, int maxLength, long currentTime) throws Exception {
        int length;
        IoBuffer buf = (IoBuffer) req.getMessage();
        int localWrittenBytes = 0;
        if (buf.hasRemaining()) {
            if (hasFragmentation) {
                length = Math.min(buf.remaining(), maxLength);
            } else {
                length = buf.remaining();
            }
            try {
                localWrittenBytes = write(session, buf, length);
            } catch (IOException e) {
                buf.free();
                session.close(true);
                destroy(session);
                return 0;
            }
        }
        session.increaseWrittenBytes(localWrittenBytes, currentTime);
        if (!buf.hasRemaining() || (!hasFragmentation && localWrittenBytes != 0)) {
            int pos = buf.position();
            buf.reset();
            fireMessageSent(session, req);
            buf.position(pos);
        }
        return localWrittenBytes;
    }

    private int writeFile(S session, WriteRequest req, boolean hasFragmentation, int maxLength, long currentTime) throws Exception {
        int localWrittenBytes;
        int length;
        FileRegion region = (FileRegion) req.getMessage();
        if (region.getRemainingBytes() > 0) {
            if (hasFragmentation) {
                length = (int) Math.min(region.getRemainingBytes(), maxLength);
            } else {
                length = (int) Math.min(2147483647L, region.getRemainingBytes());
            }
            localWrittenBytes = transferFile(session, region, length);
            region.update(localWrittenBytes);
        } else {
            localWrittenBytes = 0;
        }
        session.increaseWrittenBytes(localWrittenBytes, currentTime);
        if (region.getRemainingBytes() <= 0 || (!hasFragmentation && localWrittenBytes != 0)) {
            fireMessageSent(session, req);
        }
        return localWrittenBytes;
    }

    private void fireMessageSent(S session, WriteRequest req) {
        session.setCurrentWriteRequest(null);
        IoFilterChain filterChain = session.getFilterChain();
        filterChain.fireMessageSent(req);
    }

    public void updateTrafficMask() {
        for (int queueSize = this.trafficControllingSessions.size(); queueSize > 0; queueSize--) {
            S session = this.trafficControllingSessions.poll();
            if (session != null) {
                SessionState state = getState(session);
                switch (state) {
                    case OPENED:
                        updateTrafficControl((AbstractPollingIoProcessor<S>) session);
                        break;
                    case CLOSING:
                        break;
                    case OPENING:
                        this.trafficControllingSessions.add(session);
                        break;
                    default:
                        throw new IllegalStateException(String.valueOf(state));
                }
            } else {
                return;
            }
        }
    }

    @Override // org.apache.mina.core.service.IoProcessor
    public void updateTrafficControl(S session) {
        try {
            setInterestedInRead(session, !session.isReadSuspended());
        } catch (Exception e) {
            IoFilterChain filterChain = session.getFilterChain();
            filterChain.fireExceptionCaught(e);
        }
        try {
            setInterestedInWrite(session, (session.getWriteRequestQueue().isEmpty(session) || session.isWriteSuspended()) ? false : true);
        } catch (Exception e2) {
            IoFilterChain filterChain2 = session.getFilterChain();
            filterChain2.fireExceptionCaught(e2);
        }
    }

    private class Processor implements Runnable {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !AbstractPollingIoProcessor.class.desiredAssertionStatus();
        }

        private Processor() {
        }

        /* synthetic */ Processor(AbstractPollingIoProcessor x0, AnonymousClass1 x1) {
            this();
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException {
            long t0;
            int selected;
            long t1;
            long delta;
            if (!$assertionsDisabled && AbstractPollingIoProcessor.this.processorRef.get() != this) {
                throw new AssertionError();
            }
            int nSessions = 0;
            AbstractPollingIoProcessor.this.lastIdleCheckTime = System.currentTimeMillis();
            while (true) {
                try {
                    t0 = System.currentTimeMillis();
                    selected = AbstractPollingIoProcessor.this.select(1000L);
                    t1 = System.currentTimeMillis();
                    delta = t1 - t0;
                } catch (ClosedSelectorException cse) {
                    ExceptionMonitor.getInstance().exceptionCaught(cse);
                } catch (Exception e) {
                    ExceptionMonitor.getInstance().exceptionCaught(e);
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e1) {
                        ExceptionMonitor.getInstance().exceptionCaught(e1);
                    }
                }
                if (selected != 0 || AbstractPollingIoProcessor.this.wakeupCalled.get() || delta >= 100) {
                    int nSessions2 = nSessions + AbstractPollingIoProcessor.this.handleNewSessions();
                    AbstractPollingIoProcessor.this.updateTrafficMask();
                    if (selected > 0) {
                        AbstractPollingIoProcessor.this.process();
                    }
                    long currentTime = System.currentTimeMillis();
                    AbstractPollingIoProcessor.this.flush(currentTime);
                    nSessions = nSessions2 - AbstractPollingIoProcessor.this.removeSessions();
                    AbstractPollingIoProcessor.this.notifyIdleSessions(currentTime);
                    if (nSessions == 0) {
                        AbstractPollingIoProcessor.this.processorRef.set(null);
                        if (AbstractPollingIoProcessor.this.newSessions.isEmpty() && AbstractPollingIoProcessor.this.isSelectorEmpty()) {
                            if (!$assertionsDisabled && AbstractPollingIoProcessor.this.processorRef.get() == this) {
                                throw new AssertionError();
                            }
                        } else {
                            if (!$assertionsDisabled && AbstractPollingIoProcessor.this.processorRef.get() == this) {
                                throw new AssertionError();
                            }
                            if (!AbstractPollingIoProcessor.this.processorRef.compareAndSet(null, this)) {
                                if (!$assertionsDisabled && AbstractPollingIoProcessor.this.processorRef.get() == this) {
                                    throw new AssertionError();
                                }
                            } else if (!$assertionsDisabled && AbstractPollingIoProcessor.this.processorRef.get() != this) {
                                throw new AssertionError();
                            }
                        }
                    }
                    if (AbstractPollingIoProcessor.this.isDisposing()) {
                        Iterator<S> i = AbstractPollingIoProcessor.this.allSessions();
                        while (i.hasNext()) {
                            AbstractPollingIoProcessor.this.scheduleRemove(i.next());
                        }
                        AbstractPollingIoProcessor.this.wakeup();
                    }
                } else if (AbstractPollingIoProcessor.this.isBrokenConnection()) {
                    AbstractPollingIoProcessor.LOG.warn("Broken connection");
                    AbstractPollingIoProcessor.this.wakeupCalled.getAndSet(false);
                } else {
                    AbstractPollingIoProcessor.LOG.warn("Create a new selector. Selected is 0, delta = " + (t1 - t0));
                    AbstractPollingIoProcessor.this.registerNewSelector();
                    AbstractPollingIoProcessor.this.wakeupCalled.getAndSet(false);
                }
            }
            try {
                synchronized (AbstractPollingIoProcessor.this.disposalLock) {
                    if (AbstractPollingIoProcessor.this.disposing) {
                        AbstractPollingIoProcessor.this.doDispose();
                    }
                }
            } catch (Exception e2) {
                ExceptionMonitor.getInstance().exceptionCaught(e2);
            } finally {
                AbstractPollingIoProcessor.this.disposalFuture.setValue(Boolean.valueOf(true));
            }
        }
    }
}
