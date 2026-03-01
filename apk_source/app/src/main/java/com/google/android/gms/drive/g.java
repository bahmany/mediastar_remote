package com.google.android.gms.drive;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class g implements Parcelable.Creator<StorageStats> {
    static void a(StorageStats storageStats, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, storageStats.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, storageStats.Nt);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, storageStats.Nu);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, storageStats.Nv);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, storageStats.Nw);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 6, storageStats.Nx);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: R, reason: merged with bridge method [inline-methods] */
    public StorageStats createFromParcel(Parcel parcel) {
        int iG = 0;
        long jI = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        long jI2 = 0;
        long jI3 = 0;
        long jI4 = 0;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    jI4 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 3:
                    jI3 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 4:
                    jI2 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 5:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 6:
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
        return new StorageStats(iG2, jI4, jI3, jI2, jI, iG);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: aY, reason: merged with bridge method [inline-methods] */
    public StorageStats[] newArray(int i) {
        return new StorageStats[i];
    }
}
