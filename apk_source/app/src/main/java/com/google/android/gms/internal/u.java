package com.google.android.gms.internal;

import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ViewSwitcher;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.bd;
import com.google.android.gms.internal.bq;
import com.google.android.gms.internal.fa;
import com.google.android.gms.internal.fd;
import com.google.android.gms.internal.fi;
import com.google.android.gms.internal.fz;
import com.google.android.gms.internal.v;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@ez
/* loaded from: classes.dex */
public class u extends bd.a implements aa, bw, bz, cb, cn, dn, dq, fa.a, fd.a, gd, t {
    private av lp;
    private final ct lq;
    private final b lr;
    private final ab ls;
    private final ae lt;
    private boolean lu;
    private final ComponentCallbacks lv;

    /* renamed from: com.google.android.gms.internal.u$1 */
    class AnonymousClass1 implements ComponentCallbacks {
        AnonymousClass1() {
        }

        @Override // android.content.ComponentCallbacks
        public void onConfigurationChanged(Configuration newConfig) {
            if (u.this.lr == null || u.this.lr.lI == null || u.this.lr.lI.rN == null) {
                return;
            }
            u.this.lr.lI.rN.bT();
        }

        @Override // android.content.ComponentCallbacks
        public void onLowMemory() {
        }
    }

    /* renamed from: com.google.android.gms.internal.u$2 */
    class AnonymousClass2 implements View.OnTouchListener {
        final /* synthetic */ v lx;

        AnonymousClass2(v vVar) {
            vVar = vVar;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            vVar.ar();
            return false;
        }
    }

    /* renamed from: com.google.android.gms.internal.u$3 */
    class AnonymousClass3 implements View.OnClickListener {
        final /* synthetic */ v lx;

        AnonymousClass3(v vVar) {
            vVar = vVar;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            vVar.ar();
        }
    }

    @ez
    private static final class a extends ViewSwitcher {
        private final gm ly;

        public a(Context context) {
            super(context);
            this.ly = new gm(context);
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent event) {
            this.ly.c(event);
            return false;
        }
    }

    @ez
    static class b {
        public final String lA;
        public final Context lB;
        public final k lC;
        public final gt lD;
        public bc lE;
        public gg lF;
        public gg lG;
        public ay lH;
        public fz lI;
        public fz.a lJ;
        public ga lK;
        public bf lL;
        public el lM;
        public eh lN;
        public et lO;
        public eu lP;
        public bt lQ;
        public bu lR;
        public List<String> lS;
        public ee lT;
        public ge lU = null;
        public View lV = null;
        public int lW = 0;
        public boolean lX = false;
        private HashSet<ga> lY = null;
        public final a lz;

        public b(Context context, ay ayVar, String str, gt gtVar) {
            if (ayVar.og) {
                this.lz = null;
            } else {
                this.lz = new a(context);
                this.lz.setMinimumWidth(ayVar.widthPixels);
                this.lz.setMinimumHeight(ayVar.heightPixels);
                this.lz.setVisibility(4);
            }
            this.lH = ayVar;
            this.lA = str;
            this.lB = context;
            this.lD = gtVar;
            this.lC = new k(new w(this));
        }

        public void a(HashSet<ga> hashSet) {
            this.lY = hashSet;
        }

        public HashSet<ga> au() {
            return this.lY;
        }
    }

    public u(Context context, ay ayVar, String str, ct ctVar, gt gtVar) {
        this(new b(context, ayVar, str, gtVar), ctVar, null);
    }

    u(b bVar, ct ctVar, ab abVar) {
        this.lv = new ComponentCallbacks() { // from class: com.google.android.gms.internal.u.1
            AnonymousClass1() {
            }

            @Override // android.content.ComponentCallbacks
            public void onConfigurationChanged(Configuration newConfig) {
                if (u.this.lr == null || u.this.lr.lI == null || u.this.lr.lI.rN == null) {
                    return;
                }
                u.this.lr.lI.rN.bT();
            }

            @Override // android.content.ComponentCallbacks
            public void onLowMemory() {
            }
        };
        this.lr = bVar;
        this.lq = ctVar;
        this.ls = abVar == null ? new ab(this) : abVar;
        this.lt = new ae();
        gj.q(this.lr.lB);
        gb.a(this.lr.lB, this.lr.lD);
        Z();
    }

    private void Z() {
        if (Build.VERSION.SDK_INT < 14 || this.lr == null || this.lr.lB == null) {
            return;
        }
        this.lr.lB.registerComponentCallbacks(this.lv);
    }

    private fi.a a(av avVar, Bundle bundle) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo;
        ApplicationInfo applicationInfo = this.lr.lB.getApplicationInfo();
        try {
            packageInfo = this.lr.lB.getPackageManager().getPackageInfo(applicationInfo.packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        Bundle bundle2 = null;
        if (!this.lr.lH.og && this.lr.lz.getParent() != null) {
            int[] iArr = new int[2];
            this.lr.lz.getLocationOnScreen(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            DisplayMetrics displayMetrics = this.lr.lB.getResources().getDisplayMetrics();
            int width = this.lr.lz.getWidth();
            int height = this.lr.lz.getHeight();
            int i3 = 0;
            if (this.lr.lz.isShown() && i + width > 0 && i2 + height > 0 && i <= displayMetrics.widthPixels && i2 <= displayMetrics.heightPixels) {
                i3 = 1;
            }
            bundle2 = new Bundle(5);
            bundle2.putInt("x", i);
            bundle2.putInt("y", i2);
            bundle2.putInt("width", width);
            bundle2.putInt("height", height);
            bundle2.putInt("visible", i3);
        }
        String strCX = gb.cX();
        this.lr.lK = new ga(strCX, this.lr.lA);
        this.lr.lK.e(avVar);
        return new fi.a(bundle2, avVar, this.lr.lH, this.lr.lA, applicationInfo, packageInfo, strCX, gb.vK, this.lr.lD, gb.a(this.lr.lB, this, strCX), this.lr.lS, bundle, gb.dd());
    }

    private gv a(v vVar) {
        gv gvVarA;
        if (this.lr.lH.og) {
            gv gvVarA2 = gv.a(this.lr.lB, this.lr.lH, false, false, this.lr.lC, this.lr.lD);
            gvVarA2.dv().a(this, null, this, this, true, this, this, vVar);
            return gvVarA2;
        }
        View nextView = this.lr.lz.getNextView();
        if (nextView instanceof gv) {
            gvVarA = (gv) nextView;
            gvVarA.a(this.lr.lB, this.lr.lH);
        } else {
            if (nextView != null) {
                this.lr.lz.removeView(nextView);
            }
            gvVarA = gv.a(this.lr.lB, this.lr.lH, false, false, this.lr.lC, this.lr.lD);
            if (this.lr.lH.oh == null) {
                c(gvVarA);
            }
        }
        gvVarA.dv().a(this, this, this, this, false, this, vVar);
        return gvVarA;
    }

    private void a(int i) {
        gs.W("Failed to load ad: " + i);
        if (this.lr.lE != null) {
            try {
                this.lr.lE.onAdFailedToLoad(i);
            } catch (RemoteException e) {
                gs.d("Could not call AdListener.onAdFailedToLoad().", e);
            }
        }
    }

    private void aa() {
        if (Build.VERSION.SDK_INT < 14 || this.lr == null || this.lr.lB == null) {
            return;
        }
        this.lr.lB.unregisterComponentCallbacks(this.lv);
    }

    private void ak() {
        gs.U("Ad closing.");
        if (this.lr.lE != null) {
            try {
                this.lr.lE.onAdClosed();
            } catch (RemoteException e) {
                gs.d("Could not call AdListener.onAdClosed().", e);
            }
        }
    }

    private void al() {
        gs.U("Ad leaving application.");
        if (this.lr.lE != null) {
            try {
                this.lr.lE.onAdLeftApplication();
            } catch (RemoteException e) {
                gs.d("Could not call AdListener.onAdLeftApplication().", e);
            }
        }
    }

    private void am() {
        gs.U("Ad opening.");
        if (this.lr.lE != null) {
            try {
                this.lr.lE.onAdOpened();
            } catch (RemoteException e) {
                gs.d("Could not call AdListener.onAdOpened().", e);
            }
        }
    }

    private void an() {
        gs.U("Ad finished loading.");
        if (this.lr.lE != null) {
            try {
                this.lr.lE.onAdLoaded();
            } catch (RemoteException e) {
                gs.d("Could not call AdListener.onAdLoaded().", e);
            }
        }
    }

    private void ao() {
        try {
            if (!(this.lr.lI.vu instanceof bo) || this.lr.lQ == null) {
                return;
            }
            this.lr.lQ.a((bo) this.lr.lI.vu);
        } catch (RemoteException e) {
            gs.d("Could not call OnAppInstallAdLoadedListener.onAppInstallAdLoaded().", e);
        }
    }

    private void ap() {
        try {
            if (!(this.lr.lI.vu instanceof bp) || this.lr.lR == null) {
                return;
            }
            this.lr.lR.a((bp) this.lr.lI.vu);
        } catch (RemoteException e) {
            gs.d("Could not call OnContentAdLoadedListener.onContentAdLoaded().", e);
        }
    }

    private void at() {
        if (this.lr.lI != null) {
            if (this.lr.lW == 0) {
                this.lr.lI.rN.destroy();
            }
            this.lr.lI = null;
            this.lr.lX = false;
        }
    }

    private boolean b(fz fzVar) {
        if (fzVar.tI) {
            try {
                View view = (View) com.google.android.gms.dynamic.e.f(fzVar.qz.getView());
                View nextView = this.lr.lz.getNextView();
                if (nextView != null) {
                    this.lr.lz.removeView(nextView);
                }
                try {
                    c(view);
                } catch (Throwable th) {
                    gs.d("Could not add mediation view to view hierarchy.", th);
                    return false;
                }
            } catch (RemoteException e) {
                gs.d("Could not get View from mediation adapter.", e);
                return false;
            }
        } else if (fzVar.vr != null) {
            fzVar.rN.a(fzVar.vr);
            this.lr.lz.removeAllViews();
            this.lr.lz.setMinimumWidth(fzVar.vr.widthPixels);
            this.lr.lz.setMinimumHeight(fzVar.vr.heightPixels);
            c(fzVar.rN);
        }
        if (this.lr.lz.getChildCount() > 1) {
            this.lr.lz.showNext();
        }
        if (this.lr.lI != null) {
            View nextView2 = this.lr.lz.getNextView();
            if (nextView2 instanceof gv) {
                ((gv) nextView2).a(this.lr.lB, this.lr.lH);
            } else if (nextView2 != null) {
                this.lr.lz.removeView(nextView2);
            }
            if (this.lr.lI.qz != null) {
                try {
                    this.lr.lI.qz.destroy();
                } catch (RemoteException e2) {
                    gs.W("Could not destroy previous mediation adapter.");
                }
            }
        }
        this.lr.lz.setVisibility(0);
        return true;
    }

    private void c(View view) {
        this.lr.lz.addView(view, new ViewGroup.LayoutParams(-2, -2));
    }

    private void c(boolean z) {
        if (this.lr.lI == null) {
            gs.W("Ad state was null when trying to ping impression URLs.");
            return;
        }
        gs.S("Pinging Impression URLs.");
        this.lr.lK.cP();
        if (this.lr.lI.qg != null) {
            gj.a(this.lr.lB, this.lr.lD.wD, this.lr.lI.qg);
        }
        if (this.lr.lI.vq != null && this.lr.lI.vq.qg != null) {
            cr.a(this.lr.lB, this.lr.lD.wD, this.lr.lI, this.lr.lA, z, this.lr.lI.vq.qg);
        }
        if (this.lr.lI.qy == null || this.lr.lI.qy.qb == null) {
            return;
        }
        cr.a(this.lr.lB, this.lr.lD.wD, this.lr.lI, this.lr.lA, z, this.lr.lI.qy.qb);
    }

    @Override // com.google.android.gms.internal.bd
    public com.google.android.gms.dynamic.d X() {
        com.google.android.gms.common.internal.n.aT("getAdFrame must be called on the main UI thread.");
        return com.google.android.gms.dynamic.e.k(this.lr.lz);
    }

    @Override // com.google.android.gms.internal.bd
    public ay Y() {
        com.google.android.gms.common.internal.n.aT("getAdSize must be called on the main UI thread.");
        return this.lr.lH;
    }

    Bundle a(an anVar) {
        String strAO;
        if (anVar == null) {
            return null;
        }
        if (anVar.aZ()) {
            anVar.wakeup();
        }
        ak akVarAX = anVar.aX();
        if (akVarAX != null) {
            strAO = akVarAX.aO();
            gs.S("In AdManger: loadAd, " + akVarAX.toString());
        } else {
            strAO = null;
        }
        if (strAO == null) {
            return null;
        }
        Bundle bundle = new Bundle(1);
        bundle.putString("fingerprint", strAO);
        return bundle;
    }

    @Override // com.google.android.gms.internal.bd
    public void a(ay ayVar) {
        com.google.android.gms.common.internal.n.aT("setAdSize must be called on the main UI thread.");
        this.lr.lH = ayVar;
        if (this.lr.lI != null && this.lr.lW == 0) {
            this.lr.lI.rN.a(ayVar);
        }
        if (this.lr.lz.getChildCount() > 1) {
            this.lr.lz.removeView(this.lr.lz.getNextView());
        }
        this.lr.lz.setMinimumWidth(ayVar.widthPixels);
        this.lr.lz.setMinimumHeight(ayVar.heightPixels);
        this.lr.lz.requestLayout();
    }

    @Override // com.google.android.gms.internal.bd
    public void a(bc bcVar) {
        com.google.android.gms.common.internal.n.aT("setAdListener must be called on the main UI thread.");
        this.lr.lE = bcVar;
    }

    @Override // com.google.android.gms.internal.bd
    public void a(bf bfVar) {
        com.google.android.gms.common.internal.n.aT("setAppEventListener must be called on the main UI thread.");
        this.lr.lL = bfVar;
    }

    @Override // com.google.android.gms.internal.bd
    public void a(eh ehVar) {
        com.google.android.gms.common.internal.n.aT("setInAppPurchaseListener must be called on the main UI thread.");
        this.lr.lN = ehVar;
    }

    @Override // com.google.android.gms.internal.bd
    public void a(el elVar, String str) {
        com.google.android.gms.common.internal.n.aT("setPlayStorePurchaseParams must be called on the main UI thread.");
        this.lr.lT = new ee(str);
        this.lr.lM = elVar;
        if (gb.db() || elVar == null) {
            return;
        }
        new dx(this.lr.lB, this.lr.lM, this.lr.lT).start();
    }

    @Override // com.google.android.gms.internal.bd
    public void a(et etVar) {
        com.google.android.gms.common.internal.n.aT("setRawHtmlPublisherAdViewListener must be called on the main UI thread.");
        this.lr.lO = etVar;
    }

    @Override // com.google.android.gms.internal.bd
    public void a(eu euVar) {
        com.google.android.gms.common.internal.n.aT("setRawHtmlPublisherInterstitialAdListener must be called on the main UI thread.");
        this.lr.lP = euVar;
    }

    @Override // com.google.android.gms.internal.fa.a
    public void a(fz.a aVar) {
        gv gvVarA;
        this.lr.lF = null;
        this.lr.lJ = aVar;
        a((List<String>) null);
        if (aVar.vw.tS) {
            gvVarA = null;
        } else {
            v vVar = new v();
            gvVarA = a(vVar);
            vVar.a(new v.b(aVar, gvVarA));
            gvVarA.setOnTouchListener(new View.OnTouchListener() { // from class: com.google.android.gms.internal.u.2
                final /* synthetic */ v lx;

                AnonymousClass2(v vVar2) {
                    vVar = vVar2;
                }

                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View v, MotionEvent event) {
                    vVar.ar();
                    return false;
                }
            });
            gvVarA.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.gms.internal.u.3
                final /* synthetic */ v lx;

                AnonymousClass3(v vVar2) {
                    vVar = vVar2;
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    vVar.ar();
                }
            });
        }
        if (aVar.lH != null) {
            this.lr.lH = aVar.lH;
        }
        if (aVar.errorCode != -2) {
            a(new fz(aVar, gvVarA, null, null, null, null, null));
            return;
        }
        if (!aVar.vw.tI && aVar.vw.tR) {
            String string = aVar.vw.rP != null ? Uri.parse(aVar.vw.rP).buildUpon().query(null).build().toString() : null;
            er erVar = new er(this, string, aVar.vw.tG);
            try {
                if (this.lr.lO != null && !this.lr.lH.og && this.lr.lO.e(string, aVar.vw.tG)) {
                    this.lr.lW = 1;
                    this.lr.lO.a(erVar);
                    return;
                }
            } catch (RemoteException e) {
                gs.d("Could not call the rawHtmlPublisherAdViewListener.", e);
            }
            try {
                if (this.lr.lP != null && this.lr.lH.og && this.lr.lP.e(string, aVar.vw.tG)) {
                    this.lr.lW = 1;
                    this.lr.lP.a(erVar);
                    return;
                }
            } catch (RemoteException e2) {
                gs.d("Could not call the RawHtmlPublisherInterstitialAdListener.", e2);
            }
        }
        this.lr.lW = 0;
        this.lr.lG = fd.a(this.lr.lB, this, aVar, gvVarA, this.lq, this);
    }

    @Override // com.google.android.gms.internal.fd.a
    public void a(fz fzVar) {
        int i;
        int i2;
        this.lr.lG = null;
        boolean z = fzVar.vu != null;
        if (fzVar.errorCode != -2 && fzVar.errorCode != 3) {
            gb.b(this.lr.au());
        }
        if (fzVar.errorCode == -1) {
            return;
        }
        if (a(fzVar, z)) {
            gs.S("Ad refresh scheduled.");
        }
        if (fzVar.errorCode == 3 && fzVar.vq != null && fzVar.vq.qh != null) {
            gs.S("Pinging no fill URLs.");
            cr.a(this.lr.lB, this.lr.lD.wD, fzVar, this.lr.lA, false, fzVar.vq.qh);
        }
        if (fzVar.errorCode != -2) {
            a(fzVar.errorCode);
            return;
        }
        if (!this.lr.lH.og && !z && this.lr.lW == 0) {
            if (!b(fzVar)) {
                a(0);
                return;
            } else if (this.lr.lz != null) {
                this.lr.lz.ly.Q(fzVar.tN);
            }
        }
        if (this.lr.lI != null && this.lr.lI.qB != null) {
            this.lr.lI.qB.a((cn) null);
        }
        if (fzVar.qB != null) {
            fzVar.qB.a(this);
        }
        this.lt.d(this.lr.lI);
        this.lr.lI = fzVar;
        this.lr.lK.j(fzVar.vs);
        this.lr.lK.k(fzVar.vt);
        this.lr.lK.t(this.lr.lH.og);
        this.lr.lK.u(fzVar.tI);
        if (!this.lr.lH.og && !z && this.lr.lW == 0) {
            c(false);
        }
        if (this.lr.lU == null) {
            this.lr.lU = new ge(this.lr.lA);
        }
        if (fzVar.vq != null) {
            i2 = fzVar.vq.qk;
            i = fzVar.vq.ql;
        } else {
            i = 0;
            i2 = 0;
        }
        this.lr.lU.d(i2, i);
        if (this.lr.lW != 0) {
            if (this.lr.lV == null || fzVar.vp == null) {
                return;
            }
            this.lt.a(this.lr.lB, this.lr.lH, this.lr.lI, this.lr.lV, this.lr.lD);
            return;
        }
        if (!this.lr.lH.og && fzVar.rN != null && (fzVar.rN.dv().dF() || fzVar.vp != null)) {
            af afVarA = this.lt.a(this.lr.lH, this.lr.lI);
            if (fzVar.rN.dv().dF() && afVarA != null) {
                afVarA.a(new z(fzVar.rN));
            }
        }
        if (this.lr.lI.rN != null) {
            this.lr.lI.rN.bT();
            this.lr.lI.rN.dv().dG();
        }
        if (z) {
            bq.a aVar = fzVar.vu;
            if ((aVar instanceof bp) && this.lr.lR != null) {
                ap();
            } else {
                if (!(aVar instanceof bo) || this.lr.lQ == null) {
                    gs.W("No matching listener for retrieved native ad template.");
                    a(0);
                    return;
                }
                ao();
            }
        }
        an();
    }

    @Override // com.google.android.gms.internal.bz
    public void a(String str, ArrayList<String> arrayList) {
        dy dyVar = new dy(str, arrayList, this.lr.lB, this.lr.lD.wD);
        if (this.lr.lN != null) {
            try {
                this.lr.lN.a(dyVar);
                return;
            } catch (RemoteException e) {
                gs.W("Could not start In-App purchase.");
                return;
            }
        }
        gs.W("InAppPurchaseListener is not set. Try to launch default purchase flow.");
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.lr.lB) != 0) {
            gs.W("Google Play Service unavailable, cannot launch default purchase flow.");
            return;
        }
        if (this.lr.lM == null) {
            gs.W("PlayStorePurchaseListener is not set.");
            return;
        }
        if (this.lr.lT == null) {
            gs.W("PlayStorePurchaseVerifier is not initialized.");
            return;
        }
        try {
            if (!this.lr.lM.isValidPurchase(str)) {
                return;
            }
        } catch (RemoteException e2) {
            gs.W("Could not start In-App purchase.");
        }
        dz.a(this.lr.lB, this.lr.lD.wG, new dv(dyVar, this.lr.lM, this.lr.lT, this.lr.lB));
    }

    @Override // com.google.android.gms.internal.gd
    public void a(HashSet<ga> hashSet) {
        this.lr.a(hashSet);
    }

    public void a(List<String> list) {
        com.google.android.gms.common.internal.n.aT("setNativeTemplates must be called on the main UI thread.");
        this.lr.lS = list;
    }

    @Override // com.google.android.gms.internal.bd
    public boolean a(av avVar) throws PackageManager.NameNotFoundException {
        com.google.android.gms.common.internal.n.aT("loadAd must be called on the main UI thread.");
        if (this.lr.lF != null || this.lr.lG != null) {
            if (this.lp != null) {
                gs.W("Aborting last ad request since another ad request is already in progress. The current request object will still be cached for future refreshes.");
            }
            this.lp = avVar;
            return false;
        }
        if (this.lr.lH.og && this.lr.lI != null) {
            gs.W("An interstitial is already loading. Aborting.");
            return false;
        }
        if (!aq()) {
            return false;
        }
        gs.U("Starting ad request.");
        if (!avVar.nW) {
            gs.U("Use AdRequest.Builder.addTestDevice(\"" + gr.v(this.lr.lB) + "\") to get test ads on this device.");
        }
        Bundle bundleA = a(gb.cV().l(this.lr.lB));
        this.ls.cancel();
        this.lr.lW = 0;
        this.lr.lF = fa.a(this.lr.lB, a(avVar, bundleA), this.lr.lC, this);
        return true;
    }

    boolean a(fz fzVar, boolean z) {
        av avVar;
        boolean z2 = false;
        if (this.lp != null) {
            avVar = this.lp;
            this.lp = null;
        } else {
            avVar = fzVar.tx;
            if (avVar.extras != null) {
                z2 = avVar.extras.getBoolean("_noRefresh", false);
            }
        }
        boolean z3 = z2 | z;
        if (this.lr.lH.og) {
            if (this.lr.lW == 0) {
                gj.a(fzVar.rN);
            }
        } else if (!z3 && this.lr.lW == 0) {
            if (fzVar.qj > 0) {
                this.ls.a(avVar, fzVar.qj);
            } else if (fzVar.vq != null && fzVar.vq.qj > 0) {
                this.ls.a(avVar, fzVar.vq.qj);
            } else if (!fzVar.tI && fzVar.errorCode == 2) {
                this.ls.c(avVar);
            }
        }
        return this.ls.ay();
    }

    @Override // com.google.android.gms.internal.dq
    public void ab() {
        al();
    }

    @Override // com.google.android.gms.internal.dn
    public void ac() {
        this.lt.d(this.lr.lI);
        if (this.lr.lH.og) {
            at();
        }
        this.lu = false;
        ak();
        this.lr.lK.cR();
    }

    @Override // com.google.android.gms.internal.dn
    public void ad() {
        if (this.lr.lH.og) {
            c(false);
        }
        this.lu = true;
        am();
    }

    @Override // com.google.android.gms.internal.cn
    public void ae() {
        onAdClicked();
    }

    @Override // com.google.android.gms.internal.cn
    public void af() {
        ac();
    }

    @Override // com.google.android.gms.internal.cn
    public void ag() {
        ab();
    }

    @Override // com.google.android.gms.internal.cn
    public void ah() {
        ad();
    }

    @Override // com.google.android.gms.internal.cn
    public void ai() {
        if (this.lr.lI != null) {
            gs.W("Mediation adapter " + this.lr.lI.qA + " refreshed, but mediation adapters should never refresh.");
        }
        c(true);
        an();
    }

    @Override // com.google.android.gms.internal.bd
    public void aj() {
        com.google.android.gms.common.internal.n.aT("recordManualImpression must be called on the main UI thread.");
        if (this.lr.lI == null) {
            gs.W("Ad state was null when trying to ping manual tracking URLs.");
            return;
        }
        gs.S("Pinging manual tracking URLs.");
        if (this.lr.lI.tK != null) {
            gj.a(this.lr.lB, this.lr.lD.wD, this.lr.lI.tK);
        }
    }

    public boolean aq() {
        boolean z = true;
        if (!gj.a(this.lr.lB.getPackageManager(), this.lr.lB.getPackageName(), "android.permission.INTERNET")) {
            if (!this.lr.lH.og) {
                gr.a(this.lr.lz, this.lr.lH, "Missing internet permission in AndroidManifest.xml.", "Missing internet permission in AndroidManifest.xml. You must have the following declaration: <uses-permission android:name=\"android.permission.INTERNET\" />");
            }
            z = false;
        }
        if (!gj.p(this.lr.lB)) {
            if (!this.lr.lH.og) {
                gr.a(this.lr.lz, this.lr.lH, "Missing AdActivity with android:configChanges in AndroidManifest.xml.", "Missing AdActivity with android:configChanges in AndroidManifest.xml. You must have the following declaration within the <application> element: <activity android:name=\"com.google.android.gms.ads.AdActivity\" android:configChanges=\"keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize\" />");
            }
            z = false;
        }
        if (!z && !this.lr.lH.og) {
            this.lr.lz.setVisibility(0);
        }
        return z;
    }

    @Override // com.google.android.gms.internal.aa
    public void ar() {
        if (this.lr.lI == null) {
            gs.W("Ad state was null when trying to ping click URLs.");
            return;
        }
        gs.S("Pinging click URLs.");
        this.lr.lK.cQ();
        if (this.lr.lI.qf != null) {
            gj.a(this.lr.lB, this.lr.lD.wD, this.lr.lI.qf);
        }
        if (this.lr.lI.vq == null || this.lr.lI.vq.qf == null) {
            return;
        }
        cr.a(this.lr.lB, this.lr.lD.wD, this.lr.lI, this.lr.lA, false, this.lr.lI.vq.qf);
    }

    @Override // com.google.android.gms.internal.aa
    public void as() {
        c(false);
    }

    @Override // com.google.android.gms.internal.aa
    public void b(View view) {
        this.lr.lV = view;
        a(new fz(this.lr.lJ, null, null, null, null, null, null));
    }

    public void b(av avVar) throws PackageManager.NameNotFoundException {
        Object parent = this.lr.lz.getParent();
        if ((parent instanceof View) && ((View) parent).isShown() && gj.dl() && !this.lu) {
            a(avVar);
        } else {
            gs.U("Ad is not visible. Not refreshing ad.");
            this.ls.c(avVar);
        }
    }

    @Override // com.google.android.gms.internal.cb
    public void b(boolean z) {
        this.lr.lX = z;
    }

    @Override // com.google.android.gms.internal.bd
    public void destroy() {
        com.google.android.gms.common.internal.n.aT("destroy must be called on the main UI thread.");
        aa();
        this.lr.lE = null;
        this.lr.lL = null;
        this.lr.lM = null;
        this.lr.lN = null;
        this.lr.lO = null;
        this.lr.lP = null;
        this.ls.cancel();
        this.lt.stop();
        stopLoading();
        if (this.lr.lz != null) {
            this.lr.lz.removeAllViews();
        }
        if (this.lr.lI != null && this.lr.lI.rN != null) {
            this.lr.lI.rN.destroy();
        }
        if (this.lr.lI == null || this.lr.lI.qz == null) {
            return;
        }
        try {
            this.lr.lI.qz.destroy();
        } catch (RemoteException e) {
            gs.W("Could not destroy mediation adapter.");
        }
    }

    @Override // com.google.android.gms.internal.bd
    public String getMediationAdapterClassName() {
        if (this.lr.lI != null) {
            return this.lr.lI.qA;
        }
        return null;
    }

    @Override // com.google.android.gms.internal.bd
    public boolean isReady() {
        com.google.android.gms.common.internal.n.aT("isLoaded must be called on the main UI thread.");
        return this.lr.lF == null && this.lr.lG == null && this.lr.lI != null;
    }

    @Override // com.google.android.gms.internal.t
    public void onAdClicked() {
        ar();
    }

    @Override // com.google.android.gms.internal.bw
    public void onAppEvent(String name, String info) {
        if (this.lr.lL != null) {
            try {
                this.lr.lL.onAppEvent(name, info);
            } catch (RemoteException e) {
                gs.d("Could not call the AppEventListener.", e);
            }
        }
    }

    @Override // com.google.android.gms.internal.bd
    public void pause() {
        com.google.android.gms.common.internal.n.aT("pause must be called on the main UI thread.");
        if (this.lr.lI != null && this.lr.lW == 0) {
            gj.a(this.lr.lI.rN);
        }
        if (this.lr.lI != null && this.lr.lI.qz != null) {
            try {
                this.lr.lI.qz.pause();
            } catch (RemoteException e) {
                gs.W("Could not pause mediation adapter.");
            }
        }
        this.lt.pause();
        this.ls.pause();
    }

    @Override // com.google.android.gms.internal.bd
    public void resume() {
        com.google.android.gms.common.internal.n.aT("resume must be called on the main UI thread.");
        if (this.lr.lI != null && this.lr.lW == 0) {
            gj.b(this.lr.lI.rN);
        }
        if (this.lr.lI != null && this.lr.lI.qz != null) {
            try {
                this.lr.lI.qz.resume();
            } catch (RemoteException e) {
                gs.W("Could not resume mediation adapter.");
            }
        }
        this.ls.resume();
        this.lt.resume();
    }

    @Override // com.google.android.gms.internal.bd
    public void showInterstitial() {
        com.google.android.gms.common.internal.n.aT("showInterstitial must be called on the main UI thread.");
        if (!this.lr.lH.og) {
            gs.W("Cannot call showInterstitial on a banner ad.");
            return;
        }
        if (this.lr.lI == null) {
            gs.W("The interstitial has not loaded.");
            return;
        }
        if (this.lr.lW != 1) {
            if (this.lr.lI.rN.dz()) {
                gs.W("The interstitial is already showing.");
                return;
            }
            this.lr.lI.rN.x(true);
            if (this.lr.lI.rN.dv().dF() || this.lr.lI.vp != null) {
                af afVarA = this.lt.a(this.lr.lH, this.lr.lI);
                if (this.lr.lI.rN.dv().dF() && afVarA != null) {
                    afVarA.a(new z(this.lr.lI.rN));
                }
            }
            if (this.lr.lI.tI) {
                try {
                    this.lr.lI.qz.showInterstitial();
                    return;
                } catch (RemoteException e) {
                    gs.d("Could not show interstitial.", e);
                    at();
                    return;
                }
            }
            x xVar = new x(this.lr.lX, false);
            if (this.lr.lB instanceof Activity) {
                Window window = ((Activity) this.lr.lB).getWindow();
                Rect rect = new Rect();
                Rect rect2 = new Rect();
                window.getDecorView().getGlobalVisibleRect(rect);
                window.getDecorView().getWindowVisibleDisplayFrame(rect2);
                if (rect.bottom != 0 && rect2.bottom != 0) {
                    xVar = new x(this.lr.lX, rect.top == rect2.top);
                }
            }
            dk.a(this.lr.lB, new dm(this, this, this, this.lr.lI.rN, this.lr.lI.orientation, this.lr.lD, this.lr.lI.tN, xVar));
        }
    }

    @Override // com.google.android.gms.internal.bd
    public void stopLoading() {
        com.google.android.gms.common.internal.n.aT("stopLoading must be called on the main UI thread.");
        if (this.lr.lI != null && this.lr.lW == 0) {
            this.lr.lI.rN.stopLoading();
            this.lr.lI = null;
        }
        if (this.lr.lF != null) {
            this.lr.lF.cancel();
        }
        if (this.lr.lG != null) {
            this.lr.lG.cancel();
        }
    }
}
