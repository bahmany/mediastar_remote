package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataSet;

/* loaded from: classes.dex */
public class DataInsertRequest implements SafeParcelable {
    public static final Parcelable.Creator<DataInsertRequest> CREATOR = new e();
    private final int BR;
    private final DataSet Th;

    public static class Builder {
        private DataSet Th;

        public DataInsertRequest build() {
            com.google.android.gms.common.internal.n.a(this.Th != null, "Must set the data set");
            com.google.android.gms.common.internal.n.a(!this.Th.getDataPoints().isEmpty(), "Cannot use an empty data set");
            com.google.android.gms.common.internal.n.a(this.Th.getDataSource().iH() != null, "Must set the app package name for the data source");
            return new DataInsertRequest(this);
        }

        public Builder setDataSet(DataSet dataSet) {
            this.Th = dataSet;
            return this;
        }
    }

    DataInsertRequest(int versionCode, DataSet dataSet) {
        this.BR = versionCode;
        this.Th = dataSet;
    }

    private DataInsertRequest(Builder builder) {
        this.BR = 1;
        this.Th = builder.Th;
    }

    private boolean a(DataInsertRequest dataInsertRequest) {
        return com.google.android.gms.common.internal.m.equal(this.Th, dataInsertRequest.Th);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return o == this || ((o instanceof DataInsertRequest) && a((DataInsertRequest) o));
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.Th);
    }

    public DataSet iP() {
        return this.Th;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("dataSet", this.Th).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        e.a(this, dest, flags);
    }
}
