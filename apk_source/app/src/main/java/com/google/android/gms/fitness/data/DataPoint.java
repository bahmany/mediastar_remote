package com.google.android.gms.fitness.data;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public final class DataPoint implements SafeParcelable {
    public static final Parcelable.Creator<DataPoint> CREATOR = new e();
    private final int BR;
    private long SA;
    private final Value[] SB;
    private DataSource SC;
    private long SD;
    private long SE;
    private final DataSource Sh;
    private long Sz;

    DataPoint(int versionCode, DataSource dataSource, long timestampNanos, long startTimeNanos, Value[] values, DataSource originalDataSource, long rawTimestamp, long insertionTimeMillis) {
        this.BR = versionCode;
        this.Sh = dataSource;
        this.SC = originalDataSource;
        this.Sz = timestampNanos;
        this.SA = startTimeNanos;
        this.SB = values;
        this.SD = rawTimestamp;
        this.SE = insertionTimeMillis;
    }

    private DataPoint(DataSource dataSource) {
        this.BR = 4;
        this.Sh = (DataSource) com.google.android.gms.common.internal.n.b(dataSource, "Data source cannot be null");
        List<Field> fields = dataSource.getDataType().getFields();
        this.SB = new Value[fields.size()];
        int i = 0;
        Iterator<Field> it = fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return;
            }
            this.SB[i2] = new Value(it.next().getFormat());
            i = i2 + 1;
        }
    }

    DataPoint(List<DataSource> dataSources, RawDataPoint rawDataPoint) {
        this(4, a(dataSources, rawDataPoint.Tb), rawDataPoint.Sz, rawDataPoint.SA, rawDataPoint.SB, a(dataSources, rawDataPoint.Tc), rawDataPoint.SD, rawDataPoint.SE);
    }

    private static DataSource a(List<DataSource> list, int i) {
        if (i < 0 || i >= list.size()) {
            return null;
        }
        return list.get(i);
    }

    private boolean a(DataPoint dataPoint) {
        return com.google.android.gms.common.internal.m.equal(this.Sh, dataPoint.Sh) && this.Sz == dataPoint.Sz && this.SA == dataPoint.SA && Arrays.equals(this.SB, dataPoint.SB) && com.google.android.gms.common.internal.m.equal(this.SC, dataPoint.SC);
    }

    private void cB(int i) {
        List<Field> fields = getDataType().getFields();
        int size = fields.size();
        com.google.android.gms.common.internal.n.b(i == size, "Attempting to insert %s values, but needed %s: %s", Integer.valueOf(i), Integer.valueOf(size), fields);
    }

    public static DataPoint create(DataSource dataSource) {
        return new DataPoint(dataSource);
    }

    public static DataPoint extract(Intent intent) {
        if (intent == null) {
            return null;
        }
        return (DataPoint) com.google.android.gms.common.internal.safeparcel.c.a(intent, "com.google.android.gms.fitness.EXTRA_DATA_POINT", CREATOR);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return this == o || ((o instanceof DataPoint) && a((DataPoint) o));
    }

    public DataSource getDataSource() {
        return this.Sh;
    }

    public DataType getDataType() {
        return this.Sh.getDataType();
    }

    public long getEndTimeNanos() {
        return this.Sz;
    }

    public DataSource getOriginalDataSource() {
        return this.SC;
    }

    public long getStartTimeNanos() {
        return this.SA;
    }

    public long getTimestampNanos() {
        return this.Sz;
    }

    public Value getValue(Field field) {
        return this.SB[getDataType().indexOf(field)];
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.Sh, Long.valueOf(this.Sz), Long.valueOf(this.SA));
    }

    public Value[] iC() {
        return this.SB;
    }

    public long iD() {
        return this.SD;
    }

    public long iE() {
        return this.SE;
    }

    public DataPoint setFloatValues(float... values) {
        cB(values.length);
        for (int i = 0; i < values.length; i++) {
            this.SB[i].setFloat(values[i]);
        }
        return this;
    }

    public DataPoint setIntValues(int... values) {
        cB(values.length);
        for (int i = 0; i < values.length; i++) {
            this.SB[i].setInt(values[i]);
        }
        return this;
    }

    public DataPoint setTimeInterval(long startTime, long endTime, TimeUnit unit) {
        return setTimeIntervalNanos(unit.toNanos(startTime), unit.toNanos(endTime));
    }

    public DataPoint setTimeIntervalNanos(long startTimeNanos, long endTimeNanos) {
        this.SA = startTimeNanos;
        this.Sz = endTimeNanos;
        return this;
    }

    public DataPoint setTimestamp(long timestamp, TimeUnit unit) {
        return setTimestampNanos(unit.toNanos(timestamp));
    }

    public DataPoint setTimestampNanos(long timestampNanos) {
        this.Sz = timestampNanos;
        return this;
    }

    public String toString() {
        return String.format("DataPoint{%s@[%s, %s,raw=%s,insert=%s](%s %s)}", Arrays.toString(this.SB), Long.valueOf(this.SA), Long.valueOf(this.Sz), Long.valueOf(this.SD), Long.valueOf(this.SE), this.Sh, this.SC);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        e.a(this, parcel, flags);
    }
}
