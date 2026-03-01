package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class op implements Parcelable.Creator<oo> {
    static void a(oo ooVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, ooVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, ooVar.atD, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, ooVar.atE, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dC, reason: merged with bridge method [inline-methods] */
    public oo createFromParcel(Parcel parcel) {
        String[] strArrA = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        byte[][] bArrS = (byte[][]) null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    strArrA = com.google.android.gms.common.internal.safeparcel.a.A(parcel, iB);
                    break;
                case 3:
                    bArrS = com.google.android.gms.common.internal.safeparcel.a.s(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new oo(iG, strArrA, bArrS);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fC, reason: merged with bridge method [inline-methods] */
    public oo[] newArray(int i) {
        return new oo[i];
    }
}
