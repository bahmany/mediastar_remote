package com.google.android.gms.internal;

import java.io.IOException;

/* loaded from: classes.dex */
class p implements n {
    private pf kY;
    private byte[] kZ;
    private final int la;

    public p(int i) {
        this.la = i;
        reset();
    }

    @Override // com.google.android.gms.internal.n
    public byte[] A() throws IOException {
        int iQv = this.kY.qv();
        if (iQv < 0) {
            throw new IOException();
        }
        if (iQv == 0) {
            return this.kZ;
        }
        byte[] bArr = new byte[this.kZ.length - iQv];
        System.arraycopy(this.kZ, 0, bArr, 0, bArr.length);
        return bArr;
    }

    @Override // com.google.android.gms.internal.n
    public void b(int i, long j) throws IOException {
        this.kY.b(i, j);
    }

    @Override // com.google.android.gms.internal.n
    public void b(int i, String str) throws IOException {
        this.kY.b(i, str);
    }

    @Override // com.google.android.gms.internal.n
    public void reset() {
        this.kZ = new byte[this.la];
        this.kY = pf.q(this.kZ);
    }
}
