package com.google.android.gms.internal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import com.google.android.gms.internal.gw;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer;

@ez
/* loaded from: classes.dex */
public class fc implements Runnable {
    private final int lf;
    private final int lg;
    protected final gv md;
    private final Handler td;
    private final long te;
    private long tf;
    private gw.a tg;
    protected boolean th;
    protected boolean ti;

    protected final class a extends AsyncTask<Void, Void, Boolean> {
        private final WebView tj;
        private Bitmap tk;

        public a(WebView webView) {
            this.tj = webView;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        /* renamed from: a, reason: merged with bridge method [inline-methods] */
        public synchronized Boolean doInBackground(Void... voidArr) {
            boolean zValueOf;
            int width = this.tk.getWidth();
            int height = this.tk.getHeight();
            if (width == 0 || height == 0) {
                zValueOf = false;
            } else {
                int i = 0;
                for (int i2 = 0; i2 < width; i2 += 10) {
                    for (int i3 = 0; i3 < height; i3 += 10) {
                        if (this.tk.getPixel(i2, i3) != 0) {
                            i++;
                        }
                    }
                }
                zValueOf = Boolean.valueOf(((double) i) / (((double) (width * height)) / 100.0d) > 0.1d);
            }
            return zValueOf;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        /* renamed from: a, reason: merged with bridge method [inline-methods] */
        public void onPostExecute(Boolean bool) {
            fc.c(fc.this);
            if (bool.booleanValue() || fc.this.cB() || fc.this.tf <= 0) {
                fc.this.ti = bool.booleanValue();
                fc.this.tg.a(fc.this.md);
            } else if (fc.this.tf > 0) {
                if (gs.u(2)) {
                    gs.S("Ad not detected, scheduling another run.");
                }
                fc.this.td.postDelayed(fc.this, fc.this.te);
            }
        }

        @Override // android.os.AsyncTask
        protected synchronized void onPreExecute() {
            this.tk = Bitmap.createBitmap(fc.this.lf, fc.this.lg, Bitmap.Config.ARGB_8888);
            this.tj.setVisibility(0);
            this.tj.measure(View.MeasureSpec.makeMeasureSpec(fc.this.lf, 0), View.MeasureSpec.makeMeasureSpec(fc.this.lg, 0));
            this.tj.layout(0, 0, fc.this.lf, fc.this.lg);
            this.tj.draw(new Canvas(this.tk));
            this.tj.invalidate();
        }
    }

    public fc(gw.a aVar, gv gvVar, int i, int i2) {
        this(aVar, gvVar, i, i2, 200L, 50L);
    }

    public fc(gw.a aVar, gv gvVar, int i, int i2, long j, long j2) {
        this.te = j;
        this.tf = j2;
        this.td = new Handler(Looper.getMainLooper());
        this.md = gvVar;
        this.tg = aVar;
        this.th = false;
        this.ti = false;
        this.lg = i2;
        this.lf = i;
    }

    static /* synthetic */ long c(fc fcVar) {
        long j = fcVar.tf - 1;
        fcVar.tf = j;
        return j;
    }

    public void a(fk fkVar, ha haVar) {
        this.md.setWebViewClient(haVar);
        this.md.loadDataWithBaseURL(TextUtils.isEmpty(fkVar.rP) ? null : gj.L(fkVar.rP), fkVar.tG, HttpServer.MIME_HTML, "UTF-8", null);
    }

    public void b(fk fkVar) {
        a(fkVar, new ha(this, this.md, fkVar.tP));
    }

    public synchronized void cA() {
        this.th = true;
    }

    public synchronized boolean cB() {
        return this.th;
    }

    public boolean cC() {
        return this.ti;
    }

    public void cz() {
        this.td.postDelayed(this, this.te);
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.md == null || cB()) {
            this.tg.a(this.md);
        } else {
            new a(this.md).execute(new Void[0]);
        }
    }
}
