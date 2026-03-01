package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataSource;

/* loaded from: classes.dex */
public class lh implements SafeParcelable {
    public static final Parcelable.Creator<lh> CREATOR = new li();
    private final int BR;
    private final DataSource Sh;

    lh(int i, DataSource dataSource) {
        this.BR = i;
        this.Sh = dataSource;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DataSource getDataSource() {
        return this.Sh;
    }

    int getVersionCode() {
        return this.BR;
    }

    public String toString() {
        return String.format("ApplicationUnregistrationRequest{%s}", this.Sh);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        li.a(this, parcel, flags);
    }
}
