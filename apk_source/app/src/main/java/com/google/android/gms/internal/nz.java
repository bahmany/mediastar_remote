package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.internal.ny;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public class nz implements Parcelable.Creator<ny> {
    static void a(ny nyVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        Set<Integer> set = nyVar.alR;
        if (set.contains(1)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, nyVar.BR);
        }
        if (set.contains(2)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, nyVar.amP, true);
        }
        if (set.contains(3)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) nyVar.amQ, i, true);
        }
        if (set.contains(4)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, nyVar.amR, true);
        }
        if (set.contains(5)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, nyVar.amS, true);
        }
        if (set.contains(6)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 6, nyVar.amT);
        }
        if (set.contains(7)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, (Parcelable) nyVar.amU, i, true);
        }
        if (set.contains(8)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, nyVar.amV, true);
        }
        if (set.contains(9)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, nyVar.Nz, true);
        }
        if (set.contains(12)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 12, nyVar.om);
        }
        if (set.contains(14)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 14, nyVar.BL, true);
        }
        if (set.contains(15)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 15, (Parcelable) nyVar.amW, i, true);
        }
        if (set.contains(16)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 16, nyVar.amX);
        }
        if (set.contains(19)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 19, (Parcelable) nyVar.amY, i, true);
        }
        if (set.contains(18)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 18, nyVar.Fc, true);
        }
        if (set.contains(21)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 21, nyVar.ana);
        }
        if (set.contains(20)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 20, nyVar.amZ, true);
        }
        if (set.contains(23)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 23, nyVar.anc, true);
        }
        if (set.contains(22)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 22, nyVar.anb, true);
        }
        if (set.contains(25)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 25, nyVar.ane);
        }
        if (set.contains(24)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 24, nyVar.and);
        }
        if (set.contains(27)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 27, nyVar.uR, true);
        }
        if (set.contains(26)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 26, nyVar.anf, true);
        }
        if (set.contains(29)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 29, nyVar.anh);
        }
        if (set.contains(28)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 28, nyVar.ang, true);
        }
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dd */
    public ny createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        HashSet hashSet = new HashSet();
        int iG = 0;
        String strO = null;
        ny.a aVar = null;
        String strO2 = null;
        String strO3 = null;
        int iG2 = 0;
        ny.b bVar = null;
        String strO4 = null;
        String strO5 = null;
        int iG3 = 0;
        String strO6 = null;
        ny.c cVar = null;
        boolean zC = false;
        String strO7 = null;
        ny.d dVar = null;
        String strO8 = null;
        int iG4 = 0;
        ArrayList arrayListC = null;
        ArrayList arrayListC2 = null;
        int iG5 = 0;
        int iG6 = 0;
        String strO9 = null;
        String strO10 = null;
        ArrayList arrayListC3 = null;
        boolean zC2 = false;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    hashSet.add(1);
                    break;
                case 2:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(2);
                    break;
                case 3:
                    ny.a aVar2 = (ny.a) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ny.a.CREATOR);
                    hashSet.add(3);
                    aVar = aVar2;
                    break;
                case 4:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(4);
                    break;
                case 5:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(5);
                    break;
                case 6:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    hashSet.add(6);
                    break;
                case 7:
                    ny.b bVar2 = (ny.b) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ny.b.CREATOR);
                    hashSet.add(7);
                    bVar = bVar2;
                    break;
                case 8:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(8);
                    break;
                case 9:
                    strO5 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(9);
                    break;
                case 10:
                case 11:
                case 13:
                case 17:
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
                case 12:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    hashSet.add(12);
                    break;
                case 14:
                    strO6 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(14);
                    break;
                case 15:
                    ny.c cVar2 = (ny.c) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ny.c.CREATOR);
                    hashSet.add(15);
                    cVar = cVar2;
                    break;
                case 16:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    hashSet.add(16);
                    break;
                case 18:
                    strO7 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(18);
                    break;
                case 19:
                    ny.d dVar2 = (ny.d) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ny.d.CREATOR);
                    hashSet.add(19);
                    dVar = dVar2;
                    break;
                case 20:
                    strO8 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(20);
                    break;
                case 21:
                    iG4 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    hashSet.add(21);
                    break;
                case 22:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, ny.f.CREATOR);
                    hashSet.add(22);
                    break;
                case 23:
                    arrayListC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, ny.g.CREATOR);
                    hashSet.add(23);
                    break;
                case 24:
                    iG5 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    hashSet.add(24);
                    break;
                case 25:
                    iG6 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    hashSet.add(25);
                    break;
                case 26:
                    strO9 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(26);
                    break;
                case 27:
                    strO10 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(27);
                    break;
                case 28:
                    arrayListC3 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, ny.h.CREATOR);
                    hashSet.add(28);
                    break;
                case 29:
                    zC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    hashSet.add(29);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new ny(hashSet, iG, strO, aVar, strO2, strO3, iG2, bVar, strO4, strO5, iG3, strO6, cVar, zC, strO7, dVar, strO8, iG4, arrayListC, arrayListC2, iG5, iG6, strO9, strO10, arrayListC3, zC2);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: eU */
    public ny[] newArray(int i) {
        return new ny[i];
    }
}
