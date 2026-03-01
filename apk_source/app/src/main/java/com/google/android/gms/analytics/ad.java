package com.google.android.gms.analytics;

import android.content.Context;
import android.util.DisplayMetrics;

/* loaded from: classes.dex */
class ad implements l {
    private static ad Bi;
    private static Object xz = new Object();
    private final Context mContext;

    protected ad(Context context) {
        this.mContext = context;
    }

    public static ad eR() {
        ad adVar;
        synchronized (xz) {
            adVar = Bi;
        }
        return adVar;
    }

    public static void y(Context context) {
        synchronized (xz) {
            if (Bi == null) {
                Bi = new ad(context);
            }
        }
    }

    public boolean ac(String str) {
        return "&sr".equals(str);
    }

    protected String eS() {
        DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels + "x" + displayMetrics.heightPixels;
    }

    @Override // com.google.android.gms.analytics.l
    public String getValue(String field) {
        if (field != null && field.equals("&sr")) {
            return eS();
        }
        return null;
    }
}
