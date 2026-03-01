package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class ht implements Parcelable.Creator<hs> {
    static void a(hs hsVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, (Parcelable) hsVar.CD, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, hsVar.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, hsVar.CE);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 3, hsVar.CF);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, hsVar.oT, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, (Parcelable) hsVar.CG, i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: R, reason: merged with bridge method [inline-methods] */
    public hs[] newArray(int i) {
        return new hs[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: s, reason: merged with bridge method [inline-methods] */
    public hs createFromParcel(Parcel parcel) {
        int iG = 0;
        he heVar = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        long jI = 0;
        String strO = null;
        hg hgVar = null;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    hgVar = (hg) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, hg.CREATOR);
                    break;
                case 2:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 3:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 4:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 5:
                    heVar = (he) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, he.CREATOR);
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
        return new hs(iG2, hgVar, jI, iG, strO, heVar);
    }
}
