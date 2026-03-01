package com.google.android.gms.plus.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class j implements Parcelable.Creator<h> {
    static void a(h hVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, hVar.getAccountName(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, hVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, hVar.ne(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, hVar.nf(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, hVar.ng(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, hVar.nh(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, hVar.ni(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, hVar.nj(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, hVar.nk(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, (Parcelable) hVar.nl(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: da */
    public h createFromParcel(Parcel parcel) {
        PlusCommonExtras plusCommonExtras = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        String strO = null;
        String strO2 = null;
        String strO3 = null;
        String strO4 = null;
        String[] strArrA = null;
        String[] strArrA2 = null;
        String[] strArrA3 = null;
        String strO5 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    strO5 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 2:
                    strArrA3 = com.google.android.gms.common.internal.safeparcel.a.A(parcel, iB);
                    break;
                case 3:
                    strArrA2 = com.google.android.gms.common.internal.safeparcel.a.A(parcel, iB);
                    break;
                case 4:
                    strArrA = com.google.android.gms.common.internal.safeparcel.a.A(parcel, iB);
                    break;
                case 5:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 6:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 7:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 8:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 9:
                    plusCommonExtras = (PlusCommonExtras) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, PlusCommonExtras.CREATOR);
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
        return new h(iG, strO5, strArrA3, strArrA2, strArrA, strO4, strO3, strO2, strO, plusCommonExtras);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: eR */
    public h[] newArray(int i) {
        return new h[i];
    }
}
