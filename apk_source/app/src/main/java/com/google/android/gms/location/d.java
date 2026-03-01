package com.google.android.gms.location;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class d implements Parcelable.Creator<c> {
    static void a(c cVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, cVar.aem);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, cVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, cVar.aen);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, cVar.aeo);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ct */
    public c createFromParcel(Parcel parcel) {
        int iG = 1;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG2 = 0;
        long jI = 0;
        int iG3 = 1;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 3:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 1000:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new c(iG2, iG3, iG, jI);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: eg */
    public c[] newArray(int i) {
        return new c[i];
    }
}
