package org.apache.mina.core.future;

import java.util.EventListener;
import org.apache.mina.core.future.IoFuture;

/* loaded from: classes.dex */
public interface IoFutureListener<F extends IoFuture> extends EventListener {
    public static final IoFutureListener<IoFuture> CLOSE = new IoFutureListener<IoFuture>() { // from class: org.apache.mina.core.future.IoFutureListener.1
        AnonymousClass1() {
        }

        @Override // org.apache.mina.core.future.IoFutureListener
        public void operationComplete(IoFuture future) {
            future.getSession().close(true);
        }
    };

    void operationComplete(F f);

    /* renamed from: org.apache.mina.core.future.IoFutureListener$1 */
    static class AnonymousClass1 implements IoFutureListener<IoFuture> {
        AnonymousClass1() {
        }

        @Override // org.apache.mina.core.future.IoFutureListener
        public void operationComplete(IoFuture future) {
            future.getSession().close(true);
        }
    }
}
