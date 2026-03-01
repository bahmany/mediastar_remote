package com.google.android.gms.internal;

import android.content.Context;
import android.content.SharedPreferences;

@ez
/* loaded from: classes.dex */
public final class gh {
    public static void a(Context context, boolean z) {
        SharedPreferences.Editor editorEdit = n(context).edit();
        editorEdit.putBoolean("use_https", z);
        editorEdit.commit();
    }

    private static SharedPreferences n(Context context) {
        return context.getSharedPreferences("admob", 0);
    }

    public static boolean o(Context context) {
        return n(context).getBoolean("use_https", true);
    }
}
