package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.internal.fd;
import com.google.android.gms.internal.fz;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ez
/* loaded from: classes.dex */
public class fn extends gg {
    private final Object mw;
    private final fk sZ;
    private final fo tU;
    private Future<fz> tV;
    private final fd.a tm;
    private final fz.a tn;

    public fn(Context context, u uVar, ai aiVar, fz.a aVar, fd.a aVar2) {
        this(aVar, aVar2, new fo(context, uVar, aiVar, new go(), aVar));
    }

    fn(fz.a aVar, fd.a aVar2, fo foVar) {
        this.mw = new Object();
        this.tn = aVar;
        this.sZ = aVar.vw;
        this.tm = aVar2;
        this.tU = foVar;
    }

    private fz r(int i) {
        return new fz(this.tn.vv.tx, null, null, i, null, null, this.sZ.orientation, this.sZ.qj, this.tn.vv.tA, false, null, null, null, null, null, this.sZ.tJ, this.tn.lH, this.sZ.tH, this.tn.vs, this.sZ.tM, this.sZ.tN, this.tn.vp, null);
    }

    @Override // com.google.android.gms.internal.gg
    public void cp() throws ExecutionException, InterruptedException, TimeoutException {
        int i;
        final fz fzVarR;
        try {
            synchronized (this.mw) {
                this.tV = gi.submit(this.tU);
            }
            fzVarR = this.tV.get(60000L, TimeUnit.MILLISECONDS);
            i = -2;
        } catch (InterruptedException e) {
            fzVarR = null;
            i = -1;
        } catch (CancellationException e2) {
            fzVarR = null;
            i = -1;
        } catch (ExecutionException e3) {
            i = 0;
            fzVarR = null;
        } catch (TimeoutException e4) {
            gs.W("Timed out waiting for native ad.");
            i = 2;
            fzVarR = null;
        }
        if (fzVarR == null) {
            fzVarR = r(i);
        }
        gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.fn.1
            @Override // java.lang.Runnable
            public void run() {
                fn.this.tm.a(fzVarR);
            }
        });
    }

    @Override // com.google.android.gms.internal.gg
    public void onStop() {
        synchronized (this.mw) {
            if (this.tV != null) {
                this.tV.cancel(true);
            }
        }
    }
}
