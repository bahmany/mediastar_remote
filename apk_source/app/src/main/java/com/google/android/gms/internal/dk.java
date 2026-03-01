package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.google.android.gms.ads.AdActivity;
import com.google.android.gms.internal.ds;
import com.google.android.gms.internal.gw;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer;

@ez
/* loaded from: classes.dex */
public class dk extends ds.a {
    private static final int ru = Color.argb(0, 0, 0, 0);
    private gv md;
    private final Activity nr;
    private FrameLayout rB;
    private WebChromeClient.CustomViewCallback rC;
    private RelativeLayout rG;
    private dm rv;
    private Cdo rw;
    private c rx;
    private dp ry;
    private boolean rz;
    private boolean rA = false;
    private boolean rD = false;
    private boolean rE = false;
    private boolean rF = false;

    /* renamed from: com.google.android.gms.internal.dk$1 */
    class AnonymousClass1 implements gw.a {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.internal.gw.a
        public void a(gv gvVar) {
            gvVar.ca();
        }
    }

    @ez
    private static final class a extends Exception {
        public a(String str) {
            super(str);
        }
    }

    @ez
    private static final class b extends RelativeLayout {
        private final gm ly;

        public b(Context context, String str) {
            super(context);
            this.ly = new gm(context, str);
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent event) {
            this.ly.c(event);
            return false;
        }
    }

    @ez
    private static final class c {
        public final int index;
        public final ViewGroup.LayoutParams rI;
        public final ViewGroup rJ;

        public c(gv gvVar) throws a {
            this.rI = gvVar.getLayoutParams();
            ViewParent parent = gvVar.getParent();
            if (!(parent instanceof ViewGroup)) {
                throw new a("Could not get the parent of the WebView for an overlay.");
            }
            this.rJ = (ViewGroup) parent;
            this.index = this.rJ.indexOfChild(gvVar);
            this.rJ.removeView(gvVar);
            gvVar.x(true);
        }
    }

    public dk(Activity activity) {
        this.nr = activity;
    }

    private static RelativeLayout.LayoutParams a(int i, int i2, int i3, int i4) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(i3, i4);
        layoutParams.setMargins(i, i2, 0, 0);
        layoutParams.addRule(10);
        layoutParams.addRule(9);
        return layoutParams;
    }

    public static void a(Context context, dm dmVar) {
        Intent intent = new Intent();
        intent.setClassName(context, AdActivity.CLASS_NAME);
        intent.putExtra("com.google.android.gms.ads.internal.overlay.useClientJar", dmVar.lD.wG);
        dm.a(intent, dmVar);
        intent.addFlags(524288);
        if (!(context instanceof Activity)) {
            intent.addFlags(268435456);
        }
        context.startActivity(intent);
    }

    @Override // com.google.android.gms.internal.ds
    public void U() {
        this.rz = true;
    }

    public void a(View view, WebChromeClient.CustomViewCallback customViewCallback) {
        this.rB = new FrameLayout(this.nr);
        this.rB.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        this.rB.addView(view, -1, -1);
        this.nr.setContentView(this.rB);
        U();
        this.rC = customViewCallback;
        this.rA = true;
    }

    public void b(int i, int i2, int i3, int i4) {
        if (this.rw != null) {
            this.rw.setLayoutParams(a(i, i2, i3, i4));
        }
    }

    public Cdo bW() {
        return this.rw;
    }

    public void bX() {
        if (this.rv != null && this.rA) {
            setRequestedOrientation(this.rv.orientation);
        }
        if (this.rB != null) {
            this.nr.setContentView(this.rG);
            U();
            this.rB.removeAllViews();
            this.rB = null;
        }
        if (this.rC != null) {
            this.rC.onCustomViewHidden();
            this.rC = null;
        }
        this.rA = false;
    }

    public void bY() {
        this.rG.removeView(this.ry);
        n(true);
    }

    void bZ() {
        if (!this.nr.isFinishing() || this.rE) {
            return;
        }
        this.rE = true;
        if (this.nr.isFinishing()) {
            if (this.md != null) {
                cb();
                this.rG.removeView(this.md);
                if (this.rx != null) {
                    this.md.x(false);
                    this.rx.rJ.addView(this.md, this.rx.index, this.rx.rI);
                }
            }
            if (this.rv == null || this.rv.rM == null) {
                return;
            }
            this.rv.rM.ac();
        }
    }

    public void c(int i, int i2, int i3, int i4) {
        if (this.rw == null) {
            this.rw = new Cdo(this.nr, this.md);
            this.rG.addView(this.rw, 0, a(i, i2, i3, i4));
            this.md.dv().y(false);
        }
    }

    void ca() {
        this.md.ca();
    }

    void cb() {
        this.md.cb();
    }

    public void close() {
        this.nr.finish();
    }

    public void n(boolean z) {
        this.ry = new dp(this.nr, z ? 50 : 32);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.addRule(10);
        layoutParams.addRule(z ? 11 : 9);
        this.ry.o(this.rv.rQ);
        this.rG.addView(this.ry, layoutParams);
    }

    public void o(boolean z) {
        if (this.ry != null) {
            this.ry.o(z);
        }
    }

    @Override // com.google.android.gms.internal.ds
    public void onCreate(Bundle savedInstanceState) throws a {
        this.rD = savedInstanceState != null ? savedInstanceState.getBoolean("com.google.android.gms.ads.internal.overlay.hasResumed", false) : false;
        try {
            this.rv = dm.b(this.nr.getIntent());
            if (this.rv == null) {
                throw new a("Could not get info for ad overlay.");
            }
            if (this.rv.rW != null) {
                this.rF = this.rv.rW.lX;
            } else {
                this.rF = false;
            }
            if (savedInstanceState == null) {
                if (this.rv.rM != null) {
                    this.rv.rM.ad();
                }
                if (this.rv.rT != 1 && this.rv.rL != null) {
                    this.rv.rL.onAdClicked();
                }
            }
            switch (this.rv.rT) {
                case 1:
                    p(false);
                    return;
                case 2:
                    this.rx = new c(this.rv.rN);
                    p(false);
                    return;
                case 3:
                    p(true);
                    return;
                case 4:
                    if (this.rD) {
                        this.nr.finish();
                        return;
                    } else {
                        if (dh.a(this.nr, this.rv.rK, this.rv.rS)) {
                            return;
                        }
                        this.nr.finish();
                        return;
                    }
                default:
                    throw new a("Could not determine ad overlay type.");
            }
        } catch (a e) {
            gs.W(e.getMessage());
            this.nr.finish();
        }
    }

    @Override // com.google.android.gms.internal.ds
    public void onDestroy() {
        if (this.rw != null) {
            this.rw.destroy();
        }
        if (this.md != null) {
            this.rG.removeView(this.md);
        }
        bZ();
    }

    @Override // com.google.android.gms.internal.ds
    public void onPause() {
        if (this.rw != null) {
            this.rw.pause();
        }
        bX();
        if (this.md != null && (!this.nr.isFinishing() || this.rx == null)) {
            gj.a(this.md);
        }
        bZ();
    }

    @Override // com.google.android.gms.internal.ds
    public void onRestart() {
    }

    @Override // com.google.android.gms.internal.ds
    public void onResume() {
        if (this.rv != null && this.rv.rT == 4) {
            if (this.rD) {
                this.nr.finish();
            } else {
                this.rD = true;
            }
        }
        if (this.md != null) {
            gj.b(this.md);
        }
    }

    @Override // com.google.android.gms.internal.ds
    public void onSaveInstanceState(Bundle outBundle) {
        outBundle.putBoolean("com.google.android.gms.ads.internal.overlay.hasResumed", this.rD);
    }

    @Override // com.google.android.gms.internal.ds
    public void onStart() {
    }

    @Override // com.google.android.gms.internal.ds
    public void onStop() {
        bZ();
    }

    void p(boolean z) throws a {
        if (!this.rz) {
            this.nr.requestWindowFeature(1);
        }
        Window window = this.nr.getWindow();
        if (!this.rF || this.rv.rW.mh) {
            window.setFlags(1024, 1024);
        }
        setRequestedOrientation(this.rv.orientation);
        if (Build.VERSION.SDK_INT >= 11) {
            gs.S("Enabling hardware acceleration on the AdActivity window.");
            gn.a(window);
        }
        this.rG = new b(this.nr, this.rv.rV);
        if (this.rF) {
            this.rG.setBackgroundColor(ru);
        } else {
            this.rG.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        }
        this.nr.setContentView(this.rG);
        U();
        boolean zDF = this.rv.rN.dv().dF();
        if (z) {
            this.md = gv.a(this.nr, this.rv.rN.Y(), true, zDF, null, this.rv.lD);
            this.md.dv().a(null, null, this.rv.rO, this.rv.rS, true, this.rv.rU, this.rv.rN.dv().dE());
            this.md.dv().a(new gw.a() { // from class: com.google.android.gms.internal.dk.1
                AnonymousClass1() {
                }

                @Override // com.google.android.gms.internal.gw.a
                public void a(gv gvVar) {
                    gvVar.ca();
                }
            });
            if (this.rv.rq != null) {
                this.md.loadUrl(this.rv.rq);
            } else {
                if (this.rv.rR == null) {
                    throw new a("No URL or HTML to display in ad overlay.");
                }
                this.md.loadDataWithBaseURL(this.rv.rP, this.rv.rR, HttpServer.MIME_HTML, "UTF-8", null);
            }
        } else {
            this.md = this.rv.rN;
            this.md.setContext(this.nr);
        }
        this.md.a(this);
        ViewParent parent = this.md.getParent();
        if (parent != null && (parent instanceof ViewGroup)) {
            ((ViewGroup) parent).removeView(this.md);
        }
        if (this.rF) {
            this.md.setBackgroundColor(ru);
        }
        this.rG.addView(this.md, -1, -1);
        if (!z) {
            ca();
        }
        n(zDF);
        if (this.md.dw()) {
            o(true);
        }
    }

    public void setRequestedOrientation(int requestedOrientation) {
        this.nr.setRequestedOrientation(requestedOrientation);
    }
}
