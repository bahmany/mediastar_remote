package com.google.android.gms.common.api;

import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.b;
import com.google.android.gms.common.internal.i;
import com.google.android.gms.common.internal.n;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class BaseImplementation {

    public static abstract class AbstractPendingResult<R extends Result> implements b<R>, PendingResult<R> {
        private ResultCallback<R> Io;
        private volatile R Ip;
        private volatile boolean Iq;
        private boolean Ir;
        private boolean Is;
        private i It;
        protected CallbackHandler<R> mHandler;
        private final Object Im = new Object();
        private final CountDownLatch mg = new CountDownLatch(1);
        private final ArrayList<PendingResult.a> In = new ArrayList<>();

        AbstractPendingResult() {
        }

        public AbstractPendingResult(Looper looper) {
            this.mHandler = new CallbackHandler<>(looper);
        }

        public AbstractPendingResult(CallbackHandler<R> callbackHandler) {
            this.mHandler = callbackHandler;
        }

        private void c(R r) {
            this.Ip = r;
            this.It = null;
            this.mg.countDown();
            Status status = this.Ip.getStatus();
            if (this.Io != null) {
                this.mHandler.removeTimeoutMessages();
                if (!this.Ir) {
                    this.mHandler.sendResultCallback(this.Io, gg());
                }
            }
            Iterator<PendingResult.a> it = this.In.iterator();
            while (it.hasNext()) {
                it.next().n(status);
            }
            this.In.clear();
        }

        private R gg() {
            R r;
            synchronized (this.Im) {
                n.a(!this.Iq, "Result has already been consumed.");
                n.a(isReady(), "Result is not ready.");
                r = this.Ip;
                gh();
            }
            return r;
        }

        private void gi() {
            synchronized (this.Im) {
                if (!isReady()) {
                    b((AbstractPendingResult<R>) c(Status.Jp));
                    this.Is = true;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void gj() {
            synchronized (this.Im) {
                if (!isReady()) {
                    b((AbstractPendingResult<R>) c(Status.Jr));
                    this.Is = true;
                }
            }
        }

        protected void a(CallbackHandler<R> callbackHandler) {
            this.mHandler = callbackHandler;
        }

        @Override // com.google.android.gms.common.api.PendingResult
        public final void a(PendingResult.a aVar) {
            n.a(!this.Iq, "Result has already been consumed.");
            synchronized (this.Im) {
                if (isReady()) {
                    aVar.n(this.Ip.getStatus());
                } else {
                    this.In.add(aVar);
                }
            }
        }

        protected final void a(i iVar) {
            synchronized (this.Im) {
                this.It = iVar;
            }
        }

        @Override // com.google.android.gms.common.api.PendingResult
        public final R await() throws InterruptedException {
            n.a(Looper.myLooper() != Looper.getMainLooper(), "await must not be called on the UI thread");
            n.a(this.Iq ? false : true, "Result has already been consumed");
            try {
                this.mg.await();
            } catch (InterruptedException e) {
                gi();
            }
            n.a(isReady(), "Result is not ready.");
            return (R) gg();
        }

        @Override // com.google.android.gms.common.api.PendingResult
        public final R await(long j, TimeUnit timeUnit) {
            n.a(j <= 0 || Looper.myLooper() != Looper.getMainLooper(), "await must not be called on the UI thread when time is greater than zero.");
            n.a(this.Iq ? false : true, "Result has already been consumed.");
            try {
                if (!this.mg.await(j, timeUnit)) {
                    gj();
                }
            } catch (InterruptedException e) {
                gi();
            }
            n.a(isReady(), "Result is not ready.");
            return (R) gg();
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.b
        public final void b(R r) {
            synchronized (this.Im) {
                if (this.Is || this.Ir) {
                    BaseImplementation.a(r);
                    return;
                }
                n.a(!isReady(), "Results have already been set");
                n.a(this.Iq ? false : true, "Result has already been consumed");
                c((AbstractPendingResult<R>) r);
            }
        }

        protected abstract R c(Status status);

        @Override // com.google.android.gms.common.api.PendingResult
        public void cancel() {
            synchronized (this.Im) {
                if (this.Ir || this.Iq) {
                    return;
                }
                if (this.It != null) {
                    try {
                        this.It.cancel();
                    } catch (RemoteException e) {
                    }
                }
                BaseImplementation.a(this.Ip);
                this.Io = null;
                this.Ir = true;
                c((AbstractPendingResult<R>) c(Status.Js));
            }
        }

        protected void gh() {
            this.Iq = true;
            this.Ip = null;
            this.Io = null;
        }

        @Override // com.google.android.gms.common.api.PendingResult
        public boolean isCanceled() {
            boolean z;
            synchronized (this.Im) {
                z = this.Ir;
            }
            return z;
        }

        public final boolean isReady() {
            return this.mg.getCount() == 0;
        }

        @Override // com.google.android.gms.common.api.PendingResult
        public final void setResultCallback(ResultCallback<R> callback) {
            n.a(!this.Iq, "Result has already been consumed.");
            synchronized (this.Im) {
                if (isCanceled()) {
                    return;
                }
                if (isReady()) {
                    this.mHandler.sendResultCallback(callback, gg());
                } else {
                    this.Io = callback;
                }
            }
        }

        @Override // com.google.android.gms.common.api.PendingResult
        public final void setResultCallback(ResultCallback<R> callback, long time, TimeUnit units) {
            n.a(!this.Iq, "Result has already been consumed.");
            n.a(this.mHandler != null, "CallbackHandler has not been set before calling setResultCallback.");
            synchronized (this.Im) {
                if (isCanceled()) {
                    return;
                }
                if (isReady()) {
                    this.mHandler.sendResultCallback(callback, gg());
                } else {
                    this.Io = callback;
                    this.mHandler.sendTimeoutResultCallback(this, units.toMillis(time));
                }
            }
        }
    }

    public static class CallbackHandler<R extends Result> extends Handler {
        public static final int CALLBACK_ON_COMPLETE = 1;
        public static final int CALLBACK_ON_TIMEOUT = 2;

        public CallbackHandler() {
            this(Looper.getMainLooper());
        }

        public CallbackHandler(Looper looper) {
            super(looper);
        }

        protected void deliverResultCallback(ResultCallback<R> callback, R result) {
            try {
                callback.onResult(result);
            } catch (RuntimeException e) {
                BaseImplementation.a(result);
                throw e;
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Pair pair = (Pair) msg.obj;
                    deliverResultCallback((ResultCallback) pair.first, (Result) pair.second);
                    break;
                case 2:
                    ((AbstractPendingResult) msg.obj).gj();
                    break;
                default:
                    Log.wtf("GoogleApi", "Don't know how to handle this message.");
                    break;
            }
        }

        public void removeTimeoutMessages() {
            removeMessages(2);
        }

        public void sendResultCallback(ResultCallback<R> callback, R result) {
            sendMessage(obtainMessage(1, new Pair(callback, result)));
        }

        public void sendTimeoutResultCallback(AbstractPendingResult<R> pendingResult, long millis) {
            sendMessageDelayed(obtainMessage(2, pendingResult), millis);
        }
    }

    public static abstract class a<R extends Result, A extends Api.a> extends AbstractPendingResult<R> implements b.c<A> {
        private final Api.c<A> Ik;
        private b.a Iu;

        protected a(Api.c<A> cVar) {
            this.Ik = (Api.c) n.i(cVar);
        }

        private void a(RemoteException remoteException) {
            m(new Status(8, remoteException.getLocalizedMessage(), null));
        }

        protected abstract void a(A a) throws RemoteException;

        @Override // com.google.android.gms.common.api.b.c
        public void a(b.a aVar) {
            this.Iu = aVar;
        }

        @Override // com.google.android.gms.common.api.b.c
        public final void b(A a) throws DeadObjectException {
            if (this.mHandler == null) {
                a(new CallbackHandler<>(a.getLooper()));
            }
            try {
                a((a<R, A>) a);
            } catch (DeadObjectException e) {
                a(e);
                throw e;
            } catch (RemoteException e2) {
                a(e2);
            }
        }

        @Override // com.google.android.gms.common.api.b.c
        public final Api.c<A> gf() {
            return this.Ik;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        protected void gh() {
            super.gh();
            if (this.Iu != null) {
                this.Iu.b(this);
                this.Iu = null;
            }
        }

        @Override // com.google.android.gms.common.api.b.c
        public int gk() {
            return 0;
        }

        @Override // com.google.android.gms.common.api.b.c
        public final void m(Status status) {
            n.b(!status.isSuccess(), "Failed result must not be success");
            b((a<R, A>) c(status));
        }
    }

    public interface b<R> {
        void b(R r);
    }

    static void a(Result result) {
        if (result instanceof Releasable) {
            try {
                ((Releasable) result).release();
            } catch (RuntimeException e) {
                Log.w("GoogleApi", "Unable to release " + result, e);
            }
        }
    }
}
