package com.google.android.gms.analytics;

import android.app.Activity;
import android.content.Context;
import android.support.v7.internal.widget.ActivityChooserView;
import android.text.TextUtils;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.t;
import com.google.android.gms.internal.ju;
import com.google.android.gms.internal.jw;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/* loaded from: classes.dex */
public class Tracker {
    private final TrackerHandler Bm;
    private ac Bn;
    private final h Bo;
    private final ad Bp;
    private final g Bq;
    private boolean Br;
    private a Bs;
    private ai Bt;
    private ExceptionReporter Bu;
    private Context mContext;
    private final Map<String, String> qM;

    private class a implements GoogleAnalytics.a {
        private long Bz;
        private boolean Bv = false;
        private int Bw = 0;
        private long Bx = -1;
        private boolean By = false;
        private ju yD = jw.hA();

        public a() {
        }

        private void eX() {
            GoogleAnalytics googleAnalyticsEE = GoogleAnalytics.eE();
            if (googleAnalyticsEE == null) {
                z.T("GoogleAnalytics isn't initialized for the Tracker!");
            } else if (this.Bx >= 0 || this.Bv) {
                googleAnalyticsEE.a(Tracker.this.Bs);
            } else {
                googleAnalyticsEE.b(Tracker.this.Bs);
            }
        }

        public long eU() {
            return this.Bx;
        }

        public boolean eV() {
            return this.Bv;
        }

        public boolean eW() {
            boolean z = this.By;
            this.By = false;
            return z;
        }

        boolean eY() {
            return this.yD.elapsedRealtime() >= this.Bz + Math.max(1000L, this.Bx);
        }

        public void enableAutoActivityTracking(boolean enabled) {
            this.Bv = enabled;
            eX();
        }

        @Override // com.google.android.gms.analytics.GoogleAnalytics.a
        public void i(Activity activity) {
            t.eq().a(t.a.EASY_TRACKER_ACTIVITY_START);
            if (this.Bw == 0 && eY()) {
                this.By = true;
            }
            this.Bw++;
            if (this.Bv) {
                HashMap map = new HashMap();
                map.put("&t", "screenview");
                t.eq().B(true);
                Tracker.this.set("&cd", Tracker.this.Bt != null ? Tracker.this.Bt.k(activity) : activity.getClass().getCanonicalName());
                Tracker.this.send(map);
                t.eq().B(false);
            }
        }

        @Override // com.google.android.gms.analytics.GoogleAnalytics.a
        public void j(Activity activity) {
            t.eq().a(t.a.EASY_TRACKER_ACTIVITY_STOP);
            this.Bw--;
            this.Bw = Math.max(0, this.Bw);
            if (this.Bw == 0) {
                this.Bz = this.yD.elapsedRealtime();
            }
        }

        public void setSessionTimeout(long sessionTimeout) {
            this.Bx = sessionTimeout;
            eX();
        }
    }

    Tracker(String trackingId, TrackerHandler handler, Context context) {
        this(trackingId, handler, h.dR(), ad.eR(), g.dQ(), new y("tracking"), context);
    }

    Tracker(String trackingId, TrackerHandler handler, h clientIdDefaultProvider, ad screenResolutionDefaultProvider, g appFieldsDefaultProvider, ac rateLimiter, Context context) {
        this.qM = new HashMap();
        this.Bm = handler;
        if (context != null) {
            this.mContext = context.getApplicationContext();
        }
        if (trackingId != null) {
            this.qM.put("&tid", trackingId);
        }
        this.qM.put("useSecure", "1");
        this.Bo = clientIdDefaultProvider;
        this.Bp = screenResolutionDefaultProvider;
        this.Bq = appFieldsDefaultProvider;
        this.qM.put("&a", Integer.toString(new Random().nextInt(ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED) + 1));
        this.Bn = rateLimiter;
        this.Bs = new a();
        enableAdvertisingIdCollection(false);
    }

    void a(ai aiVar) {
        z.V("Loading Tracker config values.");
        this.Bt = aiVar;
        if (this.Bt.fa()) {
            String strFb = this.Bt.fb();
            set("&tid", strFb);
            z.V("[Tracker] trackingId loaded: " + strFb);
        }
        if (this.Bt.fc()) {
            String string = Double.toString(this.Bt.fd());
            set("&sf", string);
            z.V("[Tracker] sample frequency loaded: " + string);
        }
        if (this.Bt.fe()) {
            setSessionTimeout(this.Bt.getSessionTimeout());
            z.V("[Tracker] session timeout loaded: " + eU());
        }
        if (this.Bt.ff()) {
            enableAutoActivityTracking(this.Bt.fg());
            z.V("[Tracker] auto activity tracking loaded: " + eV());
        }
        if (this.Bt.fh()) {
            if (this.Bt.fi()) {
                set("&aip", "1");
                z.V("[Tracker] anonymize ip loaded: true");
            }
            z.V("[Tracker] anonymize ip loaded: false");
        }
        enableExceptionReporting(this.Bt.fj());
    }

    long eU() {
        return this.Bs.eU();
    }

    boolean eV() {
        return this.Bs.eV();
    }

    public void enableAdvertisingIdCollection(boolean enabled) {
        if (!enabled) {
            this.qM.put("&ate", null);
            this.qM.put("&adid", null);
            return;
        }
        if (this.qM.containsKey("&ate")) {
            this.qM.remove("&ate");
        }
        if (this.qM.containsKey("&adid")) {
            this.qM.remove("&adid");
        }
    }

    public void enableAutoActivityTracking(boolean enabled) {
        this.Bs.enableAutoActivityTracking(enabled);
    }

    public void enableExceptionReporting(boolean enabled) {
        if (this.Br == enabled) {
            return;
        }
        this.Br = enabled;
        if (enabled) {
            this.Bu = new ExceptionReporter(this, Thread.getDefaultUncaughtExceptionHandler(), this.mContext);
            Thread.setDefaultUncaughtExceptionHandler(this.Bu);
            z.V("Uncaught exceptions will be reported to Google Analytics.");
        } else {
            if (this.Bu != null) {
                Thread.setDefaultUncaughtExceptionHandler(this.Bu.dZ());
            } else {
                Thread.setDefaultUncaughtExceptionHandler(null);
            }
            z.V("Uncaught exceptions will not be reported to Google Analytics.");
        }
    }

    public String get(String key) {
        t.eq().a(t.a.GET);
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        if (this.qM.containsKey(key)) {
            return this.qM.get(key);
        }
        if (key.equals("&ul")) {
            return aj.a(Locale.getDefault());
        }
        if (this.Bo != null && this.Bo.ac(key)) {
            return this.Bo.getValue(key);
        }
        if (this.Bp != null && this.Bp.ac(key)) {
            return this.Bp.getValue(key);
        }
        if (this.Bq == null || !this.Bq.ac(key)) {
            return null;
        }
        return this.Bq.getValue(key);
    }

    public void send(Map<String, String> params) {
        t.eq().a(t.a.SEND);
        HashMap map = new HashMap();
        map.putAll(this.qM);
        if (params != null) {
            map.putAll(params);
        }
        if (TextUtils.isEmpty((CharSequence) map.get("&tid"))) {
            z.W(String.format("Missing tracking id (%s) parameter.", "&tid"));
        }
        String str = (String) map.get("&t");
        if (TextUtils.isEmpty(str)) {
            z.W(String.format("Missing hit type (%s) parameter.", "&t"));
            str = "";
        }
        if (this.Bs.eW()) {
            map.put("&sc", "start");
        }
        String lowerCase = str.toLowerCase();
        if ("screenview".equals(lowerCase) || "pageview".equals(lowerCase) || "appview".equals(lowerCase) || TextUtils.isEmpty(lowerCase)) {
            int i = Integer.parseInt(this.qM.get("&a")) + 1;
            if (i >= Integer.MAX_VALUE) {
                i = 1;
            }
            this.qM.put("&a", Integer.toString(i));
        }
        if (lowerCase.equals("transaction") || lowerCase.equals("item") || this.Bn.eK()) {
            this.Bm.u(map);
        } else {
            z.W("Too many hits sent too quickly, rate limiting invoked.");
        }
    }

    public void set(String key, String value) {
        com.google.android.gms.common.internal.n.b(key, (Object) "Key should be non-null");
        t.eq().a(t.a.SET);
        this.qM.put(key, value);
    }

    public void setAnonymizeIp(boolean anonymize) {
        set("&aip", aj.C(anonymize));
    }

    public void setAppId(String appId) {
        set("&aid", appId);
    }

    public void setAppInstallerId(String appInstallerId) {
        set("&aiid", appInstallerId);
    }

    public void setAppName(String appName) {
        set("&an", appName);
    }

    public void setAppVersion(String appVersion) {
        set("&av", appVersion);
    }

    public void setClientId(String clientId) {
        set("&cid", clientId);
    }

    public void setEncoding(String encoding) {
        set("&de", encoding);
    }

    public void setHostname(String hostname) {
        set("&dh", hostname);
    }

    public void setLanguage(String language) {
        set("&ul", language);
    }

    public void setLocation(String location) {
        set("&dl", location);
    }

    public void setPage(String page) {
        set("&dp", page);
    }

    public void setReferrer(String referrer) {
        set("&dr", referrer);
    }

    public void setSampleRate(double sampleRate) {
        set("&sf", Double.toHexString(sampleRate));
    }

    public void setScreenColors(String screenColors) {
        set("&sd", screenColors);
    }

    public void setScreenName(String screenName) {
        set("&cd", screenName);
    }

    public void setScreenResolution(int width, int height) {
        if (width >= 0 || height >= 0) {
            set("&sr", width + "x" + height);
        } else {
            z.W("Invalid width or height. The values should be non-negative.");
        }
    }

    public void setSessionTimeout(long sessionTimeout) {
        this.Bs.setSessionTimeout(1000 * sessionTimeout);
    }

    public void setTitle(String title) {
        set("&dt", title);
    }

    public void setUseSecure(boolean useSecure) {
        set("useSecure", aj.C(useSecure));
    }

    public void setViewportSize(String viewportSize) {
        set("&vp", viewportSize);
    }
}
