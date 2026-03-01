package com.iflytek.cloud;

import com.iflytek.msc.MSC;

/* loaded from: classes.dex */
public class Setting {
    public static boolean a = true;
    public static boolean b = true;
    public static boolean c = true;
    public static LOG_LEVEL d = LOG_LEVEL.none;
    public static String e = null;

    public enum LOG_LEVEL {
        all,
        detail,
        normal,
        low,
        none
    }

    private Setting() {
    }

    public static void checkNetwork(boolean z) {
        b = z;
    }

    public static void saveLogFile(LOG_LEVEL log_level, String str) {
        d = log_level;
        e = str;
    }

    public static void setLocationEnable(boolean z) {
        c = z;
    }

    public static void showLogcat(boolean z) {
        a = z;
        MSC.DebugLog(z);
    }
}
