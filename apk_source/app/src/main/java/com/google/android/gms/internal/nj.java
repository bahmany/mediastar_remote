package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class nj implements Parcelable.Creator<nh> {
    static void a(nh nhVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, nhVar.versionCode);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, nhVar.akw);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, nhVar.tag, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, nhVar.akx, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, nhVar.aky, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cX, reason: merged with bridge method [inline-methods] */
    public nh createFromParcel(Parcel parcel) {
        Bundle bundleQ = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        long jI = 0;
        byte[] bArrR = null;
        String strO = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 3:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    bArrR = com.google.android.gms.common.internal.safeparcel.a.r(parcel, iB);
                    break;
                case 5:
                    bundleQ = com.google.android.gms.common.internal.safeparcel.a.q(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new nh(iG, jI, strO, bArrR, bundleQ);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: eN, reason: merged with bridge method [inline-methods] */
    public nh[] newArray(int i) {
        return new nh[i];
    }
}
