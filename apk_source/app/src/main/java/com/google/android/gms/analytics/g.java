package com.google.android.gms.analytics;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/* loaded from: classes.dex */
class g implements l {
    private static g xP;
    private static Object xz = new Object();
    protected String xL;
    protected String xM;
    protected String xN;
    protected String xO;

    protected g() {
    }

    private g(Context context) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        this.xN = context.getPackageName();
        this.xO = packageManager.getInstallerPackageName(this.xN);
        String string = this.xN;
        String str = null;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (packageInfo != null) {
                string = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
                str = packageInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            z.T("Error retrieving package info: appName set to " + string);
        }
        this.xL = string;
        this.xM = str;
    }

    public static g dQ() {
        return xP;
    }

    public static void y(Context context) {
        synchronized (xz) {
            if (xP == null) {
                xP = new g(context);
            }
        }
    }

    public boolean ac(String str) {
        return "&an".equals(str) || "&av".equals(str) || "&aid".equals(str) || "&aiid".equals(str);
    }

    @Override // com.google.android.gms.analytics.l
    public String getValue(String field) {
        if (field == null) {
            return null;
        }
        if (field.equals("&an")) {
            return this.xL;
        }
        if (field.equals("&av")) {
            return this.xM;
        }
        if (field.equals("&aid")) {
            return this.xN;
        }
        if (field.equals("&aiid")) {
            return this.xO;
        }
        return null;
    }
}
