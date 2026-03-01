package com.google.android.gms.internal;

import android.content.Context;

/* loaded from: classes.dex */
public class ie {
    private final md<lw> Dh;
    private final Context mContext;

    private ie(Context context, md<lw> mdVar) {
        this.mContext = context;
        this.Dh = mdVar;
    }

    public static ie a(Context context, md<lw> mdVar) {
        return new ie(context, mdVar);
    }
}
