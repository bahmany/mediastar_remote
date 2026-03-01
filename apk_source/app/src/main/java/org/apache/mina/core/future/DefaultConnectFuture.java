package org.apache.mina.core.future;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.session.IoSession;

/* loaded from: classes.dex */
public class DefaultConnectFuture extends DefaultIoFuture implements ConnectFuture {
    private static final Object CANCELED = new Object();

    @Override // org.apache.mina.core.future.DefaultIoFuture, org.apache.mina.core.future.IoFuture
    public /* bridge */ /* synthetic */ IoFuture addListener(IoFutureListener x0) {
        return addListener((IoFutureListener<?>) x0);
    }

    @Override // org.apache.mina.core.future.DefaultIoFuture, org.apache.mina.core.future.IoFuture
    public /* bridge */ /* synthetic */ IoFuture removeListener(IoFutureListener x0) {
        return removeListener((IoFutureListener<?>) x0);
    }

    public static ConnectFuture newFailedFuture(Throwable exception) {
        DefaultConnectFuture failedFuture = new DefaultConnectFuture();
        failedFuture.setException(exception);
        return failedFuture;
    }

    public DefaultConnectFuture() {
        super(null);
    }

    @Override // org.apache.mina.core.future.DefaultIoFuture, org.apache.mina.core.future.IoFuture
    public IoSession getSession() {
        Object v = getValue();
        if (v instanceof RuntimeException) {
            throw ((RuntimeException) v);
        }
        if (v instanceof Error) {
            throw ((Error) v);
        }
        if (v instanceof Throwable) {
            throw ((RuntimeIoException) new RuntimeIoException("Failed to get the session.").initCause((Throwable) v));
        }
        if (v instanceof IoSession) {
            return (IoSession) v;
        }
        return null;
    }

    @Override // org.apache.mina.core.future.ConnectFuture
    public Throwable getException() {
        Object v = getValue();
        if (v instanceof Throwable) {
            return (Throwable) v;
        }
        return null;
    }

    @Override // org.apache.mina.core.future.ConnectFuture
    public boolean isConnected() {
        return getValue() instanceof IoSession;
    }

    @Override // org.apache.mina.core.future.ConnectFuture
    public boolean isCanceled() {
        return getValue() == CANCELED;
    }

    @Override // org.apache.mina.core.future.ConnectFuture
    public void setSession(IoSession session) {
        if (session == null) {
            throw new IllegalArgumentException("session");
        }
        setValue(session);
    }

    @Override // org.apache.mina.core.future.ConnectFuture
    public void setException(Throwable exception) {
        if (exception == null) {
            throw new IllegalArgumentException("exception");
        }
        setValue(exception);
    }

    @Override // org.apache.mina.core.future.ConnectFuture
    public void cancel() {
        setValue(CANCELED);
    }

    @Override // org.apache.mina.core.future.DefaultIoFuture, org.apache.mina.core.future.IoFuture
    public ConnectFuture await() throws InterruptedException {
        return (ConnectFuture) super.await();
    }

    @Override // org.apache.mina.core.future.DefaultIoFuture, org.apache.mina.core.future.IoFuture
    public ConnectFuture awaitUninterruptibly() {
        return (ConnectFuture) super.awaitUninterruptibly();
    }

    @Override // org.apache.mina.core.future.DefaultIoFuture, org.apache.mina.core.future.IoFuture
    public ConnectFuture addListener(IoFutureListener<?> listener) {
        return (ConnectFuture) super.addListener(listener);
    }

    @Override // org.apache.mina.core.future.DefaultIoFuture, org.apache.mina.core.future.IoFuture
    public ConnectFuture removeListener(IoFutureListener<?> listener) {
        return (ConnectFuture) super.removeListener(listener);
    }
}
