package com.google.android.gms.internal;

import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.io.IOException;
import java.util.Arrays;

/* loaded from: classes.dex */
public interface pq {

    public static final class a extends pg<a> {
        public String[] awT;
        public String[] awU;
        public int[] awV;

        public a() {
            qH();
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (this.awT != null && this.awT.length > 0) {
                for (int i = 0; i < this.awT.length; i++) {
                    String str = this.awT[i];
                    if (str != null) {
                        pfVar.b(1, str);
                    }
                }
            }
            if (this.awU != null && this.awU.length > 0) {
                for (int i2 = 0; i2 < this.awU.length; i2++) {
                    String str2 = this.awU[i2];
                    if (str2 != null) {
                        pfVar.b(2, str2);
                    }
                }
            }
            if (this.awV != null && this.awV.length > 0) {
                for (int i3 = 0; i3 < this.awV.length; i3++) {
                    pfVar.s(3, this.awV[i3]);
                }
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int i;
            int iC = super.c();
            if (this.awT == null || this.awT.length <= 0) {
                i = iC;
            } else {
                int iDf = 0;
                int i2 = 0;
                for (int i3 = 0; i3 < this.awT.length; i3++) {
                    String str = this.awT[i3];
                    if (str != null) {
                        i2++;
                        iDf += pf.df(str);
                    }
                }
                i = iC + iDf + (i2 * 1);
            }
            if (this.awU != null && this.awU.length > 0) {
                int iDf2 = 0;
                int i4 = 0;
                for (int i5 = 0; i5 < this.awU.length; i5++) {
                    String str2 = this.awU[i5];
                    if (str2 != null) {
                        i4++;
                        iDf2 += pf.df(str2);
                    }
                }
                i = i + iDf2 + (i4 * 1);
            }
            if (this.awV == null || this.awV.length <= 0) {
                return i;
            }
            int iGv = 0;
            for (int i6 = 0; i6 < this.awV.length; i6++) {
                iGv += pf.gv(this.awV[i6]);
            }
            return i + iGv + (this.awV.length * 1);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof a)) {
                return false;
            }
            a aVar = (a) o;
            if (pk.equals(this.awT, aVar.awT) && pk.equals(this.awU, aVar.awU) && pk.equals(this.awV, aVar.awV)) {
                return a(aVar);
            }
            return false;
        }

        public int hashCode() {
            return ((((((pk.hashCode(this.awT) + 527) * 31) + pk.hashCode(this.awU)) * 31) + pk.hashCode(this.awV)) * 31) + qx();
        }

        public a qH() {
            this.awT = pp.awQ;
            this.awU = pp.awQ;
            this.awV = pp.awL;
            this.awy = null;
            this.awJ = -1;
            return this;
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: v, reason: merged with bridge method [inline-methods] */
        public a b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 10:
                        int iB = pp.b(peVar, 10);
                        int length = this.awT == null ? 0 : this.awT.length;
                        String[] strArr = new String[iB + length];
                        if (length != 0) {
                            System.arraycopy(this.awT, 0, strArr, 0, length);
                        }
                        while (length < strArr.length - 1) {
                            strArr[length] = peVar.readString();
                            peVar.qg();
                            length++;
                        }
                        strArr[length] = peVar.readString();
                        this.awT = strArr;
                        break;
                    case 18:
                        int iB2 = pp.b(peVar, 18);
                        int length2 = this.awU == null ? 0 : this.awU.length;
                        String[] strArr2 = new String[iB2 + length2];
                        if (length2 != 0) {
                            System.arraycopy(this.awU, 0, strArr2, 0, length2);
                        }
                        while (length2 < strArr2.length - 1) {
                            strArr2[length2] = peVar.readString();
                            peVar.qg();
                            length2++;
                        }
                        strArr2[length2] = peVar.readString();
                        this.awU = strArr2;
                        break;
                    case 24:
                        int iB3 = pp.b(peVar, 24);
                        int length3 = this.awV == null ? 0 : this.awV.length;
                        int[] iArr = new int[iB3 + length3];
                        if (length3 != 0) {
                            System.arraycopy(this.awV, 0, iArr, 0, length3);
                        }
                        while (length3 < iArr.length - 1) {
                            iArr[length3] = peVar.qj();
                            peVar.qg();
                            length3++;
                        }
                        iArr[length3] = peVar.qj();
                        this.awV = iArr;
                        break;
                    case 26:
                        int iGo = peVar.go(peVar.qn());
                        int position = peVar.getPosition();
                        int i = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i++;
                        }
                        peVar.gq(position);
                        int length4 = this.awV == null ? 0 : this.awV.length;
                        int[] iArr2 = new int[i + length4];
                        if (length4 != 0) {
                            System.arraycopy(this.awV, 0, iArr2, 0, length4);
                        }
                        while (length4 < iArr2.length) {
                            iArr2[length4] = peVar.qj();
                            length4++;
                        }
                        this.awV = iArr2;
                        peVar.gp(iGo);
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

    public static final class b extends pg<b> {
        public int awW;
        public String awX;
        public String version;

        public b() {
            qI();
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (this.awW != 0) {
                pfVar.s(1, this.awW);
            }
            if (!this.awX.equals("")) {
                pfVar.b(2, this.awX);
            }
            if (!this.version.equals("")) {
                pfVar.b(3, this.version);
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iC = super.c();
            if (this.awW != 0) {
                iC += pf.u(1, this.awW);
            }
            if (!this.awX.equals("")) {
                iC += pf.j(2, this.awX);
            }
            return !this.version.equals("") ? iC + pf.j(3, this.version) : iC;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof b)) {
                return false;
            }
            b bVar = (b) o;
            if (this.awW != bVar.awW) {
                return false;
            }
            if (this.awX == null) {
                if (bVar.awX != null) {
                    return false;
                }
            } else if (!this.awX.equals(bVar.awX)) {
                return false;
            }
            if (this.version == null) {
                if (bVar.version != null) {
                    return false;
                }
            } else if (!this.version.equals(bVar.version)) {
                return false;
            }
            return a(bVar);
        }

        public int hashCode() {
            return (((((this.awX == null ? 0 : this.awX.hashCode()) + ((this.awW + 527) * 31)) * 31) + (this.version != null ? this.version.hashCode() : 0)) * 31) + qx();
        }

        public b qI() {
            this.awW = 0;
            this.awX = "";
            this.version = "";
            this.awy = null;
            this.awJ = -1;
            return this;
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: w, reason: merged with bridge method [inline-methods] */
        public b b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 8:
                        int iQj = peVar.qj();
                        switch (iQj) {
                            case 0:
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
                            case 18:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                                this.awW = iQj;
                                break;
                        }
                    case 18:
                        this.awX = peVar.readString();
                        break;
                    case 26:
                        this.version = peVar.readString();
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

    public static final class c extends pg<c> {
        public long awY;
        public int awZ;
        public int axa;
        public boolean axb;
        public d[] axc;
        public b axd;
        public byte[] axe;
        public byte[] axf;
        public byte[] axg;
        public a axh;
        public String axi;
        public String tag;

        public c() {
            qJ();
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (this.awY != 0) {
                pfVar.b(1, this.awY);
            }
            if (!this.tag.equals("")) {
                pfVar.b(2, this.tag);
            }
            if (this.axc != null && this.axc.length > 0) {
                for (int i = 0; i < this.axc.length; i++) {
                    d dVar = this.axc[i];
                    if (dVar != null) {
                        pfVar.a(3, dVar);
                    }
                }
            }
            if (!Arrays.equals(this.axe, pp.awS)) {
                pfVar.a(6, this.axe);
            }
            if (this.axh != null) {
                pfVar.a(7, this.axh);
            }
            if (!Arrays.equals(this.axf, pp.awS)) {
                pfVar.a(8, this.axf);
            }
            if (this.axd != null) {
                pfVar.a(9, this.axd);
            }
            if (this.axb) {
                pfVar.b(10, this.axb);
            }
            if (this.awZ != 0) {
                pfVar.s(11, this.awZ);
            }
            if (this.axa != 0) {
                pfVar.s(12, this.axa);
            }
            if (!Arrays.equals(this.axg, pp.awS)) {
                pfVar.a(13, this.axg);
            }
            if (!this.axi.equals("")) {
                pfVar.b(14, this.axi);
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iC = super.c();
            if (this.awY != 0) {
                iC += pf.d(1, this.awY);
            }
            if (!this.tag.equals("")) {
                iC += pf.j(2, this.tag);
            }
            if (this.axc != null && this.axc.length > 0) {
                int iC2 = iC;
                for (int i = 0; i < this.axc.length; i++) {
                    d dVar = this.axc[i];
                    if (dVar != null) {
                        iC2 += pf.c(3, dVar);
                    }
                }
                iC = iC2;
            }
            if (!Arrays.equals(this.axe, pp.awS)) {
                iC += pf.b(6, this.axe);
            }
            if (this.axh != null) {
                iC += pf.c(7, this.axh);
            }
            if (!Arrays.equals(this.axf, pp.awS)) {
                iC += pf.b(8, this.axf);
            }
            if (this.axd != null) {
                iC += pf.c(9, this.axd);
            }
            if (this.axb) {
                iC += pf.c(10, this.axb);
            }
            if (this.awZ != 0) {
                iC += pf.u(11, this.awZ);
            }
            if (this.axa != 0) {
                iC += pf.u(12, this.axa);
            }
            if (!Arrays.equals(this.axg, pp.awS)) {
                iC += pf.b(13, this.axg);
            }
            return !this.axi.equals("") ? iC + pf.j(14, this.axi) : iC;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof c)) {
                return false;
            }
            c cVar = (c) o;
            if (this.awY != cVar.awY) {
                return false;
            }
            if (this.tag == null) {
                if (cVar.tag != null) {
                    return false;
                }
            } else if (!this.tag.equals(cVar.tag)) {
                return false;
            }
            if (this.awZ != cVar.awZ || this.axa != cVar.axa || this.axb != cVar.axb || !pk.equals(this.axc, cVar.axc)) {
                return false;
            }
            if (this.axd == null) {
                if (cVar.axd != null) {
                    return false;
                }
            } else if (!this.axd.equals(cVar.axd)) {
                return false;
            }
            if (!Arrays.equals(this.axe, cVar.axe) || !Arrays.equals(this.axf, cVar.axf) || !Arrays.equals(this.axg, cVar.axg)) {
                return false;
            }
            if (this.axh == null) {
                if (cVar.axh != null) {
                    return false;
                }
            } else if (!this.axh.equals(cVar.axh)) {
                return false;
            }
            if (this.axi == null) {
                if (cVar.axi != null) {
                    return false;
                }
            } else if (!this.axi.equals(cVar.axi)) {
                return false;
            }
            return a(cVar);
        }

        public int hashCode() {
            return (((((this.axh == null ? 0 : this.axh.hashCode()) + (((((((((this.axd == null ? 0 : this.axd.hashCode()) + (((((this.axb ? 1231 : 1237) + (((((((this.tag == null ? 0 : this.tag.hashCode()) + ((((int) (this.awY ^ (this.awY >>> 32))) + 527) * 31)) * 31) + this.awZ) * 31) + this.axa) * 31)) * 31) + pk.hashCode(this.axc)) * 31)) * 31) + Arrays.hashCode(this.axe)) * 31) + Arrays.hashCode(this.axf)) * 31) + Arrays.hashCode(this.axg)) * 31)) * 31) + (this.axi != null ? this.axi.hashCode() : 0)) * 31) + qx();
        }

        public c qJ() {
            this.awY = 0L;
            this.tag = "";
            this.awZ = 0;
            this.axa = 0;
            this.axb = false;
            this.axc = d.qK();
            this.axd = null;
            this.axe = pp.awS;
            this.axf = pp.awS;
            this.axg = pp.awS;
            this.axh = null;
            this.axi = "";
            this.awy = null;
            this.awJ = -1;
            return this;
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: x, reason: merged with bridge method [inline-methods] */
        public c b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 8:
                        this.awY = peVar.qi();
                        break;
                    case 18:
                        this.tag = peVar.readString();
                        break;
                    case 26:
                        int iB = pp.b(peVar, 26);
                        int length = this.axc == null ? 0 : this.axc.length;
                        d[] dVarArr = new d[iB + length];
                        if (length != 0) {
                            System.arraycopy(this.axc, 0, dVarArr, 0, length);
                        }
                        while (length < dVarArr.length - 1) {
                            dVarArr[length] = new d();
                            peVar.a(dVarArr[length]);
                            peVar.qg();
                            length++;
                        }
                        dVarArr[length] = new d();
                        peVar.a(dVarArr[length]);
                        this.axc = dVarArr;
                        break;
                    case 50:
                        this.axe = peVar.readBytes();
                        break;
                    case 58:
                        if (this.axh == null) {
                            this.axh = new a();
                        }
                        peVar.a(this.axh);
                        break;
                    case 66:
                        this.axf = peVar.readBytes();
                        break;
                    case 74:
                        if (this.axd == null) {
                            this.axd = new b();
                        }
                        peVar.a(this.axd);
                        break;
                    case 80:
                        this.axb = peVar.qk();
                        break;
                    case 88:
                        this.awZ = peVar.qj();
                        break;
                    case 96:
                        this.axa = peVar.qj();
                        break;
                    case 106:
                        this.axg = peVar.readBytes();
                        break;
                    case KeyInfo.KEYCODE_VOLUME_DOWN /* 114 */:
                        this.axi = peVar.readString();
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

    public static final class d extends pg<d> {
        private static volatile d[] axj;
        public String fv;
        public String value;

        public d() {
            qL();
        }

        public static d[] qK() {
            if (axj == null) {
                synchronized (pk.awI) {
                    if (axj == null) {
                        axj = new d[0];
                    }
                }
            }
            return axj;
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (!this.fv.equals("")) {
                pfVar.b(1, this.fv);
            }
            if (!this.value.equals("")) {
                pfVar.b(2, this.value);
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iC = super.c();
            if (!this.fv.equals("")) {
                iC += pf.j(1, this.fv);
            }
            return !this.value.equals("") ? iC + pf.j(2, this.value) : iC;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof d)) {
                return false;
            }
            d dVar = (d) o;
            if (this.fv == null) {
                if (dVar.fv != null) {
                    return false;
                }
            } else if (!this.fv.equals(dVar.fv)) {
                return false;
            }
            if (this.value == null) {
                if (dVar.value != null) {
                    return false;
                }
            } else if (!this.value.equals(dVar.value)) {
                return false;
            }
            return a(dVar);
        }

        public int hashCode() {
            return (((((this.fv == null ? 0 : this.fv.hashCode()) + 527) * 31) + (this.value != null ? this.value.hashCode() : 0)) * 31) + qx();
        }

        public d qL() {
            this.fv = "";
            this.value = "";
            this.awy = null;
            this.awJ = -1;
            return this;
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: y, reason: merged with bridge method [inline-methods] */
        public d b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 10:
                        this.fv = peVar.readString();
                        break;
                    case 18:
                        this.value = peVar.readString();
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
}
