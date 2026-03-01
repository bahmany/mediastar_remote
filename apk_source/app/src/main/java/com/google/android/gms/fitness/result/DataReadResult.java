package com.google.android.gms.fitness.result;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.RawBucket;
import com.google.android.gms.fitness.data.RawDataSet;
import com.google.android.gms.fitness.request.DataReadRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class DataReadResult implements Result, SafeParcelable {
    public static final Parcelable.Creator<DataReadResult> CREATOR = new b();
    private final int BR;
    private final Status CM;
    private final List<DataSource> SH;
    private final List<DataSet> Sw;
    private final List<Bucket> UK;
    private int UL;
    private final List<DataType> UM;

    DataReadResult(int versionCode, List<RawDataSet> dataSets, Status status, List<RawBucket> buckets, int batchCount, List<DataSource> uniqueDataSources, List<DataType> uniqueDataTypes) {
        this.BR = versionCode;
        this.CM = status;
        this.UL = batchCount;
        this.SH = uniqueDataSources;
        this.UM = uniqueDataTypes;
        this.Sw = new ArrayList(dataSets.size());
        Iterator<RawDataSet> it = dataSets.iterator();
        while (it.hasNext()) {
            this.Sw.add(new DataSet(it.next(), uniqueDataSources, uniqueDataTypes));
        }
        this.UK = new ArrayList(buckets.size());
        Iterator<RawBucket> it2 = buckets.iterator();
        while (it2.hasNext()) {
            this.UK.add(new Bucket(it2.next(), uniqueDataSources, uniqueDataTypes));
        }
    }

    public DataReadResult(List<DataSet> dataSets, List<Bucket> buckets, Status status) {
        this.BR = 5;
        this.Sw = dataSets;
        this.CM = status;
        this.UK = buckets;
        this.UL = 1;
        this.SH = new ArrayList();
        this.UM = new ArrayList();
    }

    public static DataReadResult a(Status status, DataReadRequest dataReadRequest) {
        ArrayList arrayList = new ArrayList();
        Iterator<DataSource> it = dataReadRequest.getDataSources().iterator();
        while (it.hasNext()) {
            arrayList.add(DataSet.create(it.next()));
        }
        Iterator<DataType> it2 = dataReadRequest.getDataTypes().iterator();
        while (it2.hasNext()) {
            arrayList.add(DataSet.create(new DataSource.Builder().setDataType(it2.next()).setType(1).setName("Default").build()));
        }
        return new DataReadResult(arrayList, Collections.emptyList(), status);
    }

    private void a(Bucket bucket, List<Bucket> list) {
        for (Bucket bucket2 : list) {
            if (bucket2.b(bucket)) {
                Iterator<DataSet> it = bucket.getDataSets().iterator();
                while (it.hasNext()) {
                    a(it.next(), bucket2.getDataSets());
                }
                return;
            }
        }
        this.UK.add(bucket);
    }

    private void a(DataSet dataSet, List<DataSet> list) {
        for (DataSet dataSet2 : list) {
            if (dataSet2.getDataSource().equals(dataSet.getDataSource())) {
                dataSet2.a(dataSet.getDataPoints());
                return;
            }
        }
        list.add(dataSet);
    }

    private boolean c(DataReadResult dataReadResult) {
        return this.CM.equals(dataReadResult.CM) && m.equal(this.Sw, dataReadResult.Sw) && m.equal(this.UK, dataReadResult.UK);
    }

    public void b(DataReadResult dataReadResult) {
        Iterator<DataSet> it = dataReadResult.getDataSets().iterator();
        while (it.hasNext()) {
            a(it.next(), this.Sw);
        }
        Iterator<Bucket> it2 = dataReadResult.getBuckets().iterator();
        while (it2.hasNext()) {
            a(it2.next(), this.UK);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof DataReadResult) && c((DataReadResult) that));
    }

    public List<Bucket> getBuckets() {
        return this.UK;
    }

    public DataSet getDataSet(DataSource dataSource) {
        for (DataSet dataSet : this.Sw) {
            if (dataSource.equals(dataSet.getDataSource())) {
                return dataSet;
            }
        }
        throw new IllegalArgumentException(String.format("Attempting to read data for %s, which was not requested", dataSource.getStreamIdentifier()));
    }

    public DataSet getDataSet(DataType dataType) {
        for (DataSet dataSet : this.Sw) {
            if (dataType.equals(dataSet.getDataType())) {
                return dataSet;
            }
        }
        throw new IllegalArgumentException(String.format("Attempting to read data for %s, which was not requested", dataType.getName()));
    }

    public List<DataSet> getDataSets() {
        return this.Sw;
    }

    @Override // com.google.android.gms.common.api.Result
    public Status getStatus() {
        return this.CM;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(this.CM, this.Sw, this.UK);
    }

    List<DataSource> iG() {
        return this.SH;
    }

    public int jF() {
        return this.UL;
    }

    List<RawBucket> jG() {
        ArrayList arrayList = new ArrayList(this.UK.size());
        Iterator<Bucket> it = this.UK.iterator();
        while (it.hasNext()) {
            arrayList.add(new RawBucket(it.next(), this.SH, this.UM));
        }
        return arrayList;
    }

    List<RawDataSet> jH() {
        ArrayList arrayList = new ArrayList(this.Sw.size());
        Iterator<DataSet> it = this.Sw.iterator();
        while (it.hasNext()) {
            arrayList.add(new RawDataSet(it.next(), this.SH, this.UM));
        }
        return arrayList;
    }

    List<DataType> jI() {
        return this.UM;
    }

    public String toString() {
        return m.h(this).a("status", this.CM).a("dataSets", this.Sw.size() > 5 ? this.Sw.size() + " data sets" : this.Sw).a("buckets", this.UK.size() > 5 ? this.UK.size() + " buckets" : this.UK).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        b.a(this, dest, flags);
    }
}
