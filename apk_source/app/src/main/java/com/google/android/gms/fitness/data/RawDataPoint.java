package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public final class RawDataPoint implements SafeParcelable {
    public static final Parcelable.Creator<RawDataPoint> CREATOR = new n();
    final int BR;
    final long SA;
    final Value[] SB;
    final long SD;
    final long SE;
    final long Sz;
    final int Tb;
    final int Tc;

    RawDataPoint(int versionCode, long timestampNanos, long startTimeNanos, Value[] values, int dataSourceIndex, int originalDataSourceIndex, long rawTimestamp, long insertionTimeMillis) {
        this.BR = versionCode;
        this.Sz = timestampNanos;
        this.SA = startTimeNanos;
        this.Tb = dataSourceIndex;
        this.Tc = originalDataSourceIndex;
        this.SD = rawTimestamp;
        this.SE = insertionTimeMillis;
        this.SB = values;
    }

    RawDataPoint(DataPoint dataPoint, List<DataSource> dataSources) {
        this.BR = 4;
        this.Sz = dataPoint.getTimestampNanos();
        this.SA = dataPoint.getStartTimeNanos();
        this.SB = dataPoint.iC();
        this.Tb = t.a(dataPoint.getDataSource(), dataSources);
        this.Tc = t.a(dataPoint.getOriginalDataSource(), dataSources);
        this.SD = dataPoint.iD();
        this.SE = dataPoint.iE();
    }

    private boolean a(RawDataPoint rawDataPoint) {
        return this.Sz == rawDataPoint.Sz && this.SA == rawDataPoint.SA && Arrays.equals(this.SB, rawDataPoint.SB) && this.Tb == rawDataPoint.Tb && this.Tc == rawDataPoint.Tc && this.SD == rawDataPoint.SD;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return this == o || ((o instanceof RawDataPoint) && a((RawDataPoint) o));
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(Long.valueOf(this.Sz), Long.valueOf(this.SA));
    }

    public String toString() {
        return String.format("RawDataPoint{%s@[%s, %s](%d,%d)}", Arrays.toString(this.SB), Long.valueOf(this.SA), Long.valueOf(this.Sz), Integer.valueOf(this.Tb), Integer.valueOf(this.Tc));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        n.a(this, parcel, flags);
    }
}
