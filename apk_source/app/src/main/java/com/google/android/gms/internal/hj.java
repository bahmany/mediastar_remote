package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class hj implements Parcelable.Creator<hi> {
    static void a(hi hiVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, hiVar.Ce, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, hiVar.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) hiVar.Cf, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, hiVar.Cg);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, hiVar.Ch, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: K, reason: merged with bridge method [inline-methods] */
    public hi[] newArray(int i) {
        return new hi[i];
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: n, reason: merged with bridge method [inline-methods] */
    public hi createFromParcel(Parcel parcel) {
        byte[] bArrR = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        int iG2 = -1;
        hq hqVar = null;
        String strO = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    hqVar = (hq) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, hq.CREATOR);
                    break;
                case 4:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    bArrR = com.google.android.gms.common.internal.safeparcel.a.r(parcel, iB);
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
        return new hi(iG, strO, hqVar, iG2, bArrR);
    }
}
