package com.google.android.gms.internal;

import android.os.Bundle;

@ez
/* loaded from: classes.dex */
public final class bn {
    private static boolean pk;
    private static final Bundle pj = new Bundle();
    public static iv<String> oX = a("gads:sdk_core_location", "https://googleads.g.doubleclick.net/mads/static/mad/sdk/native/sdk-core-v40.html");
    public static iv<String> oY = a("gads:sdk_core_experiment_id", (String) null);
    public static iv<Boolean> oZ = c("gads:sdk_crash_report_enabled", false);
    public static iv<Boolean> pa = c("gads:sdk_crash_report_full_stacktrace", false);
    public static iv<Boolean> pb = c("gads:block_autoclicks", false);
    public static iv<String> pc = a("gads:block_autoclicks_experiment_id", (String) null);
    public static iv<Boolean> pd = c("gads:enable_content_fetching", false);
    public static iv<Integer> pe = a("gads:content_length_weight", 1);
    public static iv<Integer> pf = a("gads:content_age_weight", 1);
    public static iv<Integer> pg = a("gads:min_content_len", 11);
    public static iv<Integer> ph = a("gads:fingerprint_number", 10);
    public static iv<Integer> pi = a("gads:sleep_sec", 10);

    static {
        pk = false;
        pk = true;
    }

    private static iv<Integer> a(String str, int i) {
        pj.putInt(str, i);
        return iv.a(str, Integer.valueOf(i));
    }

    private static iv<String> a(String str, String str2) {
        pj.putString(str, str2);
        return iv.m(str, str2);
    }

    public static Bundle bs() {
        return pj;
    }

    private static iv<Boolean> c(String str, boolean z) {
        pj.putBoolean(str, z);
        return iv.g(str, z);
    }
}
