package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class mc implements Parcelable.Creator<mb> {
    static void a(mb mbVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, mbVar.getRequestId(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, mbVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, mbVar.getExpirationTime());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, mbVar.lY());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, mbVar.getLatitude());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, mbVar.getLongitude());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, mbVar.lZ());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 7, mbVar.ma());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 8, mbVar.getNotificationResponsiveness());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 9, mbVar.mb());
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cw */
    public mb createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        String strO = null;
        int iG2 = 0;
        short sF = 0;
        double dM = 0.0d;
        double dM2 = 0.0d;
        float fL = 0.0f;
        long jI = 0;
        int iG3 = 0;
        int iG4 = -1;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 2:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 3:
                    sF = com.google.android.gms.common.internal.safeparcel.a.f(parcel, iB);
                    break;
                case 4:
                    dM = com.google.android.gms.common.internal.safeparcel.a.m(parcel, iB);
                    break;
                case 5:
                    dM2 = com.google.android.gms.common.internal.safeparcel.a.m(parcel, iB);
                    break;
                case 6:
                    fL = com.google.android.gms.common.internal.safeparcel.a.l(parcel, iB);
                    break;
                case 7:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 8:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 9:
                    iG4 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
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
        return new mb(iG, strO, iG2, sF, dM, dM2, fL, jI, iG3, iG4);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: el */
    public mb[] newArray(int i) {
        return new mb[i];
    }
}
