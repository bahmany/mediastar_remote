package com.google.android.gms.internal;

import android.content.Context;

/* loaded from: classes.dex */
public class lo {
    private final String Dd;
    private final md<lw> Dh;
    private final String IH;
    private lp aep = null;
    private final Context mContext;

    private lo(Context context, String str, String str2, md<lw> mdVar) {
        this.mContext = context;
        this.Dd = str;
        this.Dh = mdVar;
        this.IH = str2;
    }

    public static lo a(Context context, String str, String str2, md<lw> mdVar) {
        return new lo(context, str, str2, mdVar);
    }
}
