package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.internal.ji;

/* loaded from: classes.dex */
public class jk implements Parcelable.Creator<ji.a> {
    static void a(ji.a aVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, aVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, aVar.hd());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, aVar.hj());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, aVar.he());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, aVar.hk());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, aVar.hl(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 7, aVar.hm());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, aVar.ho(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, (Parcelable) aVar.hq(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: I, reason: merged with bridge method [inline-methods] */
    public ji.a createFromParcel(Parcel parcel) {
        jd jdVar = null;
        int iG = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        String strO = null;
        String strO2 = null;
        boolean zC = false;
        int iG2 = 0;
        boolean zC2 = false;
        int iG3 = 0;
        int iG4 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG4 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 3:
                    zC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 4:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 6:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 7:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 8:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 9:
                    jdVar = (jd) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, jd.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new ji.a(iG4, iG3, zC2, iG2, zC, strO2, iG, strO, jdVar);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: aI, reason: merged with bridge method [inline-methods] */
    public ji.a[] newArray(int i) {
        return new ji.a[i];
    }
}
