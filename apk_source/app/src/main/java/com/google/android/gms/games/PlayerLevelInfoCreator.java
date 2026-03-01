package com.google.android.gms.games;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;

/* loaded from: classes.dex */
public class PlayerLevelInfoCreator implements Parcelable.Creator<PlayerLevelInfo> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(PlayerLevelInfo playerLevelInfo, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, playerLevelInfo.getCurrentXpTotal());
        b.c(parcel, 1000, playerLevelInfo.getVersionCode());
        b.a(parcel, 2, playerLevelInfo.getLastLevelUpTimestamp());
        b.a(parcel, 3, (Parcelable) playerLevelInfo.getCurrentLevel(), i, false);
        b.a(parcel, 4, (Parcelable) playerLevelInfo.getNextLevel(), i, false);
        b.H(parcel, iD);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public PlayerLevelInfo createFromParcel(Parcel parcel) {
        long jI = 0;
        PlayerLevel playerLevel = null;
        int iC = a.C(parcel);
        int iG = 0;
        PlayerLevel playerLevel2 = null;
        long jI2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    jI2 = a.i(parcel, iB);
                    break;
                case 2:
                    jI = a.i(parcel, iB);
                    break;
                case 3:
                    playerLevel2 = (PlayerLevel) a.a(parcel, iB, PlayerLevel.CREATOR);
                    break;
                case 4:
                    playerLevel = (PlayerLevel) a.a(parcel, iB, PlayerLevel.CREATOR);
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
        return new PlayerLevelInfo(iG, jI2, jI, playerLevel2, playerLevel);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public PlayerLevelInfo[] newArray(int size) {
        return new PlayerLevelInfo[size];
    }
}
