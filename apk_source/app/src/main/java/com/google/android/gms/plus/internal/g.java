package com.google.android.gms.plus.internal;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import com.google.android.gms.plus.PlusOneDummyView;
import com.google.android.gms.plus.internal.c;

/* loaded from: classes.dex */
public final class g extends com.google.android.gms.dynamic.g<c> {
    private static final g alr = new g();

    private g() {
        super("com.google.android.gms.plus.plusone.PlusOneButtonCreatorImpl");
    }

    public static View a(Context context, int i, int i2, String str, int i3) {
        try {
            if (str == null) {
                throw new NullPointerException();
            }
            return (View) com.google.android.gms.dynamic.e.f(alr.L(context).a(com.google.android.gms.dynamic.e.k(context), i, i2, str, i3));
        } catch (Exception e) {
            return new PlusOneDummyView(context, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.dynamic.g
    /* renamed from: bI, reason: merged with bridge method [inline-methods] */
    public c d(IBinder iBinder) {
        return c.a.bF(iBinder);
    }
}
