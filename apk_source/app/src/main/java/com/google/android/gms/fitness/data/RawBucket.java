package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class RawBucket implements SafeParcelable {
    public static final Parcelable.Creator<RawBucket> CREATOR = new m();
    final int BR;
    final long KL;
    final long Si;
    final Session Sk;
    final int Sv;
    final List<RawDataSet> Sw;
    final int Sx;
    final boolean Sy;

    RawBucket(int versionCode, long startTimeMillis, long endTimeMillis, Session session, int activity, List<RawDataSet> dataSets, int bucketType, boolean serverHasMoreData) {
        this.BR = versionCode;
        this.KL = startTimeMillis;
        this.Si = endTimeMillis;
        this.Sk = session;
        this.Sv = activity;
        this.Sw = dataSets;
        this.Sx = bucketType;
        this.Sy = serverHasMoreData;
    }

    public RawBucket(Bucket bucket, List<DataSource> uniqueDataSources, List<DataType> uniqueDataTypes) {
        this.BR = 2;
        this.KL = bucket.getStartTimeMillis();
        this.Si = bucket.getEndTimeMillis();
        this.Sk = bucket.getSession();
        this.Sv = bucket.getActivity();
        this.Sx = bucket.getBucketType();
        this.Sy = bucket.iB();
        List<DataSet> dataSets = bucket.getDataSets();
        this.Sw = new ArrayList(dataSets.size());
        Iterator<DataSet> it = dataSets.iterator();
        while (it.hasNext()) {
            this.Sw.add(new RawDataSet(it.next(), uniqueDataSources, uniqueDataTypes));
        }
    }

    private boolean a(RawBucket rawBucket) {
        return this.KL == rawBucket.KL && this.Si == rawBucket.Si && this.Sv == rawBucket.Sv && com.google.android.gms.common.internal.m.equal(this.Sw, rawBucket.Sw) && this.Sx == rawBucket.Sx && this.Sy == rawBucket.Sy;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return this == o || ((o instanceof RawBucket) && a((RawBucket) o));
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(Long.valueOf(this.KL), Long.valueOf(this.Si), Integer.valueOf(this.Sx));
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("startTime", Long.valueOf(this.KL)).a("endTime", Long.valueOf(this.Si)).a("activity", Integer.valueOf(this.Sv)).a("dataSets", this.Sw).a("bucketType", Integer.valueOf(this.Sx)).a("serverHasMoreData", Boolean.valueOf(this.Sy)).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        m.a(this, parcel, flags);
    }
}
