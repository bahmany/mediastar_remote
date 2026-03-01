package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class e implements Parcelable.Creator<d> {
    static void a(d dVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, dVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) dVar.aso, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) dVar.asp, i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dq, reason: merged with bridge method [inline-methods] */
    public d createFromParcel(Parcel parcel) {
        OfferWalletObject offerWalletObject;
        LoyaltyWalletObject loyaltyWalletObject;
        int iG;
        OfferWalletObject offerWalletObject2 = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int i = 0;
        LoyaltyWalletObject loyaltyWalletObject2 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    OfferWalletObject offerWalletObject3 = offerWalletObject2;
                    loyaltyWalletObject = loyaltyWalletObject2;
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    offerWalletObject = offerWalletObject3;
                    break;
                case 2:
                    LoyaltyWalletObject loyaltyWalletObject3 = (LoyaltyWalletObject) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, LoyaltyWalletObject.CREATOR);
                    iG = i;
                    offerWalletObject = offerWalletObject2;
                    loyaltyWalletObject = loyaltyWalletObject3;
                    break;
                case 3:
                    offerWalletObject = (OfferWalletObject) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, OfferWalletObject.CREATOR);
                    loyaltyWalletObject = loyaltyWalletObject2;
                    iG = i;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    offerWalletObject = offerWalletObject2;
                    loyaltyWalletObject = loyaltyWalletObject2;
                    iG = i;
                    break;
            }
            i = iG;
            loyaltyWalletObject2 = loyaltyWalletObject;
            offerWalletObject2 = offerWalletObject;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new d(i, loyaltyWalletObject2, offerWalletObject2);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fq, reason: merged with bridge method [inline-methods] */
    public d[] newArray(int i) {
        return new d[i];
    }
}
