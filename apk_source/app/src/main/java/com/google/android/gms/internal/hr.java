package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class hr implements Parcelable.Creator<hq> {
    static void a(hq hqVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, hqVar.name, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, hqVar.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, hqVar.Co, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, hqVar.Cp);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, hqVar.weight);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, hqVar.Cq);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, hqVar.Cr, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, (Parcelable[]) hqVar.Cs, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, hqVar.Ct, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 11, hqVar.Cu, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: Q, reason: merged with bridge method [inline-methods] */
    public hq[] newArray(int i) {
        return new hq[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: r, reason: merged with bridge method [inline-methods] */
    public hq createFromParcel(Parcel parcel) {
        boolean zC = false;
        String strO = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 1;
        int[] iArrU = null;
        hk[] hkVarArr = null;
        String strO2 = null;
        boolean zC2 = false;
        String strO3 = null;
        String strO4 = null;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 2:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    zC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 4:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 6:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 7:
                    hkVarArr = (hk[]) com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB, hk.CREATOR);
                    break;
                case 8:
                    iArrU = com.google.android.gms.common.internal.safeparcel.a.u(parcel, iB);
                    break;
                case 11:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 1000:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new hq(iG2, strO4, strO3, zC2, iG, zC, strO2, hkVarArr, iArrU, strO);
    }
}
