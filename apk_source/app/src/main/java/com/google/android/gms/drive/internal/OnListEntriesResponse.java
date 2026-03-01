package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class OnListEntriesResponse extends com.google.android.gms.drive.i implements SafeParcelable {
    public static final Parcelable.Creator<OnListEntriesResponse> CREATOR = new an();
    final int BR;
    final boolean Or;
    final DataHolder Pm;

    OnListEntriesResponse(int versionCode, DataHolder entries, boolean moreEntriesMayExist) {
        this.BR = versionCode;
        this.Pm = entries;
        this.Or = moreEntriesMayExist;
    }

    @Override // com.google.android.gms.drive.i
    protected void I(Parcel parcel, int i) {
        an.a(this, parcel, i);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DataHolder ii() {
        return this.Pm;
    }

    public boolean ij() {
        return this.Or;
    }
}
