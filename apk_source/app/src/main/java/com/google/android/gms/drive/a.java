package com.google.android.gms.drive;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class a implements Parcelable.Creator<Contents> {
    static void a(Contents contents, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, contents.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) contents.Kx, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 3, contents.uQ);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, contents.MN);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, (Parcelable) contents.MO, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, contents.MP);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: N */
    public Contents createFromParcel(Parcel parcel) {
        DriveId driveId = null;
        boolean zC = false;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        int iG2 = 0;
        ParcelFileDescriptor parcelFileDescriptor = null;
        int iG3 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    parcelFileDescriptor = (ParcelFileDescriptor) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, ParcelFileDescriptor.CREATOR);
                    break;
                case 3:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 4:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 5:
                    driveId = (DriveId) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DriveId.CREATOR);
                    break;
                case 6:
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
                case 7:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new Contents(iG3, parcelFileDescriptor, iG2, iG, driveId, zC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: aS */
    public Contents[] newArray(int i) {
        return new Contents[i];
    }
}
