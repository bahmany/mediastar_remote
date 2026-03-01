package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;

/* loaded from: classes.dex */
public class j implements Parcelable.Creator<CreateFileRequest> {
    static void a(CreateFileRequest createFileRequest, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, createFileRequest.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) createFileRequest.Of, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) createFileRequest.Od, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, (Parcelable) createFileRequest.NX, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, createFileRequest.Oe, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, createFileRequest.Og);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, createFileRequest.Nf, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 8, createFileRequest.Oh);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 9, createFileRequest.Oi);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ac */
    public CreateFileRequest createFromParcel(Parcel parcel) {
        int iG = 0;
        String strO = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG2 = 0;
        boolean zC = false;
        Integer numH = null;
        Contents contents = null;
        MetadataBundle metadataBundle = null;
        DriveId driveId = null;
        int iG3 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    driveId = (DriveId) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DriveId.CREATOR);
                    break;
                case 3:
                    metadataBundle = (MetadataBundle) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, MetadataBundle.CREATOR);
                    break;
                case 4:
                    contents = (Contents) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, Contents.CREATOR);
                    break;
                case 5:
                    numH = com.google.android.gms.common.internal.safeparcel.a.h(parcel, iB);
                    break;
                case 6:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 7:
                    strO = com.google.android.gms.common.internal.safeparcel.a.o(parcel, iB);
                    break;
                case 8:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 9:
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
        return new CreateFileRequest(iG3, driveId, metadataBundle, contents, numH, zC, strO, iG2, iG);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bm */
    public CreateFileRequest[] newArray(int i) {
        return new CreateFileRequest[i];
    }
}
