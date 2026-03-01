package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
class pj {
    private ph<?, ?> awF;
    private Object awG;
    private List<po> awH = new ArrayList();

    pj() {
    }

    private byte[] toByteArray() throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        byte[] bArr = new byte[c()];
        a(pf.q(bArr));
        return bArr;
    }

    void a(pf pfVar) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (this.awG != null) {
            this.awF.a(this.awG, pfVar);
            return;
        }
        Iterator<po> it = this.awH.iterator();
        while (it.hasNext()) {
            it.next().a(pfVar);
        }
    }

    void a(po poVar) {
        this.awH.add(poVar);
    }

    /* JADX WARN: Multi-variable type inference failed */
    <T> T b(ph<?, T> phVar) {
        if (this.awG == null) {
            this.awF = phVar;
            this.awG = phVar.l(this.awH);
            this.awH = null;
        } else if (this.awF != phVar) {
            throw new IllegalStateException("Tried to getExtension with a differernt Extension.");
        }
        return (T) this.awG;
    }

    int c() {
        int iC = 0;
        if (this.awG != null) {
            return this.awF.A(this.awG);
        }
        Iterator<po> it = this.awH.iterator();
        while (true) {
            int i = iC;
            if (!it.hasNext()) {
                return i;
            }
            iC = it.next().c() + i;
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof pj)) {
            return false;
        }
        pj pjVar = (pj) o;
        if (this.awG != null && pjVar.awG != null) {
            if (this.awF == pjVar.awF) {
                return !this.awF.awz.isArray() ? this.awG.equals(pjVar.awG) : this.awG instanceof byte[] ? Arrays.equals((byte[]) this.awG, (byte[]) pjVar.awG) : this.awG instanceof int[] ? Arrays.equals((int[]) this.awG, (int[]) pjVar.awG) : this.awG instanceof long[] ? Arrays.equals((long[]) this.awG, (long[]) pjVar.awG) : this.awG instanceof float[] ? Arrays.equals((float[]) this.awG, (float[]) pjVar.awG) : this.awG instanceof double[] ? Arrays.equals((double[]) this.awG, (double[]) pjVar.awG) : this.awG instanceof boolean[] ? Arrays.equals((boolean[]) this.awG, (boolean[]) pjVar.awG) : Arrays.deepEquals((Object[]) this.awG, (Object[]) pjVar.awG);
            }
            return false;
        }
        if (this.awH != null && pjVar.awH != null) {
            return this.awH.equals(pjVar.awH);
        }
        try {
            return Arrays.equals(toByteArray(), pjVar.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public int hashCode() {
        try {
            return Arrays.hashCode(toByteArray()) + 527;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
