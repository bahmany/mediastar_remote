package com.google.android.gms.games.snapshot;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;

/* loaded from: classes.dex */
public class SnapshotEntityCreator implements Parcelable.Creator<SnapshotEntity> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(SnapshotEntity snapshotEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, (Parcelable) snapshotEntity.getMetadata(), i, false);
        b.c(parcel, 1000, snapshotEntity.getVersionCode());
        b.a(parcel, 3, (Parcelable) snapshotEntity.getSnapshotContents(), i, false);
        b.H(parcel, iD);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public SnapshotEntity createFromParcel(Parcel parcel) {
        SnapshotContents snapshotContents;
        SnapshotMetadataEntity snapshotMetadataEntity;
        int iG;
        SnapshotContents snapshotContents2 = null;
        int iC = a.C(parcel);
        int i = 0;
        SnapshotMetadataEntity snapshotMetadataEntity2 = null;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    SnapshotMetadataEntity snapshotMetadataEntity3 = (SnapshotMetadataEntity) a.a(parcel, iB, SnapshotMetadataEntity.CREATOR);
                    iG = i;
                    snapshotContents = snapshotContents2;
                    snapshotMetadataEntity = snapshotMetadataEntity3;
                    break;
                case 3:
                    snapshotContents = (SnapshotContents) a.a(parcel, iB, SnapshotContents.CREATOR);
                    snapshotMetadataEntity = snapshotMetadataEntity2;
                    iG = i;
                    break;
                case 1000:
                    SnapshotContents snapshotContents3 = snapshotContents2;
                    snapshotMetadataEntity = snapshotMetadataEntity2;
                    iG = a.g(parcel, iB);
                    snapshotContents = snapshotContents3;
                    break;
                default:
                    a.b(parcel, iB);
                    snapshotContents = snapshotContents2;
                    snapshotMetadataEntity = snapshotMetadataEntity2;
                    iG = i;
                    break;
            }
            i = iG;
            snapshotMetadataEntity2 = snapshotMetadataEntity;
            snapshotContents2 = snapshotContents;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new SnapshotEntity(i, snapshotMetadataEntity2, snapshotContents2);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public SnapshotEntity[] newArray(int size) {
        return new SnapshotEntity[size];
    }
}
