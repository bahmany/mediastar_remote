package com.google.android.gms.internal;

import android.content.Context;
import android.os.SystemClock;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.internal.fd;
import com.google.android.gms.internal.fz;
import com.google.android.gms.internal.gw;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer;

@ez
/* loaded from: classes.dex */
public class fe extends gg implements gw.a {
    private final ct lq;
    private final Context mContext;
    private final gv md;
    private cm pR;
    private fk sZ;
    private final fd.a tm;
    private final fz.a tn;
    private ck tp;
    private cq tq;
    private final Object sV = new Object();
    private final Object mw = new Object();
    private boolean to = false;

    private static final class a extends Exception {
        private final int tc;

        public a(String str, int i) {
            super(str);
            this.tc = i;
        }

        public int getErrorCode() {
            return this.tc;
        }
    }

    public fe(Context context, fz.a aVar, gv gvVar, ct ctVar, fd.a aVar2) {
        this.mContext = context;
        this.tn = aVar;
        this.sZ = aVar.vw;
        this.md = gvVar;
        this.lq = ctVar;
        this.tm = aVar2;
        this.pR = aVar.vq;
    }

    private void a(fi fiVar, long j) throws a {
        synchronized (this.sV) {
            this.tp = new ck(this.mContext, fiVar, this.lq, this.pR);
        }
        this.tq = this.tp.a(j, 60000L);
        switch (this.tq.qx) {
            case 0:
                return;
            case 1:
                throw new a("No fill from any mediation ad networks.", 3);
            default:
                throw new a("Unexpected mediation result: " + this.tq.qx, 0);
        }
    }

    private boolean c(long j) throws InterruptedException, a {
        long jElapsedRealtime = 60000 - (SystemClock.elapsedRealtime() - j);
        if (jElapsedRealtime <= 0) {
            return false;
        }
        try {
            this.mw.wait(jElapsedRealtime);
            return true;
        } catch (InterruptedException e) {
            throw new a("Ad request cancelled.", -1);
        }
    }

    private void f(long j) throws a {
        gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.fe.3
            @Override // java.lang.Runnable
            public void run() {
                synchronized (fe.this.mw) {
                    if (fe.this.sZ.errorCode != -2) {
                        return;
                    }
                    fe.this.md.dv().a(fe.this);
                    if (fe.this.sZ.errorCode == -3) {
                        gs.V("Loading URL in WebView: " + fe.this.sZ.rP);
                        fe.this.md.loadUrl(fe.this.sZ.rP);
                    } else {
                        gs.V("Loading HTML in WebView.");
                        fe.this.md.loadDataWithBaseURL(gj.L(fe.this.sZ.rP), fe.this.sZ.tG, HttpServer.MIME_HTML, "UTF-8", null);
                    }
                }
            }
        });
        h(j);
    }

    private void h(long j) throws a {
        while (c(j)) {
            if (this.to) {
                return;
            }
        }
        throw new a("Timed out waiting for WebView to finish loading.", 2);
    }

    @Override // com.google.android.gms.internal.gw.a
    public void a(gv gvVar) {
        synchronized (this.mw) {
            gs.S("WebView finished loading.");
            this.to = true;
            this.mw.notify();
        }
    }

    @Override // com.google.android.gms.internal.gg
    public void cp() {
        synchronized (this.mw) {
            gs.S("AdRendererBackgroundTask started.");
            fi fiVar = this.tn.vv;
            int errorCode = this.tn.errorCode;
            try {
                long jElapsedRealtime = SystemClock.elapsedRealtime();
                if (this.sZ.tI) {
                    a(fiVar, jElapsedRealtime);
                } else if (this.sZ.tO) {
                    g(jElapsedRealtime);
                } else {
                    f(jElapsedRealtime);
                }
            } catch (a e) {
                errorCode = e.getErrorCode();
                if (errorCode == 3 || errorCode == -1) {
                    gs.U(e.getMessage());
                } else {
                    gs.W(e.getMessage());
                }
                if (this.sZ == null) {
                    this.sZ = new fk(errorCode);
                } else {
                    this.sZ = new fk(errorCode, this.sZ.qj);
                }
                gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.fe.1
                    @Override // java.lang.Runnable
                    public void run() {
                        fe.this.onStop();
                    }
                });
            }
            final fz fzVar = new fz(fiVar.tx, this.md, this.sZ.qf, errorCode, this.sZ.qg, this.sZ.tK, this.sZ.orientation, this.sZ.qj, fiVar.tA, this.sZ.tI, this.tq != null ? this.tq.qy : null, this.tq != null ? this.tq.qz : null, this.tq != null ? this.tq.qA : AdMobAdapter.class.getName(), this.pR, this.tq != null ? this.tq.qB : null, this.sZ.tJ, this.tn.lH, this.sZ.tH, this.tn.vs, this.sZ.tM, this.sZ.tN, this.tn.vp, null);
            gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.fe.2
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (fe.this.mw) {
                        fe.this.tm.a(fzVar);
                    }
                }
            });
        }
    }

    protected void g(long j) throws a {
        int i;
        int i2;
        ay ayVarY = this.md.Y();
        if (ayVarY.og) {
            i = this.mContext.getResources().getDisplayMetrics().widthPixels;
            i2 = this.mContext.getResources().getDisplayMetrics().heightPixels;
        } else {
            i = ayVarY.widthPixels;
            i2 = ayVarY.heightPixels;
        }
        final fc fcVar = new fc(this, this.md, i, i2);
        gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.fe.4
            @Override // java.lang.Runnable
            public void run() {
                synchronized (fe.this.mw) {
                    if (fe.this.sZ.errorCode != -2) {
                        return;
                    }
                    fe.this.md.dv().a(fe.this);
                    fcVar.b(fe.this.sZ);
                }
            }
        });
        h(j);
        if (fcVar.cB()) {
            gs.S("Ad-Network indicated no fill with passback URL.");
            throw new a("AdNetwork sent passback url", 3);
        }
        if (!fcVar.cC()) {
            throw new a("AdNetwork timed out", 2);
        }
    }

    @Override // com.google.android.gms.internal.gg
    public void onStop() {
        synchronized (this.sV) {
            this.md.stopLoading();
            gj.a(this.md);
            if (this.tp != null) {
                this.tp.cancel();
            }
        }
    }
}
