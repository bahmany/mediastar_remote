package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class mh implements Parcelable.Creator<mg> {
    static void a(mg mgVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, mgVar.ma());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, mgVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, mgVar.me());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) mgVar.mf(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cy, reason: merged with bridge method [inline-methods] */
    public mg createFromParcel(Parcel parcel) {
        int iG = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG2 = -1;
        mi miVar = null;
        int iG3 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 3:
                    miVar = (mi) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, mi.CREATOR);
                    break;
                case 1000:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new mg(iG3, iG, iG2, miVar);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: en, reason: merged with bridge method [inline-methods] */
    public mg[] newArray(int i) {
        return new mg[i];
    }
}
