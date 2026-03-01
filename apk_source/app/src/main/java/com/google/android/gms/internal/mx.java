package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class mx implements Parcelable.Creator<mw> {
    static void a(mw mwVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, mwVar.ahY, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, mwVar.versionCode);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, mwVar.ahZ, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, mwVar.Dv, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cF */
    public mw createFromParcel(Parcel parcel) {
        String strO = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        String strO2 = null;
        String strO3 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 2:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 1000:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new mw(iG, strO3, strO2, strO);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: eu */
    public mw[] newArray(int i) {
        return new mw[i];
    }
}
