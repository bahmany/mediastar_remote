package com.google.android.gms.games.snapshot;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.gms.drive.Contents;

/* loaded from: classes.dex */
public class SnapshotContentsCreator implements Parcelable.Creator<SnapshotContents> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(SnapshotContents snapshotContents, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, (Parcelable) snapshotContents.getContents(), i, false);
        b.c(parcel, 1000, snapshotContents.getVersionCode());
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    public SnapshotContents createFromParcel(Parcel parcel) {
        int iC = a.C(parcel);
        int iG = 0;
        Contents contents = null;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    contents = (Contents) a.a(parcel, iB, Contents.CREATOR);
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
        return new SnapshotContents(iG, contents);
    }

    @Override // android.os.Parcelable.Creator
    public SnapshotContents[] newArray(int size) {
        return new SnapshotContents[size];
    }
}
