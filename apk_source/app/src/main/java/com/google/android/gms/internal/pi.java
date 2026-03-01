package com.google.android.gms.internal;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class pi {
    private static final pj awB = new pj();
    private boolean awC;
    private int[] awD;
    private pj[] awE;
    private int mSize;

    public pi() {
        this(10);
    }

    public pi(int i) {
        this.awC = false;
        int iIdealIntArraySize = idealIntArraySize(i);
        this.awD = new int[iIdealIntArraySize];
        this.awE = new pj[iIdealIntArraySize];
        this.mSize = 0;
    }

    private boolean a(int[] iArr, int[] iArr2, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            if (iArr[i2] != iArr2[i2]) {
                return false;
            }
        }
        return true;
    }

    private boolean a(pj[] pjVarArr, pj[] pjVarArr2, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            if (!pjVarArr[i2].equals(pjVarArr2[i2])) {
                return false;
            }
        }
        return true;
    }

    private int gF(int i) {
        int i2 = 0;
        int i3 = this.mSize - 1;
        while (i2 <= i3) {
            int i4 = (i2 + i3) >>> 1;
            int i5 = this.awD[i4];
            if (i5 < i) {
                i2 = i4 + 1;
            } else {
                if (i5 <= i) {
                    return i4;
                }
                i3 = i4 - 1;
            }
        }
        return i2 ^ (-1);
    }

    private void gc() {
        int i = this.mSize;
        int[] iArr = this.awD;
        pj[] pjVarArr = this.awE;
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            pj pjVar = pjVarArr[i3];
            if (pjVar != awB) {
                if (i3 != i2) {
                    iArr[i2] = iArr[i3];
                    pjVarArr[i2] = pjVar;
                    pjVarArr[i3] = null;
                }
                i2++;
            }
        }
        this.awC = false;
        this.mSize = i2;
    }

    private int idealByteArraySize(int need) {
        for (int i = 4; i < 32; i++) {
            if (need <= (1 << i) - 12) {
                return (1 << i) - 12;
            }
        }
        return need;
    }

    private int idealIntArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    public void a(int i, pj pjVar) {
        int iGF = gF(i);
        if (iGF >= 0) {
            this.awE[iGF] = pjVar;
            return;
        }
        int iGF2 = iGF ^ (-1);
        if (iGF2 < this.mSize && this.awE[iGF2] == awB) {
            this.awD[iGF2] = i;
            this.awE[iGF2] = pjVar;
            return;
        }
        if (this.awC && this.mSize >= this.awD.length) {
            gc();
            iGF2 = gF(i) ^ (-1);
        }
        if (this.mSize >= this.awD.length) {
            int iIdealIntArraySize = idealIntArraySize(this.mSize + 1);
            int[] iArr = new int[iIdealIntArraySize];
            pj[] pjVarArr = new pj[iIdealIntArraySize];
            System.arraycopy(this.awD, 0, iArr, 0, this.awD.length);
            System.arraycopy(this.awE, 0, pjVarArr, 0, this.awE.length);
            this.awD = iArr;
            this.awE = pjVarArr;
        }
        if (this.mSize - iGF2 != 0) {
            System.arraycopy(this.awD, iGF2, this.awD, iGF2 + 1, this.mSize - iGF2);
            System.arraycopy(this.awE, iGF2, this.awE, iGF2 + 1, this.mSize - iGF2);
        }
        this.awD[iGF2] = i;
        this.awE[iGF2] = pjVar;
        this.mSize++;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof pi)) {
            return false;
        }
        pi piVar = (pi) o;
        if (size() != piVar.size()) {
            return false;
        }
        return a(this.awD, piVar.awD, this.mSize) && a(this.awE, piVar.awE, this.mSize);
    }

    public pj gD(int i) {
        int iGF = gF(i);
        if (iGF < 0 || this.awE[iGF] == awB) {
            return null;
        }
        return this.awE[iGF];
    }

    public pj gE(int i) {
        if (this.awC) {
            gc();
        }
        return this.awE[i];
    }

    public int hashCode() {
        if (this.awC) {
            gc();
        }
        int iHashCode = 17;
        for (int i = 0; i < this.mSize; i++) {
            iHashCode = (((iHashCode * 31) + this.awD[i]) * 31) + this.awE[i].hashCode();
        }
        return iHashCode;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        if (this.awC) {
            gc();
        }
        return this.mSize;
    }
}
