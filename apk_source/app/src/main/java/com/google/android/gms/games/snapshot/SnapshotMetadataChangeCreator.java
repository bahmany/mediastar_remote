package com.google.android.gms.games.snapshot;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;

/* loaded from: classes.dex */
public class SnapshotMetadataChangeCreator implements Parcelable.Creator<SnapshotMetadataChange> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(SnapshotMetadataChange snapshotMetadataChange, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, snapshotMetadataChange.getDescription(), false);
        b.c(parcel, 1000, snapshotMetadataChange.getVersionCode());
        b.a(parcel, 2, snapshotMetadataChange.getPlayedTimeMillis(), false);
        b.a(parcel, 4, (Parcelable) snapshotMetadataChange.getCoverImageUri(), i, false);
        b.a(parcel, 5, (Parcelable) snapshotMetadataChange.lK(), i, false);
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    public SnapshotMetadataChange createFromParcel(Parcel parcel) {
        Uri uri = null;
        int iC = a.C(parcel);
        int iG = 0;
        com.google.android.gms.common.data.a aVar = null;
        Long lJ = null;
        String strO = null;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    strO = a.o(parcel, iB);
                    break;
                case 2:
                    lJ = a.j(parcel, iB);
                    break;
                case 4:
                    uri = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 5:
                    aVar = (com.google.android.gms.common.data.a) a.a(parcel, iB, com.google.android.gms.common.data.a.CREATOR);
                    break;
                case 1000:
                    iG = a.g(parcel, iB);
                    break;
                default:
                    a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new SnapshotMetadataChange(iG, strO, lJ, aVar, uri);
    }

    @Override // android.os.Parcelable.Creator
    public SnapshotMetadataChange[] newArray(int size) {
        return new SnapshotMetadataChange[size];
    }
}
