package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class om implements SafeParcelable {
    public static final Parcelable.Creator<om> CREATOR = new on();
    private final int BR;
    int[] atC;

    om() {
        this(1, null);
    }

    om(int i, int[] iArr) {
        this.BR = i;
        this.atC = iArr;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        on.a(this, out, flags);
    }
}
