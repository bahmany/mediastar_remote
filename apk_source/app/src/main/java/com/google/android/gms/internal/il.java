package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class il implements SafeParcelable {
    public static final Parcelable.Creator<il> CREATOR = new im();
    private final int BR;
    private double FA;
    private boolean FB;
    private int GB;
    private int GC;
    private ApplicationMetadata GN;

    public il() {
        this(3, Double.NaN, false, -1, null, -1);
    }

    il(int i, double d, boolean z, int i2, ApplicationMetadata applicationMetadata, int i3) {
        this.BR = i;
        this.FA = d;
        this.FB = z;
        this.GB = i2;
        this.GN = applicationMetadata;
        this.GC = i3;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof il)) {
            return false;
        }
        il ilVar = (il) obj;
        return this.FA == ilVar.FA && this.FB == ilVar.FB && this.GB == ilVar.GB && ik.a(this.GN, ilVar.GN) && this.GC == ilVar.GC;
    }

    public double fF() {
        return this.FA;
    }

    public boolean fN() {
        return this.FB;
    }

    public int fO() {
        return this.GB;
    }

    public int fP() {
        return this.GC;
    }

    public ApplicationMetadata getApplicationMetadata() {
        return this.GN;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(Double.valueOf(this.FA), Boolean.valueOf(this.FB), Integer.valueOf(this.GB), this.GN, Integer.valueOf(this.GC));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        im.a(this, out, flags);
    }
}
