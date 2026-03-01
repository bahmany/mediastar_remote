package com.google.android.gms.internal;

import android.os.SystemClock;

/* loaded from: classes.dex */
public final class jw implements ju {
    private static jw MK;

    public static synchronized ju hA() {
        if (MK == null) {
            MK = new jw();
        }
        return MK;
    }

    @Override // com.google.android.gms.internal.ju
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override // com.google.android.gms.internal.ju
    public long elapsedRealtime() {
        return SystemClock.elapsedRealtime();
    }
}
