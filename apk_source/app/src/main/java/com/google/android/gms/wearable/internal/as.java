package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class as implements SafeParcelable {
    public static final Parcelable.Creator<as> CREATOR = new at();
    public final int avD;
    public final int statusCode;
    public final int versionCode;

    as(int i, int i2, int i3) {
        this.versionCode = i;
        this.statusCode = i2;
        this.avD = i3;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        at.a(this, dest, flags);
    }
}
