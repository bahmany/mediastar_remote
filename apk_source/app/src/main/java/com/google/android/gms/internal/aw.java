package com.google.android.gms.internal;

import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class aw implements Parcelable.Creator<av> {
    static void a(av avVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, avVar.versionCode);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, avVar.nT);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, avVar.extras, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, avVar.nU);
        com.google.android.gms.common.internal.safeparcel.b.b(parcel, 5, avVar.nV, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, avVar.nW);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 7, avVar.nX);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, avVar.nY);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, avVar.nZ, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 10, (Parcelable) avVar.oa, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 11, (Parcelable) avVar.ob, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 12, avVar.oc, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 13, avVar.od, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: b */
    public av createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        long jI = 0;
        Bundle bundleQ = null;
        int iG2 = 0;
        ArrayList<String> arrayListC = null;
        boolean zC = false;
        int iG3 = 0;
        boolean zC2 = false;
        String strO = null;
        bj bjVar = null;
        Location location = null;
        String strO2 = null;
        Bundle bundleQ2 = null;
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
                    bundleQ = com.google.android.gms.common.internal.safeparcel.a.q(parcel, iB);
                    break;
                case 4:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.C(parcel, iB);
                    break;
                case 6:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 7:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 8:
                    zC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 9:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 10:
                    bjVar = (bj) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, bj.CREATOR);
                    break;
                case 11:
                    location = (Location) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Location.CREATOR);
                    break;
                case 12:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 13:
                    bundleQ2 = com.google.android.gms.common.internal.safeparcel.a.q(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new av(iG, jI, bundleQ, iG2, arrayListC, zC, iG3, zC2, strO, bjVar, location, strO2, bundleQ2);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: e */
    public av[] newArray(int i) {
        return new av[i];
    }
}
