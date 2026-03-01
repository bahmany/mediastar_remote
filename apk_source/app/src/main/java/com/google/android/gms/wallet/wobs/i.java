package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class i implements Parcelable.Creator<f> {
    static void a(f fVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, fVar.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, fVar.label, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) fVar.aur, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, fVar.type, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, (Parcelable) fVar.asR, i, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dK, reason: merged with bridge method [inline-methods] */
    public f createFromParcel(Parcel parcel) {
        l lVar = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        String strO = null;
        g gVar = null;
        String strO2 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    gVar = (g) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, g.CREATOR);
                    break;
                case 4:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 5:
                    lVar = (l) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, l.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new f(iG, strO2, gVar, strO, lVar);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: fM, reason: merged with bridge method [inline-methods] */
    public f[] newArray(int i) {
        return new f[i];
    }
}
