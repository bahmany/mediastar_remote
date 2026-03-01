package com.google.android.gms.games.request;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.gms.games.GameEntity;
import com.google.android.gms.games.PlayerEntity;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GameRequestEntityCreator implements Parcelable.Creator<GameRequestEntity> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(GameRequestEntity gameRequestEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, (Parcelable) gameRequestEntity.getGame(), i, false);
        b.c(parcel, 1000, gameRequestEntity.getVersionCode());
        b.a(parcel, 2, (Parcelable) gameRequestEntity.getSender(), i, false);
        b.a(parcel, 3, gameRequestEntity.getData(), false);
        b.a(parcel, 4, gameRequestEntity.getRequestId(), false);
        b.c(parcel, 5, gameRequestEntity.getRecipients(), false);
        b.c(parcel, 7, gameRequestEntity.getType());
        b.a(parcel, 9, gameRequestEntity.getCreationTimestamp());
        b.a(parcel, 10, gameRequestEntity.getExpirationTimestamp());
        b.a(parcel, 11, gameRequestEntity.lJ(), false);
        b.c(parcel, 12, gameRequestEntity.getStatus());
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    public GameRequestEntity createFromParcel(Parcel parcel) {
        int iC = a.C(parcel);
        int iG = 0;
        GameEntity gameEntity = null;
        PlayerEntity playerEntity = null;
        byte[] bArrR = null;
        String strO = null;
        ArrayList arrayListC = null;
        int iG2 = 0;
        long jI = 0;
        long jI2 = 0;
        Bundle bundleQ = null;
        int iG3 = 0;
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
                    bArrR = a.r(parcel, iB);
                    break;
                case 4:
                    strO = a.o(parcel, iB);
                    break;
                case 5:
                    arrayListC = a.c(parcel, iB, PlayerEntity.CREATOR);
                    break;
                case 7:
                    iG2 = a.g(parcel, iB);
                    break;
                case 9:
                    jI = a.i(parcel, iB);
                    break;
                case 10:
                    jI2 = a.i(parcel, iB);
                    break;
                case 11:
                    bundleQ = a.q(parcel, iB);
                    break;
                case 12:
                    iG3 = a.g(parcel, iB);
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
        return new GameRequestEntity(iG, gameEntity, playerEntity, bArrR, strO, arrayListC, iG2, jI, jI2, bundleQ, iG3);
    }

    @Override // android.os.Parcelable.Creator
    public GameRequestEntity[] newArray(int size) {
        return new GameRequestEntity[size];
    }
}
