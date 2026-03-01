package com.google.android.gms.games.achievement;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.gms.games.PlayerEntity;

/* loaded from: classes.dex */
public class AchievementEntityCreator implements Parcelable.Creator<AchievementEntity> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(AchievementEntity achievementEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, achievementEntity.getAchievementId(), false);
        b.c(parcel, 2, achievementEntity.getType());
        b.a(parcel, 3, achievementEntity.getName(), false);
        b.a(parcel, 4, achievementEntity.getDescription(), false);
        b.a(parcel, 5, (Parcelable) achievementEntity.getUnlockedImageUri(), i, false);
        b.a(parcel, 6, achievementEntity.getUnlockedImageUrl(), false);
        b.a(parcel, 7, (Parcelable) achievementEntity.getRevealedImageUri(), i, false);
        b.a(parcel, 8, achievementEntity.getRevealedImageUrl(), false);
        b.c(parcel, 9, achievementEntity.getTotalSteps());
        b.a(parcel, 10, achievementEntity.getFormattedTotalSteps(), false);
        b.a(parcel, 11, (Parcelable) achievementEntity.getPlayer(), i, false);
        b.c(parcel, 12, achievementEntity.getState());
        b.c(parcel, 13, achievementEntity.getCurrentSteps());
        b.a(parcel, 14, achievementEntity.getFormattedCurrentSteps(), false);
        b.a(parcel, 15, achievementEntity.getLastUpdatedTimestamp());
        b.a(parcel, 16, achievementEntity.getXpValue());
        b.c(parcel, 1000, achievementEntity.getVersionCode());
        b.H(parcel, iD);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public AchievementEntity createFromParcel(Parcel parcel) {
        int iC = a.C(parcel);
        int iG = 0;
        String strO = null;
        int iG2 = 0;
        String strO2 = null;
        String strO3 = null;
        Uri uri = null;
        String strO4 = null;
        Uri uri2 = null;
        String strO5 = null;
        int iG3 = 0;
        String strO6 = null;
        PlayerEntity playerEntity = null;
        int iG4 = 0;
        int iG5 = 0;
        String strO7 = null;
        long jI = 0;
        long jI2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    strO = a.o(parcel, iB);
                    break;
                case 2:
                    iG2 = a.g(parcel, iB);
                    break;
                case 3:
                    strO2 = a.o(parcel, iB);
                    break;
                case 4:
                    strO3 = a.o(parcel, iB);
                    break;
                case 5:
                    uri = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 6:
                    strO4 = a.o(parcel, iB);
                    break;
                case 7:
                    uri2 = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 8:
                    strO5 = a.o(parcel, iB);
                    break;
                case 9:
                    iG3 = a.g(parcel, iB);
                    break;
                case 10:
                    strO6 = a.o(parcel, iB);
                    break;
                case 11:
                    playerEntity = (PlayerEntity) a.a(parcel, iB, PlayerEntity.CREATOR);
                    break;
                case 12:
                    iG4 = a.g(parcel, iB);
                    break;
                case 13:
                    iG5 = a.g(parcel, iB);
                    break;
                case 14:
                    strO7 = a.o(parcel, iB);
                    break;
                case 15:
                    jI = a.i(parcel, iB);
                    break;
                case 16:
                    jI2 = a.i(parcel, iB);
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
        return new AchievementEntity(iG, strO, iG2, strO2, strO3, uri, strO4, uri2, strO5, iG3, strO6, playerEntity, iG4, iG5, strO7, jI, jI2);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public AchievementEntity[] newArray(int size) {
        return new AchievementEntity[size];
    }
}
