package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class mg implements SafeParcelable {
    public static final mh CREATOR = new mh();
    private final int BR;
    private final int adW;
    private final int afe;
    private final mi aff;

    mg(int i, int i2, int i3, mi miVar) {
        this.BR = i;
        this.adW = i2;
        this.afe = i3;
        this.aff = miVar;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        mh mhVar = CREATOR;
        return 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof mg)) {
            return false;
        }
        mg mgVar = (mg) object;
        return this.adW == mgVar.adW && this.afe == mgVar.afe && this.aff.equals(mgVar.aff);
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(Integer.valueOf(this.adW), Integer.valueOf(this.afe));
    }

    public int ma() {
        return this.adW;
    }

    public int me() {
        return this.afe;
    }

    public mi mf() {
        return this.aff;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("transitionTypes", Integer.valueOf(this.adW)).a("loiteringTimeMillis", Integer.valueOf(this.afe)).a("placeFilter", this.aff).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        mh mhVar = CREATOR;
        mh.a(this, parcel, flags);
    }
}
