package com.google.android.gms.games.internal.game;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;

/* loaded from: classes.dex */
public class GameBadgeEntityCreator implements Parcelable.Creator<GameBadgeEntity> {
    static void a(GameBadgeEntity gameBadgeEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.c(parcel, 1, gameBadgeEntity.getType());
        b.c(parcel, 1000, gameBadgeEntity.getVersionCode());
        b.a(parcel, 2, gameBadgeEntity.getTitle(), false);
        b.a(parcel, 3, gameBadgeEntity.getDescription(), false);
        b.a(parcel, 4, (Parcelable) gameBadgeEntity.getIconImageUri(), i, false);
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ch */
    public GameBadgeEntity createFromParcel(Parcel parcel) {
        int iG = 0;
        Uri uri = null;
        int iC = a.C(parcel);
        String strO = null;
        String strO2 = null;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    iG = a.g(parcel, iB);
                    break;
                case 2:
                    strO2 = a.o(parcel, iB);
                    break;
                case 3:
                    strO = a.o(parcel, iB);
                    break;
                case 4:
                    uri = (Uri) a.a(parcel, iB, Uri.CREATOR);
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
        return new GameBadgeEntity(iG2, iG, strO2, strO, uri);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dL */
    public GameBadgeEntity[] newArray(int i) {
        return new GameBadgeEntity[i];
    }
}
