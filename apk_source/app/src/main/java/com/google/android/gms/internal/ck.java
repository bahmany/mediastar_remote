package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;

@ez
/* loaded from: classes.dex */
public final class ck {
    private final ct lq;
    private final Context mContext;
    private final fi pQ;
    private final cm pR;
    private cp pT;
    private final Object mw = new Object();
    private boolean pS = false;

    public ck(Context context, fi fiVar, ct ctVar, cm cmVar) {
        this.mContext = context;
        this.pQ = fiVar;
        this.lq = ctVar;
        this.pR = cmVar;
    }

    public cq a(long j, long j2) {
        gs.S("Starting mediation.");
        for (cl clVar : this.pR.qd) {
            gs.U("Trying mediation network: " + clVar.pX);
            for (String str : clVar.pY) {
                synchronized (this.mw) {
                    if (this.pS) {
                        return new cq(-1);
                    }
                    this.pT = new cp(this.mContext, str, this.lq, this.pR, clVar, this.pQ.tx, this.pQ.lH, this.pQ.lD);
                    final cq cqVarB = this.pT.b(j, j2);
                    if (cqVarB.qx == 0) {
                        gs.S("Adapter succeeded.");
                        return cqVarB;
                    }
                    if (cqVarB.qz != null) {
                        gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.ck.1
                            @Override // java.lang.Runnable
                            public void run() {
                                try {
                                    cqVarB.qz.destroy();
                                } catch (RemoteException e) {
                                    gs.d("Could not destroy mediation adapter.", e);
                                }
                            }
                        });
                    }
                }
            }
        }
        return new cq(1);
    }

    public void cancel() {
        synchronized (this.mw) {
            this.pS = true;
            if (this.pT != null) {
                this.pT.cancel();
            }
        }
    }
}
