package com.google.android.gms.internal;

import android.content.Context;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class jt {
    private static Pattern MJ = null;

    public static boolean K(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.type.watch");
    }

    public static int aN(int i) {
        return i / 1000;
    }

    public static int aO(int i) {
        return (i % 1000) / 100;
    }

    public static boolean aP(int i) {
        return aO(i) == 3;
    }
}
