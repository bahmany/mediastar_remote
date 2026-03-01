package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import com.google.android.gms.internal.cq;

@ez
/* loaded from: classes.dex */
public final class cp implements cq.a {
    private final ct lq;
    private final Context mContext;
    private final av ml;
    private final String qo;
    private final long qp;
    private final cl qq;
    private final ay qr;
    private final gt qs;
    private cu qt;
    private final Object mw = new Object();
    private int qu = -2;

    public cp(Context context, String str, ct ctVar, cm cmVar, cl clVar, av avVar, ay ayVar, gt gtVar) {
        this.mContext = context;
        this.lq = ctVar;
        this.qq = clVar;
        if ("com.google.ads.mediation.customevent.CustomEventAdapter".equals(str)) {
            this.qo = bE();
        } else {
            this.qo = str;
        }
        this.qp = cmVar.qe != -1 ? cmVar.qe : 10000L;
        this.ml = avVar;
        this.qr = ayVar;
        this.qs = gtVar;
    }

    private void a(long j, long j2, long j3, long j4) throws InterruptedException {
        while (this.qu == -2) {
            b(j, j2, j3, j4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(co coVar) {
        try {
            if (this.qs.wF < 4100000) {
                if (this.qr.og) {
                    this.qt.a(com.google.android.gms.dynamic.e.k(this.mContext), this.ml, this.qq.qc, coVar);
                } else {
                    this.qt.a(com.google.android.gms.dynamic.e.k(this.mContext), this.qr, this.ml, this.qq.qc, coVar);
                }
            } else if (this.qr.og) {
                this.qt.a(com.google.android.gms.dynamic.e.k(this.mContext), this.ml, this.qq.qc, this.qq.pW, coVar);
            } else {
                this.qt.a(com.google.android.gms.dynamic.e.k(this.mContext), this.qr, this.ml, this.qq.qc, this.qq.pW, coVar);
            }
        } catch (RemoteException e) {
            gs.d("Could not request ad from mediation adapter.", e);
            j(5);
        }
    }

    private void b(long j, long j2, long j3, long j4) throws InterruptedException {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        long j5 = j2 - (jElapsedRealtime - j);
        long j6 = j4 - (jElapsedRealtime - j3);
        if (j5 <= 0 || j6 <= 0) {
            gs.U("Timed out waiting for adapter.");
            this.qu = 3;
        } else {
            try {
                this.mw.wait(Math.min(j5, j6));
            } catch (InterruptedException e) {
                this.qu = -1;
            }
        }
    }

    private String bE() {
        try {
            if (!TextUtils.isEmpty(this.qq.qa)) {
                return this.lq.y(this.qq.qa) ? "com.google.android.gms.ads.mediation.customevent.CustomEventAdapter" : "com.google.ads.mediation.customevent.CustomEventAdapter";
            }
        } catch (RemoteException e) {
            gs.W("Fail to determine the custom event's version, assuming the old one.");
        }
        return "com.google.ads.mediation.customevent.CustomEventAdapter";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public cu bF() {
        gs.U("Instantiating mediation adapter: " + this.qo);
        try {
            return this.lq.x(this.qo);
        } catch (RemoteException e) {
            gs.a("Could not instantiate mediation adapter: " + this.qo, e);
            return null;
        }
    }

    public cq b(long j, long j2) {
        cq cqVar;
        synchronized (this.mw) {
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            final co coVar = new co();
            gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.cp.1
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (cp.this.mw) {
                        if (cp.this.qu != -2) {
                            return;
                        }
                        cp.this.qt = cp.this.bF();
                        if (cp.this.qt == null) {
                            cp.this.j(4);
                        } else {
                            coVar.a(cp.this);
                            cp.this.a(coVar);
                        }
                    }
                }
            });
            a(jElapsedRealtime, this.qp, j, j2);
            cqVar = new cq(this.qq, this.qt, this.qo, coVar, this.qu);
        }
        return cqVar;
    }

    public void cancel() {
        synchronized (this.mw) {
            try {
            } catch (RemoteException e) {
                gs.d("Could not destroy mediation adapter.", e);
            }
            if (this.qt != null) {
                this.qt.destroy();
                this.qu = -1;
                this.mw.notify();
            } else {
                this.qu = -1;
                this.mw.notify();
            }
        }
    }

    @Override // com.google.android.gms.internal.cq.a
    public void j(int i) {
        synchronized (this.mw) {
            this.qu = i;
            this.mw.notify();
        }
    }
}
