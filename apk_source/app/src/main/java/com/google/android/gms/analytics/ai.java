package com.google.android.gms.analytics;

import android.app.Activity;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
class ai implements i {
    String BC;
    double BD = -1.0d;
    int BE = -1;
    int BF = -1;
    int BG = -1;
    int BH = -1;
    Map<String, String> BI = new HashMap();

    ai() {
    }

    public String am(String str) {
        String str2 = this.BI.get(str);
        return str2 != null ? str2 : str;
    }

    public boolean fa() {
        return this.BC != null;
    }

    public String fb() {
        return this.BC;
    }

    public boolean fc() {
        return this.BD >= 0.0d;
    }

    public double fd() {
        return this.BD;
    }

    public boolean fe() {
        return this.BE >= 0;
    }

    public boolean ff() {
        return this.BF != -1;
    }

    public boolean fg() {
        return this.BF == 1;
    }

    public boolean fh() {
        return this.BG != -1;
    }

    public boolean fi() {
        return this.BG == 1;
    }

    public boolean fj() {
        return this.BH == 1;
    }

    public int getSessionTimeout() {
        return this.BE;
    }

    public String k(Activity activity) {
        return am(activity.getClass().getCanonicalName());
    }
}
