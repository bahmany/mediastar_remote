package com.google.android.gms.internal;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.HashMap;
import java.util.Map;
import master.flame.danmaku.danmaku.parser.IDataSource;

@ez
/* loaded from: classes.dex */
public class gw extends WebViewClient {
    protected final gv md;
    private final Object mw;
    private cb pJ;
    private bz pL;
    private v pM;
    private bw pz;
    private a tg;
    private final HashMap<String, by> wP;
    private t wQ;
    private dn wR;
    private boolean wS;
    private boolean wT;
    private dq wU;
    private final dg wV;

    /* renamed from: com.google.android.gms.internal.gw$1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ dk wW;

        AnonymousClass1(dk dkVar) {
            dkVar = dkVar;
        }

        @Override // java.lang.Runnable
        public void run() {
            dkVar.bY();
        }
    }

    public interface a {
        void a(gv gvVar);
    }

    public gw(gv gvVar, boolean z) {
        this(gvVar, z, new dg(gvVar, gvVar.getContext(), new bl(gvVar.getContext())));
    }

    gw(gv gvVar, boolean z, dg dgVar) {
        this.wP = new HashMap<>();
        this.mw = new Object();
        this.wS = false;
        this.md = gvVar;
        this.wT = z;
        this.wV = dgVar;
    }

    private static boolean d(Uri uri) {
        String scheme = uri.getScheme();
        return IDataSource.SCHEME_HTTP_TAG.equalsIgnoreCase(scheme) || IDataSource.SCHEME_HTTPS_TAG.equalsIgnoreCase(scheme);
    }

    private void e(Uri uri) {
        String path = uri.getPath();
        by byVar = this.wP.get(path);
        if (byVar == null) {
            gs.V("No GMSG handler found for GMSG: " + uri);
            return;
        }
        Map<String, String> mapC = gj.c(uri);
        if (gs.u(2)) {
            gs.V("Received GMSG: " + path);
            for (String str : mapC.keySet()) {
                gs.V("  " + str + ": " + mapC.get(str));
            }
        }
        byVar.a(this.md, mapC);
    }

    public final void a(dj djVar) {
        boolean zDz = this.md.dz();
        a(new dm(djVar, (!zDz || this.md.Y().og) ? this.wQ : null, zDz ? null : this.wR, this.wU, this.md.dy()));
    }

    protected void a(dm dmVar) {
        dk.a(this.md.getContext(), dmVar);
    }

    public final void a(a aVar) {
        this.tg = aVar;
    }

    public void a(t tVar, dn dnVar, bw bwVar, dq dqVar, boolean z, bz bzVar, cb cbVar, v vVar) {
        a(tVar, dnVar, bwVar, dqVar, z, bzVar, vVar);
        a("/setInterstitialProperties", new ca(cbVar));
        this.pJ = cbVar;
    }

    public void a(t tVar, dn dnVar, bw bwVar, dq dqVar, boolean z, bz bzVar, v vVar) {
        if (vVar == null) {
            vVar = new v(false);
        }
        a("/appEvent", new bv(bwVar));
        a("/canOpenURLs", bx.pB);
        a("/click", bx.pC);
        a("/close", bx.pD);
        a("/customClose", bx.pE);
        a("/httpTrack", bx.pF);
        a("/log", bx.pG);
        a("/open", new cd(bzVar, vVar));
        a("/touch", bx.pH);
        a("/video", bx.pI);
        a("/mraid", new cc());
        this.wQ = tVar;
        this.wR = dnVar;
        this.pz = bwVar;
        this.pL = bzVar;
        this.wU = dqVar;
        this.pM = vVar;
        y(z);
    }

    public final void a(String str, by byVar) {
        this.wP.put(str, byVar);
    }

    public final void a(boolean z, int i) {
        a(new dm((!this.md.dz() || this.md.Y().og) ? this.wQ : null, this.wR, this.wU, this.md, z, i, this.md.dy()));
    }

    public final void a(boolean z, int i, String str) {
        boolean zDz = this.md.dz();
        a(new dm((!zDz || this.md.Y().og) ? this.wQ : null, zDz ? null : this.wR, this.pz, this.wU, this.md, z, i, str, this.md.dy(), this.pL));
    }

    public final void a(boolean z, int i, String str, String str2) {
        boolean zDz = this.md.dz();
        a(new dm((!zDz || this.md.Y().og) ? this.wQ : null, zDz ? null : this.wR, this.pz, this.wU, this.md, z, i, str, str2, this.md.dy(), this.pL));
    }

    public final void bY() {
        synchronized (this.mw) {
            this.wS = false;
            this.wT = true;
            dk dkVarDu = this.md.du();
            if (dkVarDu != null) {
                if (gr.dt()) {
                    dkVarDu.bY();
                } else {
                    gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.gw.1
                        final /* synthetic */ dk wW;

                        AnonymousClass1(dk dkVarDu2) {
                            dkVar = dkVarDu2;
                        }

                        @Override // java.lang.Runnable
                        public void run() {
                            dkVar.bY();
                        }
                    });
                }
            }
        }
    }

    public v dE() {
        return this.pM;
    }

    public boolean dF() {
        boolean z;
        synchronized (this.mw) {
            z = this.wT;
        }
        return z;
    }

    public void dG() {
        if (dF()) {
            this.wV.bQ();
        }
    }

    @Override // android.webkit.WebViewClient
    public final void onLoadResource(WebView webView, String url) {
        gs.V("Loading resource: " + url);
        Uri uri = Uri.parse(url);
        if ("gmsg".equalsIgnoreCase(uri.getScheme()) && "mobileads.google.com".equalsIgnoreCase(uri.getHost())) {
            e(uri);
        }
    }

    @Override // android.webkit.WebViewClient
    public final void onPageFinished(WebView webView, String url) {
        if (this.tg != null) {
            this.tg.a(this.md);
            this.tg = null;
        }
    }

    public final void reset() {
        synchronized (this.mw) {
            this.wP.clear();
            this.wQ = null;
            this.wR = null;
            this.tg = null;
            this.pz = null;
            this.wS = false;
            this.wT = false;
            this.pL = null;
            this.wU = null;
        }
    }

    @Override // android.webkit.WebViewClient
    public final boolean shouldOverrideUrlLoading(WebView webView, String url) {
        Uri uri;
        gs.V("AdWebView shouldOverrideUrlLoading: " + url);
        Uri uriA = Uri.parse(url);
        if ("gmsg".equalsIgnoreCase(uriA.getScheme()) && "mobileads.google.com".equalsIgnoreCase(uriA.getHost())) {
            e(uriA);
        } else {
            if (this.wS && webView == this.md && d(uriA)) {
                return super.shouldOverrideUrlLoading(webView, url);
            }
            if (this.md.willNotDraw()) {
                gs.W("AdWebView unable to handle URL: " + url);
            } else {
                try {
                    k kVarDx = this.md.dx();
                    if (kVarDx != null && kVarDx.b(uriA)) {
                        uriA = kVarDx.a(uriA, this.md.getContext());
                    }
                    uri = uriA;
                } catch (l e) {
                    gs.W("Unable to append parameter to URL: " + url);
                    uri = uriA;
                }
                if (this.pM == null || this.pM.av()) {
                    a(new dj("android.intent.action.VIEW", uri.toString(), null, null, null, null, null));
                } else {
                    this.pM.d(url);
                }
            }
        }
        return true;
    }

    public final void y(boolean z) {
        this.wS = z;
    }
}
