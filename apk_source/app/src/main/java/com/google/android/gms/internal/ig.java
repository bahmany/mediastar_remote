package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class ig implements SafeParcelable {
    public static final Parcelable.Creator<ig> CREATOR = new ih();
    private final int BR;
    private String Gn;

    public ig() {
        this(1, null);
    }

    ig(int i, String str) {
        this.BR = i;
        this.Gn = str;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ig) {
            return ik.a(this.Gn, ((ig) obj).Gn);
        }
        return false;
    }

    public String fz() {
        return this.Gn;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.Gn);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        ih.a(this, out, flags);
    }
}
