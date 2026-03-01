package com.google.android.gms.internal;

/* loaded from: classes.dex */
public class pd {
    private final byte[] awl = new byte[256];
    private int awm;
    private int awn;

    public pd(byte[] bArr) {
        for (int i = 0; i < 256; i++) {
            this.awl[i] = (byte) i;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < 256; i3++) {
            i2 = (i2 + this.awl[i3] + bArr[i3 % bArr.length]) & 255;
            byte b = this.awl[i3];
            this.awl[i3] = this.awl[i2];
            this.awl[i2] = b;
        }
        this.awm = 0;
        this.awn = 0;
    }

    public void o(byte[] bArr) {
        int i = this.awm;
        int i2 = this.awn;
        for (int i3 = 0; i3 < bArr.length; i3++) {
            i = (i + 1) & 255;
            i2 = (i2 + this.awl[i]) & 255;
            byte b = this.awl[i];
            this.awl[i] = this.awl[i2];
            this.awl[i2] = b;
            bArr[i3] = (byte) (bArr[i3] ^ this.awl[(this.awl[i] + this.awl[i2]) & 255]);
        }
        this.awm = i;
        this.awn = i2;
    }
}
