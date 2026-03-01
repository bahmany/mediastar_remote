package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.drive.DriveId;

/* loaded from: classes.dex */
public class au implements Parcelable.Creator<OpenContentsRequest> {
    static void a(OpenContentsRequest openContentsRequest, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, openContentsRequest.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) openContentsRequest.NV, i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 3, openContentsRequest.MN);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, openContentsRequest.Pp);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: aw, reason: merged with bridge method [inline-methods] */
    public OpenContentsRequest createFromParcel(Parcel parcel) {
        int iG;
        int iG2;
        DriveId driveId;
        int iG3;
        int i = 0;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        DriveId driveId2 = null;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    int i4 = i;
                    iG2 = i2;
                    driveId = driveId2;
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    iG = i4;
                    break;
                case 2:
                    iG3 = i3;
                    int i5 = i2;
                    driveId = (DriveId) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DriveId.CREATOR);
                    iG = i;
                    iG2 = i5;
                    break;
                case 3:
                    driveId = driveId2;
                    iG3 = i3;
                    int i6 = i;
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    iG = i6;
                    break;
                case 4:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    iG2 = i2;
                    driveId = driveId2;
                    iG3 = i3;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    iG = i;
                    iG2 = i2;
                    driveId = driveId2;
                    iG3 = i3;
                    break;
            }
            i3 = iG3;
            driveId2 = driveId;
            i2 = iG2;
            i = iG;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new OpenContentsRequest(i3, driveId2, i2, i);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bI, reason: merged with bridge method [inline-methods] */
    public OpenContentsRequest[] newArray(int i) {
        return new OpenContentsRequest[i];
    }
}
