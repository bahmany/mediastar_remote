package com.google.android.gms.analytics;

/* loaded from: classes.dex */
public final class n {
    public static String A(int i) {
        return d("&promo", i);
    }

    public static String B(int i) {
        return d("pi", i);
    }

    public static String C(int i) {
        return d("&il", i);
    }

    public static String D(int i) {
        return d("cd", i);
    }

    public static String E(int i) {
        return d("cm", i);
    }

    private static String d(String str, int i) {
        if (i >= 1) {
            return str + i;
        }
        z.T("index out of range for " + str + " (" + i + ")");
        return "";
    }

    static String x(int i) {
        return d("&cd", i);
    }

    static String y(int i) {
        return d("&cm", i);
    }

    public static String z(int i) {
        return d("&pr", i);
    }
}
