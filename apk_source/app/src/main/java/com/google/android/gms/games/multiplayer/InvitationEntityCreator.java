package com.google.android.gms.games.multiplayer;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.gms.games.GameEntity;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class InvitationEntityCreator implements Parcelable.Creator<InvitationEntity> {
    static void a(InvitationEntity invitationEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, (Parcelable) invitationEntity.getGame(), i, false);
        b.c(parcel, 1000, invitationEntity.getVersionCode());
        b.a(parcel, 2, invitationEntity.getInvitationId(), false);
        b.a(parcel, 3, invitationEntity.getCreationTimestamp());
        b.c(parcel, 4, invitationEntity.getInvitationType());
        b.a(parcel, 5, (Parcelable) invitationEntity.getInviter(), i, false);
        b.c(parcel, 6, invitationEntity.getParticipants(), false);
        b.c(parcel, 7, invitationEntity.getVariant());
        b.c(parcel, 8, invitationEntity.getAvailableAutoMatchSlots());
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cl */
    public InvitationEntity createFromParcel(Parcel parcel) {
        ArrayList arrayListC = null;
        int iG = 0;
        int iC = a.C(parcel);
        long jI = 0;
        int iG2 = 0;
        ParticipantEntity participantEntity = null;
        int iG3 = 0;
        String strO = null;
        GameEntity gameEntity = null;
        int iG4 = 0;
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
                    iG3 = a.g(parcel, iB);
                    break;
                case 5:
                    participantEntity = (ParticipantEntity) a.a(parcel, iB, ParticipantEntity.CREATOR);
                    break;
                case 6:
                    arrayListC = a.c(parcel, iB, ParticipantEntity.CREATOR);
                    break;
                case 7:
                    iG2 = a.g(parcel, iB);
                    break;
                case 8:
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
        return new InvitationEntity(iG4, gameEntity, strO, jI, iG3, participantEntity, arrayListC, iG2, iG);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: dS, reason: merged with bridge method [inline-methods] */
    public InvitationEntity[] newArray(int i) {
        return new InvitationEntity[i];
    }
}
