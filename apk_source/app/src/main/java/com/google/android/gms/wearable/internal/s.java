package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class s implements Parcelable.Creator<r> {
    static void a(r rVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, rVar.versionCode);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, rVar.statusCode);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) rVar.avm, i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dW, reason: merged with bridge method [inline-methods] */
    public r createFromParcel(Parcel parcel) {
        int iG = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        com.google.android.gms.wearable.c cVar = null;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 3:
                    cVar = (com.google.android.gms.wearable.c) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, com.google.android.gms.wearable.c.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new r(iG2, iG, cVar);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fY, reason: merged with bridge method [inline-methods] */
    public r[] newArray(int i) {
        return new r[i];
    }
}
