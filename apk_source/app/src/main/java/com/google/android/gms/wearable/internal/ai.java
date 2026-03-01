package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class ai implements Parcelable.Creator<ah> {
    static void a(ah ahVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, ahVar.BR);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, ahVar.getRequestId());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, ahVar.getPath(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, ahVar.getData(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, ahVar.getSourceNodeId(), false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ec, reason: merged with bridge method [inline-methods] */
    public ah createFromParcel(Parcel parcel) {
        int iG = 0;
        String strO = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        byte[] bArrR = null;
        String strO2 = null;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 3:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    bArrR = com.google.android.gms.common.internal.safeparcel.a.r(parcel, iB);
                    break;
                case 5:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new ah(iG2, iG, strO2, bArrR, strO);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ge, reason: merged with bridge method [inline-methods] */
    public ah[] newArray(int i) {
        return new ah[i];
    }
}
