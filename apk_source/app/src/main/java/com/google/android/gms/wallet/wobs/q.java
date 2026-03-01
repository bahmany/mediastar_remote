package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class q implements Parcelable.Creator<p> {
    static void a(p pVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, pVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, pVar.auy, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, pVar.tG, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, (Parcelable) pVar.auC, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, (Parcelable) pVar.auD, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, (Parcelable) pVar.auE, i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dO, reason: merged with bridge method [inline-methods] */
    public p createFromParcel(Parcel parcel) {
        n nVar = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        n nVar2 = null;
        l lVar = null;
        String strO = null;
        String strO2 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    lVar = (l) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, l.CREATOR);
                    break;
                case 5:
                    nVar2 = (n) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, n.CREATOR);
                    break;
                case 6:
                    nVar = (n) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, n.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new p(iG, strO2, strO, lVar, nVar2, nVar);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fQ, reason: merged with bridge method [inline-methods] */
    public p[] newArray(int i) {
        return new p[i];
    }
}
