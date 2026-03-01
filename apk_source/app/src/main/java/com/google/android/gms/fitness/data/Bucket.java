package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class Bucket implements SafeParcelable {
    public static final Parcelable.Creator<Bucket> CREATOR = new d();
    public static final int TYPE_ACTIVITY_SEGMENT = 4;
    public static final int TYPE_ACTIVITY_TYPE = 3;
    public static final int TYPE_SESSION = 2;
    public static final int TYPE_TIME = 1;
    private final int BR;
    private final long KL;
    private final long Si;
    private final Session Sk;
    private final int Sv;
    private final List<DataSet> Sw;
    private final int Sx;
    private boolean Sy;

    Bucket(int versionCode, long startTimeMillis, long endTimeMillis, Session session, int activity, List<DataSet> dataSets, int bucketType, boolean serverHasMoreData) {
        this.Sy = false;
        this.BR = versionCode;
        this.KL = startTimeMillis;
        this.Si = endTimeMillis;
        this.Sk = session;
        this.Sv = activity;
        this.Sw = dataSets;
        this.Sx = bucketType;
        this.Sy = serverHasMoreData;
    }

    public Bucket(RawBucket bucket, List<DataSource> uniqueDataSources, List<DataType> uniqueDataTypes) {
        this(2, bucket.KL, bucket.Si, bucket.Sk, bucket.Sv, a(bucket.Sw, uniqueDataSources, uniqueDataTypes), bucket.Sx, bucket.Sy);
    }

    private static List<DataSet> a(List<RawDataSet> list, List<DataSource> list2, List<DataType> list3) {
        ArrayList arrayList = new ArrayList(list.size());
        Iterator<RawDataSet> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(new DataSet(it.next(), list2, list3));
        }
        return arrayList;
    }

    private boolean a(Bucket bucket) {
        return this.KL == bucket.KL && this.Si == bucket.Si && this.Sv == bucket.Sv && com.google.android.gms.common.internal.m.equal(this.Sw, bucket.Sw) && this.Sx == bucket.Sx && this.Sy == bucket.Sy;
    }

    public static String cz(int i) {
        switch (i) {
            case 0:
                return "unknown";
            case 1:
                return "time";
            case 2:
                return "session";
            case 3:
                return PlaylistSQLiteHelper.COL_TYPE;
            case 4:
                return "segment";
            default:
                return "bug";
        }
    }

    public boolean b(Bucket bucket) {
        return this.KL == bucket.KL && this.Si == bucket.Si && this.Sv == bucket.Sv && this.Sx == bucket.Sx;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return o == this || ((o instanceof Bucket) && a((Bucket) o));
    }

    public int getActivity() {
        return this.Sv;
    }

    public int getBucketType() {
        return this.Sx;
    }

    public DataSet getDataSet(DataType dataType) {
        for (DataSet dataSet : this.Sw) {
            if (dataSet.getDataType().equals(dataType)) {
                return dataSet;
            }
        }
        return null;
    }

    public List<DataSet> getDataSets() {
        return this.Sw;
    }

    public long getEndTimeMillis() {
        return this.Si;
    }

    public Session getSession() {
        return this.Sk;
    }

    public long getStartTimeMillis() {
        return this.KL;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(Long.valueOf(this.KL), Long.valueOf(this.Si), Integer.valueOf(this.Sv), Integer.valueOf(this.Sx));
    }

    public boolean iB() {
        if (this.Sy) {
            return true;
        }
        Iterator<DataSet> it = this.Sw.iterator();
        while (it.hasNext()) {
            if (it.next().iB()) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("startTime", Long.valueOf(this.KL)).a("endTime", Long.valueOf(this.Si)).a("activity", Integer.valueOf(this.Sv)).a("dataSets", this.Sw).a("bucketType", cz(this.Sx)).a("serverHasMoreData", Boolean.valueOf(this.Sy)).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        d.a(this, dest, flags);
    }
}
