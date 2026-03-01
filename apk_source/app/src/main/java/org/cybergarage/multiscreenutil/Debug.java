package org.cybergarage.multiscreenutil;

import android.util.Log;

/* loaded from: classes.dex */
public final class Debug {
    public static final int STACK_LEVEL = 5;
    public static final String TAG_PREFIX = "[HiMultiScreenHttpServer]";
    public static boolean enabled = false;

    public static final void on() {
        enabled = true;
    }

    public static final void off() {
        enabled = false;
    }

    public static boolean isOn() {
        return enabled;
    }

    public static final void message(String s) {
        if (enabled) {
            Log.v(TAG_PREFIX, s);
        }
    }

    public static final void message(String m1, String m2) {
        if (enabled) {
            Log.v(TAG_PREFIX, "http message:");
            Log.v(TAG_PREFIX, m1);
            Log.v(TAG_PREFIX, m2);
        }
    }

    public static final void warning(String s) {
        Log.v(TAG_PREFIX, s);
    }

    public static final void warning(String m, Exception e) {
        Log.v(TAG_PREFIX, "http warning:" + m + "(" + e.getMessage() + ")");
    }

    public static final void warning(Exception e) {
        warning(e.getMessage());
        e.printStackTrace();
    }
}
