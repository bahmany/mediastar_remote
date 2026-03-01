package com.google.android.gms.drive.realtime.internal.event;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.safeparcel.a;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class c implements Parcelable.Creator<ParcelableEventList> {
    static void a(ParcelableEventList parcelableEventList, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, parcelableEventList.BR);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, parcelableEventList.me, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, (Parcelable) parcelableEventList.Rw, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, parcelableEventList.Rx);
        com.google.android.gms.common.internal.safeparcel.b.b(parcel, 5, parcelableEventList.Ry, false);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ba */
    public ParcelableEventList createFromParcel(Parcel parcel) {
        boolean zC = false;
        ArrayList<String> arrayListC = null;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        DataHolder dataHolder = null;
        ArrayList arrayListC2 = null;
        int iG = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 2:
                    arrayListC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, ParcelableEvent.CREATOR);
                    break;
                case 3:
                    dataHolder = (DataHolder) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DataHolder.CREATOR);
                    break;
                case 4:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 5:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.C(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new ParcelableEventList(iG, arrayListC2, dataHolder, zC, arrayListC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cn */
    public ParcelableEventList[] newArray(int i) {
        return new ParcelableEventList[i];
    }
}
