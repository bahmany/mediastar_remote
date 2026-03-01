package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class bk implements Parcelable.Creator<bj> {
    static void a(bj bjVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, bjVar.versionCode);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, bjVar.oH);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 3, bjVar.backgroundColor);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, bjVar.oI);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, bjVar.oJ);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 6, bjVar.oK);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 7, bjVar.oL);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 8, bjVar.oM);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 9, bjVar.oN);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 10, bjVar.oO, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 11, bjVar.oP);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 12, bjVar.oQ, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 13, bjVar.oR);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 14, bjVar.oS);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 15, bjVar.oT, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: d */
    public bj createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        int iG2 = 0;
        int iG3 = 0;
        int iG4 = 0;
        int iG5 = 0;
        int iG6 = 0;
        int iG7 = 0;
        int iG8 = 0;
        int iG9 = 0;
        String strO = null;
        int iG10 = 0;
        String strO2 = null;
        int iG11 = 0;
        int iG12 = 0;
        String strO3 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 3:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 4:
                    iG4 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    iG5 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 6:
                    iG6 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 7:
                    iG7 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 8:
                    iG8 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 9:
                    iG9 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 10:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 11:
                    iG10 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 12:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 13:
                    iG11 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 14:
                    iG12 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 15:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new bj(iG, iG2, iG3, iG4, iG5, iG6, iG7, iG8, iG9, strO, iG10, strO2, iG11, iG12, strO3);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: h */
    public bj[] newArray(int i) {
        return new bj[i];
    }
}
