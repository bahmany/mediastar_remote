package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class OnListParentsResponse extends com.google.android.gms.drive.i implements SafeParcelable {
    public static final Parcelable.Creator<OnListParentsResponse> CREATOR = new ao();
    final int BR;
    final DataHolder Pn;

    OnListParentsResponse(int versionCode, DataHolder parents) {
        this.BR = versionCode;
        this.Pn = parents;
    }

    @Override // com.google.android.gms.drive.i
    protected void I(Parcel parcel, int i) {
        ao.a(this, parcel, i);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DataHolder ik() {
        return this.Pn;
    }
}
