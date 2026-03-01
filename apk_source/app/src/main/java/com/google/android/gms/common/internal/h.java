package com.google.android.gms.common.internal;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.internal.ne;

/* loaded from: classes.dex */
public final class h {
    private final String LX;

    public h(String str) {
        this.LX = (String) n.i(str);
    }

    public void a(Context context, String str, String str2, Throwable th) {
        StackTraceElement[] stackTrace = th.getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stackTrace.length && i < 2; i++) {
            sb.append(stackTrace[i].toString());
            sb.append("\n");
        }
        ne neVar = new ne(context, 10);
        neVar.a("GMS_WTF", null, "GMS_WTF", sb.toString());
        neVar.send();
        if (aC(7)) {
            Log.e(str, str2, th);
            Log.wtf(str, str2, th);
        }
    }

    public void a(String str, String str2, Throwable th) {
        if (aC(4)) {
            Log.i(str, str2, th);
        }
    }

    public boolean aC(int i) {
        return Log.isLoggable(this.LX, i);
    }

    public void b(String str, String str2, Throwable th) {
        if (aC(5)) {
            Log.w(str, str2, th);
        }
    }

    public void c(String str, String str2, Throwable th) {
        if (aC(6)) {
            Log.e(str, str2, th);
        }
    }

    public void n(String str, String str2) {
        if (aC(3)) {
            Log.d(str, str2);
        }
    }

    public void o(String str, String str2) {
        if (aC(2)) {
            Log.v(str, str2);
        }
    }

    public void p(String str, String str2) {
        if (aC(5)) {
            Log.w(str, str2);
        }
    }

    public void q(String str, String str2) {
        if (aC(6)) {
            Log.e(str, str2);
        }
    }
}
