package com.google.android.gms.games.multiplayer.turnbased;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.gms.games.GameEntity;
import com.google.android.gms.games.multiplayer.ParticipantEntity;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class TurnBasedMatchEntityCreator implements Parcelable.Creator<TurnBasedMatchEntity> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(TurnBasedMatchEntity turnBasedMatchEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, (Parcelable) turnBasedMatchEntity.getGame(), i, false);
        b.a(parcel, 2, turnBasedMatchEntity.getMatchId(), false);
        b.a(parcel, 3, turnBasedMatchEntity.getCreatorId(), false);
        b.a(parcel, 4, turnBasedMatchEntity.getCreationTimestamp());
        b.a(parcel, 5, turnBasedMatchEntity.getLastUpdaterId(), false);
        b.a(parcel, 6, turnBasedMatchEntity.getLastUpdatedTimestamp());
        b.a(parcel, 7, turnBasedMatchEntity.getPendingParticipantId(), false);
        b.c(parcel, 8, turnBasedMatchEntity.getStatus());
        b.c(parcel, 10, turnBasedMatchEntity.getVariant());
        b.c(parcel, 11, turnBasedMatchEntity.getVersion());
        b.a(parcel, 12, turnBasedMatchEntity.getData(), false);
        b.c(parcel, 13, turnBasedMatchEntity.getParticipants(), false);
        b.a(parcel, 14, turnBasedMatchEntity.getRematchId(), false);
        b.a(parcel, 15, turnBasedMatchEntity.getPreviousMatchData(), false);
        b.a(parcel, 17, turnBasedMatchEntity.getAutoMatchCriteria(), false);
        b.c(parcel, 16, turnBasedMatchEntity.getMatchNumber());
        b.c(parcel, 1000, turnBasedMatchEntity.getVersionCode());
        b.a(parcel, 19, turnBasedMatchEntity.isLocallyModified());
        b.c(parcel, 18, turnBasedMatchEntity.getTurnStatus());
        b.a(parcel, 21, turnBasedMatchEntity.getDescriptionParticipantId(), false);
        b.a(parcel, 20, turnBasedMatchEntity.getDescription(), false);
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    public TurnBasedMatchEntity createFromParcel(Parcel parcel) {
        int iC = a.C(parcel);
        int iG = 0;
        GameEntity gameEntity = null;
        String strO = null;
        String strO2 = null;
        long jI = 0;
        String strO3 = null;
        long jI2 = 0;
        String strO4 = null;
        int iG2 = 0;
        int iG3 = 0;
        int iG4 = 0;
        byte[] bArrR = null;
        ArrayList arrayListC = null;
        String strO5 = null;
        byte[] bArrR2 = null;
        int iG5 = 0;
        Bundle bundleQ = null;
        int iG6 = 0;
        boolean zC = false;
        String strO6 = null;
        String strO7 = null;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    gameEntity = (GameEntity) a.a(parcel, iB, GameEntity.CREATOR);
                    break;
                case 2:
                    strO = a.o(parcel, iB);
                    break;
                case 3:
                    strO2 = a.o(parcel, iB);
                    break;
                case 4:
                    jI = a.i(parcel, iB);
                    break;
                case 5:
                    strO3 = a.o(parcel, iB);
                    break;
                case 6:
                    jI2 = a.i(parcel, iB);
                    break;
                case 7:
                    strO4 = a.o(parcel, iB);
                    break;
                case 8:
                    iG2 = a.g(parcel, iB);
                    break;
                case 10:
                    iG3 = a.g(parcel, iB);
                    break;
                case 11:
                    iG4 = a.g(parcel, iB);
                    break;
                case 12:
                    bArrR = a.r(parcel, iB);
                    break;
                case 13:
                    arrayListC = a.c(parcel, iB, ParticipantEntity.CREATOR);
                    break;
                case 14:
                    strO5 = a.o(parcel, iB);
                    break;
                case 15:
                    bArrR2 = a.r(parcel, iB);
                    break;
                case 16:
                    iG5 = a.g(parcel, iB);
                    break;
                case 17:
                    bundleQ = a.q(parcel, iB);
                    break;
                case 18:
                    iG6 = a.g(parcel, iB);
                    break;
                case 19:
                    zC = a.c(parcel, iB);
                    break;
                case 20:
                    strO6 = a.o(parcel, iB);
                    break;
                case 21:
                    strO7 = a.o(parcel, iB);
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
        return new TurnBasedMatchEntity(iG, gameEntity, strO, strO2, jI, strO3, jI2, strO4, iG2, iG3, iG4, bArrR, arrayListC, strO5, bArrR2, iG5, bundleQ, iG6, zC, strO6, strO7);
    }

    @Override // android.os.Parcelable.Creator
    public TurnBasedMatchEntity[] newArray(int size) {
        return new TurnBasedMatchEntity[size];
    }
}
