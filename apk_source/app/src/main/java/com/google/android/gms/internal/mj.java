package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class mj implements Parcelable.Creator<mi> {
    static void a(mi miVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, miVar.afg, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, miVar.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, miVar.mg(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, miVar.mh());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, miVar.afj, false);
        com.google.android.gms.common.internal.safeparcel.b.b(parcel, 6, miVar.afk, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cz */
    public mi createFromParcel(Parcel parcel) {
        boolean zC = false;
        ArrayList<String> arrayListC = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        ArrayList arrayListC2 = null;
        String strO = null;
        ArrayList arrayListC3 = null;
        int iG = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    arrayListC3 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, mo.CREATOR);
                    break;
                case 2:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 4:
                    arrayListC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, ms.CREATOR);
                    break;
                case 6:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.C(parcel, iB);
                    break;
                case 1000:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new mi(iG, arrayListC3, strO, zC, arrayListC2, arrayListC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: eo */
    public mi[] newArray(int i) {
        return new mi[i];
    }
}
