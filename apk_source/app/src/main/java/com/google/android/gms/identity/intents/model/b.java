package com.google.android.gms.identity.intents.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class b implements Parcelable.Creator<UserAddress> {
    static void a(UserAddress userAddress, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, userAddress.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, userAddress.name, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, userAddress.adC, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, userAddress.adD, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, userAddress.adE, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, userAddress.adF, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, userAddress.adG, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, userAddress.adH, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, userAddress.adI, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 10, userAddress.uW, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 11, userAddress.adJ, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 12, userAddress.adK, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 13, userAddress.adL, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 14, userAddress.adM);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 15, userAddress.adN, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 16, userAddress.adO, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cr, reason: merged with bridge method [inline-methods] */
    public UserAddress createFromParcel(Parcel parcel) {
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
        String strO11 = null;
        String strO12 = null;
        boolean zC = false;
        String strO13 = null;
        String strO14 = null;
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
                    strO11 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 13:
                    strO12 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 14:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 15:
                    strO13 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 16:
                    strO14 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new UserAddress(iG, strO, strO2, strO3, strO4, strO5, strO6, strO7, strO8, strO9, strO10, strO11, strO12, zC, strO13, strO14);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dZ, reason: merged with bridge method [inline-methods] */
    public UserAddress[] newArray(int i) {
        return new UserAddress[i];
    }
}
