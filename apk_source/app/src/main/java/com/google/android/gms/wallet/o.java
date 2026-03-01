package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class o implements Parcelable.Creator<ProxyCard> {
    static void a(ProxyCard proxyCard, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, proxyCard.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, proxyCard.atu, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, proxyCard.atv, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, proxyCard.atw);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, proxyCard.atx);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dA, reason: merged with bridge method [inline-methods] */
    public ProxyCard createFromParcel(Parcel parcel) {
        String strO = null;
        int iG = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG2 = 0;
        String strO2 = null;
        int iG3 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
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
        return new ProxyCard(iG3, strO2, strO, iG2, iG);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fA, reason: merged with bridge method [inline-methods] */
    public ProxyCard[] newArray(int i) {
        return new ProxyCard[i];
    }
}
