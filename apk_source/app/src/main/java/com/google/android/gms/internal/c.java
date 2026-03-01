package com.google.android.gms.internal;

import android.support.v4.media.TransportMediator;
import com.alibaba.fastjson.asm.Opcodes;
import com.google.android.gms.internal.d;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.io.IOException;

/* loaded from: classes.dex */
public interface c {

    public static final class a extends pg<a> {
        public int fn;
        public int fo;
        public int level;

        public a() {
            b();
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: a */
        public a b(pe peVar) throws IOException {
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
                                this.level = iQj;
                                break;
                        }
                    case 16:
                        this.fn = peVar.qj();
                        break;
                    case 24:
                        this.fo = peVar.qj();
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

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (this.level != 1) {
                pfVar.s(1, this.level);
            }
            if (this.fn != 0) {
                pfVar.s(2, this.fn);
            }
            if (this.fo != 0) {
                pfVar.s(3, this.fo);
            }
            super.a(pfVar);
        }

        public a b() {
            this.level = 1;
            this.fn = 0;
            this.fo = 0;
            this.awy = null;
            this.awJ = -1;
            return this;
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iC = super.c();
            if (this.level != 1) {
                iC += pf.u(1, this.level);
            }
            if (this.fn != 0) {
                iC += pf.u(2, this.fn);
            }
            return this.fo != 0 ? iC + pf.u(3, this.fo) : iC;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof a)) {
                return false;
            }
            a aVar = (a) o;
            if (this.level == aVar.level && this.fn == aVar.fn && this.fo == aVar.fo) {
                return a(aVar);
            }
            return false;
        }

        public int hashCode() {
            return ((((((this.level + 527) * 31) + this.fn) * 31) + this.fo) * 31) + qx();
        }
    }

    public static final class b extends pg<b> {
        private static volatile b[] fp;
        public int[] fq;
        public int fr;
        public boolean fs;
        public boolean ft;
        public int name;

        public b() {
            e();
        }

        public static b[] d() {
            if (fp == null) {
                synchronized (pk.awI) {
                    if (fp == null) {
                        fp = new b[0];
                    }
                }
            }
            return fp;
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (this.ft) {
                pfVar.b(1, this.ft);
            }
            pfVar.s(2, this.fr);
            if (this.fq != null && this.fq.length > 0) {
                for (int i = 0; i < this.fq.length; i++) {
                    pfVar.s(3, this.fq[i]);
                }
            }
            if (this.name != 0) {
                pfVar.s(4, this.name);
            }
            if (this.fs) {
                pfVar.b(6, this.fs);
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iU;
            int iGv = 0;
            int iC = super.c();
            if (this.ft) {
                iC += pf.c(1, this.ft);
            }
            int iU2 = pf.u(2, this.fr) + iC;
            if (this.fq == null || this.fq.length <= 0) {
                iU = iU2;
            } else {
                for (int i = 0; i < this.fq.length; i++) {
                    iGv += pf.gv(this.fq[i]);
                }
                iU = iU2 + iGv + (this.fq.length * 1);
            }
            if (this.name != 0) {
                iU += pf.u(4, this.name);
            }
            return this.fs ? iU + pf.c(6, this.fs) : iU;
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: c */
        public b b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 8:
                        this.ft = peVar.qk();
                        break;
                    case 16:
                        this.fr = peVar.qj();
                        break;
                    case 24:
                        int iB = pp.b(peVar, 24);
                        int length = this.fq == null ? 0 : this.fq.length;
                        int[] iArr = new int[iB + length];
                        if (length != 0) {
                            System.arraycopy(this.fq, 0, iArr, 0, length);
                        }
                        while (length < iArr.length - 1) {
                            iArr[length] = peVar.qj();
                            peVar.qg();
                            length++;
                        }
                        iArr[length] = peVar.qj();
                        this.fq = iArr;
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
                        int length2 = this.fq == null ? 0 : this.fq.length;
                        int[] iArr2 = new int[i + length2];
                        if (length2 != 0) {
                            System.arraycopy(this.fq, 0, iArr2, 0, length2);
                        }
                        while (length2 < iArr2.length) {
                            iArr2[length2] = peVar.qj();
                            length2++;
                        }
                        this.fq = iArr2;
                        peVar.gp(iGo);
                        break;
                    case 32:
                        this.name = peVar.qj();
                        break;
                    case 48:
                        this.fs = peVar.qk();
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

        public b e() {
            this.fq = pp.awL;
            this.fr = 0;
            this.name = 0;
            this.fs = false;
            this.ft = false;
            this.awy = null;
            this.awJ = -1;
            return this;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof b)) {
                return false;
            }
            b bVar = (b) o;
            if (pk.equals(this.fq, bVar.fq) && this.fr == bVar.fr && this.name == bVar.name && this.fs == bVar.fs && this.ft == bVar.ft) {
                return a(bVar);
            }
            return false;
        }

        public int hashCode() {
            return (((((this.fs ? 1231 : 1237) + ((((((pk.hashCode(this.fq) + 527) * 31) + this.fr) * 31) + this.name) * 31)) * 31) + (this.ft ? 1231 : 1237)) * 31) + qx();
        }
    }

    /* renamed from: com.google.android.gms.internal.c$c */
    public static final class C0040c extends pg<C0040c> {
        private static volatile C0040c[] fu;
        public String fv;
        public long fw;
        public long fx;
        public boolean fy;
        public long fz;

        public C0040c() {
            g();
        }

        public static C0040c[] f() {
            if (fu == null) {
                synchronized (pk.awI) {
                    if (fu == null) {
                        fu = new C0040c[0];
                    }
                }
            }
            return fu;
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (!this.fv.equals("")) {
                pfVar.b(1, this.fv);
            }
            if (this.fw != 0) {
                pfVar.b(2, this.fw);
            }
            if (this.fx != 2147483647L) {
                pfVar.b(3, this.fx);
            }
            if (this.fy) {
                pfVar.b(4, this.fy);
            }
            if (this.fz != 0) {
                pfVar.b(5, this.fz);
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iC = super.c();
            if (!this.fv.equals("")) {
                iC += pf.j(1, this.fv);
            }
            if (this.fw != 0) {
                iC += pf.d(2, this.fw);
            }
            if (this.fx != 2147483647L) {
                iC += pf.d(3, this.fx);
            }
            if (this.fy) {
                iC += pf.c(4, this.fy);
            }
            return this.fz != 0 ? iC + pf.d(5, this.fz) : iC;
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: d */
        public C0040c b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 10:
                        this.fv = peVar.readString();
                        break;
                    case 16:
                        this.fw = peVar.qi();
                        break;
                    case 24:
                        this.fx = peVar.qi();
                        break;
                    case 32:
                        this.fy = peVar.qk();
                        break;
                    case 40:
                        this.fz = peVar.qi();
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

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof C0040c)) {
                return false;
            }
            C0040c c0040c = (C0040c) o;
            if (this.fv == null) {
                if (c0040c.fv != null) {
                    return false;
                }
            } else if (!this.fv.equals(c0040c.fv)) {
                return false;
            }
            if (this.fw == c0040c.fw && this.fx == c0040c.fx && this.fy == c0040c.fy && this.fz == c0040c.fz) {
                return a(c0040c);
            }
            return false;
        }

        public C0040c g() {
            this.fv = "";
            this.fw = 0L;
            this.fx = 2147483647L;
            this.fy = false;
            this.fz = 0L;
            this.awy = null;
            this.awJ = -1;
            return this;
        }

        public int hashCode() {
            return (((((this.fy ? 1231 : 1237) + (((((((this.fv == null ? 0 : this.fv.hashCode()) + 527) * 31) + ((int) (this.fw ^ (this.fw >>> 32)))) * 31) + ((int) (this.fx ^ (this.fx >>> 32)))) * 31)) * 31) + ((int) (this.fz ^ (this.fz >>> 32)))) * 31) + qx();
        }
    }

    public static final class d extends pg<d> {
        public d.a[] fA;
        public d.a[] fB;
        public C0040c[] fC;

        public d() {
            h();
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (this.fA != null && this.fA.length > 0) {
                for (int i = 0; i < this.fA.length; i++) {
                    d.a aVar = this.fA[i];
                    if (aVar != null) {
                        pfVar.a(1, aVar);
                    }
                }
            }
            if (this.fB != null && this.fB.length > 0) {
                for (int i2 = 0; i2 < this.fB.length; i2++) {
                    d.a aVar2 = this.fB[i2];
                    if (aVar2 != null) {
                        pfVar.a(2, aVar2);
                    }
                }
            }
            if (this.fC != null && this.fC.length > 0) {
                for (int i3 = 0; i3 < this.fC.length; i3++) {
                    C0040c c0040c = this.fC[i3];
                    if (c0040c != null) {
                        pfVar.a(3, c0040c);
                    }
                }
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iC = super.c();
            if (this.fA != null && this.fA.length > 0) {
                int iC2 = iC;
                for (int i = 0; i < this.fA.length; i++) {
                    d.a aVar = this.fA[i];
                    if (aVar != null) {
                        iC2 += pf.c(1, aVar);
                    }
                }
                iC = iC2;
            }
            if (this.fB != null && this.fB.length > 0) {
                int iC3 = iC;
                for (int i2 = 0; i2 < this.fB.length; i2++) {
                    d.a aVar2 = this.fB[i2];
                    if (aVar2 != null) {
                        iC3 += pf.c(2, aVar2);
                    }
                }
                iC = iC3;
            }
            if (this.fC != null && this.fC.length > 0) {
                for (int i3 = 0; i3 < this.fC.length; i3++) {
                    C0040c c0040c = this.fC[i3];
                    if (c0040c != null) {
                        iC += pf.c(3, c0040c);
                    }
                }
            }
            return iC;
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: e */
        public d b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 10:
                        int iB = pp.b(peVar, 10);
                        int length = this.fA == null ? 0 : this.fA.length;
                        d.a[] aVarArr = new d.a[iB + length];
                        if (length != 0) {
                            System.arraycopy(this.fA, 0, aVarArr, 0, length);
                        }
                        while (length < aVarArr.length - 1) {
                            aVarArr[length] = new d.a();
                            peVar.a(aVarArr[length]);
                            peVar.qg();
                            length++;
                        }
                        aVarArr[length] = new d.a();
                        peVar.a(aVarArr[length]);
                        this.fA = aVarArr;
                        break;
                    case 18:
                        int iB2 = pp.b(peVar, 18);
                        int length2 = this.fB == null ? 0 : this.fB.length;
                        d.a[] aVarArr2 = new d.a[iB2 + length2];
                        if (length2 != 0) {
                            System.arraycopy(this.fB, 0, aVarArr2, 0, length2);
                        }
                        while (length2 < aVarArr2.length - 1) {
                            aVarArr2[length2] = new d.a();
                            peVar.a(aVarArr2[length2]);
                            peVar.qg();
                            length2++;
                        }
                        aVarArr2[length2] = new d.a();
                        peVar.a(aVarArr2[length2]);
                        this.fB = aVarArr2;
                        break;
                    case 26:
                        int iB3 = pp.b(peVar, 26);
                        int length3 = this.fC == null ? 0 : this.fC.length;
                        C0040c[] c0040cArr = new C0040c[iB3 + length3];
                        if (length3 != 0) {
                            System.arraycopy(this.fC, 0, c0040cArr, 0, length3);
                        }
                        while (length3 < c0040cArr.length - 1) {
                            c0040cArr[length3] = new C0040c();
                            peVar.a(c0040cArr[length3]);
                            peVar.qg();
                            length3++;
                        }
                        c0040cArr[length3] = new C0040c();
                        peVar.a(c0040cArr[length3]);
                        this.fC = c0040cArr;
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

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof d)) {
                return false;
            }
            d dVar = (d) o;
            if (pk.equals(this.fA, dVar.fA) && pk.equals(this.fB, dVar.fB) && pk.equals(this.fC, dVar.fC)) {
                return a(dVar);
            }
            return false;
        }

        public d h() {
            this.fA = d.a.r();
            this.fB = d.a.r();
            this.fC = C0040c.f();
            this.awy = null;
            this.awJ = -1;
            return this;
        }

        public int hashCode() {
            return ((((((pk.hashCode(this.fA) + 527) * 31) + pk.hashCode(this.fB)) * 31) + pk.hashCode(this.fC)) * 31) + qx();
        }
    }

    public static final class e extends pg<e> {
        private static volatile e[] fD;
        public int key;
        public int value;

        public e() {
            j();
        }

        public static e[] i() {
            if (fD == null) {
                synchronized (pk.awI) {
                    if (fD == null) {
                        fD = new e[0];
                    }
                }
            }
            return fD;
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            pfVar.s(1, this.key);
            pfVar.s(2, this.value);
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            return super.c() + pf.u(1, this.key) + pf.u(2, this.value);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof e)) {
                return false;
            }
            e eVar = (e) o;
            if (this.key == eVar.key && this.value == eVar.value) {
                return a(eVar);
            }
            return false;
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: f */
        public e b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 8:
                        this.key = peVar.qj();
                        break;
                    case 16:
                        this.value = peVar.qj();
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

        public int hashCode() {
            return ((((this.key + 527) * 31) + this.value) * 31) + qx();
        }

        public e j() {
            this.key = 0;
            this.value = 0;
            this.awy = null;
            this.awJ = -1;
            return this;
        }
    }

    public static final class f extends pg<f> {
        public String[] fE;
        public String[] fF;
        public d.a[] fG;
        public e[] fH;
        public b[] fI;
        public b[] fJ;
        public b[] fK;
        public g[] fL;
        public String fM;
        public String fN;
        public String fO;
        public a fP;
        public float fQ;
        public boolean fR;
        public String[] fS;
        public int fT;
        public String version;

        public f() {
            k();
        }

        public static f a(byte[] bArr) throws pl {
            return (f) pm.a(new f(), bArr);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (this.fF != null && this.fF.length > 0) {
                for (int i = 0; i < this.fF.length; i++) {
                    String str = this.fF[i];
                    if (str != null) {
                        pfVar.b(1, str);
                    }
                }
            }
            if (this.fG != null && this.fG.length > 0) {
                for (int i2 = 0; i2 < this.fG.length; i2++) {
                    d.a aVar = this.fG[i2];
                    if (aVar != null) {
                        pfVar.a(2, aVar);
                    }
                }
            }
            if (this.fH != null && this.fH.length > 0) {
                for (int i3 = 0; i3 < this.fH.length; i3++) {
                    e eVar = this.fH[i3];
                    if (eVar != null) {
                        pfVar.a(3, eVar);
                    }
                }
            }
            if (this.fI != null && this.fI.length > 0) {
                for (int i4 = 0; i4 < this.fI.length; i4++) {
                    b bVar = this.fI[i4];
                    if (bVar != null) {
                        pfVar.a(4, bVar);
                    }
                }
            }
            if (this.fJ != null && this.fJ.length > 0) {
                for (int i5 = 0; i5 < this.fJ.length; i5++) {
                    b bVar2 = this.fJ[i5];
                    if (bVar2 != null) {
                        pfVar.a(5, bVar2);
                    }
                }
            }
            if (this.fK != null && this.fK.length > 0) {
                for (int i6 = 0; i6 < this.fK.length; i6++) {
                    b bVar3 = this.fK[i6];
                    if (bVar3 != null) {
                        pfVar.a(6, bVar3);
                    }
                }
            }
            if (this.fL != null && this.fL.length > 0) {
                for (int i7 = 0; i7 < this.fL.length; i7++) {
                    g gVar = this.fL[i7];
                    if (gVar != null) {
                        pfVar.a(7, gVar);
                    }
                }
            }
            if (!this.fM.equals("")) {
                pfVar.b(9, this.fM);
            }
            if (!this.fN.equals("")) {
                pfVar.b(10, this.fN);
            }
            if (!this.fO.equals("0")) {
                pfVar.b(12, this.fO);
            }
            if (!this.version.equals("")) {
                pfVar.b(13, this.version);
            }
            if (this.fP != null) {
                pfVar.a(14, this.fP);
            }
            if (Float.floatToIntBits(this.fQ) != Float.floatToIntBits(0.0f)) {
                pfVar.b(15, this.fQ);
            }
            if (this.fS != null && this.fS.length > 0) {
                for (int i8 = 0; i8 < this.fS.length; i8++) {
                    String str2 = this.fS[i8];
                    if (str2 != null) {
                        pfVar.b(16, str2);
                    }
                }
            }
            if (this.fT != 0) {
                pfVar.s(17, this.fT);
            }
            if (this.fR) {
                pfVar.b(18, this.fR);
            }
            if (this.fE != null && this.fE.length > 0) {
                for (int i9 = 0; i9 < this.fE.length; i9++) {
                    String str3 = this.fE[i9];
                    if (str3 != null) {
                        pfVar.b(19, str3);
                    }
                }
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iC;
            int iC2 = super.c();
            if (this.fF == null || this.fF.length <= 0) {
                iC = iC2;
            } else {
                int iDf = 0;
                int i = 0;
                for (int i2 = 0; i2 < this.fF.length; i2++) {
                    String str = this.fF[i2];
                    if (str != null) {
                        i++;
                        iDf += pf.df(str);
                    }
                }
                iC = iC2 + iDf + (i * 1);
            }
            if (this.fG != null && this.fG.length > 0) {
                int iC3 = iC;
                for (int i3 = 0; i3 < this.fG.length; i3++) {
                    d.a aVar = this.fG[i3];
                    if (aVar != null) {
                        iC3 += pf.c(2, aVar);
                    }
                }
                iC = iC3;
            }
            if (this.fH != null && this.fH.length > 0) {
                int iC4 = iC;
                for (int i4 = 0; i4 < this.fH.length; i4++) {
                    e eVar = this.fH[i4];
                    if (eVar != null) {
                        iC4 += pf.c(3, eVar);
                    }
                }
                iC = iC4;
            }
            if (this.fI != null && this.fI.length > 0) {
                int iC5 = iC;
                for (int i5 = 0; i5 < this.fI.length; i5++) {
                    b bVar = this.fI[i5];
                    if (bVar != null) {
                        iC5 += pf.c(4, bVar);
                    }
                }
                iC = iC5;
            }
            if (this.fJ != null && this.fJ.length > 0) {
                int iC6 = iC;
                for (int i6 = 0; i6 < this.fJ.length; i6++) {
                    b bVar2 = this.fJ[i6];
                    if (bVar2 != null) {
                        iC6 += pf.c(5, bVar2);
                    }
                }
                iC = iC6;
            }
            if (this.fK != null && this.fK.length > 0) {
                int iC7 = iC;
                for (int i7 = 0; i7 < this.fK.length; i7++) {
                    b bVar3 = this.fK[i7];
                    if (bVar3 != null) {
                        iC7 += pf.c(6, bVar3);
                    }
                }
                iC = iC7;
            }
            if (this.fL != null && this.fL.length > 0) {
                int iC8 = iC;
                for (int i8 = 0; i8 < this.fL.length; i8++) {
                    g gVar = this.fL[i8];
                    if (gVar != null) {
                        iC8 += pf.c(7, gVar);
                    }
                }
                iC = iC8;
            }
            if (!this.fM.equals("")) {
                iC += pf.j(9, this.fM);
            }
            if (!this.fN.equals("")) {
                iC += pf.j(10, this.fN);
            }
            if (!this.fO.equals("0")) {
                iC += pf.j(12, this.fO);
            }
            if (!this.version.equals("")) {
                iC += pf.j(13, this.version);
            }
            if (this.fP != null) {
                iC += pf.c(14, this.fP);
            }
            if (Float.floatToIntBits(this.fQ) != Float.floatToIntBits(0.0f)) {
                iC += pf.c(15, this.fQ);
            }
            if (this.fS != null && this.fS.length > 0) {
                int iDf2 = 0;
                int i9 = 0;
                for (int i10 = 0; i10 < this.fS.length; i10++) {
                    String str2 = this.fS[i10];
                    if (str2 != null) {
                        i9++;
                        iDf2 += pf.df(str2);
                    }
                }
                iC = iC + iDf2 + (i9 * 2);
            }
            if (this.fT != 0) {
                iC += pf.u(17, this.fT);
            }
            if (this.fR) {
                iC += pf.c(18, this.fR);
            }
            if (this.fE == null || this.fE.length <= 0) {
                return iC;
            }
            int iDf3 = 0;
            int i11 = 0;
            for (int i12 = 0; i12 < this.fE.length; i12++) {
                String str3 = this.fE[i12];
                if (str3 != null) {
                    i11++;
                    iDf3 += pf.df(str3);
                }
            }
            return iC + iDf3 + (i11 * 2);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof f)) {
                return false;
            }
            f fVar = (f) o;
            if (!pk.equals(this.fE, fVar.fE) || !pk.equals(this.fF, fVar.fF) || !pk.equals(this.fG, fVar.fG) || !pk.equals(this.fH, fVar.fH) || !pk.equals(this.fI, fVar.fI) || !pk.equals(this.fJ, fVar.fJ) || !pk.equals(this.fK, fVar.fK) || !pk.equals(this.fL, fVar.fL)) {
                return false;
            }
            if (this.fM == null) {
                if (fVar.fM != null) {
                    return false;
                }
            } else if (!this.fM.equals(fVar.fM)) {
                return false;
            }
            if (this.fN == null) {
                if (fVar.fN != null) {
                    return false;
                }
            } else if (!this.fN.equals(fVar.fN)) {
                return false;
            }
            if (this.fO == null) {
                if (fVar.fO != null) {
                    return false;
                }
            } else if (!this.fO.equals(fVar.fO)) {
                return false;
            }
            if (this.version == null) {
                if (fVar.version != null) {
                    return false;
                }
            } else if (!this.version.equals(fVar.version)) {
                return false;
            }
            if (this.fP == null) {
                if (fVar.fP != null) {
                    return false;
                }
            } else if (!this.fP.equals(fVar.fP)) {
                return false;
            }
            if (Float.floatToIntBits(this.fQ) == Float.floatToIntBits(fVar.fQ) && this.fR == fVar.fR && pk.equals(this.fS, fVar.fS) && this.fT == fVar.fT) {
                return a(fVar);
            }
            return false;
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: g */
        public f b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 10:
                        int iB = pp.b(peVar, 10);
                        int length = this.fF == null ? 0 : this.fF.length;
                        String[] strArr = new String[iB + length];
                        if (length != 0) {
                            System.arraycopy(this.fF, 0, strArr, 0, length);
                        }
                        while (length < strArr.length - 1) {
                            strArr[length] = peVar.readString();
                            peVar.qg();
                            length++;
                        }
                        strArr[length] = peVar.readString();
                        this.fF = strArr;
                        break;
                    case 18:
                        int iB2 = pp.b(peVar, 18);
                        int length2 = this.fG == null ? 0 : this.fG.length;
                        d.a[] aVarArr = new d.a[iB2 + length2];
                        if (length2 != 0) {
                            System.arraycopy(this.fG, 0, aVarArr, 0, length2);
                        }
                        while (length2 < aVarArr.length - 1) {
                            aVarArr[length2] = new d.a();
                            peVar.a(aVarArr[length2]);
                            peVar.qg();
                            length2++;
                        }
                        aVarArr[length2] = new d.a();
                        peVar.a(aVarArr[length2]);
                        this.fG = aVarArr;
                        break;
                    case 26:
                        int iB3 = pp.b(peVar, 26);
                        int length3 = this.fH == null ? 0 : this.fH.length;
                        e[] eVarArr = new e[iB3 + length3];
                        if (length3 != 0) {
                            System.arraycopy(this.fH, 0, eVarArr, 0, length3);
                        }
                        while (length3 < eVarArr.length - 1) {
                            eVarArr[length3] = new e();
                            peVar.a(eVarArr[length3]);
                            peVar.qg();
                            length3++;
                        }
                        eVarArr[length3] = new e();
                        peVar.a(eVarArr[length3]);
                        this.fH = eVarArr;
                        break;
                    case 34:
                        int iB4 = pp.b(peVar, 34);
                        int length4 = this.fI == null ? 0 : this.fI.length;
                        b[] bVarArr = new b[iB4 + length4];
                        if (length4 != 0) {
                            System.arraycopy(this.fI, 0, bVarArr, 0, length4);
                        }
                        while (length4 < bVarArr.length - 1) {
                            bVarArr[length4] = new b();
                            peVar.a(bVarArr[length4]);
                            peVar.qg();
                            length4++;
                        }
                        bVarArr[length4] = new b();
                        peVar.a(bVarArr[length4]);
                        this.fI = bVarArr;
                        break;
                    case 42:
                        int iB5 = pp.b(peVar, 42);
                        int length5 = this.fJ == null ? 0 : this.fJ.length;
                        b[] bVarArr2 = new b[iB5 + length5];
                        if (length5 != 0) {
                            System.arraycopy(this.fJ, 0, bVarArr2, 0, length5);
                        }
                        while (length5 < bVarArr2.length - 1) {
                            bVarArr2[length5] = new b();
                            peVar.a(bVarArr2[length5]);
                            peVar.qg();
                            length5++;
                        }
                        bVarArr2[length5] = new b();
                        peVar.a(bVarArr2[length5]);
                        this.fJ = bVarArr2;
                        break;
                    case 50:
                        int iB6 = pp.b(peVar, 50);
                        int length6 = this.fK == null ? 0 : this.fK.length;
                        b[] bVarArr3 = new b[iB6 + length6];
                        if (length6 != 0) {
                            System.arraycopy(this.fK, 0, bVarArr3, 0, length6);
                        }
                        while (length6 < bVarArr3.length - 1) {
                            bVarArr3[length6] = new b();
                            peVar.a(bVarArr3[length6]);
                            peVar.qg();
                            length6++;
                        }
                        bVarArr3[length6] = new b();
                        peVar.a(bVarArr3[length6]);
                        this.fK = bVarArr3;
                        break;
                    case 58:
                        int iB7 = pp.b(peVar, 58);
                        int length7 = this.fL == null ? 0 : this.fL.length;
                        g[] gVarArr = new g[iB7 + length7];
                        if (length7 != 0) {
                            System.arraycopy(this.fL, 0, gVarArr, 0, length7);
                        }
                        while (length7 < gVarArr.length - 1) {
                            gVarArr[length7] = new g();
                            peVar.a(gVarArr[length7]);
                            peVar.qg();
                            length7++;
                        }
                        gVarArr[length7] = new g();
                        peVar.a(gVarArr[length7]);
                        this.fL = gVarArr;
                        break;
                    case 74:
                        this.fM = peVar.readString();
                        break;
                    case 82:
                        this.fN = peVar.readString();
                        break;
                    case 98:
                        this.fO = peVar.readString();
                        break;
                    case 106:
                        this.version = peVar.readString();
                        break;
                    case KeyInfo.KEYCODE_VOLUME_DOWN /* 114 */:
                        if (this.fP == null) {
                            this.fP = new a();
                        }
                        peVar.a(this.fP);
                        break;
                    case 125:
                        this.fQ = peVar.readFloat();
                        break;
                    case TransportMediator.KEYCODE_MEDIA_RECORD /* 130 */:
                        int iB8 = pp.b(peVar, TransportMediator.KEYCODE_MEDIA_RECORD);
                        int length8 = this.fS == null ? 0 : this.fS.length;
                        String[] strArr2 = new String[iB8 + length8];
                        if (length8 != 0) {
                            System.arraycopy(this.fS, 0, strArr2, 0, length8);
                        }
                        while (length8 < strArr2.length - 1) {
                            strArr2[length8] = peVar.readString();
                            peVar.qg();
                            length8++;
                        }
                        strArr2[length8] = peVar.readString();
                        this.fS = strArr2;
                        break;
                    case 136:
                        this.fT = peVar.qj();
                        break;
                    case 144:
                        this.fR = peVar.qk();
                        break;
                    case Opcodes.IFNE /* 154 */:
                        int iB9 = pp.b(peVar, Opcodes.IFNE);
                        int length9 = this.fE == null ? 0 : this.fE.length;
                        String[] strArr3 = new String[iB9 + length9];
                        if (length9 != 0) {
                            System.arraycopy(this.fE, 0, strArr3, 0, length9);
                        }
                        while (length9 < strArr3.length - 1) {
                            strArr3[length9] = peVar.readString();
                            peVar.qg();
                            length9++;
                        }
                        strArr3[length9] = peVar.readString();
                        this.fE = strArr3;
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

        public int hashCode() {
            return (((((((this.fR ? 1231 : 1237) + (((((((this.version == null ? 0 : this.version.hashCode()) + (((this.fO == null ? 0 : this.fO.hashCode()) + (((this.fN == null ? 0 : this.fN.hashCode()) + (((this.fM == null ? 0 : this.fM.hashCode()) + ((((((((((((((((pk.hashCode(this.fE) + 527) * 31) + pk.hashCode(this.fF)) * 31) + pk.hashCode(this.fG)) * 31) + pk.hashCode(this.fH)) * 31) + pk.hashCode(this.fI)) * 31) + pk.hashCode(this.fJ)) * 31) + pk.hashCode(this.fK)) * 31) + pk.hashCode(this.fL)) * 31)) * 31)) * 31)) * 31)) * 31) + (this.fP != null ? this.fP.hashCode() : 0)) * 31) + Float.floatToIntBits(this.fQ)) * 31)) * 31) + pk.hashCode(this.fS)) * 31) + this.fT) * 31) + qx();
        }

        public f k() {
            this.fE = pp.awQ;
            this.fF = pp.awQ;
            this.fG = d.a.r();
            this.fH = e.i();
            this.fI = b.d();
            this.fJ = b.d();
            this.fK = b.d();
            this.fL = g.l();
            this.fM = "";
            this.fN = "";
            this.fO = "0";
            this.version = "";
            this.fP = null;
            this.fQ = 0.0f;
            this.fR = false;
            this.fS = pp.awQ;
            this.fT = 0;
            this.awy = null;
            this.awJ = -1;
            return this;
        }
    }

    public static final class g extends pg<g> {
        private static volatile g[] fU;
        public int[] fV;
        public int[] fW;
        public int[] fX;
        public int[] fY;
        public int[] fZ;
        public int[] ga;
        public int[] gb;
        public int[] gc;
        public int[] gd;
        public int[] ge;

        public g() {
            m();
        }

        public static g[] l() {
            if (fU == null) {
                synchronized (pk.awI) {
                    if (fU == null) {
                        fU = new g[0];
                    }
                }
            }
            return fU;
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (this.fV != null && this.fV.length > 0) {
                for (int i = 0; i < this.fV.length; i++) {
                    pfVar.s(1, this.fV[i]);
                }
            }
            if (this.fW != null && this.fW.length > 0) {
                for (int i2 = 0; i2 < this.fW.length; i2++) {
                    pfVar.s(2, this.fW[i2]);
                }
            }
            if (this.fX != null && this.fX.length > 0) {
                for (int i3 = 0; i3 < this.fX.length; i3++) {
                    pfVar.s(3, this.fX[i3]);
                }
            }
            if (this.fY != null && this.fY.length > 0) {
                for (int i4 = 0; i4 < this.fY.length; i4++) {
                    pfVar.s(4, this.fY[i4]);
                }
            }
            if (this.fZ != null && this.fZ.length > 0) {
                for (int i5 = 0; i5 < this.fZ.length; i5++) {
                    pfVar.s(5, this.fZ[i5]);
                }
            }
            if (this.ga != null && this.ga.length > 0) {
                for (int i6 = 0; i6 < this.ga.length; i6++) {
                    pfVar.s(6, this.ga[i6]);
                }
            }
            if (this.gb != null && this.gb.length > 0) {
                for (int i7 = 0; i7 < this.gb.length; i7++) {
                    pfVar.s(7, this.gb[i7]);
                }
            }
            if (this.gc != null && this.gc.length > 0) {
                for (int i8 = 0; i8 < this.gc.length; i8++) {
                    pfVar.s(8, this.gc[i8]);
                }
            }
            if (this.gd != null && this.gd.length > 0) {
                for (int i9 = 0; i9 < this.gd.length; i9++) {
                    pfVar.s(9, this.gd[i9]);
                }
            }
            if (this.ge != null && this.ge.length > 0) {
                for (int i10 = 0; i10 < this.ge.length; i10++) {
                    pfVar.s(10, this.ge[i10]);
                }
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int length;
            int iC = super.c();
            if (this.fV == null || this.fV.length <= 0) {
                length = iC;
            } else {
                int iGv = 0;
                for (int i = 0; i < this.fV.length; i++) {
                    iGv += pf.gv(this.fV[i]);
                }
                length = iC + iGv + (this.fV.length * 1);
            }
            if (this.fW != null && this.fW.length > 0) {
                int iGv2 = 0;
                for (int i2 = 0; i2 < this.fW.length; i2++) {
                    iGv2 += pf.gv(this.fW[i2]);
                }
                length = length + iGv2 + (this.fW.length * 1);
            }
            if (this.fX != null && this.fX.length > 0) {
                int iGv3 = 0;
                for (int i3 = 0; i3 < this.fX.length; i3++) {
                    iGv3 += pf.gv(this.fX[i3]);
                }
                length = length + iGv3 + (this.fX.length * 1);
            }
            if (this.fY != null && this.fY.length > 0) {
                int iGv4 = 0;
                for (int i4 = 0; i4 < this.fY.length; i4++) {
                    iGv4 += pf.gv(this.fY[i4]);
                }
                length = length + iGv4 + (this.fY.length * 1);
            }
            if (this.fZ != null && this.fZ.length > 0) {
                int iGv5 = 0;
                for (int i5 = 0; i5 < this.fZ.length; i5++) {
                    iGv5 += pf.gv(this.fZ[i5]);
                }
                length = length + iGv5 + (this.fZ.length * 1);
            }
            if (this.ga != null && this.ga.length > 0) {
                int iGv6 = 0;
                for (int i6 = 0; i6 < this.ga.length; i6++) {
                    iGv6 += pf.gv(this.ga[i6]);
                }
                length = length + iGv6 + (this.ga.length * 1);
            }
            if (this.gb != null && this.gb.length > 0) {
                int iGv7 = 0;
                for (int i7 = 0; i7 < this.gb.length; i7++) {
                    iGv7 += pf.gv(this.gb[i7]);
                }
                length = length + iGv7 + (this.gb.length * 1);
            }
            if (this.gc != null && this.gc.length > 0) {
                int iGv8 = 0;
                for (int i8 = 0; i8 < this.gc.length; i8++) {
                    iGv8 += pf.gv(this.gc[i8]);
                }
                length = length + iGv8 + (this.gc.length * 1);
            }
            if (this.gd != null && this.gd.length > 0) {
                int iGv9 = 0;
                for (int i9 = 0; i9 < this.gd.length; i9++) {
                    iGv9 += pf.gv(this.gd[i9]);
                }
                length = length + iGv9 + (this.gd.length * 1);
            }
            if (this.ge == null || this.ge.length <= 0) {
                return length;
            }
            int iGv10 = 0;
            for (int i10 = 0; i10 < this.ge.length; i10++) {
                iGv10 += pf.gv(this.ge[i10]);
            }
            return length + iGv10 + (this.ge.length * 1);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof g)) {
                return false;
            }
            g gVar = (g) o;
            if (pk.equals(this.fV, gVar.fV) && pk.equals(this.fW, gVar.fW) && pk.equals(this.fX, gVar.fX) && pk.equals(this.fY, gVar.fY) && pk.equals(this.fZ, gVar.fZ) && pk.equals(this.ga, gVar.ga) && pk.equals(this.gb, gVar.gb) && pk.equals(this.gc, gVar.gc) && pk.equals(this.gd, gVar.gd) && pk.equals(this.ge, gVar.ge)) {
                return a(gVar);
            }
            return false;
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: h */
        public g b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 8:
                        int iB = pp.b(peVar, 8);
                        int length = this.fV == null ? 0 : this.fV.length;
                        int[] iArr = new int[iB + length];
                        if (length != 0) {
                            System.arraycopy(this.fV, 0, iArr, 0, length);
                        }
                        while (length < iArr.length - 1) {
                            iArr[length] = peVar.qj();
                            peVar.qg();
                            length++;
                        }
                        iArr[length] = peVar.qj();
                        this.fV = iArr;
                        break;
                    case 10:
                        int iGo = peVar.go(peVar.qn());
                        int position = peVar.getPosition();
                        int i = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i++;
                        }
                        peVar.gq(position);
                        int length2 = this.fV == null ? 0 : this.fV.length;
                        int[] iArr2 = new int[i + length2];
                        if (length2 != 0) {
                            System.arraycopy(this.fV, 0, iArr2, 0, length2);
                        }
                        while (length2 < iArr2.length) {
                            iArr2[length2] = peVar.qj();
                            length2++;
                        }
                        this.fV = iArr2;
                        peVar.gp(iGo);
                        break;
                    case 16:
                        int iB2 = pp.b(peVar, 16);
                        int length3 = this.fW == null ? 0 : this.fW.length;
                        int[] iArr3 = new int[iB2 + length3];
                        if (length3 != 0) {
                            System.arraycopy(this.fW, 0, iArr3, 0, length3);
                        }
                        while (length3 < iArr3.length - 1) {
                            iArr3[length3] = peVar.qj();
                            peVar.qg();
                            length3++;
                        }
                        iArr3[length3] = peVar.qj();
                        this.fW = iArr3;
                        break;
                    case 18:
                        int iGo2 = peVar.go(peVar.qn());
                        int position2 = peVar.getPosition();
                        int i2 = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i2++;
                        }
                        peVar.gq(position2);
                        int length4 = this.fW == null ? 0 : this.fW.length;
                        int[] iArr4 = new int[i2 + length4];
                        if (length4 != 0) {
                            System.arraycopy(this.fW, 0, iArr4, 0, length4);
                        }
                        while (length4 < iArr4.length) {
                            iArr4[length4] = peVar.qj();
                            length4++;
                        }
                        this.fW = iArr4;
                        peVar.gp(iGo2);
                        break;
                    case 24:
                        int iB3 = pp.b(peVar, 24);
                        int length5 = this.fX == null ? 0 : this.fX.length;
                        int[] iArr5 = new int[iB3 + length5];
                        if (length5 != 0) {
                            System.arraycopy(this.fX, 0, iArr5, 0, length5);
                        }
                        while (length5 < iArr5.length - 1) {
                            iArr5[length5] = peVar.qj();
                            peVar.qg();
                            length5++;
                        }
                        iArr5[length5] = peVar.qj();
                        this.fX = iArr5;
                        break;
                    case 26:
                        int iGo3 = peVar.go(peVar.qn());
                        int position3 = peVar.getPosition();
                        int i3 = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i3++;
                        }
                        peVar.gq(position3);
                        int length6 = this.fX == null ? 0 : this.fX.length;
                        int[] iArr6 = new int[i3 + length6];
                        if (length6 != 0) {
                            System.arraycopy(this.fX, 0, iArr6, 0, length6);
                        }
                        while (length6 < iArr6.length) {
                            iArr6[length6] = peVar.qj();
                            length6++;
                        }
                        this.fX = iArr6;
                        peVar.gp(iGo3);
                        break;
                    case 32:
                        int iB4 = pp.b(peVar, 32);
                        int length7 = this.fY == null ? 0 : this.fY.length;
                        int[] iArr7 = new int[iB4 + length7];
                        if (length7 != 0) {
                            System.arraycopy(this.fY, 0, iArr7, 0, length7);
                        }
                        while (length7 < iArr7.length - 1) {
                            iArr7[length7] = peVar.qj();
                            peVar.qg();
                            length7++;
                        }
                        iArr7[length7] = peVar.qj();
                        this.fY = iArr7;
                        break;
                    case 34:
                        int iGo4 = peVar.go(peVar.qn());
                        int position4 = peVar.getPosition();
                        int i4 = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i4++;
                        }
                        peVar.gq(position4);
                        int length8 = this.fY == null ? 0 : this.fY.length;
                        int[] iArr8 = new int[i4 + length8];
                        if (length8 != 0) {
                            System.arraycopy(this.fY, 0, iArr8, 0, length8);
                        }
                        while (length8 < iArr8.length) {
                            iArr8[length8] = peVar.qj();
                            length8++;
                        }
                        this.fY = iArr8;
                        peVar.gp(iGo4);
                        break;
                    case 40:
                        int iB5 = pp.b(peVar, 40);
                        int length9 = this.fZ == null ? 0 : this.fZ.length;
                        int[] iArr9 = new int[iB5 + length9];
                        if (length9 != 0) {
                            System.arraycopy(this.fZ, 0, iArr9, 0, length9);
                        }
                        while (length9 < iArr9.length - 1) {
                            iArr9[length9] = peVar.qj();
                            peVar.qg();
                            length9++;
                        }
                        iArr9[length9] = peVar.qj();
                        this.fZ = iArr9;
                        break;
                    case 42:
                        int iGo5 = peVar.go(peVar.qn());
                        int position5 = peVar.getPosition();
                        int i5 = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i5++;
                        }
                        peVar.gq(position5);
                        int length10 = this.fZ == null ? 0 : this.fZ.length;
                        int[] iArr10 = new int[i5 + length10];
                        if (length10 != 0) {
                            System.arraycopy(this.fZ, 0, iArr10, 0, length10);
                        }
                        while (length10 < iArr10.length) {
                            iArr10[length10] = peVar.qj();
                            length10++;
                        }
                        this.fZ = iArr10;
                        peVar.gp(iGo5);
                        break;
                    case 48:
                        int iB6 = pp.b(peVar, 48);
                        int length11 = this.ga == null ? 0 : this.ga.length;
                        int[] iArr11 = new int[iB6 + length11];
                        if (length11 != 0) {
                            System.arraycopy(this.ga, 0, iArr11, 0, length11);
                        }
                        while (length11 < iArr11.length - 1) {
                            iArr11[length11] = peVar.qj();
                            peVar.qg();
                            length11++;
                        }
                        iArr11[length11] = peVar.qj();
                        this.ga = iArr11;
                        break;
                    case 50:
                        int iGo6 = peVar.go(peVar.qn());
                        int position6 = peVar.getPosition();
                        int i6 = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i6++;
                        }
                        peVar.gq(position6);
                        int length12 = this.ga == null ? 0 : this.ga.length;
                        int[] iArr12 = new int[i6 + length12];
                        if (length12 != 0) {
                            System.arraycopy(this.ga, 0, iArr12, 0, length12);
                        }
                        while (length12 < iArr12.length) {
                            iArr12[length12] = peVar.qj();
                            length12++;
                        }
                        this.ga = iArr12;
                        peVar.gp(iGo6);
                        break;
                    case 56:
                        int iB7 = pp.b(peVar, 56);
                        int length13 = this.gb == null ? 0 : this.gb.length;
                        int[] iArr13 = new int[iB7 + length13];
                        if (length13 != 0) {
                            System.arraycopy(this.gb, 0, iArr13, 0, length13);
                        }
                        while (length13 < iArr13.length - 1) {
                            iArr13[length13] = peVar.qj();
                            peVar.qg();
                            length13++;
                        }
                        iArr13[length13] = peVar.qj();
                        this.gb = iArr13;
                        break;
                    case 58:
                        int iGo7 = peVar.go(peVar.qn());
                        int position7 = peVar.getPosition();
                        int i7 = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i7++;
                        }
                        peVar.gq(position7);
                        int length14 = this.gb == null ? 0 : this.gb.length;
                        int[] iArr14 = new int[i7 + length14];
                        if (length14 != 0) {
                            System.arraycopy(this.gb, 0, iArr14, 0, length14);
                        }
                        while (length14 < iArr14.length) {
                            iArr14[length14] = peVar.qj();
                            length14++;
                        }
                        this.gb = iArr14;
                        peVar.gp(iGo7);
                        break;
                    case 64:
                        int iB8 = pp.b(peVar, 64);
                        int length15 = this.gc == null ? 0 : this.gc.length;
                        int[] iArr15 = new int[iB8 + length15];
                        if (length15 != 0) {
                            System.arraycopy(this.gc, 0, iArr15, 0, length15);
                        }
                        while (length15 < iArr15.length - 1) {
                            iArr15[length15] = peVar.qj();
                            peVar.qg();
                            length15++;
                        }
                        iArr15[length15] = peVar.qj();
                        this.gc = iArr15;
                        break;
                    case 66:
                        int iGo8 = peVar.go(peVar.qn());
                        int position8 = peVar.getPosition();
                        int i8 = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i8++;
                        }
                        peVar.gq(position8);
                        int length16 = this.gc == null ? 0 : this.gc.length;
                        int[] iArr16 = new int[i8 + length16];
                        if (length16 != 0) {
                            System.arraycopy(this.gc, 0, iArr16, 0, length16);
                        }
                        while (length16 < iArr16.length) {
                            iArr16[length16] = peVar.qj();
                            length16++;
                        }
                        this.gc = iArr16;
                        peVar.gp(iGo8);
                        break;
                    case 72:
                        int iB9 = pp.b(peVar, 72);
                        int length17 = this.gd == null ? 0 : this.gd.length;
                        int[] iArr17 = new int[iB9 + length17];
                        if (length17 != 0) {
                            System.arraycopy(this.gd, 0, iArr17, 0, length17);
                        }
                        while (length17 < iArr17.length - 1) {
                            iArr17[length17] = peVar.qj();
                            peVar.qg();
                            length17++;
                        }
                        iArr17[length17] = peVar.qj();
                        this.gd = iArr17;
                        break;
                    case 74:
                        int iGo9 = peVar.go(peVar.qn());
                        int position9 = peVar.getPosition();
                        int i9 = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i9++;
                        }
                        peVar.gq(position9);
                        int length18 = this.gd == null ? 0 : this.gd.length;
                        int[] iArr18 = new int[i9 + length18];
                        if (length18 != 0) {
                            System.arraycopy(this.gd, 0, iArr18, 0, length18);
                        }
                        while (length18 < iArr18.length) {
                            iArr18[length18] = peVar.qj();
                            length18++;
                        }
                        this.gd = iArr18;
                        peVar.gp(iGo9);
                        break;
                    case 80:
                        int iB10 = pp.b(peVar, 80);
                        int length19 = this.ge == null ? 0 : this.ge.length;
                        int[] iArr19 = new int[iB10 + length19];
                        if (length19 != 0) {
                            System.arraycopy(this.ge, 0, iArr19, 0, length19);
                        }
                        while (length19 < iArr19.length - 1) {
                            iArr19[length19] = peVar.qj();
                            peVar.qg();
                            length19++;
                        }
                        iArr19[length19] = peVar.qj();
                        this.ge = iArr19;
                        break;
                    case 82:
                        int iGo10 = peVar.go(peVar.qn());
                        int position10 = peVar.getPosition();
                        int i10 = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i10++;
                        }
                        peVar.gq(position10);
                        int length20 = this.ge == null ? 0 : this.ge.length;
                        int[] iArr20 = new int[i10 + length20];
                        if (length20 != 0) {
                            System.arraycopy(this.ge, 0, iArr20, 0, length20);
                        }
                        while (length20 < iArr20.length) {
                            iArr20[length20] = peVar.qj();
                            length20++;
                        }
                        this.ge = iArr20;
                        peVar.gp(iGo10);
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

        public int hashCode() {
            return ((((((((((((((((((((pk.hashCode(this.fV) + 527) * 31) + pk.hashCode(this.fW)) * 31) + pk.hashCode(this.fX)) * 31) + pk.hashCode(this.fY)) * 31) + pk.hashCode(this.fZ)) * 31) + pk.hashCode(this.ga)) * 31) + pk.hashCode(this.gb)) * 31) + pk.hashCode(this.gc)) * 31) + pk.hashCode(this.gd)) * 31) + pk.hashCode(this.ge)) * 31) + qx();
        }

        public g m() {
            this.fV = pp.awL;
            this.fW = pp.awL;
            this.fX = pp.awL;
            this.fY = pp.awL;
            this.fZ = pp.awL;
            this.ga = pp.awL;
            this.gb = pp.awL;
            this.gc = pp.awL;
            this.gd = pp.awL;
            this.ge = pp.awL;
            this.awy = null;
            this.awJ = -1;
            return this;
        }
    }

    public static final class h extends pg<h> {
        public static final ph<d.a, h> gf = ph.a(11, h.class, 810);
        private static final h[] gg = new h[0];
        public int[] gh;
        public int[] gi;
        public int[] gj;
        public int gk;
        public int[] gl;
        public int gm;
        public int gn;

        public h() {
            n();
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (this.gh != null && this.gh.length > 0) {
                for (int i = 0; i < this.gh.length; i++) {
                    pfVar.s(1, this.gh[i]);
                }
            }
            if (this.gi != null && this.gi.length > 0) {
                for (int i2 = 0; i2 < this.gi.length; i2++) {
                    pfVar.s(2, this.gi[i2]);
                }
            }
            if (this.gj != null && this.gj.length > 0) {
                for (int i3 = 0; i3 < this.gj.length; i3++) {
                    pfVar.s(3, this.gj[i3]);
                }
            }
            if (this.gk != 0) {
                pfVar.s(4, this.gk);
            }
            if (this.gl != null && this.gl.length > 0) {
                for (int i4 = 0; i4 < this.gl.length; i4++) {
                    pfVar.s(5, this.gl[i4]);
                }
            }
            if (this.gm != 0) {
                pfVar.s(6, this.gm);
            }
            if (this.gn != 0) {
                pfVar.s(7, this.gn);
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iU;
            int iC = super.c();
            if (this.gh == null || this.gh.length <= 0) {
                iU = iC;
            } else {
                int iGv = 0;
                for (int i = 0; i < this.gh.length; i++) {
                    iGv += pf.gv(this.gh[i]);
                }
                iU = iC + iGv + (this.gh.length * 1);
            }
            if (this.gi != null && this.gi.length > 0) {
                int iGv2 = 0;
                for (int i2 = 0; i2 < this.gi.length; i2++) {
                    iGv2 += pf.gv(this.gi[i2]);
                }
                iU = iU + iGv2 + (this.gi.length * 1);
            }
            if (this.gj != null && this.gj.length > 0) {
                int iGv3 = 0;
                for (int i3 = 0; i3 < this.gj.length; i3++) {
                    iGv3 += pf.gv(this.gj[i3]);
                }
                iU = iU + iGv3 + (this.gj.length * 1);
            }
            if (this.gk != 0) {
                iU += pf.u(4, this.gk);
            }
            if (this.gl != null && this.gl.length > 0) {
                int iGv4 = 0;
                for (int i4 = 0; i4 < this.gl.length; i4++) {
                    iGv4 += pf.gv(this.gl[i4]);
                }
                iU = iU + iGv4 + (this.gl.length * 1);
            }
            if (this.gm != 0) {
                iU += pf.u(6, this.gm);
            }
            return this.gn != 0 ? iU + pf.u(7, this.gn) : iU;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof h)) {
                return false;
            }
            h hVar = (h) o;
            if (pk.equals(this.gh, hVar.gh) && pk.equals(this.gi, hVar.gi) && pk.equals(this.gj, hVar.gj) && this.gk == hVar.gk && pk.equals(this.gl, hVar.gl) && this.gm == hVar.gm && this.gn == hVar.gn) {
                return a(hVar);
            }
            return false;
        }

        public int hashCode() {
            return ((((((((((((((pk.hashCode(this.gh) + 527) * 31) + pk.hashCode(this.gi)) * 31) + pk.hashCode(this.gj)) * 31) + this.gk) * 31) + pk.hashCode(this.gl)) * 31) + this.gm) * 31) + this.gn) * 31) + qx();
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: i */
        public h b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 8:
                        int iB = pp.b(peVar, 8);
                        int length = this.gh == null ? 0 : this.gh.length;
                        int[] iArr = new int[iB + length];
                        if (length != 0) {
                            System.arraycopy(this.gh, 0, iArr, 0, length);
                        }
                        while (length < iArr.length - 1) {
                            iArr[length] = peVar.qj();
                            peVar.qg();
                            length++;
                        }
                        iArr[length] = peVar.qj();
                        this.gh = iArr;
                        break;
                    case 10:
                        int iGo = peVar.go(peVar.qn());
                        int position = peVar.getPosition();
                        int i = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i++;
                        }
                        peVar.gq(position);
                        int length2 = this.gh == null ? 0 : this.gh.length;
                        int[] iArr2 = new int[i + length2];
                        if (length2 != 0) {
                            System.arraycopy(this.gh, 0, iArr2, 0, length2);
                        }
                        while (length2 < iArr2.length) {
                            iArr2[length2] = peVar.qj();
                            length2++;
                        }
                        this.gh = iArr2;
                        peVar.gp(iGo);
                        break;
                    case 16:
                        int iB2 = pp.b(peVar, 16);
                        int length3 = this.gi == null ? 0 : this.gi.length;
                        int[] iArr3 = new int[iB2 + length3];
                        if (length3 != 0) {
                            System.arraycopy(this.gi, 0, iArr3, 0, length3);
                        }
                        while (length3 < iArr3.length - 1) {
                            iArr3[length3] = peVar.qj();
                            peVar.qg();
                            length3++;
                        }
                        iArr3[length3] = peVar.qj();
                        this.gi = iArr3;
                        break;
                    case 18:
                        int iGo2 = peVar.go(peVar.qn());
                        int position2 = peVar.getPosition();
                        int i2 = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i2++;
                        }
                        peVar.gq(position2);
                        int length4 = this.gi == null ? 0 : this.gi.length;
                        int[] iArr4 = new int[i2 + length4];
                        if (length4 != 0) {
                            System.arraycopy(this.gi, 0, iArr4, 0, length4);
                        }
                        while (length4 < iArr4.length) {
                            iArr4[length4] = peVar.qj();
                            length4++;
                        }
                        this.gi = iArr4;
                        peVar.gp(iGo2);
                        break;
                    case 24:
                        int iB3 = pp.b(peVar, 24);
                        int length5 = this.gj == null ? 0 : this.gj.length;
                        int[] iArr5 = new int[iB3 + length5];
                        if (length5 != 0) {
                            System.arraycopy(this.gj, 0, iArr5, 0, length5);
                        }
                        while (length5 < iArr5.length - 1) {
                            iArr5[length5] = peVar.qj();
                            peVar.qg();
                            length5++;
                        }
                        iArr5[length5] = peVar.qj();
                        this.gj = iArr5;
                        break;
                    case 26:
                        int iGo3 = peVar.go(peVar.qn());
                        int position3 = peVar.getPosition();
                        int i3 = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i3++;
                        }
                        peVar.gq(position3);
                        int length6 = this.gj == null ? 0 : this.gj.length;
                        int[] iArr6 = new int[i3 + length6];
                        if (length6 != 0) {
                            System.arraycopy(this.gj, 0, iArr6, 0, length6);
                        }
                        while (length6 < iArr6.length) {
                            iArr6[length6] = peVar.qj();
                            length6++;
                        }
                        this.gj = iArr6;
                        peVar.gp(iGo3);
                        break;
                    case 32:
                        this.gk = peVar.qj();
                        break;
                    case 40:
                        int iB4 = pp.b(peVar, 40);
                        int length7 = this.gl == null ? 0 : this.gl.length;
                        int[] iArr7 = new int[iB4 + length7];
                        if (length7 != 0) {
                            System.arraycopy(this.gl, 0, iArr7, 0, length7);
                        }
                        while (length7 < iArr7.length - 1) {
                            iArr7[length7] = peVar.qj();
                            peVar.qg();
                            length7++;
                        }
                        iArr7[length7] = peVar.qj();
                        this.gl = iArr7;
                        break;
                    case 42:
                        int iGo4 = peVar.go(peVar.qn());
                        int position4 = peVar.getPosition();
                        int i4 = 0;
                        while (peVar.qs() > 0) {
                            peVar.qj();
                            i4++;
                        }
                        peVar.gq(position4);
                        int length8 = this.gl == null ? 0 : this.gl.length;
                        int[] iArr8 = new int[i4 + length8];
                        if (length8 != 0) {
                            System.arraycopy(this.gl, 0, iArr8, 0, length8);
                        }
                        while (length8 < iArr8.length) {
                            iArr8[length8] = peVar.qj();
                            length8++;
                        }
                        this.gl = iArr8;
                        peVar.gp(iGo4);
                        break;
                    case 48:
                        this.gm = peVar.qj();
                        break;
                    case 56:
                        this.gn = peVar.qj();
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

        public h n() {
            this.gh = pp.awL;
            this.gi = pp.awL;
            this.gj = pp.awL;
            this.gk = 0;
            this.gl = pp.awL;
            this.gm = 0;
            this.gn = 0;
            this.awy = null;
            this.awJ = -1;
            return this;
        }
    }

    public static final class i extends pg<i> {
        private static volatile i[] go;
        public d.a gp;
        public d gq;
        public String name;

        public i() {
            p();
        }

        public static i[] o() {
            if (go == null) {
                synchronized (pk.awI) {
                    if (go == null) {
                        go = new i[0];
                    }
                }
            }
            return go;
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (!this.name.equals("")) {
                pfVar.b(1, this.name);
            }
            if (this.gp != null) {
                pfVar.a(2, this.gp);
            }
            if (this.gq != null) {
                pfVar.a(3, this.gq);
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iC = super.c();
            if (!this.name.equals("")) {
                iC += pf.j(1, this.name);
            }
            if (this.gp != null) {
                iC += pf.c(2, this.gp);
            }
            return this.gq != null ? iC + pf.c(3, this.gq) : iC;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof i)) {
                return false;
            }
            i iVar = (i) o;
            if (this.name == null) {
                if (iVar.name != null) {
                    return false;
                }
            } else if (!this.name.equals(iVar.name)) {
                return false;
            }
            if (this.gp == null) {
                if (iVar.gp != null) {
                    return false;
                }
            } else if (!this.gp.equals(iVar.gp)) {
                return false;
            }
            if (this.gq == null) {
                if (iVar.gq != null) {
                    return false;
                }
            } else if (!this.gq.equals(iVar.gq)) {
                return false;
            }
            return a(iVar);
        }

        public int hashCode() {
            return (((((this.gp == null ? 0 : this.gp.hashCode()) + (((this.name == null ? 0 : this.name.hashCode()) + 527) * 31)) * 31) + (this.gq != null ? this.gq.hashCode() : 0)) * 31) + qx();
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: j */
        public i b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 10:
                        this.name = peVar.readString();
                        break;
                    case 18:
                        if (this.gp == null) {
                            this.gp = new d.a();
                        }
                        peVar.a(this.gp);
                        break;
                    case 26:
                        if (this.gq == null) {
                            this.gq = new d();
                        }
                        peVar.a(this.gq);
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

        public i p() {
            this.name = "";
            this.gp = null;
            this.gq = null;
            this.awy = null;
            this.awJ = -1;
            return this;
        }
    }

    public static final class j extends pg<j> {
        public i[] gr;
        public f gs;
        public String gt;

        public j() {
            q();
        }

        public static j b(byte[] bArr) throws pl {
            return (j) pm.a(new j(), bArr);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        public void a(pf pfVar) throws IOException {
            if (this.gr != null && this.gr.length > 0) {
                for (int i = 0; i < this.gr.length; i++) {
                    i iVar = this.gr[i];
                    if (iVar != null) {
                        pfVar.a(1, iVar);
                    }
                }
            }
            if (this.gs != null) {
                pfVar.a(2, this.gs);
            }
            if (!this.gt.equals("")) {
                pfVar.b(3, this.gt);
            }
            super.a(pfVar);
        }

        @Override // com.google.android.gms.internal.pg, com.google.android.gms.internal.pm
        protected int c() {
            int iC = super.c();
            if (this.gr != null && this.gr.length > 0) {
                for (int i = 0; i < this.gr.length; i++) {
                    i iVar = this.gr[i];
                    if (iVar != null) {
                        iC += pf.c(1, iVar);
                    }
                }
            }
            if (this.gs != null) {
                iC += pf.c(2, this.gs);
            }
            return !this.gt.equals("") ? iC + pf.j(3, this.gt) : iC;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof j)) {
                return false;
            }
            j jVar = (j) o;
            if (!pk.equals(this.gr, jVar.gr)) {
                return false;
            }
            if (this.gs == null) {
                if (jVar.gs != null) {
                    return false;
                }
            } else if (!this.gs.equals(jVar.gs)) {
                return false;
            }
            if (this.gt == null) {
                if (jVar.gt != null) {
                    return false;
                }
            } else if (!this.gt.equals(jVar.gt)) {
                return false;
            }
            return a(jVar);
        }

        public int hashCode() {
            return (((((this.gs == null ? 0 : this.gs.hashCode()) + ((pk.hashCode(this.gr) + 527) * 31)) * 31) + (this.gt != null ? this.gt.hashCode() : 0)) * 31) + qx();
        }

        @Override // com.google.android.gms.internal.pm
        /* renamed from: k */
        public j b(pe peVar) throws IOException {
            while (true) {
                int iQg = peVar.qg();
                switch (iQg) {
                    case 0:
                        break;
                    case 10:
                        int iB = pp.b(peVar, 10);
                        int length = this.gr == null ? 0 : this.gr.length;
                        i[] iVarArr = new i[iB + length];
                        if (length != 0) {
                            System.arraycopy(this.gr, 0, iVarArr, 0, length);
                        }
                        while (length < iVarArr.length - 1) {
                            iVarArr[length] = new i();
                            peVar.a(iVarArr[length]);
                            peVar.qg();
                            length++;
                        }
                        iVarArr[length] = new i();
                        peVar.a(iVarArr[length]);
                        this.gr = iVarArr;
                        break;
                    case 18:
                        if (this.gs == null) {
                            this.gs = new f();
                        }
                        peVar.a(this.gs);
                        break;
                    case 26:
                        this.gt = peVar.readString();
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

        public j q() {
            this.gr = i.o();
            this.gs = null;
            this.gt = "";
            this.awy = null;
            this.awJ = -1;
            return this;
        }
    }
}
