package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataType;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class lf implements SafeParcelable {
    public static final Parcelable.Creator<lf> CREATOR = new lg();
    private final int BR;
    private final List<DataType> Su;

    lf(int i, List<DataType> list) {
        this.BR = i;
        this.Su = list;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public List<DataType> getDataTypes() {
        return Collections.unmodifiableList(this.Su);
    }

    int getVersionCode() {
        return this.BR;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("dataTypes", this.Su).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        lg.a(this, parcel, flags);
    }
}
