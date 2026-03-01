package com.google.android.gms.games.multiplayer.realtime;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.gms.games.multiplayer.ParticipantEntity;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class RoomEntityCreator implements Parcelable.Creator<RoomEntity> {
    static void a(RoomEntity roomEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, roomEntity.getRoomId(), false);
        b.c(parcel, 1000, roomEntity.getVersionCode());
        b.a(parcel, 2, roomEntity.getCreatorId(), false);
        b.a(parcel, 3, roomEntity.getCreationTimestamp());
        b.c(parcel, 4, roomEntity.getStatus());
        b.a(parcel, 5, roomEntity.getDescription(), false);
        b.c(parcel, 6, roomEntity.getVariant());
        b.a(parcel, 7, roomEntity.getAutoMatchCriteria(), false);
        b.c(parcel, 8, roomEntity.getParticipants(), false);
        b.c(parcel, 9, roomEntity.getAutoMatchWaitEstimateSeconds());
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: co */
    public RoomEntity createFromParcel(Parcel parcel) {
        int iG = 0;
        ArrayList arrayListC = null;
        int iC = a.C(parcel);
        long jI = 0;
        Bundle bundleQ = null;
        int iG2 = 0;
        String strO = null;
        int iG3 = 0;
        String strO2 = null;
        String strO3 = null;
        int iG4 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    strO3 = a.o(parcel, iB);
                    break;
                case 2:
                    strO2 = a.o(parcel, iB);
                    break;
                case 3:
                    jI = a.i(parcel, iB);
                    break;
                case 4:
                    iG3 = a.g(parcel, iB);
                    break;
                case 5:
                    strO = a.o(parcel, iB);
                    break;
                case 6:
                    iG2 = a.g(parcel, iB);
                    break;
                case 7:
                    bundleQ = a.q(parcel, iB);
                    break;
                case 8:
                    arrayListC = a.c(parcel, iB, ParticipantEntity.CREATOR);
                    break;
                case 9:
                    iG = a.g(parcel, iB);
                    break;
                case 1000:
                    iG4 = a.g(parcel, iB);
                    break;
                default:
                    a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new RoomEntity(iG4, strO3, strO2, jI, iG3, strO, iG2, bundleQ, arrayListC, iG);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dV, reason: merged with bridge method [inline-methods] */
    public RoomEntity[] newArray(int i) {
        return new RoomEntity[i];
    }
}
