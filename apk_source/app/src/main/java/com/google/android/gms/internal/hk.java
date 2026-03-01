package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class hk implements SafeParcelable {
    public static final hl CREATOR = new hl();
    final int BR;
    final Bundle Ci;
    public final int id;

    hk(int i, int i2, Bundle bundle) {
        this.BR = i;
        this.id = i2;
        this.Ci = bundle;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        hl hlVar = CREATOR;
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        hl hlVar = CREATOR;
        hl.a(this, dest, flags);
    }
}
