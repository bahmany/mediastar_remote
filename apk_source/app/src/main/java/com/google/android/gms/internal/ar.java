package com.google.android.gms.internal;

import java.security.MessageDigest;

/* loaded from: classes.dex */
public class ar extends ao {
    private MessageDigest nP;

    byte[] a(String[] strArr) {
        byte[] bArr = new byte[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            bArr[i] = (byte) (aq.o(strArr[i]) & 255);
        }
        return bArr;
    }

    @Override // com.google.android.gms.internal.ao
    public byte[] l(String str) {
        byte[] bArr;
        byte[] bArrA = a(str.split(" "));
        this.nP = ba();
        synchronized (this.mw) {
            if (this.nP == null) {
                bArr = new byte[0];
            } else {
                this.nP.reset();
                this.nP.update(bArrA);
                byte[] bArrDigest = this.nP.digest();
                bArr = new byte[bArrDigest.length <= 4 ? bArrDigest.length : 4];
                System.arraycopy(bArrDigest, 0, bArr, 0, bArr.length);
            }
        }
        return bArr;
    }
}
