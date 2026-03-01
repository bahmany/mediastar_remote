package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class dl implements Parcelable.Creator<dm> {
    static void a(dm dmVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, dmVar.versionCode);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) dmVar.rK, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, dmVar.cc(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, dmVar.cd(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, dmVar.ce(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, dmVar.cf(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, dmVar.rP, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, dmVar.rQ);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, dmVar.rR, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 10, dmVar.ch(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 11, dmVar.orientation);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 12, dmVar.rT);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 13, dmVar.rq, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 14, (Parcelable) dmVar.lD, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 15, dmVar.cg(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 17, (Parcelable) dmVar.rW, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 16, dmVar.rV, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: f */
    public dm createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        dj djVar = null;
        IBinder iBinderP = null;
        IBinder iBinderP2 = null;
        IBinder iBinderP3 = null;
        IBinder iBinderP4 = null;
        String strO = null;
        boolean zC = false;
        String strO2 = null;
        IBinder iBinderP5 = null;
        int iG2 = 0;
        int iG3 = 0;
        String strO3 = null;
        gt gtVar = null;
        IBinder iBinderP6 = null;
        String strO4 = null;
        x xVar = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    djVar = (dj) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, dj.CREATOR);
                    break;
                case 3:
                    iBinderP = com.google.android.gms.common.internal.safeparcel.a.p(parcel, iB);
                    break;
                case 4:
                    iBinderP2 = com.google.android.gms.common.internal.safeparcel.a.p(parcel, iB);
                    break;
                case 5:
                    iBinderP3 = com.google.android.gms.common.internal.safeparcel.a.p(parcel, iB);
                    break;
                case 6:
                    iBinderP4 = com.google.android.gms.common.internal.safeparcel.a.p(parcel, iB);
                    break;
                case 7:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 8:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 9:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 10:
                    iBinderP5 = com.google.android.gms.common.internal.safeparcel.a.p(parcel, iB);
                    break;
                case 11:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 12:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 13:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 14:
                    gtVar = (gt) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, gt.CREATOR);
                    break;
                case 15:
                    iBinderP6 = com.google.android.gms.common.internal.safeparcel.a.p(parcel, iB);
                    break;
                case 16:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 17:
                    xVar = (x) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, x.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new dm(iG, djVar, iBinderP, iBinderP2, iBinderP3, iBinderP4, strO, zC, strO2, iBinderP5, iG2, iG3, strO3, gtVar, iBinderP6, strO4, xVar);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: m */
    public dm[] newArray(int i) {
        return new dm[i];
    }
}
