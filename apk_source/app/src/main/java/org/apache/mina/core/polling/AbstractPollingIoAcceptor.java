package org.apache.mina.core.polling;

import java.net.SocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.service.AbstractIoAcceptor;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.util.ExceptionMonitor;

/* loaded from: classes.dex */
public abstract class AbstractPollingIoAcceptor<S extends AbstractIoSession, H> extends AbstractIoAcceptor {
    private AtomicReference<AbstractPollingIoAcceptor<S, H>.Acceptor> acceptorRef;
    protected int backlog;
    private final Map<SocketAddress, H> boundHandles;
    private final Queue<AbstractIoAcceptor.AcceptorOperationFuture> cancelQueue;
    private final boolean createdProcessor;
    private final AbstractIoService.ServiceOperationFuture disposalFuture;
    private final Semaphore lock;
    private final IoProcessor<S> processor;
    private final Queue<AbstractIoAcceptor.AcceptorOperationFuture> registerQueue;
    protected boolean reuseAddress;
    private volatile boolean selectable;

    protected abstract S accept(IoProcessor<S> ioProcessor, H h) throws Exception;

    protected abstract void close(H h) throws Exception;

    protected abstract void destroy() throws Exception;

    protected abstract void init() throws Exception;

    protected abstract void init(SelectorProvider selectorProvider) throws Exception;

    protected abstract SocketAddress localAddress(H h) throws Exception;

    protected abstract H open(SocketAddress socketAddress) throws Exception;

    protected abstract int select() throws Exception;

    protected abstract Iterator<H> selectedHandles();

    protected abstract void wakeup();

    protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Class<? extends IoProcessor<S>> processorClass) {
        this(sessionConfig, null, new SimpleIoProcessorPool(processorClass), true, null);
    }

    protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Class<? extends IoProcessor<S>> processorClass, int processorCount) {
        this(sessionConfig, null, new SimpleIoProcessorPool(processorClass, processorCount), true, null);
    }

    protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Class<? extends IoProcessor<S>> processorClass, int processorCount, SelectorProvider selectorProvider) {
        this(sessionConfig, null, new SimpleIoProcessorPool(processorClass, processorCount, selectorProvider), true, selectorProvider);
    }

    protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, IoProcessor<S> processor) {
        this(sessionConfig, null, processor, false, null);
    }

    protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Executor executor, IoProcessor<S> processor) {
        this(sessionConfig, executor, processor, false, null);
    }

    private AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Executor executor, IoProcessor<S> processor, boolean createdProcessor, SelectorProvider selectorProvider) {
        super(sessionConfig, executor);
        this.lock = new Semaphore(1);
        this.registerQueue = new ConcurrentLinkedQueue();
        this.cancelQueue = new ConcurrentLinkedQueue();
        this.boundHandles = Collections.synchronizedMap(new HashMap());
        this.disposalFuture = new AbstractIoService.ServiceOperationFuture();
        this.acceptorRef = new AtomicReference<>();
        this.reuseAddress = false;
        this.backlog = 50;
        if (processor == null) {
            throw new IllegalArgumentException("processor");
        }
        this.processor = processor;
        this.createdProcessor = createdProcessor;
        try {
            try {
                try {
                    init(selectorProvider);
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
                }
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

    @Override // org.apache.mina.core.service.AbstractIoService
    protected void dispose0() throws Exception {
        unbind();
        startupAcceptor();
        wakeup();
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
            for (H handle : this.boundHandles.values()) {
                newLocalAddresses.add(localAddress(handle));
            }
            return newLocalAddresses;
        } catch (Throwable th) {
            this.lock.release();
            throw th;
        }
    }

    private void startupAcceptor() throws InterruptedException {
        if (!this.selectable) {
            this.registerQueue.clear();
            this.cancelQueue.clear();
        }
        AbstractPollingIoAcceptor<S, H>.Acceptor acceptor = this.acceptorRef.get();
        if (acceptor == null) {
            this.lock.acquire();
            AbstractPollingIoAcceptor<S, H>.Acceptor acceptor2 = new Acceptor();
            if (this.acceptorRef.compareAndSet(null, acceptor2)) {
                executeWorker(acceptor2);
            } else {
                this.lock.release();
            }
        }
    }

    @Override // org.apache.mina.core.service.AbstractIoAcceptor
    protected final void unbind0(List<? extends SocketAddress> localAddresses) throws Exception {
        AbstractIoAcceptor.AcceptorOperationFuture future = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
        this.cancelQueue.add(future);
        startupAcceptor();
        wakeup();
        future.awaitUninterruptibly();
        if (future.getException() != null) {
            throw future.getException();
        }
    }

    private class Acceptor implements Runnable {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !AbstractPollingIoAcceptor.class.desiredAssertionStatus();
        }

        private Acceptor() {
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException {
            if (!$assertionsDisabled && AbstractPollingIoAcceptor.this.acceptorRef.get() != this) {
                throw new AssertionError();
            }
            int nHandles = 0;
            AbstractPollingIoAcceptor.this.lock.release();
            while (true) {
                if (!AbstractPollingIoAcceptor.this.selectable) {
                    break;
                }
                try {
                    int selected = AbstractPollingIoAcceptor.this.select();
                    int nHandles2 = nHandles + AbstractPollingIoAcceptor.this.registerHandles();
                    if (nHandles2 == 0) {
                        AbstractPollingIoAcceptor.this.acceptorRef.set(null);
                        if (AbstractPollingIoAcceptor.this.registerQueue.isEmpty() && AbstractPollingIoAcceptor.this.cancelQueue.isEmpty()) {
                            if (!$assertionsDisabled && AbstractPollingIoAcceptor.this.acceptorRef.get() == this) {
                                throw new AssertionError();
                            }
                        } else if (AbstractPollingIoAcceptor.this.acceptorRef.compareAndSet(null, this)) {
                            if (!$assertionsDisabled && AbstractPollingIoAcceptor.this.acceptorRef.get() != this) {
                                throw new AssertionError();
                            }
                        } else if (!$assertionsDisabled && AbstractPollingIoAcceptor.this.acceptorRef.get() == this) {
                            throw new AssertionError();
                        }
                    }
                    if (selected > 0) {
                        processHandles(AbstractPollingIoAcceptor.this.selectedHandles());
                    }
                    nHandles = nHandles2 - AbstractPollingIoAcceptor.this.unregisterHandles();
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
            }
            if (AbstractPollingIoAcceptor.this.selectable && AbstractPollingIoAcceptor.this.isDisposing()) {
                AbstractPollingIoAcceptor.this.selectable = false;
                try {
                    if (AbstractPollingIoAcceptor.this.createdProcessor) {
                        AbstractPollingIoAcceptor.this.processor.dispose();
                    }
                    try {
                        synchronized (AbstractPollingIoAcceptor.this.disposalLock) {
                            if (AbstractPollingIoAcceptor.this.isDisposing()) {
                                AbstractPollingIoAcceptor.this.destroy();
                            }
                        }
                    } catch (Exception e2) {
                        ExceptionMonitor.getInstance().exceptionCaught(e2);
                    } finally {
                    }
                } catch (Throwable th) {
                    try {
                    } catch (Exception e3) {
                        ExceptionMonitor.getInstance().exceptionCaught(e3);
                    } finally {
                    }
                    synchronized (AbstractPollingIoAcceptor.this.disposalLock) {
                        if (AbstractPollingIoAcceptor.this.isDisposing()) {
                            AbstractPollingIoAcceptor.this.destroy();
                        }
                        throw th;
                    }
                }
            }
        }

        private void processHandles(Iterator<H> handles) throws Exception {
            while (handles.hasNext()) {
                H handle = handles.next();
                handles.remove();
                AbstractIoSession abstractIoSessionAccept = AbstractPollingIoAcceptor.this.accept(AbstractPollingIoAcceptor.this.processor, handle);
                if (abstractIoSessionAccept != null) {
                    AbstractPollingIoAcceptor.this.initSession(abstractIoSessionAccept, null, null);
                    abstractIoSessionAccept.getProcessor().add(abstractIoSessionAccept);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public int registerHandles() {
        while (true) {
            AbstractIoAcceptor.AcceptorOperationFuture future = this.registerQueue.poll();
            if (future == null) {
                return 0;
            }
            ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
            List<SocketAddress> localAddresses = future.getLocalAddresses();
            try {
                for (SocketAddress a : localAddresses) {
                    Object objOpen = open(a);
                    concurrentHashMap.put(localAddress(objOpen), objOpen);
                }
                this.boundHandles.putAll(concurrentHashMap);
                future.setDone();
                int size = concurrentHashMap.size();
            } catch (Exception e) {
                try {
                    future.setException(e);
                    if (future.getException() != null) {
                        Iterator i$ = concurrentHashMap.values().iterator();
                        while (i$.hasNext()) {
                            try {
                                close(i$.next());
                            } catch (Exception e2) {
                                ExceptionMonitor.getInstance().exceptionCaught(e2);
                            }
                        }
                        wakeup();
                    }
                } finally {
                    if (future.getException() != null) {
                        Iterator i$2 = concurrentHashMap.values().iterator();
                        while (i$2.hasNext()) {
                            try {
                                close(i$2.next());
                            } catch (Exception e3) {
                                ExceptionMonitor.getInstance().exceptionCaught(e3);
                            }
                        }
                        wakeup();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int unregisterHandles() {
        int cancelledHandles = 0;
        while (true) {
            AbstractIoAcceptor.AcceptorOperationFuture future = this.cancelQueue.poll();
            if (future != null) {
                for (SocketAddress a : future.getLocalAddresses()) {
                    H handle = this.boundHandles.remove(a);
                    if (handle != null) {
                        try {
                            close(handle);
                            wakeup();
                        } catch (Exception e) {
                            ExceptionMonitor.getInstance().exceptionCaught(e);
                        } finally {
                            int cancelledHandles2 = cancelledHandles + 1;
                        }
                    }
                }
                future.setDone();
            } else {
                return cancelledHandles;
            }
        }
    }

    @Override // org.apache.mina.core.service.IoAcceptor
    public final IoSession newSession(SocketAddress remoteAddress, SocketAddress localAddress) {
        throw new UnsupportedOperationException();
    }

    public int getBacklog() {
        return this.backlog;
    }

    public void setBacklog(int backlog) {
        synchronized (this.bindLock) {
            if (isActive()) {
                throw new IllegalStateException("backlog can't be set while the acceptor is bound.");
            }
            this.backlog = backlog;
        }
    }

    public boolean isReuseAddress() {
        return this.reuseAddress;
    }

    public void setReuseAddress(boolean reuseAddress) {
        synchronized (this.bindLock) {
            if (isActive()) {
                throw new IllegalStateException("backlog can't be set while the acceptor is bound.");
            }
            this.reuseAddress = reuseAddress;
        }
    }

    @Override // org.apache.mina.core.service.IoService
    public SocketSessionConfig getSessionConfig() {
        return (SocketSessionConfig) this.sessionConfig;
    }
}
