package com.google.android.gms.internal;

import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.io.IOException;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class pc extends pg<pc> {
    public a[] avS;

    public static final class a extends pg<a> {
        private static volatile a[] avT;
        public C0092a avU;
        public String name;

        /* renamed from: com.google.android.gms.internal.pc$a$a */
        public static final class C0092a extends pg<C0092a> {
            private static volatile C0092a[] avV;
            public C0093a avW;
            public int type;

            /* renamed from: com.google.android.gms.internal.pc$a$a$a */
            public static final class C0093a extends pg<C0093a> {
                public byte[] avX;
                public String avY;
                public double avZ;
                public float awa;
                public long awb;
                public int awc;
                public int awd;
                public boolean awe;
                public a[] awf;
                public C0092a[] awg;
                public String[] awh;
                public long[] awi;
                public float[] awj;
                public long awk;

                public C0093a() {
                    qf();
                }

                @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
                public void a(pf pfVar) throws IOException {
                    if (!Arrays.equals(this.avX, pp.awS)) {
                        pfVar.a(1, this.avX);
                    }
                    if (!this.avY.equals("")) {
                        pfVar.b(2, this.avY);
                    }
                    if (Double.doubleToLongBits(this.avZ) != Double.doubleToLongBits(0.0d)) {
                        pfVar.a(3, this.avZ);
                    }
                    if (Float.floatToIntBits(this.awa) != Float.floatToIntBits(0.0f)) {
                        pfVar.b(4, this.awa);
                    }
                    if (this.awb != 0) {
                        pfVar.b(5, this.awb);
                    }
                    if (this.awc != 0) {
                        pfVar.s(6, this.awc);
                    }
                    if (this.awd != 0) {
                        pfVar.t(7, this.awd);
                    }
                    if (this.awe) {
                        pfVar.b(8, this.awe);
                    }
                    if (this.awf != null && this.awf.length > 0) {
                        for (int i = 0; i < this.awf.length; i++) {
                            a aVar = this.awf[i];
                            if (aVar != null) {
                                pfVar.a(9, aVar);
                            }
                        }
                    }
                    if (this.awg != null && this.awg.length > 0) {
                        for (int i2 = 0; i2 < this.awg.length; i2++) {
                            C0092a c0092a = this.awg[i2];
                            if (c0092a != null) {
                                pfVar.a(10, c0092a);
                            }
                        }
                    }
                    if (this.awh != null && this.awh.length > 0) {
                        for (int i3 = 0; i3 < this.awh.length; i3++) {
                            String str = this.awh[i3];
                            if (str != null) {
                                pfVar.b(11, str);
                            }
                        }
                    }
                    if (this.awi != null && this.awi.length > 0) {
                        for (int i4 = 0; i4 < this.awi.length; i4++) {
                            pfVar.b(12, this.awi[i4]);
                        }
                    }
                    if (this.awk != 0) {
                        pfVar.b(13, this.awk);
                    }
                    if (this.awj != null && this.awj.length > 0) {
                        for (int i5 = 0; i5 < this.awj.length; i5++) {
                            pfVar.b(14, this.awj[i5]);
                        }
                    }
                    super.a(pfVar);
                }

                @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
                protected int c() {
                    int iC = super.c();
                    if (!Arrays.equals(this.avX, pp.awS)) {
                        iC += pf.b(1, this.avX);
                    }
                    if (!this.avY.equals("")) {
                        iC += pf.j(2, this.avY);
                    }
                    if (Double.doubleToLongBits(this.avZ) != Double.doubleToLongBits(0.0d)) {
                        iC += pf.b(3, this.avZ);
                    }
                    if (Float.floatToIntBits(this.awa) != Float.floatToIntBits(0.0f)) {
                        iC += pf.c(4, this.awa);
                    }
                    if (this.awb != 0) {
                        iC += pf.d(5, this.awb);
                    }
                    if (this.awc != 0) {
                        iC += pf.u(6, this.awc);
                    }
                    if (this.awd != 0) {
                        iC += pf.v(7, this.awd);
                    }
                    if (this.awe) {
                        iC += pf.c(8, this.awe);
                    }
                    if (this.awf != null && this.awf.length > 0) {
                        int iC2 = iC;
                        for (int i = 0; i < this.awf.length; i++) {
                            a aVar = this.awf[i];
                            if (aVar != null) {
                                iC2 += pf.c(9, aVar);
                            }
                        }
                        iC = iC2;
                    }
                    if (this.awg != null && this.awg.length > 0) {
                        int iC3 = iC;
                        for (int i2 = 0; i2 < this.awg.length; i2++) {
                            C0092a c0092a = this.awg[i2];
                            if (c0092a != null) {
                                iC3 += pf.c(10, c0092a);
                            }
                        }
                        iC = iC3;
                    }
                    if (this.awh != null && this.awh.length > 0) {
                        int iDf = 0;
                        int i3 = 0;
                        for (int i4 = 0; i4 < this.awh.length; i4++) {
                            String str = this.awh[i4];
                            if (str != null) {
                                i3++;
                                iDf += pf.df(str);
                            }
                        }
                        iC = iC + iDf + (i3 * 1);
                    }
                    if (this.awi != null && this.awi.length > 0) {
                        int iD = 0;
                        for (int i5 = 0; i5 < this.awi.length; i5++) {
                            iD += pf.D(this.awi[i5]);
                        }
                        iC = iC + iD + (this.awi.length * 1);
                    }
                    if (this.awk != 0) {
                        iC += pf.d(13, this.awk);
                    }
                    return (this.awj == null || this.awj.length <= 0) ? iC : iC + (this.awj.length * 4) + (this.awj.length * 1);
                }

                public boolean equals(Object o) {
                    if (o == this) {
                        return true;
                    }
                    if (!(o instanceof C0093a)) {
                        return false;
                    }
                    C0093a c0093a = (C0093a) o;
                    if (!Arrays.equals(this.avX, c0093a.avX)) {
                        return false;
                    }
                    if (this.avY == null) {
                        if (c0093a.avY != null) {
                            return false;
                        }
                    } else if (!this.avY.equals(c0093a.avY)) {
                        return false;
                    }
                    if (Double.doubleToLongBits(this.avZ) == Double.doubleToLongBits(c0093a.avZ) && Float.floatToIntBits(this.awa) == Float.floatToIntBits(c0093a.awa) && this.awb == c0093a.awb && this.awc == c0093a.awc && this.awd == c0093a.awd && this.awe == c0093a.awe && pk.equals(this.awf, c0093a.awf) && pk.equals(this.awg, c0093a.awg) && pk.equals(this.awh, c0093a.awh) && pk.equals(this.awi, c0093a.awi) && pk.equals(this.awj, c0093a.awj) && this.awk == c0093a.awk) {
                        return a(c0093a);
                    }
                    return false;
                }

                public int hashCode() {
                    int iHashCode = (Arrays.hashCode(this.avX) + 527) * 31;
                    int iHashCode2 = this.avY == null ? 0 : this.avY.hashCode();
                    long jDoubleToLongBits = Double.doubleToLongBits(this.avZ);
                    return (((((((((((((((this.awe ? 1231 : 1237) + ((((((((((((iHashCode2 + iHashCode) * 31) + ((int) (jDoubleToLongBits ^ (jDoubleToLongBits >>> 32)))) * 31) + Float.floatToIntBits(this.awa)) * 31) + ((int) (this.awb ^ (this.awb >>> 32)))) * 31) + this.awc) * 31) + this.awd) * 31)) * 31) + pk.hashCode(this.awf)) * 31) + pk.hashCode(this.awg)) * 31) + pk.hashCode(this.awh)) * 31) + pk.hashCode(this.awi)) * 31) + pk.hashCode(this.awj)) * 31) + ((int) (this.awk ^ (this.awk >>> 32)))) * 31) + qx();
                }

                public C0093a qf() {
                    this.avX = pp.awS;
                    this.avY = "";
                    this.avZ = 0.0d;
                    this.awa = 0.0f;
                    this.awb = 0L;
                    this.awc = 0;
                    this.awd = 0;
                    this.awe = false;
                    this.awf = a.qb();
                    this.awg = C0092a.qd();
                    this.awh = pp.awQ;
                    this.awi = pp.awM;
                    this.awj = pp.awN;
                    this.awk = 0L;
                    this.awy = null;
                    this.awJ = -1;
                    return this;
                }

                @Override // com.google.android.gms.internal.pm
                /* renamed from: t */
                public C0093a b(pe peVar) throws IOException {
                    while (true) {
                        int iQg = peVar.qg();
                        switch (iQg) {
                            case 0:
                                break;
                            case 10:
                                this.avX = peVar.readBytes();
                                break;
                            case 18:
                                this.avY = peVar.readString();
                                break;
                            case 25:
                                this.avZ = peVar.readDouble();
                                break;
                            case 37:
                                this.awa = peVar.readFloat();
                                break;
                            case 40:
                                this.awb = peVar.qi();
                                break;
                            case 48:
                                this.awc = peVar.qj();
                                break;
                            case 56:
                                this.awd = peVar.ql();
                                break;
                            case 64:
                                this.awe = peVar.qk();
                                break;
                            case 74:
                                int iB = pp.b(peVar, 74);
                                int length = this.awf == null ? 0 : this.awf.length;
                                a[] aVarArr = new a[iB + length];
                                if (length != 0) {
                                    System.arraycopy(this.awf, 0, aVarArr, 0, length);
                                }
                                while (length < aVarArr.length - 1) {
                                    aVarArr[length] = new a();
                                    peVar.a(aVarArr[length]);
                                    peVar.qg();
                                    length++;
                                }
                                aVarArr[length] = new a();
                                peVar.a(aVarArr[length]);
                                this.awf = aVarArr;
                                break;
                            case 82:
                                int iB2 = pp.b(peVar, 82);
                                int length2 = this.awg == null ? 0 : this.awg.length;
                                C0092a[] c0092aArr = new C0092a[iB2 + length2];
                                if (length2 != 0) {
                                    System.arraycopy(this.awg, 0, c0092aArr, 0, length2);
                                }
                                while (length2 < c0092aArr.length - 1) {
                                    c0092aArr[length2] = new C0092a();
                                    peVar.a(c0092aArr[length2]);
                                    peVar.qg();
                                    length2++;
                                }
                                c0092aArr[length2] = new C0092a();
                                peVar.a(c0092aArr[length2]);
                                this.awg = c0092aArr;
                                break;
                            case 90:
                                int iB3 = pp.b(peVar, 90);
                                int length3 = this.awh == null ? 0 : this.awh.length;
                                String[] strArr = new String[iB3 + length3];
                                if (length3 != 0) {
                                    System.arraycopy(this.awh, 0, strArr, 0, length3);
                                }
                                while (length3 < strArr.length - 1) {
                                    strArr[length3] = peVar.readString();
                                    peVar.qg();
                                    length3++;
                                }
                                strArr[length3] = peVar.readString();
                                this.awh = strArr;
                                break;
                            case 96:
                                int iB4 = pp.b(peVar, 96);
                                int length4 = this.awi == null ? 0 : this.awi.length;
                                long[] jArr = new long[iB4 + length4];
                                if (length4 != 0) {
                                    System.arraycopy(this.awi, 0, jArr, 0, length4);
                                }
                                while (length4 < jArr.length - 1) {
                                    jArr[length4] = peVar.qi();
                                    peVar.qg();
                                    length4++;
                                }
                                jArr[length4] = peVar.qi();
                                this.awi = jArr;
                                break;
                            case 98:
                                int iGo = peVar.go(peVar.qn());
                                int position = peVar.getPosition();
                                int i = 0;
                                while (peVar.qs() > 0) {
                                    peVar.qi();
                                    i++;
                                }
                                peVar.gq(position);
                                int length5 = this.awi == null ? 0 : this.awi.length;
                                long[] jArr2 = new long[i + length5];
                                if (length5 != 0) {
                                    System.arraycopy(this.awi, 0, jArr2, 0, length5);
                                }
                                while (length5 < jArr2.length) {
                                    jArr2[length5] = peVar.qi();
                                    length5++;
                                }
                                this.awi = jArr2;
                                peVar.gp(iGo);
                                break;
                            case 104:
                                this.awk = peVar.qi();
                                break;
                            case KeyInfo.KEYCODE_VOLUME_DOWN /* 114 */:
                                int iQn = peVar.qn();
                                int iGo2 = peVar.go(iQn);
                                int i2 = iQn / 4;
                                int length6 = this.awj == null ? 0 : this.awj.length;
                                float[] fArr = new float[i2 + length6];
                                if (length6 != 0) {
                                    System.arraycopy(this.awj, 0, fArr, 0, length6);
                                }
                                while (length6 < fArr.length) {
                                    fArr[length6] = peVar.readFloat();
                                    length6++;
                                }
                                this.awj = fArr;
                                peVar.gp(iGo2);
                                break;
                            case 117:
                                int iB5 = pp.b(peVar, 117);
                                int length7 = this.awj == null ? 0 : this.awj.length;
                                float[] fArr2 = new float[iB5 + length7];
                                if (length7 != 0) {
                                    System.arraycopy(this.awj, 0, fArr2, 0, length7);
                                }
                                while (length7 < fArr2.length - 1) {
                                    fArr2[length7] = peVar.readFloat();
                                    peVar.qg();
                                    length7++;
                                }
                                fArr2[length7] = peVar.readFloat();
                                this.awj = fArr2;
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
            }

            public C0092a() {
                qe();
            }

            public static C0092a[] qd() {
                if (avV == null) {
                    synchronized (pk.awI) {
                        if (avV == null) {
                            avV = new C0092a[0];
                        }
                    }
                }
                return avV;
            }

            @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
            public void a(pf pfVar) throws IOException {
                pfVar.s(1, this.type);
                if (this.avW != null) {
                    pfVar.a(2, this.avW);
                }
                super.a(pfVar);
            }

            @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
            protected int c() {
                int iC = super.c() + pf.u(1, this.type);
                return this.avW != null ? iC + pf.c(2, this.avW) : iC;
            }

            public boolean equals(Object o) {
                if (o == this) {
                    return true;
                }
                if (!(o instanceof C0092a)) {
                    return false;
                }
                C0092a c0092a = (C0092a) o;
                if (this.type != c0092a.type) {
                    return false;
                }
                if (this.avW == null) {
                    if (c0092a.avW != null) {
                        return false;
                    }
                } else if (!this.avW.equals(c0092a.avW)) {
                    return false;
                }
                return a(c0092a);
            }

            public int hashCode() {
                return (((this.avW == null ? 0 : this.avW.hashCode()) + ((this.type + 527) * 31)) * 31) + qx();
            }

            public C0092a qe() {
                this.type = 1;
                this.avW = null;
                this.awy = null;
                this.awJ = -1;
                return this;
            }

            @Override // com.google.android.gms.internal.pm
            /* renamed from: s */
            public C0092a b(pe peVar) throws IOException {
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
                                case 9:
                                case 10:
                                case 11:
                                case 12:
                                case 13:
                                case 14:
                                case 15:
                                    this.type = iQj;
                                    break;
                            }
                        case 18:
                            if (this.avW == null) {
                                this.avW = new C0093a();
                            }
                            peVar.a(this.avW);
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
        }

        public a() {
            qc();
        }

        public static a[] qb() {
            if (avT == null) {
                synchronized (pk.awI) {
                    if (avT == null) {
                        avT = new a[0];
                    }
                }
            }
            return avT;
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            pfVar.b(1, this.name);
            if (this.avU != null) {
                pfVar.a(2, this.avU);
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iC = super.c() + pf.j(1, this.name);
            return this.avU != null ? iC + pf.c(2, this.avU) : iC;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof a)) {
                return false;
            }
            a aVar = (a) o;
            if (this.name == null) {
                if (aVar.name != null) {
                    return false;
                }
            } else if (!this.name.equals(aVar.name)) {
                return false;
            }
            if (this.avU == null) {
                if (aVar.avU != null) {
                    return false;
                }
            } else if (!this.avU.equals(aVar.avU)) {
                return false;
            }
            return a(aVar);
        }

        public int hashCode() {
            return (((((this.name == null ? 0 : this.name.hashCode()) + 527) * 31) + (this.avU != null ? this.avU.hashCode() : 0)) * 31) + qx();
        }

        public a qc() {
            this.name = "";
            this.avU = null;
            this.awy = null;
            this.awJ = -1;
            return this;
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: r */
        public a b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 10:
                        this.name = peVar.readString();
                        break;
                    case 18:
                        if (this.avU == null) {
                            this.avU = new C0092a();
                        }
                        peVar.a(this.avU);
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
    }

    public pc() {
        qa();
    }

    public static pc n(byte[] bArr) throws pl {
        return (pc) pm.a(new pc(), bArr);
    }

    @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
    public void a(pf pfVar) throws IOException {
        if (this.avS != null && this.avS.length > 0) {
            for (int i = 0; i < this.avS.length; i++) {
                a aVar = this.avS[i];
                if (aVar != null) {
                    pfVar.a(1, aVar);
                }
            }
        }
        super.a(pfVar);
    }

    @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
    protected int c() {
        int iC = super.c();
        if (this.avS != null && this.avS.length > 0) {
            for (int i = 0; i < this.avS.length; i++) {
                a aVar = this.avS[i];
                if (aVar != null) {
                    iC += pf.c(1, aVar);
                }
            }
        }
        return iC;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof pc)) {
            return false;
        }
        pc pcVar = (pc) o;
        if (pk.equals(this.avS, pcVar.avS)) {
            return a(pcVar);
        }
        return false;
    }

    public int hashCode() {
        return ((pk.hashCode(this.avS) + 527) * 31) + qx();
    }

    @Override // com.google.android.gms.internal.pm
    /* renamed from: q */
    public pc b(pe peVar) throws IOException {
        while (true) {
            int iQg = peVar.qg();
            switch (iQg) {
                case 0:
                    break;
                case 10:
                    int iB = pp.b(peVar, 10);
                    int length = this.avS == null ? 0 : this.avS.length;
                    a[] aVarArr = new a[iB + length];
                    if (length != 0) {
                        System.arraycopy(this.avS, 0, aVarArr, 0, length);
                    }
                    while (length < aVarArr.length - 1) {
                        aVarArr[length] = new a();
                        peVar.a(aVarArr[length]);
                        peVar.qg();
                        length++;
                    }
                    aVarArr[length] = new a();
                    peVar.a(aVarArr[length]);
                    this.avS = aVarArr;
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

    public pc qa() {
        this.avS = a.qb();
        this.awy = null;
        this.awJ = -1;
        return this;
    }
}
