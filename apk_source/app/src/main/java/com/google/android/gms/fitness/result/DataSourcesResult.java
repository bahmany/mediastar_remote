package com.google.android.gms.fitness.result;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class DataSourcesResult implements Result, SafeParcelable {
    public static final Parcelable.Creator<DataSourcesResult> CREATOR = new c();
    private final int BR;
    private final Status CM;
    private final List<DataSource> TZ;

    DataSourcesResult(int versionCode, List<DataSource> dataSources, Status status) {
        this.BR = versionCode;
        this.TZ = Collections.unmodifiableList(dataSources);
        this.CM = status;
    }

    public DataSourcesResult(List<DataSource> dataSources, Status status) {
        this.BR = 3;
        this.TZ = Collections.unmodifiableList(dataSources);
        this.CM = status;
    }

    public static DataSourcesResult E(Status status) {
        return new DataSourcesResult(Collections.emptyList(), status);
    }

    private boolean b(DataSourcesResult dataSourcesResult) {
        return this.CM.equals(dataSourcesResult.CM) && m.equal(this.TZ, dataSourcesResult.TZ);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof DataSourcesResult) && b((DataSourcesResult) that));
    }

    public List<DataSource> getDataSources() {
        return this.TZ;
    }

    public List<DataSource> getDataSources(DataType dataType) {
        ArrayList arrayList = new ArrayList();
        for (DataSource dataSource : this.TZ) {
            if (dataSource.getDataType().equals(dataType)) {
                arrayList.add(dataSource);
            }
        }
        return Collections.unmodifiableList(arrayList);
    }

    @Override // com.google.android.gms.common.api.Result
    public Status getStatus() {
        return this.CM;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(this.CM, this.TZ);
    }

    public String toString() {
        return m.h(this).a("status", this.CM).a("dataSets", this.TZ).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        c.a(this, dest, flags);
    }
}
