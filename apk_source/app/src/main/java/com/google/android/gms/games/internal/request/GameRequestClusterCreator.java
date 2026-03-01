package com.google.android.gms.games.internal.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.gms.games.request.GameRequestEntity;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GameRequestClusterCreator implements Parcelable.Creator<GameRequestCluster> {
    static void a(GameRequestCluster gameRequestCluster, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.c(parcel, 1, gameRequestCluster.lu(), false);
        b.c(parcel, 1000, gameRequestCluster.getVersionCode());
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ck, reason: merged with bridge method [inline-methods] */
    public GameRequestCluster createFromParcel(Parcel parcel) {
        int iC = a.C(parcel);
        int iG = 0;
        ArrayList arrayListC = null;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    arrayListC = a.c(parcel, iB, GameRequestEntity.CREATOR);
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
        return new GameRequestCluster(iG, arrayListC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dQ, reason: merged with bridge method [inline-methods] */
    public GameRequestCluster[] newArray(int i) {
        return new GameRequestCluster[i];
    }
}
