package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class an implements Parcelable.Creator<OnListEntriesResponse> {
    static void a(OnListEntriesResponse onListEntriesResponse, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, onListEntriesResponse.BR);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, (Parcelable) onListEntriesResponse.Pm, i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, onListEntriesResponse.Or);
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: ap, reason: merged with bridge method [inline-methods] */
    public OnListEntriesResponse createFromParcel(Parcel parcel) {
        boolean zC;
        DataHolder dataHolder;
        int iG;
        boolean z = false;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        DataHolder dataHolder2 = null;
        int i = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    boolean z2 = z;
                    dataHolder = dataHolder2;
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    zC = z2;
                    break;
                case 2:
                    DataHolder dataHolder3 = (DataHolder) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DataHolder.CREATOR);
                    iG = i;
                    zC = z;
                    dataHolder = dataHolder3;
                    break;
                case 3:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    dataHolder = dataHolder2;
                    iG = i;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    zC = z;
                    dataHolder = dataHolder2;
                    iG = i;
                    break;
            }
            i = iG;
            dataHolder2 = dataHolder;
            z = zC;
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new OnListEntriesResponse(i, dataHolder2, z);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bB, reason: merged with bridge method [inline-methods] */
    public OnListEntriesResponse[] newArray(int i) {
        return new OnListEntriesResponse[i];
    }
}
