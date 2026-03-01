package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class f implements Parcelable.Creator<DataReadRequest> {
    static void a(DataReadRequest dataReadRequest, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1, dataReadRequest.getDataTypes(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, dataReadRequest.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 2, dataReadRequest.getDataSources(), false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, dataReadRequest.getStartTimeMillis());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, dataReadRequest.getEndTimeMillis());
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 5, dataReadRequest.ja(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 6, dataReadRequest.jb(), false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 7, dataReadRequest.getBucketType());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, dataReadRequest.jc());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 9, (Parcelable) dataReadRequest.jd(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 10, dataReadRequest.je());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 11, dataReadRequest.jf());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 12, dataReadRequest.jh());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 13, dataReadRequest.jg());
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bD, reason: merged with bridge method [inline-methods] */
    public DataReadRequest createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        ArrayList arrayListC = null;
        ArrayList arrayListC2 = null;
        long jI = 0;
        long jI2 = 0;
        ArrayList arrayListC3 = null;
        ArrayList arrayListC4 = null;
        int iG2 = 0;
        long jI3 = 0;
        DataSource dataSource = null;
        int iG3 = 0;
        boolean zC = false;
        boolean zC2 = false;
        boolean zC3 = false;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    arrayListC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, DataType.CREATOR);
                    break;
                case 2:
                    arrayListC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, DataSource.CREATOR);
                    break;
                case 3:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 4:
                    jI2 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 5:
                    arrayListC3 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, DataType.CREATOR);
                    break;
                case 6:
                    arrayListC4 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB, DataSource.CREATOR);
                    break;
                case 7:
                    iG2 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 8:
                    jI3 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 9:
                    dataSource = (DataSource) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DataSource.CREATOR);
                    break;
                case 10:
                    iG3 = com.google.android.gms.common.internal.safeparcel.a.g(parcel, iB);
                    break;
                case 11:
                    zC = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 12:
                    zC2 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
                    break;
                case 13:
                    zC3 = com.google.android.gms.common.internal.safeparcel.a.c(parcel, iB);
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
        return new DataReadRequest(iG, arrayListC, arrayListC2, jI, jI2, arrayListC3, arrayListC4, iG2, jI3, dataSource, iG3, zC, zC2, zC3);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cU, reason: merged with bridge method [inline-methods] */
    public DataReadRequest[] newArray(int i) {
        return new DataReadRequest[i];
    }
}
