package com.google.android.gms.games;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;

/* loaded from: classes.dex */
public class GameEntityCreator implements Parcelable.Creator<GameEntity> {
    static void a(GameEntity gameEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, gameEntity.getApplicationId(), false);
        b.a(parcel, 2, gameEntity.getDisplayName(), false);
        b.a(parcel, 3, gameEntity.getPrimaryCategory(), false);
        b.a(parcel, 4, gameEntity.getSecondaryCategory(), false);
        b.a(parcel, 5, gameEntity.getDescription(), false);
        b.a(parcel, 6, gameEntity.getDeveloperName(), false);
        b.a(parcel, 7, (Parcelable) gameEntity.getIconImageUri(), i, false);
        b.a(parcel, 8, (Parcelable) gameEntity.getHiResImageUri(), i, false);
        b.a(parcel, 9, (Parcelable) gameEntity.getFeaturedImageUri(), i, false);
        b.a(parcel, 10, gameEntity.jL());
        b.a(parcel, 11, gameEntity.jN());
        b.a(parcel, 12, gameEntity.jO(), false);
        b.c(parcel, 13, gameEntity.jP());
        b.c(parcel, 14, gameEntity.getAchievementTotalCount());
        b.c(parcel, 15, gameEntity.getLeaderboardCount());
        b.a(parcel, 17, gameEntity.isTurnBasedMultiplayerEnabled());
        b.a(parcel, 16, gameEntity.isRealTimeMultiplayerEnabled());
        b.c(parcel, 1000, gameEntity.getVersionCode());
        b.a(parcel, 19, gameEntity.getHiResImageUrl(), false);
        b.a(parcel, 18, gameEntity.getIconImageUrl(), false);
        b.a(parcel, 21, gameEntity.isMuted());
        b.a(parcel, 20, gameEntity.getFeaturedImageUrl(), false);
        b.a(parcel, 23, gameEntity.areSnapshotsEnabled());
        b.a(parcel, 22, gameEntity.jM());
        b.a(parcel, 24, gameEntity.getThemeColor(), false);
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cd */
    public GameEntity createFromParcel(Parcel parcel) {
        int iC = a.C(parcel);
        int iG = 0;
        String strO = null;
        String strO2 = null;
        String strO3 = null;
        String strO4 = null;
        String strO5 = null;
        String strO6 = null;
        Uri uri = null;
        Uri uri2 = null;
        Uri uri3 = null;
        boolean zC = false;
        boolean zC2 = false;
        String strO7 = null;
        int iG2 = 0;
        int iG3 = 0;
        int iG4 = 0;
        boolean zC3 = false;
        boolean zC4 = false;
        String strO8 = null;
        String strO9 = null;
        String strO10 = null;
        boolean zC5 = false;
        boolean zC6 = false;
        boolean zC7 = false;
        String strO11 = null;
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
                    strO3 = a.o(parcel, iB);
                    break;
                case 4:
                    strO4 = a.o(parcel, iB);
                    break;
                case 5:
                    strO5 = a.o(parcel, iB);
                    break;
                case 6:
                    strO6 = a.o(parcel, iB);
                    break;
                case 7:
                    uri = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 8:
                    uri2 = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 9:
                    uri3 = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 10:
                    zC = a.c(parcel, iB);
                    break;
                case 11:
                    zC2 = a.c(parcel, iB);
                    break;
                case 12:
                    strO7 = a.o(parcel, iB);
                    break;
                case 13:
                    iG2 = a.g(parcel, iB);
                    break;
                case 14:
                    iG3 = a.g(parcel, iB);
                    break;
                case 15:
                    iG4 = a.g(parcel, iB);
                    break;
                case 16:
                    zC3 = a.c(parcel, iB);
                    break;
                case 17:
                    zC4 = a.c(parcel, iB);
                    break;
                case 18:
                    strO8 = a.o(parcel, iB);
                    break;
                case 19:
                    strO9 = a.o(parcel, iB);
                    break;
                case 20:
                    strO10 = a.o(parcel, iB);
                    break;
                case 21:
                    zC5 = a.c(parcel, iB);
                    break;
                case 22:
                    zC6 = a.c(parcel, iB);
                    break;
                case 23:
                    zC7 = a.c(parcel, iB);
                    break;
                case 24:
                    strO11 = a.o(parcel, iB);
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
        return new GameEntity(iG, strO, strO2, strO3, strO4, strO5, strO6, uri, uri2, uri3, zC, zC2, strO7, iG2, iG3, iG4, zC3, zC4, strO8, strO9, strO10, zC5, zC6, zC7, strO11);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dv */
    public GameEntity[] newArray(int i) {
        return new GameEntity[i];
    }
}
