package com.google.android.gms.common.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.internal.n;

/* loaded from: classes.dex */
public final class c<L> {
    private final c<L>.a Jl;
    private volatile L mListener;

    private final class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            n.K(msg.what == 1);
            c.this.b((b) msg.obj);
        }
    }

    public interface b<L> {
        void d(L l);

        void gs();
    }

    c(Looper looper, L l) {
        this.Jl = new a(looper);
        this.mListener = (L) n.b(l, "Listener must not be null");
    }

    public void a(b<L> bVar) {
        n.b(bVar, "Notifier must not be null");
        this.Jl.sendMessage(this.Jl.obtainMessage(1, bVar));
    }

    void b(b<L> bVar) {
        L l = this.mListener;
        if (l == null) {
            bVar.gs();
            return;
        }
        try {
            bVar.d(l);
        } catch (Exception e) {
            Log.w("ListenerHolder", "Notifying listener failed", e);
            bVar.gs();
        }
    }

    public void clear() {
        this.mListener = null;
    }
}
