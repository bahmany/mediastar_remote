package com.google.android.gms.games.quest;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;

/* loaded from: classes.dex */
public class MilestoneEntityCreator implements Parcelable.Creator<MilestoneEntity> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(MilestoneEntity milestoneEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, milestoneEntity.getMilestoneId(), false);
        b.c(parcel, 1000, milestoneEntity.getVersionCode());
        b.a(parcel, 2, milestoneEntity.getCurrentProgress());
        b.a(parcel, 3, milestoneEntity.getTargetProgress());
        b.a(parcel, 4, milestoneEntity.getCompletionRewardData(), false);
        b.c(parcel, 5, milestoneEntity.getState());
        b.a(parcel, 6, milestoneEntity.getEventId(), false);
        b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    public MilestoneEntity createFromParcel(Parcel parcel) {
        long jI = 0;
        int iG = 0;
        String strO = null;
        int iC = a.C(parcel);
        byte[] bArrR = null;
        long jI2 = 0;
        String strO2 = null;
        int iG2 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    strO2 = a.o(parcel, iB);
                    break;
                case 2:
                    jI2 = a.i(parcel, iB);
                    break;
                case 3:
                    jI = a.i(parcel, iB);
                    break;
                case 4:
                    bArrR = a.r(parcel, iB);
                    break;
                case 5:
                    iG = a.g(parcel, iB);
                    break;
                case 6:
                    strO = a.o(parcel, iB);
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
        return new MilestoneEntity(iG2, strO2, jI2, jI, bArrR, iG, strO);
    }

    @Override // android.os.Parcelable.Creator
    public MilestoneEntity[] newArray(int size) {
        return new MilestoneEntity[size];
    }
}
