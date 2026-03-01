package com.google.android.gms.internal;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import com.google.android.gms.internal.ah;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public final class af implements ViewTreeObserver.OnGlobalLayoutListener, ViewTreeObserver.OnScrollChangedListener {
    private static final long mK = TimeUnit.MILLISECONDS.toNanos(100);
    private WeakReference<ViewTreeObserver> mA;
    private final WeakReference<View> mB;
    private final ad mC;
    private final Context mD;
    private final ah mE;
    private boolean mF;
    private final WindowManager mG;
    private final PowerManager mH;
    private final KeyguardManager mI;
    private ag mJ;
    private boolean mL;
    private final BlockingQueue<Runnable> mM;
    private long mN;
    private boolean mO;
    private boolean mP;
    private BroadcastReceiver mQ;
    private final HashSet<ac> mR;
    private boolean mn;
    private final Object mw;
    private final WeakReference<fz> mz;

    public af(Context context, ay ayVar, fz fzVar, View view, gt gtVar) {
        this(ayVar, fzVar, gtVar, view, new aj(context, gtVar));
    }

    public af(ay ayVar, fz fzVar, gt gtVar, final View view, ah ahVar) {
        this.mw = new Object();
        this.mn = false;
        this.mL = false;
        this.mM = new ArrayBlockingQueue(2);
        this.mN = Long.MIN_VALUE;
        this.mR = new HashSet<>();
        this.mz = new WeakReference<>(fzVar);
        this.mB = new WeakReference<>(view);
        this.mA = new WeakReference<>(null);
        this.mO = true;
        this.mC = new ad(UUID.randomUUID().toString(), gtVar, ayVar.of, fzVar.vp);
        this.mE = ahVar;
        this.mG = (WindowManager) view.getContext().getSystemService("window");
        this.mH = (PowerManager) view.getContext().getApplicationContext().getSystemService("power");
        this.mI = (KeyguardManager) view.getContext().getSystemService("keyguard");
        this.mD = view.getContext().getApplicationContext();
        a(ahVar);
        this.mE.a(new ah.a() { // from class: com.google.android.gms.internal.af.1
            @Override // com.google.android.gms.internal.ah.a
            public void aM() {
                af.this.mF = true;
                af.this.d(view);
                af.this.aD();
            }
        });
        b(this.mE);
        try {
            final JSONObject jSONObjectE = e(view);
            this.mM.add(new Runnable() { // from class: com.google.android.gms.internal.af.2
                @Override // java.lang.Runnable
                public void run() {
                    af.this.a(jSONObjectE);
                }
            });
        } catch (Throwable th) {
        }
        this.mM.add(new Runnable() { // from class: com.google.android.gms.internal.af.3
            @Override // java.lang.Runnable
            public void run() {
                af.this.e(false);
            }
        });
        gs.S("Tracking ad unit: " + this.mC.aC());
    }

    protected int a(int i, DisplayMetrics displayMetrics) {
        return (int) (i / displayMetrics.density);
    }

    protected void a(View view, Map<String, String> map) {
        e(false);
    }

    public void a(ac acVar) {
        this.mR.add(acVar);
    }

    public void a(ag agVar) {
        synchronized (this.mw) {
            this.mJ = agVar;
        }
    }

    protected void a(ah ahVar) {
        ahVar.f("https://googleads.g.doubleclick.net/mads/static/sdk/native/sdk-core-v40.html");
    }

    protected void a(JSONObject jSONObject) {
        try {
            JSONArray jSONArray = new JSONArray();
            JSONObject jSONObject2 = new JSONObject();
            jSONArray.put(jSONObject);
            jSONObject2.put("units", jSONArray);
            this.mE.a("AFMA_updateActiveView", jSONObject2);
        } catch (Throwable th) {
            gs.b("Skipping active view message.", th);
        }
    }

    protected boolean a(Map<String, String> map) {
        if (map == null) {
            return false;
        }
        String str = map.get("hashCode");
        return !TextUtils.isEmpty(str) && str.equals(this.mC.aC());
    }

    protected void aD() {
        synchronized (this.mw) {
            if (this.mQ != null) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            this.mQ = new BroadcastReceiver() { // from class: com.google.android.gms.internal.af.4
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    af.this.e(false);
                }
            };
            this.mD.registerReceiver(this.mQ, intentFilter);
        }
    }

    protected void aE() {
        synchronized (this.mw) {
            if (this.mQ != null) {
                this.mD.unregisterReceiver(this.mQ);
                this.mQ = null;
            }
        }
    }

    public void aF() {
        synchronized (this.mw) {
            if (this.mO) {
                this.mP = true;
                try {
                    a(aL());
                } catch (JSONException e) {
                    gs.b("JSON Failure while processing active view data.", e);
                }
                gs.S("Untracking ad unit: " + this.mC.aC());
            }
        }
    }

    protected void aG() {
        if (this.mJ != null) {
            this.mJ.a(this);
        }
    }

    public boolean aH() {
        boolean z;
        synchronized (this.mw) {
            z = this.mO;
        }
        return z;
    }

    protected void aI() {
        View view = this.mB.get();
        if (view == null) {
            return;
        }
        ViewTreeObserver viewTreeObserver = this.mA.get();
        ViewTreeObserver viewTreeObserver2 = view.getViewTreeObserver();
        if (viewTreeObserver2 != viewTreeObserver) {
            this.mA = new WeakReference<>(viewTreeObserver2);
            viewTreeObserver2.addOnScrollChangedListener(this);
            viewTreeObserver2.addOnGlobalLayoutListener(this);
        }
    }

    protected void aJ() {
        ViewTreeObserver viewTreeObserver = this.mA.get();
        if (viewTreeObserver == null || !viewTreeObserver.isAlive()) {
            return;
        }
        viewTreeObserver.removeOnScrollChangedListener(this);
        viewTreeObserver.removeGlobalOnLayoutListener(this);
    }

    protected JSONObject aK() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("afmaVersion", this.mC.aA()).put("activeViewJSON", this.mC.aB()).put("timestamp", TimeUnit.NANOSECONDS.toMillis(System.nanoTime())).put("adFormat", this.mC.az()).put("hashCode", this.mC.aC());
        return jSONObject;
    }

    protected JSONObject aL() throws JSONException {
        JSONObject jSONObjectAK = aK();
        jSONObjectAK.put("doneReasonCode", "u");
        return jSONObjectAK;
    }

    protected void b(ah ahVar) {
        ahVar.a("/updateActiveView", new by() { // from class: com.google.android.gms.internal.af.5
            @Override // com.google.android.gms.internal.by
            public void a(gv gvVar, Map<String, String> map) {
                if (af.this.a(map)) {
                    af.this.a(gvVar, map);
                }
            }
        });
        ahVar.a("/untrackActiveViewUnit", new by() { // from class: com.google.android.gms.internal.af.6
            @Override // com.google.android.gms.internal.by
            public void a(gv gvVar, Map<String, String> map) {
                if (af.this.a(map)) {
                    gs.S("Received request to untrack: " + af.this.mC.aC());
                    af.this.destroy();
                }
            }
        });
        ahVar.a("/visibilityChanged", new by() { // from class: com.google.android.gms.internal.af.7
            @Override // com.google.android.gms.internal.by
            public void a(gv gvVar, Map<String, String> map) {
                if (af.this.a(map) && map.containsKey("isVisible")) {
                    af.this.d(Boolean.valueOf("1".equals(map.get("isVisible")) || "true".equals(map.get("isVisible"))).booleanValue());
                }
            }
        });
        ahVar.a("/viewabilityChanged", bx.pA);
    }

    protected void d(View view) {
        ArrayList arrayList = new ArrayList();
        this.mM.drainTo(arrayList);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
    }

    protected void d(boolean z) {
        Iterator<ac> it = this.mR.iterator();
        while (it.hasNext()) {
            it.next().a(this, z);
        }
    }

    protected void destroy() {
        synchronized (this.mw) {
            aJ();
            aE();
            this.mO = false;
            try {
                this.mE.destroy();
            } catch (Throwable th) {
            }
            aG();
        }
    }

    protected JSONObject e(View view) throws JSONException {
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        view.getLocationInWindow(new int[2]);
        JSONObject jSONObjectAK = aK();
        DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
        Rect rect = new Rect();
        rect.left = iArr[0];
        rect.top = iArr[1];
        rect.right = rect.left + view.getWidth();
        rect.bottom = rect.top + view.getHeight();
        Rect rect2 = new Rect();
        rect2.right = this.mG.getDefaultDisplay().getWidth();
        rect2.bottom = this.mG.getDefaultDisplay().getHeight();
        Rect rect3 = new Rect();
        boolean globalVisibleRect = view.getGlobalVisibleRect(rect3, null);
        Rect rect4 = new Rect();
        jSONObjectAK.put("viewBox", new JSONObject().put("top", a(rect2.top, displayMetrics)).put("bottom", a(rect2.bottom, displayMetrics)).put("left", a(rect2.left, displayMetrics)).put("right", a(rect2.right, displayMetrics))).put("adBox", new JSONObject().put("top", a(rect.top, displayMetrics)).put("bottom", a(rect.bottom, displayMetrics)).put("left", a(rect.left, displayMetrics)).put("right", a(rect.right, displayMetrics))).put("globalVisibleBox", new JSONObject().put("top", a(rect3.top, displayMetrics)).put("bottom", a(rect3.bottom, displayMetrics)).put("left", a(rect3.left, displayMetrics)).put("right", a(rect3.right, displayMetrics))).put("globalVisibleBoxVisible", globalVisibleRect).put("localVisibleBox", new JSONObject().put("top", a(rect4.top, displayMetrics)).put("bottom", a(rect4.bottom, displayMetrics)).put("left", a(rect4.left, displayMetrics)).put("right", a(rect4.right, displayMetrics))).put("localVisibleBoxVisible", view.getLocalVisibleRect(rect4)).put("screenDensity", displayMetrics.density).put("isVisible", f(view)).put("isStopped", this.mL).put("isPaused", this.mn);
        return jSONObjectAK;
    }

    protected void e(boolean z) {
        synchronized (this.mw) {
            if (this.mF && this.mO) {
                long jNanoTime = System.nanoTime();
                if (!z || this.mN + mK <= jNanoTime) {
                    this.mN = jNanoTime;
                    fz fzVar = this.mz.get();
                    View view = this.mB.get();
                    if (view == null || fzVar == null) {
                        aF();
                        return;
                    }
                    try {
                        a(e(view));
                    } catch (JSONException e) {
                        gs.a("Active view update failed.", e);
                    }
                    aI();
                    aG();
                }
            }
        }
    }

    protected boolean f(View view) {
        return view.getVisibility() == 0 && view.isShown() && this.mH.isScreenOn() && !this.mI.inKeyguardRestrictedInputMode();
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        e(false);
    }

    @Override // android.view.ViewTreeObserver.OnScrollChangedListener
    public void onScrollChanged() {
        e(true);
    }

    public void pause() {
        synchronized (this.mw) {
            this.mn = true;
            e(false);
            this.mE.pause();
        }
    }

    public void resume() {
        synchronized (this.mw) {
            this.mE.resume();
            this.mn = false;
            e(false);
        }
    }

    public void stop() {
        synchronized (this.mw) {
            this.mL = true;
            e(false);
            this.mE.pause();
        }
    }
}
