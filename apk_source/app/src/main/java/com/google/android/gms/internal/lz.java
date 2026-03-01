package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.location.LocationRequest;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class lz implements SafeParcelable {
    private final int BR;
    LocationRequest Ux;
    boolean aeX;
    boolean aeY;
    boolean aeZ;
    List<lr> afa;
    final String mTag;
    static final List<lr> aeW = Collections.emptyList();
    public static final ma CREATOR = new ma();

    lz(int i, LocationRequest locationRequest, boolean z, boolean z2, boolean z3, List<lr> list, String str) {
        this.BR = i;
        this.Ux = locationRequest;
        this.aeX = z;
        this.aeY = z2;
        this.aeZ = z3;
        this.afa = list;
        this.mTag = str;
    }

    private lz(String str, LocationRequest locationRequest) {
        this(1, locationRequest, false, true, true, aeW, str);
    }

    public static lz a(String str, LocationRequest locationRequest) {
        return new lz(str, locationRequest);
    }

    public static lz b(LocationRequest locationRequest) {
        return a(null, locationRequest);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object other) {
        if (!(other instanceof lz)) {
            return false;
        }
        lz lzVar = (lz) other;
        return com.google.android.gms.common.internal.m.equal(this.Ux, lzVar.Ux) && this.aeX == lzVar.aeX && this.aeY == lzVar.aeY && this.aeZ == lzVar.aeZ && com.google.android.gms.common.internal.m.equal(this.afa, lzVar.afa);
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return this.Ux.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.Ux.toString());
        sb.append(" requestNlpDebugInfo=");
        sb.append(this.aeX);
        sb.append(" restorePendingIntentListeners=");
        sb.append(this.aeY);
        sb.append(" triggerUpdate=");
        sb.append(this.aeZ);
        sb.append(" clients=");
        sb.append(this.afa);
        if (this.mTag != null) {
            sb.append(" tag=");
            sb.append(this.mTag);
        }
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        ma.a(this, parcel, flags);
    }
}
