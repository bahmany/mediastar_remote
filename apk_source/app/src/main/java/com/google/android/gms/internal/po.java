package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

/* loaded from: classes.dex */
final class po {
    final byte[] awK;
    final int tag;

    po(int i, byte[] bArr) {
        this.tag = i;
        this.awK = bArr;
    }

    void a(pf pfVar) throws IOException {
        pfVar.gz(this.tag);
        pfVar.t(this.awK);
    }

    int c() {
        return 0 + pf.gA(this.tag) + this.awK.length;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof po)) {
            return false;
        }
        po poVar = (po) o;
        return this.tag == poVar.tag && Arrays.equals(this.awK, poVar.awK);
    }

    public int hashCode() {
        return ((this.tag + 527) * 31) + Arrays.hashCode(this.awK);
    }
}
