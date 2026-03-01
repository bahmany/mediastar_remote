package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.internal.jr;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.wobs.p;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class j implements Parcelable.Creator<LoyaltyWalletObject> {
    static void a(LoyaltyWalletObject loyaltyWalletObject, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, loyaltyWalletObject.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, loyaltyWalletObject.fl, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, loyaltyWalletObject.asI, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, loyaltyWalletObject.asJ, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, loyaltyWalletObject.asK, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, loyaltyWalletObject.Dv, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, loyaltyWalletObject.asL, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, loyaltyWalletObject.asM, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, loyaltyWalletObject.asN, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 10, loyaltyWalletObject.asO, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 11, loyaltyWalletObject.asP, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 12, loyaltyWalletObject.state);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 13, loyaltyWalletObject.asQ, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 14, (Parcelable) loyaltyWalletObject.asR, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 15, loyaltyWalletObject.asS, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 17, loyaltyWalletObject.asU, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 16, loyaltyWalletObject.asT, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 19, loyaltyWalletObject.asW);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 18, loyaltyWalletObject.asV, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 21, loyaltyWalletObject.asY, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 20, loyaltyWalletObject.asX, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 23, (Parcelable) loyaltyWalletObject.ata, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 22, loyaltyWalletObject.asZ, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dv, reason: merged with bridge method [inline-methods] */
    public LoyaltyWalletObject createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        String strO = null;
        String strO2 = null;
        String strO3 = null;
        String strO4 = null;
        String strO5 = null;
        String strO6 = null;
        String strO7 = null;
        String strO8 = null;
        String strO9 = null;
        String strO10 = null;
        int iG2 = 0;
        ArrayList arrayListHz = jr.hz();
        com.google.android.gms.wallet.wobs.l lVar = null;
        ArrayList arrayListHz2 = jr.hz();
        String strO11 = null;
        String strO12 = null;
        ArrayList arrayListHz3 = jr.hz();
        boolean zC = false;
        ArrayList arrayListHz4 = jr.hz();
        ArrayList arrayListHz5 = jr.hz();
        ArrayList arrayListHz6 = jr.hz();
        com.google.android.gms.wallet.wobs.f fVar = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 5:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 6:
                    strO5 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 7:
                    strO6 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 8:
                    strO7 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 9:
                    strO8 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 10:
                    strO9 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 11:
                    strO10 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 12:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 13:
                    arrayListHz = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, p.CREATOR);
                    break;
                case 14:
                    lVar = (com.google.android.gms.wallet.wobs.l) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, com.google.android.gms.wallet.wobs.l.CREATOR);
                    break;
                case 15:
                    arrayListHz2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, LatLng.CREATOR);
                    break;
                case 16:
                    strO11 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 17:
                    strO12 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 18:
                    arrayListHz3 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, com.google.android.gms.wallet.wobs.d.CREATOR);
                    break;
                case 19:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 20:
                    arrayListHz4 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, com.google.android.gms.wallet.wobs.n.CREATOR);
                    break;
                case 21:
                    arrayListHz5 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, com.google.android.gms.wallet.wobs.j.CREATOR);
                    break;
                case 22:
                    arrayListHz6 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, com.google.android.gms.wallet.wobs.n.CREATOR);
                    break;
                case 23:
                    fVar = (com.google.android.gms.wallet.wobs.f) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, com.google.android.gms.wallet.wobs.f.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new LoyaltyWalletObject(iG, strO, strO2, strO3, strO4, strO5, strO6, strO7, strO8, strO9, strO10, iG2, arrayListHz, lVar, arrayListHz2, strO11, strO12, arrayListHz3, zC, arrayListHz4, arrayListHz5, arrayListHz6, fVar);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fv, reason: merged with bridge method [inline-methods] */
    public LoyaltyWalletObject[] newArray(int i) {
        return new LoyaltyWalletObject[i];
    }
}
