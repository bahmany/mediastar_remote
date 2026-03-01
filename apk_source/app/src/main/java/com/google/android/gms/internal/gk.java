package com.google.android.gms.internal;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ez
/* loaded from: classes.dex */
public class gk<T> implements Future<T> {
    private final Object mw = new Object();
    private T wq = null;
    private boolean wr = false;
    private boolean pS = false;

    public void a(T t) {
        synchronized (this.mw) {
            if (this.wr) {
                throw new IllegalStateException("Provided CallbackFuture with multiple values.");
            }
            this.wr = true;
            this.wq = t;
            this.mw.notifyAll();
        }
    }

    @Override // java.util.concurrent.Future
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean z = false;
        if (mayInterruptIfRunning) {
            synchronized (this.mw) {
                if (!this.wr) {
                    this.pS = true;
                    this.wr = true;
                    this.mw.notifyAll();
                    z = true;
                }
            }
        }
        return z;
    }

    @Override // java.util.concurrent.Future
    public T get() {
        T t;
        synchronized (this.mw) {
            if (!this.wr) {
                try {
                    this.mw.wait();
                } catch (InterruptedException e) {
                }
            }
            if (this.pS) {
                throw new CancellationException("CallbackFuture was cancelled.");
            }
            t = this.wq;
        }
        return t;
    }

    @Override // java.util.concurrent.Future
    public T get(long timeout, TimeUnit unit) throws TimeoutException {
        T t;
        synchronized (this.mw) {
            if (!this.wr) {
                try {
                    long millis = unit.toMillis(timeout);
                    if (millis != 0) {
                        this.mw.wait(millis);
                    }
                } catch (InterruptedException e) {
                }
            }
            if (!this.wr) {
                throw new TimeoutException("CallbackFuture timed out.");
            }
            if (this.pS) {
                throw new CancellationException("CallbackFuture was cancelled.");
            }
            t = this.wq;
        }
        return t;
    }

    @Override // java.util.concurrent.Future
    public boolean isCancelled() {
        boolean z;
        synchronized (this.mw) {
            z = this.pS;
        }
        return z;
    }

    @Override // java.util.concurrent.Future
    public boolean isDone() {
        boolean z;
        synchronized (this.mw) {
            z = this.wr;
        }
        return z;
    }
}
