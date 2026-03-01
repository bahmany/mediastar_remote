package com.google.android.gms.games.quest;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.gms.games.GameEntity;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class QuestEntityCreator implements Parcelable.Creator<QuestEntity> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(QuestEntity questEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, (Parcelable) questEntity.getGame(), i, false);
        b.a(parcel, 2, questEntity.getQuestId(), false);
        b.a(parcel, 3, questEntity.getAcceptedTimestamp());
        b.a(parcel, 4, (Parcelable) questEntity.getBannerImageUri(), i, false);
        b.a(parcel, 5, questEntity.getBannerImageUrl(), false);
        b.a(parcel, 6, questEntity.getDescription(), false);
        b.a(parcel, 7, questEntity.getEndTimestamp());
        b.a(parcel, 8, questEntity.getLastUpdatedTimestamp());
        b.a(parcel, 9, (Parcelable) questEntity.getIconImageUri(), i, false);
        b.a(parcel, 10, questEntity.getIconImageUrl(), false);
        b.a(parcel, 12, questEntity.getName(), false);
        b.a(parcel, 13, questEntity.lI());
        b.a(parcel, 14, questEntity.getStartTimestamp());
        b.c(parcel, 15, questEntity.getState());
        b.c(parcel, 17, questEntity.lH(), false);
        b.c(parcel, 16, questEntity.getType());
        b.c(parcel, 1000, questEntity.getVersionCode());
        b.H(parcel, iD);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public QuestEntity createFromParcel(Parcel parcel) {
        int iC = a.C(parcel);
        int iG = 0;
        GameEntity gameEntity = null;
        String strO = null;
        long jI = 0;
        Uri uri = null;
        String strO2 = null;
        String strO3 = null;
        long jI2 = 0;
        long jI3 = 0;
        Uri uri2 = null;
        String strO4 = null;
        String strO5 = null;
        long jI4 = 0;
        long jI5 = 0;
        int iG2 = 0;
        int iG3 = 0;
        ArrayList arrayListC = null;
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
                    jI = a.i(parcel, iB);
                    break;
                case 4:
                    uri = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 5:
                    strO2 = a.o(parcel, iB);
                    break;
                case 6:
                    strO3 = a.o(parcel, iB);
                    break;
                case 7:
                    jI2 = a.i(parcel, iB);
                    break;
                case 8:
                    jI3 = a.i(parcel, iB);
                    break;
                case 9:
                    uri2 = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 10:
                    strO4 = a.o(parcel, iB);
                    break;
                case 12:
                    strO5 = a.o(parcel, iB);
                    break;
                case 13:
                    jI4 = a.i(parcel, iB);
                    break;
                case 14:
                    jI5 = a.i(parcel, iB);
                    break;
                case 15:
                    iG2 = a.g(parcel, iB);
                    break;
                case 16:
                    iG3 = a.g(parcel, iB);
                    break;
                case 17:
                    arrayListC = a.c(parcel, iB, MilestoneEntity.CREATOR);
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
        return new QuestEntity(iG, gameEntity, strO, jI, uri, strO2, strO3, jI2, jI3, uri2, strO4, strO5, jI4, jI5, iG2, iG3, arrayListC);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public QuestEntity[] newArray(int size) {
        return new QuestEntity[size];
    }
}
