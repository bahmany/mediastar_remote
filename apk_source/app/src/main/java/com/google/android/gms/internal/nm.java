package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class nm implements Parcelable.Creator<nl> {
    static void a(nl nlVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, nlVar.versionCode);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, nlVar.packageName, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 3, nlVar.akG);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, nlVar.akH);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, nlVar.akI, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, nlVar.akJ, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, nlVar.akK);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cY */
    public nl createFromParcel(Parcel parcel) {
        String strO = null;
        int iG = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        boolean zC = true;
        String strO2 = null;
        int iG2 = 0;
        String strO3 = null;
        int iG3 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 4:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 6:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 7:
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
        return new nl(iG3, strO3, iG2, iG, strO2, strO, zC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: eO */
    public nl[] newArray(int i) {
        return new nl[i];
    }
}
