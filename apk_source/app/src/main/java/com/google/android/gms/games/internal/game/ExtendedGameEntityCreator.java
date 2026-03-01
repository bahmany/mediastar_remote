package com.google.android.gms.games.internal.game;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.gms.games.GameEntity;
import com.google.android.gms.games.snapshot.SnapshotMetadataEntity;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ExtendedGameEntityCreator implements Parcelable.Creator<ExtendedGameEntity> {
    static void a(ExtendedGameEntity extendedGameEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, (Parcelable) extendedGameEntity.getGame(), i, false);
        b.c(parcel, 1000, extendedGameEntity.getVersionCode());
        b.c(parcel, 2, extendedGameEntity.kP());
        b.a(parcel, 3, extendedGameEntity.kQ());
        b.c(parcel, 4, extendedGameEntity.kR());
        b.a(parcel, 5, extendedGameEntity.kS());
        b.a(parcel, 6, extendedGameEntity.kT());
        b.a(parcel, 7, extendedGameEntity.kU(), false);
        b.a(parcel, 8, extendedGameEntity.kV());
        b.a(parcel, 9, extendedGameEntity.kW(), false);
        b.c(parcel, 10, extendedGameEntity.kO(), false);
        b.a(parcel, 11, (Parcelable) extendedGameEntity.kX(), i, false);
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cg */
    public ExtendedGameEntity createFromParcel(Parcel parcel) {
        int iC = a.C(parcel);
        int iG = 0;
        GameEntity gameEntity = null;
        int iG2 = 0;
        boolean zC = false;
        int iG3 = 0;
        long jI = 0;
        long jI2 = 0;
        String strO = null;
        long jI3 = 0;
        String strO2 = null;
        ArrayList arrayListC = null;
        SnapshotMetadataEntity snapshotMetadataEntity = null;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    gameEntity = (GameEntity) a.a(parcel, iB, GameEntity.CREATOR);
                    break;
                case 2:
                    iG2 = a.g(parcel, iB);
                    break;
                case 3:
                    zC = a.c(parcel, iB);
                    break;
                case 4:
                    iG3 = a.g(parcel, iB);
                    break;
                case 5:
                    jI = a.i(parcel, iB);
                    break;
                case 6:
                    jI2 = a.i(parcel, iB);
                    break;
                case 7:
                    strO = a.o(parcel, iB);
                    break;
                case 8:
                    jI3 = a.i(parcel, iB);
                    break;
                case 9:
                    strO2 = a.o(parcel, iB);
                    break;
                case 10:
                    arrayListC = a.c(parcel, iB, GameBadgeEntity.CREATOR);
                    break;
                case 11:
                    snapshotMetadataEntity = (SnapshotMetadataEntity) a.a(parcel, iB, SnapshotMetadataEntity.CREATOR);
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
        return new ExtendedGameEntity(iG, gameEntity, iG2, zC, iG3, jI, jI2, strO, jI3, strO2, arrayListC, snapshotMetadataEntity);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dJ, reason: merged with bridge method [inline-methods] */
    public ExtendedGameEntity[] newArray(int i) {
        return new ExtendedGameEntity[i];
    }
}
