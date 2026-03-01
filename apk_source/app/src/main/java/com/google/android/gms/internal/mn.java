package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class mn implements Parcelable.Creator<mm> {
    static void a(mm mmVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, mmVar.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) mmVar.mf(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, mmVar.getInterval());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, mmVar.getPriority());
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cB, reason: merged with bridge method [inline-methods] */
    public mm createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        mi miVar = null;
        long jI = mm.afp;
        int iG2 = 102;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 2:
                    miVar = (mi) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, mi.CREATOR);
                    break;
                case 3:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 4:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
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
        return new mm(iG, miVar, jI, iG2);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: eq, reason: merged with bridge method [inline-methods] */
    public mm[] newArray(int i) {
        return new mm[i];
    }
}
