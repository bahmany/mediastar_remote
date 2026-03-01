package com.google.android.gms.games;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.gms.games.internal.player.MostRecentGameInfoEntity;

/* loaded from: classes.dex */
public class PlayerEntityCreator implements Parcelable.Creator<PlayerEntity> {
    static void a(PlayerEntity playerEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, playerEntity.getPlayerId(), false);
        b.a(parcel, 2, playerEntity.getDisplayName(), false);
        b.a(parcel, 3, (Parcelable) playerEntity.getIconImageUri(), i, false);
        b.a(parcel, 4, (Parcelable) playerEntity.getHiResImageUri(), i, false);
        b.a(parcel, 5, playerEntity.getRetrievedTimestamp());
        b.c(parcel, 6, playerEntity.jR());
        b.a(parcel, 7, playerEntity.getLastPlayedWithTimestamp());
        b.a(parcel, 8, playerEntity.getIconImageUrl(), false);
        b.a(parcel, 9, playerEntity.getHiResImageUrl(), false);
        b.a(parcel, 14, playerEntity.getTitle(), false);
        b.a(parcel, 15, (Parcelable) playerEntity.jS(), i, false);
        b.a(parcel, 16, (Parcelable) playerEntity.getLevelInfo(), i, false);
        b.c(parcel, 1000, playerEntity.getVersionCode());
        b.a(parcel, 18, playerEntity.isProfileVisible());
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ce */
    public PlayerEntity createFromParcel(Parcel parcel) {
        int iC = a.C(parcel);
        int iG = 0;
        String strO = null;
        String strO2 = null;
        Uri uri = null;
        Uri uri2 = null;
        long jI = 0;
        int iG2 = 0;
        long jI2 = 0;
        String strO3 = null;
        String strO4 = null;
        String strO5 = null;
        MostRecentGameInfoEntity mostRecentGameInfoEntity = null;
        PlayerLevelInfo playerLevelInfo = null;
        boolean zC = false;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    strO = a.o(parcel, iB);
                    break;
                case 2:
                    strO2 = a.o(parcel, iB);
                    break;
                case 3:
                    uri = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 4:
                    uri2 = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 5:
                    jI = a.i(parcel, iB);
                    break;
                case 6:
                    iG2 = a.g(parcel, iB);
                    break;
                case 7:
                    jI2 = a.i(parcel, iB);
                    break;
                case 8:
                    strO3 = a.o(parcel, iB);
                    break;
                case 9:
                    strO4 = a.o(parcel, iB);
                    break;
                case 14:
                    strO5 = a.o(parcel, iB);
                    break;
                case 15:
                    mostRecentGameInfoEntity = (MostRecentGameInfoEntity) a.a(parcel, iB, MostRecentGameInfoEntity.CREATOR);
                    break;
                case 16:
                    playerLevelInfo = (PlayerLevelInfo) a.a(parcel, iB, PlayerLevelInfo.CREATOR);
                    break;
                case 18:
                    zC = a.c(parcel, iB);
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
        return new PlayerEntity(iG, strO, strO2, uri, uri2, jI, iG2, jI2, strO3, strO4, strO5, mostRecentGameInfoEntity, playerLevelInfo, zC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dw */
    public PlayerEntity[] newArray(int i) {
        return new PlayerEntity[i];
    }
}
