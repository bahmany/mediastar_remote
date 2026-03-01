package com.google.android.gms.internal;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class fj implements Parcelable.Creator<fi> {
    static void a(fi fiVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, fiVar.versionCode);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, fiVar.tw, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) fiVar.tx, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, (Parcelable) fiVar.lH, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, fiVar.lA, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, (Parcelable) fiVar.applicationInfo, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, (Parcelable) fiVar.ty, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, fiVar.tz, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, fiVar.tA, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 10, fiVar.tB, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 11, (Parcelable) fiVar.lD, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 12, fiVar.tC, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 13, fiVar.tD);
        com.google.android.gms.common.internal.safeparcel.b.b(parcel, 14, fiVar.lS, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 15, fiVar.tE, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 16, fiVar.tF);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: h */
    public fi createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        Bundle bundleQ = null;
        av avVar = null;
        ay ayVar = null;
        String strO = null;
        ApplicationInfo applicationInfo = null;
        PackageInfo packageInfo = null;
        String strO2 = null;
        String strO3 = null;
        String strO4 = null;
        gt gtVar = null;
        Bundle bundleQ2 = null;
        int iG2 = 0;
        ArrayList<String> arrayListC = null;
        Bundle bundleQ3 = null;
        boolean zC = false;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    bundleQ = com.google.android.gms.common.internal.safeparcel.a.q(parcel, iB);
                    break;
                case 3:
                    avVar = (av) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, av.CREATOR);
                    break;
                case 4:
                    ayVar = (ay) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ay.CREATOR);
                    break;
                case 5:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 6:
                    applicationInfo = (ApplicationInfo) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ApplicationInfo.CREATOR);
                    break;
                case 7:
                    packageInfo = (PackageInfo) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, PackageInfo.CREATOR);
                    break;
                case 8:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 9:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 10:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 11:
                    gtVar = (gt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, gt.CREATOR);
                    break;
                case 12:
                    bundleQ2 = com.google.android.gms.common.internal.safeparcel.a.q(parcel, iB);
                    break;
                case 13:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 14:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.C(parcel, iB);
                    break;
                case 15:
                    bundleQ3 = com.google.android.gms.common.internal.safeparcel.a.q(parcel, iB);
                    break;
                case 16:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new fi(iG, bundleQ, avVar, ayVar, strO, applicationInfo, packageInfo, strO2, strO3, strO4, gtVar, bundleQ2, iG2, arrayListC, bundleQ3, zC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: p */
    public fi[] newArray(int i) {
        return new fi[i];
    }
}
