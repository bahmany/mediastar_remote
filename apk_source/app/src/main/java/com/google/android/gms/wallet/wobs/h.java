package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class h implements Parcelable.Creator<g> {
    static void a(g gVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, gVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, gVar.aus);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, gVar.aut, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, gVar.auu);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, gVar.auv, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, gVar.auw);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 7, gVar.aux);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dJ, reason: merged with bridge method [inline-methods] */
    public g createFromParcel(Parcel parcel) {
        String strO = null;
        int iG = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        double dM = 0.0d;
        long jI = 0;
        int iG2 = -1;
        String strO2 = null;
        int iG3 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 3:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    dM = com.google.android.gms.common.internal.safeparcel.a.m(parcel, iB);
                    break;
                case 5:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 6:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 7:
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
        return new g(iG3, iG, strO2, dM, strO, jI, iG2);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fL, reason: merged with bridge method [inline-methods] */
    public g[] newArray(int i) {
        return new g[i];
    }
}
