package org.apache.mina.core.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.mina.core.polling.AbstractPollingIoProcessor;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.util.ExceptionMonitor;

/* loaded from: classes.dex */
public class DefaultIoFuture implements IoFuture {
    private static final long DEAD_LOCK_CHECK_INTERVAL = 5000;
    private IoFutureListener<?> firstListener;
    private final Object lock = this;
    private List<IoFutureListener<?>> otherListeners;
    private boolean ready;
    private Object result;
    private final IoSession session;
    private int waiters;

    public DefaultIoFuture(IoSession session) {
        this.session = session;
    }

    @Override // org.apache.mina.core.future.IoFuture
    public IoSession getSession() {
        return this.session;
    }

    @Override // org.apache.mina.core.future.IoFuture
    @Deprecated
    public void join() {
        awaitUninterruptibly();
    }

    @Override // org.apache.mina.core.future.IoFuture
    @Deprecated
    public boolean join(long timeoutMillis) {
        return awaitUninterruptibly(timeoutMillis);
    }

    @Override // org.apache.mina.core.future.IoFuture
    public IoFuture await() throws InterruptedException {
        synchronized (this.lock) {
            while (!this.ready) {
                this.waiters++;
                try {
                    this.lock.wait(DEAD_LOCK_CHECK_INTERVAL);
                    this.waiters--;
                    if (!this.ready) {
                        checkDeadLock();
                    }
                } catch (Throwable th) {
                    this.waiters--;
                    if (!this.ready) {
                        checkDeadLock();
                    }
                    throw th;
                }
            }
        }
        return this;
    }

    @Override // org.apache.mina.core.future.IoFuture
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return await(unit.toMillis(timeout));
    }

    @Override // org.apache.mina.core.future.IoFuture
    public boolean await(long timeoutMillis) throws InterruptedException {
        return await0(timeoutMillis, true);
    }

    @Override // org.apache.mina.core.future.IoFuture
    public IoFuture awaitUninterruptibly() {
        try {
            await0(Long.MAX_VALUE, false);
        } catch (InterruptedException e) {
        }
        return this;
    }

    @Override // org.apache.mina.core.future.IoFuture
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return awaitUninterruptibly(unit.toMillis(timeout));
    }

    @Override // org.apache.mina.core.future.IoFuture
    public boolean awaitUninterruptibly(long timeoutMillis) {
        try {
            return await0(timeoutMillis, false);
        } catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    private boolean await0(long timeoutMillis, boolean interruptable) throws InterruptedException {
        boolean z;
        long endTime = System.currentTimeMillis() + timeoutMillis;
        if (endTime < 0) {
            endTime = Long.MAX_VALUE;
        }
        synchronized (this.lock) {
            if (this.ready) {
                z = this.ready;
            } else if (timeoutMillis <= 0) {
                z = this.ready;
            } else {
                this.waiters++;
                while (true) {
                    try {
                        try {
                            long timeOut = Math.min(timeoutMillis, DEAD_LOCK_CHECK_INTERVAL);
                            this.lock.wait(timeOut);
                        } catch (InterruptedException e) {
                            if (interruptable) {
                                throw e;
                            }
                        }
                        if (this.ready) {
                            z = true;
                        } else if (endTime < System.currentTimeMillis()) {
                            z = this.ready;
                            this.waiters--;
                            if (!this.ready) {
                                checkDeadLock();
                            }
                        }
                    } finally {
                        this.waiters--;
                        if (!this.ready) {
                            checkDeadLock();
                        }
                    }
                }
            }
        }
        return z;
    }

    private void checkDeadLock() throws ClassNotFoundException {
        Class<?> cls;
        if ((this instanceof CloseFuture) || (this instanceof WriteFuture) || (this instanceof ReadFuture) || (this instanceof ConnectFuture)) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement s : stackTrace) {
                if (AbstractPollingIoProcessor.class.getName().equals(s.getClassName())) {
                    IllegalStateException e = new IllegalStateException("t");
                    e.getStackTrace();
                    throw new IllegalStateException("DEAD LOCK: " + IoFuture.class.getSimpleName() + ".await() was invoked from an I/O processor thread.  Please use " + IoFutureListener.class.getSimpleName() + " or configure a proper thread model alternatively.");
                }
            }
            for (StackTraceElement s2 : stackTrace) {
                try {
                    cls = DefaultIoFuture.class.getClassLoader().loadClass(s2.getClassName());
                } catch (Exception e2) {
                }
                if (!IoProcessor.class.isAssignableFrom(cls)) {
                    continue;
                } else {
                    throw new IllegalStateException("DEAD LOCK: " + IoFuture.class.getSimpleName() + ".await() was invoked from an I/O processor thread.  Please use " + IoFutureListener.class.getSimpleName() + " or configure a proper thread model alternatively.");
                }
            }
        }
    }

    @Override // org.apache.mina.core.future.IoFuture
    public boolean isDone() {
        boolean z;
        synchronized (this.lock) {
            z = this.ready;
        }
        return z;
    }

    public void setValue(Object newValue) {
        synchronized (this.lock) {
            if (!this.ready) {
                this.result = newValue;
                this.ready = true;
                if (this.waiters > 0) {
                    this.lock.notifyAll();
                }
                notifyListeners();
            }
        }
    }

    protected Object getValue() {
        Object obj;
        synchronized (this.lock) {
            obj = this.result;
        }
        return obj;
    }

    @Override // org.apache.mina.core.future.IoFuture
    public IoFuture addListener(IoFutureListener<?> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }
        boolean notifyNow = false;
        synchronized (this.lock) {
            if (this.ready) {
                notifyNow = true;
            } else if (this.firstListener == null) {
                this.firstListener = listener;
            } else {
                if (this.otherListeners == null) {
                    this.otherListeners = new ArrayList(1);
                }
                this.otherListeners.add(listener);
            }
        }
        if (notifyNow) {
            notifyListener(listener);
        }
        return this;
    }

    @Override // org.apache.mina.core.future.IoFuture
    public IoFuture removeListener(IoFutureListener<?> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }
        synchronized (this.lock) {
            if (!this.ready) {
                if (listener == this.firstListener) {
                    if (this.otherListeners != null && !this.otherListeners.isEmpty()) {
                        this.firstListener = this.otherListeners.remove(0);
                    } else {
                        this.firstListener = null;
                    }
                } else if (this.otherListeners != null) {
                    this.otherListeners.remove(listener);
                }
            }
        }
        return this;
    }

    private void notifyListeners() {
        if (this.firstListener != null) {
            notifyListener(this.firstListener);
            this.firstListener = null;
            if (this.otherListeners != null) {
                for (IoFutureListener<?> l : this.otherListeners) {
                    notifyListener(l);
                }
                this.otherListeners = null;
            }
        }
    }

    private void notifyListener(IoFutureListener l) {
        try {
            l.operationComplete(this);
        } catch (Exception e) {
            ExceptionMonitor.getInstance().exceptionCaught(e);
        }
    }
}
