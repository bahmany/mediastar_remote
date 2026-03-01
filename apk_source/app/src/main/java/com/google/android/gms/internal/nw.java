package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public class nw implements Parcelable.Creator<nv> {
    static void a(nv nvVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        Set<Integer> set = nvVar.alR;
        if (set.contains(1)) {
            com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, nvVar.BR);
        }
        if (set.contains(2)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, nvVar.BL, true);
        }
        if (set.contains(4)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, (Parcelable) nvVar.amM, i, true);
        }
        if (set.contains(5)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, nvVar.amE, true);
        }
        if (set.contains(6)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, (Parcelable) nvVar.amN, i, true);
        }
        if (set.contains(7)) {
            com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, nvVar.uO, true);
        }
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dc */
    public nv createFromParcel(Parcel parcel) {
        String strO = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        HashSet hashSet = new HashSet();
        int iG = 0;
        nt ntVar = null;
        String strO2 = null;
        nt ntVar2 = null;
        String strO3 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    hashSet.add(1);
                    break;
                case 2:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(2);
                    break;
                case 3:
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
                case 4:
                    nt ntVar3 = (nt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, nt.CREATOR);
                    hashSet.add(4);
                    ntVar2 = ntVar3;
                    break;
                case 5:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(5);
                    break;
                case 6:
                    nt ntVar4 = (nt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, nt.CREATOR);
                    hashSet.add(6);
                    ntVar = ntVar4;
                    break;
                case 7:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    hashSet.add(7);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new nv(hashSet, iG, strO3, ntVar2, strO2, ntVar, strO);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: eT */
    public nv[] newArray(int i) {
        return new nv[i];
    }
}
