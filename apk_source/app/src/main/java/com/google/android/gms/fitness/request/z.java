package com.google.android.gms.fitness.request;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class z implements SafeParcelable {
    public static final Parcelable.Creator<z> CREATOR = new aa();
    private final int BR;
    private final PendingIntent mPendingIntent;

    z(int i, PendingIntent pendingIntent) {
        this.BR = i;
        this.mPendingIntent = pendingIntent;
    }

    public z(PendingIntent pendingIntent) {
        this.BR = 3;
        this.mPendingIntent = pendingIntent;
    }

    private boolean a(z zVar) {
        return com.google.android.gms.common.internal.m.equal(this.mPendingIntent, zVar.mPendingIntent);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof z) && a((z) that));
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.mPendingIntent);
    }

    public PendingIntent jl() {
        return this.mPendingIntent;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("pendingIntent", this.mPendingIntent).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        aa.a(this, parcel, flags);
    }
}
