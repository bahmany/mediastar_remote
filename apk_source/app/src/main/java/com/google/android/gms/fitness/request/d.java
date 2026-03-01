package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Session;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class d implements Parcelable.Creator<DataDeleteRequest> {
    static void a(DataDeleteRequest dataDeleteRequest, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, dataDeleteRequest.getStartTimeMillis());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, dataDeleteRequest.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 2, dataDeleteRequest.getEndTimeMillis());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 3, dataDeleteRequest.getDataSources(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 4, dataDeleteRequest.getDataTypes(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, dataDeleteRequest.getSessions(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, dataDeleteRequest.iX());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, dataDeleteRequest.iY());
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bB */
    public DataDeleteRequest createFromParcel(Parcel parcel) {
        long jI = 0;
        ArrayList arrayListC = null;
        boolean zC = false;
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        boolean zC2 = false;
        ArrayList arrayListC2 = null;
        ArrayList arrayListC3 = null;
        long jI2 = 0;
        int iG = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    jI2 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 2:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 3:
                    arrayListC3 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, DataSource.CREATOR);
                    break;
                case 4:
                    arrayListC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, DataType.CREATOR);
                    break;
                case 5:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, Session.CREATOR);
                    break;
                case 6:
                    zC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 7:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 1000:
                    iG = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB);
                    break;
            }
        }
        if (parcel.dataPosition() != iC) {
            throw new a.C0011a("Overread allowed size end=" + iC, parcel);
        }
        return new DataDeleteRequest(iG, jI2, jI, arrayListC3, arrayListC2, arrayListC, zC2, zC);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cS */
    public DataDeleteRequest[] newArray(int i) {
        return new DataDeleteRequest[i];
    }
}
