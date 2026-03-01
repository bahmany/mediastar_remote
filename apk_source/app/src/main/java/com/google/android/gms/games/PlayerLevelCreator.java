package com.google.android.gms.games;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;

/* loaded from: classes.dex */
public class PlayerLevelCreator implements Parcelable.Creator<PlayerLevel> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(PlayerLevel playerLevel, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.c(parcel, 1, playerLevel.getLevelNumber());
        b.c(parcel, 1000, playerLevel.getVersionCode());
        b.a(parcel, 2, playerLevel.getMinXp());
        b.a(parcel, 3, playerLevel.getMaxXp());
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    public PlayerLevel createFromParcel(Parcel parcel) {
        long jI = 0;
        int iG = 0;
        int iC = a.C(parcel);
        long jI2 = 0;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    iG = a.g(parcel, iB);
                    break;
                case 2:
                    jI2 = a.i(parcel, iB);
                    break;
                case 3:
                    jI = a.i(parcel, iB);
                    break;
                case 1000:
                    iG2 = a.g(parcel, iB);
                    break;
                default:
                    a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new PlayerLevel(iG2, iG, jI2, jI);
    }

    @Override // android.os.Parcelable.Creator
    public PlayerLevel[] newArray(int size) {
        return new PlayerLevel[size];
    }
}
