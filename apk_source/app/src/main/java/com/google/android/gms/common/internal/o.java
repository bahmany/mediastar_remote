package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import com.google.android.gms.common.internal.l;
import com.google.android.gms.dynamic.g;

/* loaded from: classes.dex */
public final class o extends com.google.android.gms.dynamic.g<l> {
    private static final o Ma = new o();

    private o() {
        super("com.google.android.gms.common.ui.SignInButtonCreatorImpl");
    }

    public static View b(Context context, int i, int i2) throws g.a {
        return Ma.c(context, i, i2);
    }

    private View c(Context context, int i, int i2) throws g.a {
        try {
            return (View) com.google.android.gms.dynamic.e.f(L(context).a(com.google.android.gms.dynamic.e.k(context), i, i2));
        } catch (Exception e) {
            throw new g.a("Could not get button with size " + i + " and color " + i2, e);
        }
    }

    @Override // com.google.android.gms.dynamic.g
    /* renamed from: S, reason: merged with bridge method [inline-methods] */
    public l d(IBinder iBinder) {
        return l.a.R(iBinder);
    }
}
