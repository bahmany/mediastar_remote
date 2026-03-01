package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.a;

/* loaded from: classes.dex */
public class e implements Parcelable.Creator<DataPoint> {
    static void a(DataPoint dataPoint, Parcel parcel, int i) {
        int iD = com.google.android.gms.common.internal.safeparcel.b.D(parcel);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 1, (Parcelable) dataPoint.getDataSource(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.c(parcel, 1000, dataPoint.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 3, dataPoint.getTimestampNanos());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 4, dataPoint.getStartTimeNanos());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 5, (Parcelable[]) dataPoint.iC(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 6, (Parcelable) dataPoint.getOriginalDataSource(), i, false);
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 7, dataPoint.iD());
        com.google.android.gms.common.internal.safeparcel.b.a(parcel, 8, dataPoint.iE());
        com.google.android.gms.common.internal.safeparcel.b.H(parcel, iD);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: bl */
    public DataPoint createFromParcel(Parcel parcel) {
        int iC = com.google.android.gms.common.internal.safeparcel.a.C(parcel);
        int iG = 0;
        DataSource dataSource = null;
        long jI = 0;
        long jI2 = 0;
        Value[] valueArr = null;
        DataSource dataSource2 = null;
        long jI3 = 0;
        long jI4 = 0;
        while (parcel.dataPosition() < iC) {
            int iB = com.google.android.gms.common.internal.safeparcel.a.B(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.a.aD(iB)) {
                case 1:
                    dataSource = (DataSource) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DataSource.CREATOR);
                    break;
                case 3:
                    jI = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 4:
                    jI2 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 5:
                    valueArr = (Value[]) com.google.android.gms.common.internal.safeparcel.a.b(parcel, iB, Value.CREATOR);
                    break;
                case 6:
                    dataSource2 = (DataSource) com.google.android.gms.common.internal.safeparcel.a.a(parcel, iB, DataSource.CREATOR);
                    break;
                case 7:
                    jI3 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
                    break;
                case 8:
                    jI4 = com.google.android.gms.common.internal.safeparcel.a.i(parcel, iB);
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
        return new DataPoint(iG, dataSource, jI, jI2, valueArr, dataSource2, jI3, jI4);
    }

    @Override // android.os.Parcelable.Creator
    /* renamed from: cC */
    public DataPoint[] newArray(int i) {
        return new DataPoint[i];
    }
}
