package com.google.android.gms.internal;

import java.io.IOException;

/* loaded from: classes.dex */
public abstract class pm {
    protected volatile int awJ = -1;

    public static final <T extends pm> T a(T t, byte[] bArr) throws pl {
        return (T) b(t, bArr, 0, bArr.length);
    }

    public static final void a(pm pmVar, byte[] bArr, int i, int i2) {
        try {
            pf pfVarB = pf.b(bArr, i, i2);
            pmVar.a(pfVarB);
            pfVarB.qw();
        } catch (IOException e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }

    public static final <T extends pm> T b(T t, byte[] bArr, int i, int i2) throws pl {
        try {
            pe peVarA = pe.a(bArr, i, i2);
            t.b(peVarA);
            peVarA.gl(0);
            return t;
        } catch (pl e) {
            throw e;
        } catch (IOException e2) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
        }
    }

    public static final byte[] f(pm pmVar) {
        byte[] bArr = new byte[pmVar.qG()];
        a(pmVar, bArr, 0, bArr.length);
        return bArr;
    }

    public void a(pf pfVar) throws IOException {
    }

    public abstract pm b(pe peVar) throws IOException;

    protected int c() {
        return 0;
    }

    public int qF() {
        if (this.awJ < 0) {
            qG();
        }
        return this.awJ;
    }

    public int qG() {
        int iC = c();
        this.awJ = iC;
        return iC;
    }

    public String toString() {
        return pn.g(this);
    }
}
