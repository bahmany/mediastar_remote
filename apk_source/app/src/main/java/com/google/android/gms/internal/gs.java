package com.google.android.gms.internal;

import android.util.Log;
import com.google.ads.AdRequest;

@ez
/* loaded from: classes.dex */
public final class gs {
    public static void S(String str) {
        if (u(3)) {
            Log.d(AdRequest.LOGTAG, str);
        }
    }

    public static void T(String str) {
        if (u(6)) {
            Log.e(AdRequest.LOGTAG, str);
        }
    }

    public static void U(String str) {
        if (u(4)) {
            Log.i(AdRequest.LOGTAG, str);
        }
    }

    public static void V(String str) {
        if (u(2)) {
            Log.v(AdRequest.LOGTAG, str);
        }
    }

    public static void W(String str) {
        if (u(5)) {
            Log.w(AdRequest.LOGTAG, str);
        }
    }

    public static void a(String str, Throwable th) {
        if (u(3)) {
            Log.d(AdRequest.LOGTAG, str, th);
        }
    }

    public static void b(String str, Throwable th) {
        if (u(6)) {
            Log.e(AdRequest.LOGTAG, str, th);
        }
    }

    public static void c(String str, Throwable th) {
        if (u(4)) {
            Log.i(AdRequest.LOGTAG, str, th);
        }
    }

    public static void d(String str, Throwable th) {
        if (u(5)) {
            Log.w(AdRequest.LOGTAG, str, th);
        }
    }

    public static boolean u(int i) {
        return (i >= 5 || Log.isLoggable(AdRequest.LOGTAG, i)) && i != 2;
    }
}
