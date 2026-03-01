package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public class nu implements Parcelable.Creator<nt> {
    static void a(nt ntVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        Set<Integer> set = ntVar.alR;
        if (set.contains(1)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, ntVar.BR);
        }
        if (set.contains(2)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) ntVar.alS, i, true);
        }
        if (set.contains(3)) {
            com.google.android.gms.common.internal.safeparcel.b.b(parcel, 3, ntVar.alT, true);
        }
        if (set.contains(4)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, (Parcelable) ntVar.alU, i, true);
        }
        if (set.contains(5)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, ntVar.alV, true);
        }
        if (set.contains(6)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, ntVar.alW, true);
        }
        if (set.contains(7)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, ntVar.alX, true);
        }
        if (set.contains(8)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 8, ntVar.alY, true);
        }
        if (set.contains(9)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 9, ntVar.alZ);
        }
        if (set.contains(10)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 10, ntVar.ama, true);
        }
        if (set.contains(11)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 11, (Parcelable) ntVar.amb, i, true);
        }
        if (set.contains(12)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 12, ntVar.amc, true);
        }
        if (set.contains(13)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 13, ntVar.amd, true);
        }
        if (set.contains(14)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 14, ntVar.ame, true);
        }
        if (set.contains(15)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 15, (Parcelable) ntVar.amf, i, true);
        }
        if (set.contains(17)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 17, ntVar.amh, true);
        }
        if (set.contains(16)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 16, ntVar.amg, true);
        }
        if (set.contains(19)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 19, ntVar.ami, true);
        }
        if (set.contains(18)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 18, ntVar.ol, true);
        }
        if (set.contains(21)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 21, ntVar.amk, true);
        }
        if (set.contains(20)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 20, ntVar.amj, true);
        }
        if (set.contains(23)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 23, ntVar.Tg, true);
        }
        if (set.contains(22)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 22, ntVar.aml, true);
        }
        if (set.contains(25)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 25, ntVar.amn, true);
        }
        if (set.contains(24)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 24, ntVar.amm, true);
        }
        if (set.contains(27)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 27, ntVar.amp, true);
        }
        if (set.contains(26)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 26, ntVar.amo, true);
        }
        if (set.contains(29)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 29, (Parcelable) ntVar.amr, i, true);
        }
        if (set.contains(28)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 28, ntVar.amq, true);
        }
        if (set.contains(31)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 31, ntVar.amt, true);
        }
        if (set.contains(30)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 30, ntVar.ams, true);
        }
        if (set.contains(34)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 34, (Parcelable) ntVar.amv, i, true);
        }
        if (set.contains(32)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 32, ntVar.BL, true);
        }
        if (set.contains(33)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 33, ntVar.amu, true);
        }
        if (set.contains(38)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 38, ntVar.aea);
        }
        if (set.contains(39)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 39, ntVar.mName, true);
        }
        if (set.contains(36)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 36, ntVar.adZ);
        }
        if (set.contains(37)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 37, (Parcelable) ntVar.amw, i, true);
        }
        if (set.contains(42)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 42, ntVar.amz, true);
        }
        if (set.contains(43)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 43, ntVar.amA, true);
        }
        if (set.contains(40)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 40, (Parcelable) ntVar.amx, i, true);
        }
        if (set.contains(41)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 41, ntVar.amy, true);
        }
        if (set.contains(46)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 46, (Parcelable) ntVar.amD, i, true);
        }
        if (set.contains(47)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 47, ntVar.amE, true);
        }
        if (set.contains(44)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 44, ntVar.amB, true);
        }
        if (set.contains(45)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 45, ntVar.amC, true);
        }
        if (set.contains(51)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 51, ntVar.amI, true);
        }
        if (set.contains(50)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 50, (Parcelable) ntVar.amH, i, true);
        }
        if (set.contains(49)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 49, ntVar.amG, true);
        }
        if (set.contains(48)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 48, ntVar.amF, true);
        }
        if (set.contains(55)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 55, ntVar.amK, true);
        }
        if (set.contains(54)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 54, ntVar.uR, true);
        }
        if (set.contains(53)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 53, ntVar.uO, true);
        }
        if (set.contains(52)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 52, ntVar.amJ, true);
        }
        if (set.contains(56)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 56, ntVar.amL, true);
        }
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: db */
    public nt createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        HashSet hashSet = new HashSet();
        int iG = 0;
        nt ntVar = null;
        ArrayList<String> arrayListC = null;
        nt ntVar2 = null;
        String strO = null;
        String strO2 = null;
        String strO3 = null;
        ArrayList arrayListC2 = null;
        int iG2 = 0;
        ArrayList arrayListC3 = null;
        nt ntVar3 = null;
        ArrayList arrayListC4 = null;
        String strO4 = null;
        String strO5 = null;
        nt ntVar4 = null;
        String strO6 = null;
        String strO7 = null;
        String strO8 = null;
        ArrayList arrayListC5 = null;
        String strO9 = null;
        String strO10 = null;
        String strO11 = null;
        String strO12 = null;
        String strO13 = null;
        String strO14 = null;
        String strO15 = null;
        String strO16 = null;
        String strO17 = null;
        nt ntVar5 = null;
        String strO18 = null;
        String strO19 = null;
        String strO20 = null;
        String strO21 = null;
        nt ntVar6 = null;
        double dM = 0.0d;
        nt ntVar7 = null;
        double dM2 = 0.0d;
        String strO22 = null;
        nt ntVar8 = null;
        ArrayList arrayListC6 = null;
        String strO23 = null;
        String strO24 = null;
        String strO25 = null;
        String strO26 = null;
        nt ntVar9 = null;
        String strO27 = null;
        String strO28 = null;
        String strO29 = null;
        nt ntVar10 = null;
        String strO30 = null;
        String strO31 = null;
        String strO32 = null;
        String strO33 = null;
        String strO34 = null;
        String strO35 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    hashSet.add(1);
                    break;
                case 2:
                    nt ntVar11 = (nt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, nt.CREATOR);
                    hashSet.add(2);
                    ntVar = ntVar11;
                    break;
                case 3:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.C(parcel, iB);
                    hashSet.add(3);
                    break;
                case 4:
                    nt ntVar12 = (nt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, nt.CREATOR);
                    hashSet.add(4);
                    ntVar2 = ntVar12;
                    break;
                case 5:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(5);
                    break;
                case 6:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(6);
                    break;
                case 7:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(7);
                    break;
                case 8:
                    arrayListC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, nt.CREATOR);
                    hashSet.add(8);
                    break;
                case 9:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    hashSet.add(9);
                    break;
                case 10:
                    arrayListC3 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, nt.CREATOR);
                    hashSet.add(10);
                    break;
                case 11:
                    nt ntVar13 = (nt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, nt.CREATOR);
                    hashSet.add(11);
                    ntVar3 = ntVar13;
                    break;
                case 12:
                    arrayListC4 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, nt.CREATOR);
                    hashSet.add(12);
                    break;
                case 13:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(13);
                    break;
                case 14:
                    strO5 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(14);
                    break;
                case 15:
                    nt ntVar14 = (nt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, nt.CREATOR);
                    hashSet.add(15);
                    ntVar4 = ntVar14;
                    break;
                case 16:
                    strO6 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(16);
                    break;
                case 17:
                    strO7 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(17);
                    break;
                case 18:
                    strO8 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(18);
                    break;
                case 19:
                    arrayListC5 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, nt.CREATOR);
                    hashSet.add(19);
                    break;
                case 20:
                    strO9 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(20);
                    break;
                case 21:
                    strO10 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(21);
                    break;
                case 22:
                    strO11 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(22);
                    break;
                case 23:
                    strO12 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(23);
                    break;
                case 24:
                    strO13 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(24);
                    break;
                case 25:
                    strO14 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(25);
                    break;
                case 26:
                    strO15 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(26);
                    break;
                case 27:
                    strO16 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(27);
                    break;
                case 28:
                    strO17 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(28);
                    break;
                case 29:
                    nt ntVar15 = (nt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, nt.CREATOR);
                    hashSet.add(29);
                    ntVar5 = ntVar15;
                    break;
                case 30:
                    strO18 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(30);
                    break;
                case 31:
                    strO19 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(31);
                    break;
                case 32:
                    strO20 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(32);
                    break;
                case 33:
                    strO21 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(33);
                    break;
                case 34:
                    nt ntVar16 = (nt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, nt.CREATOR);
                    hashSet.add(34);
                    ntVar6 = ntVar16;
                    break;
                case 35:
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
                case 36:
                    dM = com.google.android.gms.common.internal.safeparcel.a.m(parcel, iB);
                    hashSet.add(36);
                    break;
                case 37:
                    nt ntVar17 = (nt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, nt.CREATOR);
                    hashSet.add(37);
                    ntVar7 = ntVar17;
                    break;
                case 38:
                    dM2 = com.google.android.gms.common.internal.safeparcel.a.m(parcel, iB);
                    hashSet.add(38);
                    break;
                case 39:
                    strO22 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(39);
                    break;
                case 40:
                    nt ntVar18 = (nt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, nt.CREATOR);
                    hashSet.add(40);
                    ntVar8 = ntVar18;
                    break;
                case 41:
                    arrayListC6 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, nt.CREATOR);
                    hashSet.add(41);
                    break;
                case 42:
                    strO23 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(42);
                    break;
                case 43:
                    strO24 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(43);
                    break;
                case 44:
                    strO25 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(44);
                    break;
                case 45:
                    strO26 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(45);
                    break;
                case 46:
                    nt ntVar19 = (nt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, nt.CREATOR);
                    hashSet.add(46);
                    ntVar9 = ntVar19;
                    break;
                case 47:
                    strO27 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(47);
                    break;
                case 48:
                    strO28 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(48);
                    break;
                case 49:
                    strO29 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(49);
                    break;
                case 50:
                    nt ntVar20 = (nt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, nt.CREATOR);
                    hashSet.add(50);
                    ntVar10 = ntVar20;
                    break;
                case 51:
                    strO30 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(51);
                    break;
                case 52:
                    strO31 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(52);
                    break;
                case 53:
                    strO32 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(53);
                    break;
                case 54:
                    strO33 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(54);
                    break;
                case 55:
                    strO34 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(55);
                    break;
                case 56:
                    strO35 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(56);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new nt(hashSet, iG, ntVar, arrayListC, ntVar2, strO, strO2, strO3, arrayListC2, iG2, arrayListC3, ntVar3, arrayListC4, strO4, strO5, ntVar4, strO6, strO7, strO8, arrayListC5, strO9, strO10, strO11, strO12, strO13, strO14, strO15, strO16, strO17, ntVar5, strO18, strO19, strO20, strO21, ntVar6, dM, ntVar7, dM2, strO22, ntVar8, arrayListC6, strO23, strO24, strO25, strO26, ntVar9, strO27, strO28, strO29, ntVar10, strO30, strO31, strO32, strO33, strO34, strO35);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: eS */
    public nt[] newArray(int i) {
        return new nt[i];
    }
}
