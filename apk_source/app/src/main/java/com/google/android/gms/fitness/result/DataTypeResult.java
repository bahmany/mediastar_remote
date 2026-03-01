package com.google.android.gms.fitness.result;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataType;

/* loaded from: classes.dex */
public class DataTypeResult implements Result, SafeParcelable {
    public static final Parcelable.Creator<DataTypeResult> CREATOR = new d();
    private final int BR;
    private final Status CM;
    private final DataType SF;

    DataTypeResult(int versionCode, Status status, DataType dataType) {
        this.BR = versionCode;
        this.CM = status;
        this.SF = dataType;
    }

    public DataTypeResult(Status status, DataType dataType) {
        this.BR = 2;
        this.CM = status;
        this.SF = dataType;
    }

    public static DataTypeResult F(Status status) {
        return new DataTypeResult(status, null);
    }

    private boolean b(DataTypeResult dataTypeResult) {
        return this.CM.equals(dataTypeResult.CM) && m.equal(this.SF, dataTypeResult.SF);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof DataTypeResult) && b((DataTypeResult) that));
    }

    public DataType getDataType() {
        return this.SF;
    }

    @Override // com.google.android.gms.common.api.Result
    public Status getStatus() {
        return this.CM;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(this.CM, this.SF);
    }

    public String toString() {
        return m.h(this).a("status", this.CM).a("dataType", this.SF).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        d.a(this, dest, flags);
    }
}
