package com.google.android.gms.internal;

import java.io.IOException;

/* loaded from: classes.dex */
public final class pp {
    public static final int[] awL = new int[0];
    public static final long[] awM = new long[0];
    public static final float[] awN = new float[0];
    public static final double[] awO = new double[0];
    public static final boolean[] awP = new boolean[0];
    public static final String[] awQ = new String[0];
    public static final byte[][] awR = new byte[0][];
    public static final byte[] awS = new byte[0];

    public static final int b(pe peVar, int i) throws IOException {
        int i2 = 1;
        int position = peVar.getPosition();
        peVar.gm(i);
        while (peVar.qg() == i) {
            peVar.gm(i);
            i2++;
        }
        peVar.gq(position);
        return i2;
    }

    static int gG(int i) {
        return i & 7;
    }

    public static int gH(int i) {
        return i >>> 3;
    }

    static int x(int i, int i2) {
        return (i << 3) | i2;
    }
}
