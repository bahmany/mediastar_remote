package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

@ez
/* loaded from: classes.dex */
public final class x implements SafeParcelable {
    public static final y CREATOR = new y();
    public final boolean lX;
    public final boolean mh;
    public final int versionCode;

    x(int i, boolean z, boolean z2) {
        this.versionCode = i;
        this.lX = z;
        this.mh = z2;
    }

    public x(boolean z, boolean z2) {
        this.versionCode = 1;
        this.lX = z;
        this.mh = z2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        y.a(this, out, flags);
    }
}
