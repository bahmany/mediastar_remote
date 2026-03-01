package com.google.android.gms.drive;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class h implements Parcelable.Creator<UserMetadata> {
    static void a(UserMetadata userMetadata, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, userMetadata.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, userMetadata.Ny, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, userMetadata.Nz, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, userMetadata.NA, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, userMetadata.NB);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, userMetadata.NC, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: S, reason: merged with bridge method [inline-methods] */
    public UserMetadata createFromParcel(Parcel parcel) {
        boolean zC = false;
        String strO = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        String strO2 = null;
        String strO3 = null;
        String strO4 = null;
        int iG = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    strO4 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 3:
                    strO3 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 4:
                    strO2 = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 5:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 6:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new UserMetadata(iG, strO4, strO3, strO2, zC, strO);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: aZ, reason: merged with bridge method [inline-methods] */
    public UserMetadata[] newArray(int i) {
        return new UserMetadata[i];
    }
}
