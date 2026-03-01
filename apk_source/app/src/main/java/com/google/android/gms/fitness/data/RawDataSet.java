package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.List;

/* loaded from: classes.dex */
public final class RawDataSet implements SafeParcelable {
    public static final Parcelable.Creator<RawDataSet> CREATOR = new o();
    final int BR;
    final boolean Sy;
    final int Tb;
    final int Td;
    final List<RawDataPoint> Te;

    RawDataSet(int versionCode, int dataSourceIndex, int dataTypeIndex, List<RawDataPoint> rawDataPoints, boolean serverHasMoreData) {
        this.BR = versionCode;
        this.Tb = dataSourceIndex;
        this.Td = dataTypeIndex;
        this.Te = rawDataPoints;
        this.Sy = serverHasMoreData;
    }

    public RawDataSet(DataSet dataSet, List<DataSource> uniqueDataSources, List<DataType> uniqueDataTypes) {
        this.BR = 2;
        this.Te = dataSet.e(uniqueDataSources);
        this.Sy = dataSet.iB();
        this.Tb = t.a(dataSet.getDataSource(), uniqueDataSources);
        this.Td = t.a(dataSet.getDataType(), uniqueDataTypes);
    }

    private boolean a(RawDataSet rawDataSet) {
        return this.Tb == rawDataSet.Tb && this.Td == rawDataSet.Td && this.Sy == rawDataSet.Sy && com.google.android.gms.common.internal.m.equal(this.Te, rawDataSet.Te);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return this == o || ((o instanceof RawDataSet) && a((RawDataSet) o));
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(Integer.valueOf(this.Tb), Integer.valueOf(this.Td));
    }

    public String toString() {
        return String.format("RawDataSet{%s@[%s, %s]}", Integer.valueOf(this.Tb), Integer.valueOf(this.Td), this.Te);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        o.a(this, parcel, flags);
    }
}
