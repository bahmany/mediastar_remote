package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.internal.jr;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class a implements Parcelable.Creator<CommonWalletObject> {
    static void a(CommonWalletObject commonWalletObject, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, commonWalletObject.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, commonWalletObject.fl, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, commonWalletObject.asP, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, commonWalletObject.name, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, commonWalletObject.asJ, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, commonWalletObject.asL, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, commonWalletObject.asM, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, commonWalletObject.asN, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, commonWalletObject.asO, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 10, commonWalletObject.state);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 11, commonWalletObject.asQ, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 12, (Parcelable) commonWalletObject.asR, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 13, commonWalletObject.asS, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 14, commonWalletObject.asT, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 15, commonWalletObject.asU, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 17, commonWalletObject.asW);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 16, commonWalletObject.asV, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 19, commonWalletObject.asY, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 18, commonWalletObject.asX, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 20, commonWalletObject.asZ, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dG, reason: merged with bridge method [inline-methods] */
    public CommonWalletObject createFromParcel(Parcel parcel) {
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
        int iG2 = 0;
        ArrayList arrayListHz = jr.hz();
        l lVar = null;
        ArrayList arrayListHz2 = jr.hz();
        String strO9 = null;
        String strO10 = null;
        ArrayList arrayListHz3 = jr.hz();
        boolean zC = false;
        ArrayList arrayListHz4 = jr.hz();
        ArrayList arrayListHz5 = jr.hz();
        ArrayList arrayListHz6 = jr.hz();
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
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 11:
                    arrayListHz = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, p.CREATOR);
                    break;
                case 12:
                    lVar = (l) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, l.CREATOR);
                    break;
                case 13:
                    arrayListHz2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, LatLng.CREATOR);
                    break;
                case 14:
                    strO9 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 15:
                    strO10 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 16:
                    arrayListHz3 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, d.CREATOR);
                    break;
                case 17:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 18:
                    arrayListHz4 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, n.CREATOR);
                    break;
                case 19:
                    arrayListHz5 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, j.CREATOR);
                    break;
                case 20:
                    arrayListHz6 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, n.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new CommonWalletObject(iG, strO, strO2, strO3, strO4, strO5, strO6, strO7, strO8, iG2, arrayListHz, lVar, arrayListHz2, strO9, strO10, arrayListHz3, zC, arrayListHz4, arrayListHz5, arrayListHz6);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fI, reason: merged with bridge method [inline-methods] */
    public CommonWalletObject[] newArray(int i) {
        return new CommonWalletObject[i];
    }
}
