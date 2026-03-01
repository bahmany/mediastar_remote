package com.google.android.gms.analytics;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import com.google.android.gms.analytics.t;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class GoogleAnalytics extends TrackerHandler {
    private static GoogleAnalytics AC;
    private static boolean Av;
    private Set<a> AA;
    private boolean AB;
    private boolean Aw;
    private ae Ax;
    private volatile Boolean Ay;
    private Logger Az;
    private Context mContext;
    private String xL;
    private String xM;
    private f ye;

    interface a {
        void i(Activity activity);

        void j(Activity activity);
    }

    class b implements Application.ActivityLifecycleCallbacks {
        b() {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityDestroyed(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityPaused(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityResumed(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStarted(Activity activity) {
            GoogleAnalytics.this.g(activity);
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStopped(Activity activity) {
            GoogleAnalytics.this.h(activity);
        }
    }

    protected GoogleAnalytics(Context context) {
        this(context, s.B(context), q.ea());
    }

    private GoogleAnalytics(Context context, f thread, ae serviceManager) throws PackageManager.NameNotFoundException {
        this.Ay = false;
        this.AB = false;
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        this.mContext = context.getApplicationContext();
        this.ye = thread;
        this.Ax = serviceManager;
        g.y(this.mContext);
        ad.y(this.mContext);
        h.y(this.mContext);
        this.Az = new k();
        this.AA = new HashSet();
        eF();
    }

    private Tracker a(Tracker tracker) {
        if (this.xL != null) {
            tracker.set("&an", this.xL);
        }
        if (this.xM != null) {
            tracker.set("&av", this.xM);
        }
        return tracker;
    }

    private int ai(String str) {
        String lowerCase = str.toLowerCase();
        if ("verbose".equals(lowerCase)) {
            return 0;
        }
        if ("info".equals(lowerCase)) {
            return 1;
        }
        if ("warning".equals(lowerCase)) {
            return 2;
        }
        return "error".equals(lowerCase) ? 3 : -1;
    }

    static GoogleAnalytics eE() {
        GoogleAnalytics googleAnalytics;
        synchronized (GoogleAnalytics.class) {
            googleAnalytics = AC;
        }
        return googleAnalytics;
    }

    private void eF() throws PackageManager.NameNotFoundException {
        ApplicationInfo applicationInfo;
        int i;
        v vVarW;
        if (Av) {
            return;
        }
        try {
            applicationInfo = this.mContext.getPackageManager().getApplicationInfo(this.mContext.getPackageName(), 129);
        } catch (PackageManager.NameNotFoundException e) {
            z.V("PackageManager doesn't know about package: " + e);
            applicationInfo = null;
        }
        if (applicationInfo == null) {
            z.W("Couldn't get ApplicationInfo to load gloabl config.");
            return;
        }
        Bundle bundle = applicationInfo.metaData;
        if (bundle == null || (i = bundle.getInt("com.google.android.gms.analytics.globalConfigResource")) <= 0 || (vVarW = new u(this.mContext).w(i)) == null) {
            return;
        }
        a(vVarW);
    }

    public static GoogleAnalytics getInstance(Context context) {
        GoogleAnalytics googleAnalytics;
        synchronized (GoogleAnalytics.class) {
            if (AC == null) {
                AC = new GoogleAnalytics(context);
            }
            googleAnalytics = AC;
        }
        return googleAnalytics;
    }

    void a(a aVar) {
        this.AA.add(aVar);
        if (this.mContext instanceof Application) {
            enableAutoActivityReports((Application) this.mContext);
        }
    }

    void a(v vVar) {
        int iAi;
        z.V("Loading global config values.");
        if (vVar.eu()) {
            this.xL = vVar.ev();
            z.V("app name loaded: " + this.xL);
        }
        if (vVar.ew()) {
            this.xM = vVar.ex();
            z.V("app version loaded: " + this.xM);
        }
        if (vVar.ey() && (iAi = ai(vVar.ez())) >= 0) {
            z.V("log level loaded: " + iAi);
            getLogger().setLogLevel(iAi);
        }
        if (vVar.eA()) {
            this.Ax.setLocalDispatchPeriod(vVar.eB());
        }
        if (vVar.eC()) {
            setDryRun(vVar.eD());
        }
    }

    void b(a aVar) {
        this.AA.remove(aVar);
    }

    @Deprecated
    public void dispatchLocalHits() {
        this.Ax.dispatchLocalHits();
    }

    public void enableAutoActivityReports(Application application) {
        if (Build.VERSION.SDK_INT < 14 || this.AB) {
            return;
        }
        application.registerActivityLifecycleCallbacks(new b());
        this.AB = true;
    }

    void g(Activity activity) {
        Iterator<a> it = this.AA.iterator();
        while (it.hasNext()) {
            it.next().i(activity);
        }
    }

    public boolean getAppOptOut() {
        t.eq().a(t.a.GET_APP_OPT_OUT);
        return this.Ay.booleanValue();
    }

    public Logger getLogger() {
        return this.Az;
    }

    void h(Activity activity) {
        Iterator<a> it = this.AA.iterator();
        while (it.hasNext()) {
            it.next().j(activity);
        }
    }

    public boolean isDryRunEnabled() {
        t.eq().a(t.a.GET_DRY_RUN);
        return this.Aw;
    }

    public Tracker newTracker(int configResId) {
        Tracker trackerA;
        ai aiVarW;
        synchronized (this) {
            t.eq().a(t.a.GET_TRACKER);
            Tracker tracker = new Tracker(null, this, this.mContext);
            if (configResId > 0 && (aiVarW = new ah(this.mContext).w(configResId)) != null) {
                tracker.a(aiVarW);
            }
            trackerA = a(tracker);
        }
        return trackerA;
    }

    public Tracker newTracker(String trackingId) {
        Tracker trackerA;
        synchronized (this) {
            t.eq().a(t.a.GET_TRACKER);
            trackerA = a(new Tracker(trackingId, this, this.mContext));
        }
        return trackerA;
    }

    public void reportActivityStart(Activity activity) {
        if (this.AB) {
            return;
        }
        g(activity);
    }

    public void reportActivityStop(Activity activity) {
        if (this.AB) {
            return;
        }
        h(activity);
    }

    public void setAppOptOut(boolean optOut) {
        t.eq().a(t.a.SET_APP_OPT_OUT);
        this.Ay = Boolean.valueOf(optOut);
        if (this.Ay.booleanValue()) {
            this.ye.dI();
        }
    }

    public void setDryRun(boolean dryRun) {
        t.eq().a(t.a.SET_DRY_RUN);
        this.Aw = dryRun;
    }

    @Deprecated
    public void setLocalDispatchPeriod(int dispatchPeriodInSeconds) {
        this.Ax.setLocalDispatchPeriod(dispatchPeriodInSeconds);
    }

    public void setLogger(Logger logger) {
        t.eq().a(t.a.SET_LOGGER);
        this.Az = logger;
    }

    @Override // com.google.android.gms.analytics.TrackerHandler
    void u(Map<String, String> map) {
        synchronized (this) {
            if (map == null) {
                throw new IllegalArgumentException("hit cannot be null");
            }
            aj.a(map, "&ul", aj.a(Locale.getDefault()));
            aj.a(map, "&sr", ad.eR());
            map.put("&_u", t.eq().es());
            t.eq().er();
            this.ye.u(map);
        }
    }
}
