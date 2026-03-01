package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class u implements Parcelable.Creator<t> {
    static void a(t tVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, tVar.versionCode);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, tVar.statusCode);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable[]) tVar.avn, i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dX, reason: merged with bridge method [inline-methods] */
    public t createFromParcel(Parcel parcel) {
        int iG = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        com.google.android.gms.wearable.c[] cVarArr = null;
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
                    cVarArr = (com.google.android.gms.wearable.c[]) com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB, com.google.android.gms.wearable.c.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new t(iG2, iG, cVarArr);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fZ, reason: merged with bridge method [inline-methods] */
    public t[] newArray(int i) {
        return new t[i];
    }
}
