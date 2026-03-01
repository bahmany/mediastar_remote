package com.google.android.gms.games.internal;

import com.google.android.gms.common.internal.c;
import com.google.android.gms.internal.jx;

/* loaded from: classes.dex */
public abstract class GamesDowngradeableSafeParcel extends c {
    protected static boolean c(Integer num) {
        if (num == null) {
            return false;
        }
        return jx.aQ(num.intValue());
    }
}
