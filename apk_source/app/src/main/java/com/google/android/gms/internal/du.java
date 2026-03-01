package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class du implements Parcelable.Creator<dv> {
    static void a(dv dvVar, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, dvVar.versionCode);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, dvVar.cl(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, dvVar.cm(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, dvVar.cn(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, dvVar.co(), false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: g */
    public dv createFromParcel(Parcel parcel) {
        IBinder iBinderP = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        IBinder iBinderP2 = null;
        IBinder iBinderP3 = null;
        IBinder iBinderP4 = null;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    iBinderP4 = com.google.android.gms.common.internal.safeparcel.a.p(parcel, iB);
                    break;
                case 3:
                    iBinderP3 = com.google.android.gms.common.internal.safeparcel.a.p(parcel, iB);
                    break;
                case 4:
                    iBinderP2 = com.google.android.gms.common.internal.safeparcel.a.p(parcel, iB);
                    break;
                case 5:
                    iBinderP = com.google.android.gms.common.internal.safeparcel.a.p(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new dv(iG, iBinderP4, iBinderP3, iBinderP2, iBinderP);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: n */
    public dv[] newArray(int i) {
        return new dv[i];
    }
}
