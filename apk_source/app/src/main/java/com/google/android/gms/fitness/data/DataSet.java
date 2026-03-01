package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class DataSet implements SafeParcelable {
    public static final Parcelable.Creator<DataSet> CREATOR = new f();
    private final int BR;
    private final DataType SF;
    private final List<DataPoint> SG;
    private final List<DataSource> SH;
    private final DataSource Sh;
    private boolean Sy;

    DataSet(int versionCode, DataSource dataSource, DataType dataType, List<RawDataPoint> dataPoints, List<DataSource> uniqueDataSources, boolean serverHasMoreData) {
        this.Sy = false;
        this.BR = versionCode;
        this.Sh = dataSource;
        this.SF = dataType;
        this.Sy = serverHasMoreData;
        this.SG = new ArrayList(dataPoints.size());
        this.SH = versionCode < 2 ? Collections.singletonList(dataSource) : uniqueDataSources;
        Iterator<RawDataPoint> it = dataPoints.iterator();
        while (it.hasNext()) {
            this.SG.add(new DataPoint(this.SH, it.next()));
        }
    }

    private DataSet(DataSource dataSource, DataType dataType) {
        this.Sy = false;
        this.BR = 3;
        this.Sh = (DataSource) com.google.android.gms.common.internal.n.i(dataSource);
        this.SF = (DataType) com.google.android.gms.common.internal.n.i(dataType);
        this.SG = new ArrayList();
        this.SH = new ArrayList();
        this.SH.add(this.Sh);
    }

    public DataSet(RawDataSet dataSet, List<DataSource> uniqueDataSources, List<DataType> uniqueDataTypes) {
        this(3, (DataSource) b(uniqueDataSources, dataSet.Tb), (DataType) b(uniqueDataTypes, dataSet.Td), dataSet.Te, uniqueDataSources, dataSet.Sy);
    }

    private boolean a(DataSet dataSet) {
        return com.google.android.gms.common.internal.m.equal(this.SF, dataSet.SF) && com.google.android.gms.common.internal.m.equal(this.Sh, dataSet.Sh) && com.google.android.gms.common.internal.m.equal(this.SG, dataSet.SG) && this.Sy == dataSet.Sy;
    }

    private static <T> T b(List<T> list, int i) {
        if (i < 0 || i >= list.size()) {
            return null;
        }
        return list.get(i);
    }

    public static DataSet create(DataSource dataSource) {
        return new DataSet(dataSource, dataSource.getDataType());
    }

    public void a(Iterable<DataPoint> iterable) {
        Iterator<DataPoint> it = iterable.iterator();
        while (it.hasNext()) {
            b(it.next());
        }
    }

    public void add(DataPoint dataPoint) {
        DataSource dataSource = dataPoint.getDataSource();
        com.google.android.gms.common.internal.n.b(dataSource.getStreamIdentifier().equals(this.Sh.getStreamIdentifier()), "Conflicting data sources found %s vs %s", dataSource, this.Sh);
        com.google.android.gms.common.internal.n.b(dataPoint.getDataType().getName().equals(this.SF.getName()), "Conflicting data types found %s vs %s", dataPoint.getDataType(), this.SF);
        com.google.android.gms.common.internal.n.b(dataPoint.getTimestampNanos() > 0, "Data point does not have the timestamp set: %s", dataPoint);
        com.google.android.gms.common.internal.n.b(dataPoint.getStartTimeNanos() <= dataPoint.getEndTimeNanos(), "Data point with start time greater than end time found: %s", dataPoint);
        b(dataPoint);
    }

    public void addAll(Iterable<DataPoint> dataPoints) {
        Iterator<DataPoint> it = dataPoints.iterator();
        while (it.hasNext()) {
            add(it.next());
        }
    }

    public void b(DataPoint dataPoint) {
        this.SG.add(dataPoint);
        DataSource originalDataSource = dataPoint.getOriginalDataSource();
        if (originalDataSource == null || this.SH.contains(originalDataSource)) {
            return;
        }
        this.SH.add(originalDataSource);
    }

    public DataPoint createDataPoint() {
        return DataPoint.create(this.Sh);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    List<RawDataPoint> e(List<DataSource> list) {
        ArrayList arrayList = new ArrayList(this.SG.size());
        Iterator<DataPoint> it = this.SG.iterator();
        while (it.hasNext()) {
            arrayList.add(new RawDataPoint(it.next(), list));
        }
        return arrayList;
    }

    public boolean equals(Object o) {
        return o == this || ((o instanceof DataSet) && a((DataSet) o));
    }

    public List<DataPoint> getDataPoints() {
        return Collections.unmodifiableList(this.SG);
    }

    public DataSource getDataSource() {
        return this.Sh;
    }

    public DataType getDataType() {
        return this.SF;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.SF, this.Sh);
    }

    public boolean iB() {
        return this.Sy;
    }

    List<RawDataPoint> iF() {
        return e(this.SH);
    }

    List<DataSource> iG() {
        return this.SH;
    }

    public String toString() {
        List<RawDataPoint> listIF = iF();
        Object[] objArr = new Object[2];
        objArr[0] = this.Sh.toDebugString();
        Object obj = listIF;
        if (this.SG.size() >= 10) {
            obj = String.format("%d data points, first 5: %s", Integer.valueOf(this.SG.size()), listIF.subList(0, 5));
        }
        objArr[1] = obj;
        return String.format("DataSet{%s %s}", objArr);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        f.a(this, parcel, flags);
    }
}
