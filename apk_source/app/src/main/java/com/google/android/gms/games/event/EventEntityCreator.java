package com.google.android.gms.games.event;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import com.google.android.gms.games.PlayerEntity;

/* loaded from: classes.dex */
public class EventEntityCreator implements Parcelable.Creator<EventEntity> {
    public static final int CONTENT_DESCRIPTION = 0;

    static void a(EventEntity eventEntity, Parcel parcel, int i) {
        int iD = b.D(parcel);
        b.a(parcel, 1, eventEntity.getEventId(), false);
        b.c(parcel, 1000, eventEntity.getVersionCode());
        b.a(parcel, 2, eventEntity.getName(), false);
        b.a(parcel, 3, eventEntity.getDescription(), false);
        b.a(parcel, 4, (Parcelable) eventEntity.getIconImageUri(), i, false);
        b.a(parcel, 5, eventEntity.getIconImageUrl(), false);
        b.a(parcel, 6, (Parcelable) eventEntity.getPlayer(), i, false);
        b.a(parcel, 7, eventEntity.getValue());
        b.a(parcel, 8, eventEntity.getFormattedValue(), false);
        b.a(parcel, 9, eventEntity.isVisible());
        b.H(parcel, iD);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public EventEntity createFromParcel(Parcel parcel) {
        boolean zC = false;
        String strO = null;
        int iC = a.C(parcel);
        long jI = 0;
        PlayerEntity playerEntity = null;
        String strO2 = null;
        Uri uri = null;
        String strO3 = null;
        String strO4 = null;
        String strO5 = null;
        int iG = 0;
        while (parcel.dataPosition() < iC) {
            int iB = a.B(parcel);
            switch (a.aD(iB)) {
                case 1:
                    strO5 = a.o(parcel, iB);
                    break;
                case 2:
                    strO4 = a.o(parcel, iB);
                    break;
                case 3:
                    strO3 = a.o(parcel, iB);
                    break;
                case 4:
                    uri = (Uri) a.a(parcel, iB, Uri.CREATOR);
                    break;
                case 5:
                    strO2 = a.o(parcel, iB);
                    break;
                case 6:
                    playerEntity = (PlayerEntity) a.a(parcel, iB, PlayerEntity.CREATOR);
                    break;
                case 7:
                    jI = a.i(parcel, iB);
                    break;
                case 8:
                    strO = a.o(parcel, iB);
                    break;
                case 9:
                    zC = a.c(parcel, iB);
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
        return new EventEntity(iG, strO5, strO4, strO3, uri, strO2, playerEntity, jI, strO, zC);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.Parcelable.Creator
    public EventEntity[] newArray(int size) {
        return new EventEntity[size];
    }
}
