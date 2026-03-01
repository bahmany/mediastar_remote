package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class mr implements Parcelable.Creator<mq> {
    static void a(mq mqVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, mqVar.getName(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, mqVar.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) mqVar.mj(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, mqVar.getAddress(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, mqVar.mk(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, mqVar.getPhoneNumber(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, mqVar.ml(), false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cD, reason: merged with bridge method [inline-methods] */
    public mq createFromParcel(Parcel parcel) {
        String strO = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        String strO2 = null;
        ArrayList arrayListC = null;
        String strO3 = null;
        LatLng latLng = null;
        String strO4 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 2:
                    latLng = (LatLng) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, LatLng.CREATOR);
                    break;
                case 3:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, mo.CREATOR);
                    break;
                case 5:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 6:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
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
        return new mq(iG, strO4, latLng, strO3, arrayListC, strO2, strO);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: es, reason: merged with bridge method [inline-methods] */
    public mq[] newArray(int i) {
        return new mq[i];
    }
}
