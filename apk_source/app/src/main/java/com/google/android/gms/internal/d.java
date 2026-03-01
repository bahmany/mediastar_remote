package com.google.android.gms.internal;

import java.io.IOException;

/* loaded from: classes.dex */
public interface d {

    public static final class a extends pg<a> {
        private static volatile a[] gu;
        public String gA;
        public long gB;
        public boolean gC;
        public a[] gD;
        public int[] gE;
        public boolean gF;
        public String gv;
        public a[] gw;
        public a[] gx;
        public a[] gy;
        public String gz;
        public int type;

        public a() {
            s();
        }

        public static a[] r() {
            if (gu == null) {
                synchronized (pk.awI) {
                    if (gu == null) {
                        gu = new a[0];
                    }
                }
            }
            return gu;
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            pfVar.s(1, this.type);
            if (!this.gv.equals("")) {
                pfVar.b(2, this.gv);
            }
            if (this.gw != null && this.gw.length > 0) {
                for (int i = 0; i < this.gw.length; i++) {
                    a aVar = this.gw[i];
                    if (aVar != null) {
                        pfVar.a(3, aVar);
                    }
                }
            }
            if (this.gx != null && this.gx.length > 0) {
                for (int i2 = 0; i2 < this.gx.length; i2++) {
                    a aVar2 = this.gx[i2];
                    if (aVar2 != null) {
                        pfVar.a(4, aVar2);
                    }
                }
            }
            if (this.gy != null && this.gy.length > 0) {
                for (int i3 = 0; i3 < this.gy.length; i3++) {
                    a aVar3 = this.gy[i3];
                    if (aVar3 != null) {
                        pfVar.a(5, aVar3);
                    }
                }
            }
            if (!this.gz.equals("")) {
                pfVar.b(6, this.gz);
            }
            if (!this.gA.equals("")) {
                pfVar.b(7, this.gA);
            }
            if (this.gB != 0) {
                pfVar.b(8, this.gB);
            }
            if (this.gF) {
                pfVar.b(9, this.gF);
            }
            if (this.gE != null && this.gE.length > 0) {
                for (int i4 = 0; i4 < this.gE.length; i4++) {
                    pfVar.s(10, this.gE[i4]);
                }
            }
            if (this.gD != null && this.gD.length > 0) {
                for (int i5 = 0; i5 < this.gD.length; i5++) {
                    a aVar4 = this.gD[i5];
                    if (aVar4 != null) {
                        pfVar.a(11, aVar4);
                    }
                }
            }
            if (this.gC) {
                pfVar.b(12, this.gC);
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iC = super.c() + pf.u(1, this.type);
            if (!this.gv.equals("")) {
                iC += pf.j(2, this.gv);
            }
            if (this.gw != null && this.gw.length > 0) {
                int iC2 = iC;
                for (int i = 0; i < this.gw.length; i++) {
                    a aVar = this.gw[i];
                    if (aVar != null) {
                        iC2 += pf.c(3, aVar);
                    }
                }
                iC = iC2;
            }
            if (this.gx != null && this.gx.length > 0) {
                int iC3 = iC;
                for (int i2 = 0; i2 < this.gx.length; i2++) {
                    a aVar2 = this.gx[i2];
                    if (aVar2 != null) {
                        iC3 += pf.c(4, aVar2);
                    }
                }
                iC = iC3;
            }
            if (this.gy != null && this.gy.length > 0) {
                int iC4 = iC;
                for (int i3 = 0; i3 < this.gy.length; i3++) {
                    a aVar3 = this.gy[i3];
                    if (aVar3 != null) {
                        iC4 += pf.c(5, aVar3);
                    }
                }
                iC = iC4;
            }
            if (!this.gz.equals("")) {
                iC += pf.j(6, this.gz);
            }
            if (!this.gA.equals("")) {
                iC += pf.j(7, this.gA);
            }
            if (this.gB != 0) {
                iC += pf.d(8, this.gB);
            }
            if (this.gF) {
                iC += pf.c(9, this.gF);
            }
            if (this.gE != null && this.gE.length > 0) {
                int iGv = 0;
                for (int i4 = 0; i4 < this.gE.length; i4++) {
                    iGv += pf.gv(this.gE[i4]);
                }
                iC = iC + iGv + (this.gE.length * 1);
            }
            if (this.gD != null && this.gD.length > 0) {
                for (int i5 = 0; i5 < this.gD.length; i5++) {
                    a aVar4 = this.gD[i5];
                    if (aVar4 != null) {
                        iC += pf.c(11, aVar4);
                    }
                }
            }
            return this.gC ? iC + pf.c(12, this.gC) : iC;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof a)) {
                return false;
            }
            a aVar = (a) o;
            if (this.type != aVar.type) {
                return false;
            }
            if (this.gv == null) {
                if (aVar.gv != null) {
                    return false;
                }
            } else if (!this.gv.equals(aVar.gv)) {
                return false;
            }
            if (!pk.equals(this.gw, aVar.gw) || !pk.equals(this.gx, aVar.gx) || !pk.equals(this.gy, aVar.gy)) {
                return false;
            }
            if (this.gz == null) {
                if (aVar.gz != null) {
                    return false;
                }
            } else if (!this.gz.equals(aVar.gz)) {
                return false;
            }
            if (this.gA == null) {
                if (aVar.gA != null) {
                    return false;
                }
            } else if (!this.gA.equals(aVar.gA)) {
                return false;
            }
            if (this.gB == aVar.gB && this.gC == aVar.gC && pk.equals(this.gD, aVar.gD) && pk.equals(this.gE, aVar.gE) && this.gF == aVar.gF) {
                return a(aVar);
            }
            return false;
        }

        public int hashCode() {
            return (((((((((this.gC ? 1231 : 1237) + (((((((this.gz == null ? 0 : this.gz.hashCode()) + (((((((((this.gv == null ? 0 : this.gv.hashCode()) + ((this.type + 527) * 31)) * 31) + pk.hashCode(this.gw)) * 31) + pk.hashCode(this.gx)) * 31) + pk.hashCode(this.gy)) * 31)) * 31) + (this.gA != null ? this.gA.hashCode() : 0)) * 31) + ((int) (this.gB ^ (this.gB >>> 32)))) * 31)) * 31) + pk.hashCode(this.gD)) * 31) + pk.hashCode(this.gE)) * 31) + (this.gF ? 1231 : 1237)) * 31) + qx();
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: l */
        public a b(pe peVar) throws IOException {
            int i;
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 8:
                        int iQj = peVar.qj();
                        switch (iQj) {
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                                this.type = iQj;
                                break;
                        }
                    case 18:
                        this.gv = peVar.readString();
                        break;
                    case 26:
                        int iB = pp.b(peVar, 26);
                        int length = this.gw == null ? 0 : this.gw.length;
                        a[] aVarArr = new a[iB + length];
                        if (length != 0) {
                            System.arraycopy(this.gw, 0, aVarArr, 0, length);
                        }
                        while (length < aVarArr.length - 1) {
                            aVarArr[length] = new a();
                            peVar.a(aVarArr[length]);
                            peVar.qg();
                            length++;
                        }
                        aVarArr[length] = new a();
                        peVar.a(aVarArr[length]);
                        this.gw = aVarArr;
                        break;
                    case 34:
                        int iB2 = pp.b(peVar, 34);
                        int length2 = this.gx == null ? 0 : this.gx.length;
                        a[] aVarArr2 = new a[iB2 + length2];
                        if (length2 != 0) {
                            System.arraycopy(this.gx, 0, aVarArr2, 0, length2);
                        }
                        while (length2 < aVarArr2.length - 1) {
                            aVarArr2[length2] = new a();
                            peVar.a(aVarArr2[length2]);
                            peVar.qg();
                            length2++;
                        }
                        aVarArr2[length2] = new a();
                        peVar.a(aVarArr2[length2]);
                        this.gx = aVarArr2;
                        break;
                    case 42:
                        int iB3 = pp.b(peVar, 42);
                        int length3 = this.gy == null ? 0 : this.gy.length;
                        a[] aVarArr3 = new a[iB3 + length3];
                        if (length3 != 0) {
                            System.arraycopy(this.gy, 0, aVarArr3, 0, length3);
                        }
                        while (length3 < aVarArr3.length - 1) {
                            aVarArr3[length3] = new a();
                            peVar.a(aVarArr3[length3]);
                            peVar.qg();
                            length3++;
                        }
                        aVarArr3[length3] = new a();
                        peVar.a(aVarArr3[length3]);
                        this.gy = aVarArr3;
                        break;
                    case 50:
                        this.gz = peVar.readString();
                        break;
                    case 58:
                        this.gA = peVar.readString();
                        break;
                    case 64:
                        this.gB = peVar.qi();
                        break;
                    case 72:
                        this.gF = peVar.qk();
                        break;
                    case 80:
                        int iB4 = pp.b(peVar, 80);
                        int[] iArr = new int[iB4];
                        int i2 = 0;
                        int i3 = 0;
                        while (i2 < iB4) {
                            if (i2 != 0) {
                                peVar.qg();
                            }
                            int iQj2 = peVar.qj();
                            switch (iQj2) {
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                case 10:
                                case 11:
                                case 12:
                                case 13:
                                case 14:
                                case 15:
                                case 16:
                                case 17:
                                    i = i3 + 1;
                                    iArr[i3] = iQj2;
                                    break;
                                default:
                                    i = i3;
                                    break;
                            }
                            i2++;
                            i3 = i;
                        }
                        if (i3 != 0) {
                            int length4 = this.gE == null ? 0 : this.gE.length;
                            if (length4 != 0 || i3 != iArr.length) {
                                int[] iArr2 = new int[length4 + i3];
                                if (length4 != 0) {
                                    System.arraycopy(this.gE, 0, iArr2, 0, length4);
                                }
                                System.arraycopy(iArr, 0, iArr2, length4, i3);
                                this.gE = iArr2;
                                break;
                            } else {
                                this.gE = iArr;
                                break;
                            }
                        } else {
                            break;
                        }
                        break;
                    case 82:
                        int iGo = peVar.go(peVar.qn());
                        int position = peVar.getPosition();
                        int i4 = 0;
                        while (peVar.qs() > 0) {
                            switch (peVar.qj()) {
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                case 10:
                                case 11:
                                case 12:
                                case 13:
                                case 14:
                                case 15:
                                case 16:
                                case 17:
                                    i4++;
                                    break;
                            }
                        }
                        if (i4 != 0) {
                            peVar.gq(position);
                            int length5 = this.gE == null ? 0 : this.gE.length;
                            int[] iArr3 = new int[i4 + length5];
                            if (length5 != 0) {
                                System.arraycopy(this.gE, 0, iArr3, 0, length5);
                            }
                            while (peVar.qs() > 0) {
                                int iQj3 = peVar.qj();
                                switch (iQj3) {
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4:
                                    case 5:
                                    case 6:
                                    case 7:
                                    case 8:
                                    case 9:
                                    case 10:
                                    case 11:
                                    case 12:
                                    case 13:
                                    case 14:
                                    case 15:
                                    case 16:
                                    case 17:
                                        iArr3[length5] = iQj3;
                                        length5++;
                                        break;
                                }
                            }
                            this.gE = iArr3;
                        }
                        peVar.gp(iGo);
                        break;
                    case 90:
                        int iB5 = pp.b(peVar, 90);
                        int length6 = this.gD == null ? 0 : this.gD.length;
                        a[] aVarArr4 = new a[iB5 + length6];
                        if (length6 != 0) {
                            System.arraycopy(this.gD, 0, aVarArr4, 0, length6);
                        }
                        while (length6 < aVarArr4.length - 1) {
                            aVarArr4[length6] = new a();
                            peVar.a(aVarArr4[length6]);
                            peVar.qg();
                            length6++;
                        }
                        aVarArr4[length6] = new a();
                        peVar.a(aVarArr4[length6]);
                        this.gD = aVarArr4;
                        break;
                    case 96:
                        this.gC = peVar.qk();
                        break;
                    default:
                        if (!a(peVar, iQg)) {
                            break;
                        } else {
                            break;
                        }
                }
            }
            return this;
        }

        public a s() {
            this.type = 1;
            this.gv = "";
            this.gw = r();
            this.gx = r();
            this.gy = r();
            this.gz = "";
            this.gA = "";
            this.gB = 0L;
            this.gC = false;
            this.gD = r();
            this.gE = pp.awL;
            this.gF = false;
            this.awy = null;
            this.awJ = -1;
            return this;
        }
    }
}
