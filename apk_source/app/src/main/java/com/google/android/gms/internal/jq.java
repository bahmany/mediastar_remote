package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class jq implements Parcelable.Creator<jp> {
    static void a(jp jpVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, jpVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, jpVar.hx(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) jpVar.hy(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: M */
    public jp createFromParcel(Parcel parcel) {
        jm jmVar = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        Parcel parcelD = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    parcelD = com.google.android.gms.common.internal.safeparcel.a.D(parcel, iB);
                    break;
                case 3:
                    jmVar = (jm) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, jm.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new jp(iG, parcelD, jmVar);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: aM */
    public jp[] newArray(int i) {
        return new jp[i];
    }
}
