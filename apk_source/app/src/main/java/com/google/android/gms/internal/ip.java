package com.google.android.gms.internal;

import android.text.TextUtils;
import android.util.Log;

/* loaded from: classes.dex */
public class ip {
    private static boolean GX = false;
    private boolean GY;
    private boolean GZ;
    private String Ha;
    private final String mTag;

    public ip(String str) {
        this(str, fT());
    }

    public ip(String str, boolean z) {
        this.mTag = str;
        this.GY = z;
        this.GZ = false;
    }

    private String e(String str, Object... objArr) {
        if (objArr.length != 0) {
            str = String.format(str, objArr);
        }
        return !TextUtils.isEmpty(this.Ha) ? this.Ha + str : str;
    }

    public static boolean fT() {
        return GX;
    }

    public void a(String str, Object... objArr) {
        if (fS()) {
            Log.v(this.mTag, e(str, objArr));
        }
    }

    public void a(Throwable th, String str, Object... objArr) {
        if (fR() || GX) {
            Log.d(this.mTag, e(str, objArr), th);
        }
    }

    public void aK(String str) {
        this.Ha = TextUtils.isEmpty(str) ? null : String.format("[%s] ", str);
    }

    public void b(String str, Object... objArr) {
        if (fR() || GX) {
            Log.d(this.mTag, e(str, objArr));
        }
    }

    public void c(String str, Object... objArr) {
        Log.i(this.mTag, e(str, objArr));
    }

    public void d(String str, Object... objArr) {
        Log.w(this.mTag, e(str, objArr));
    }

    public boolean fR() {
        return this.GY;
    }

    public boolean fS() {
        return this.GZ;
    }
}
