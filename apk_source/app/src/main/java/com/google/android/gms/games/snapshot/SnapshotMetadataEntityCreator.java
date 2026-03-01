package com.google.android.gms.games.snapshot;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.gms.games.GameEntity;
import com.google.android.gms.games.PlayerEntity;

/* loaded from: classes.dex */
public class SnapshotMetadataEntityCreator implements Parcelable.Creator<SnapshotMetadataEntity> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(SnapshotMetadataEntity snapshotMetadataEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, (Parcelable) snapshotMetadataEntity.getGame(), i, false);
        b.c(parcel, 1000, snapshotMetadataEntity.getVersionCode());
        b.a(parcel, 2, (Parcelable) snapshotMetadataEntity.getOwner(), i, false);
        b.a(parcel, 3, snapshotMetadataEntity.getSnapshotId(), false);
        b.a(parcel, 5, (Parcelable) snapshotMetadataEntity.getCoverImageUri(), i, false);
        b.a(parcel, 6, snapshotMetadataEntity.getCoverImageUrl(), false);
        b.a(parcel, 7, snapshotMetadataEntity.getTitle(), false);
        b.a(parcel, 8, snapshotMetadataEntity.getDescription(), false);
        b.a(parcel, 9, snapshotMetadataEntity.getLastModifiedTimestamp());
        b.a(parcel, 10, snapshotMetadataEntity.getPlayedTime());
        b.a(parcel, 11, snapshotMetadataEntity.getCoverImageAspectRatio());
        b.a(parcel, 12, snapshotMetadataEntity.getUniqueName(), false);
        b.H(parcel, iD);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public SnapshotMetadataEntity createFromParcel(Parcel parcel) {
        int iC = a.C(parcel);
        int iG = 0;
        GameEntity gameEntity = null;
        PlayerEntity playerEntity = null;
        String strO = null;
        Uri uri = null;
        String strO2 = null;
        String strO3 = null;
        String strO4 = null;
        long jI = 0;
        long jI2 = 0;
        float fL = 0.0f;
        String strO5 = null;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    gameEntity = (GameEntity) a.a(parcel, iB, GameEntity.CREATOR);
                    break;
                case 2:
                    playerEntity = (PlayerEntity) a.a(parcel, iB, PlayerEntity.CREATOR);
                    break;
                case 3:
                    strO = a.o(parcel, iB);
                    break;
                case 5:
                    uri = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 6:
                    strO2 = a.o(parcel, iB);
                    break;
                case 7:
                    strO3 = a.o(parcel, iB);
                    break;
                case 8:
                    strO4 = a.o(parcel, iB);
                    break;
                case 9:
                    jI = a.i(parcel, iB);
                    break;
                case 10:
                    jI2 = a.i(parcel, iB);
                    break;
                case 11:
                    fL = a.l(parcel, iB);
                    break;
                case 12:
                    strO5 = a.o(parcel, iB);
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
        return new SnapshotMetadataEntity(iG, gameEntity, playerEntity, strO, uri, strO2, strO3, strO4, jI, jI2, fL, strO5);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public SnapshotMetadataEntity[] newArray(int size) {
        return new SnapshotMetadataEntity[size];
    }
}
