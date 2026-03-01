package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class av implements Parcelable.Creator<au> {
    static void a(au auVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, auVar.versionCode);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, auVar.statusCode);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, auVar.avC);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, auVar.avE, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ei, reason: merged with bridge method [inline-methods] */
    public au createFromParcel(Parcel parcel) {
        int iG = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        long jI = 0;
        ArrayList arrayListC = null;
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
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 4:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, am.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new au(iG2, iG, jI, arrayListC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: gk, reason: merged with bridge method [inline-methods] */
    public au[] newArray(int i) {
        return new au[i];
    }
}
