package com.google.android.gms.fitness.request;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class t implements SafeParcelable {
    public static final Parcelable.Creator<t> CREATOR = new u();
    private final int BR;
    private final PendingIntent mPendingIntent;

    t(int i, PendingIntent pendingIntent) {
        this.BR = i;
        this.mPendingIntent = pendingIntent;
    }

    public t(PendingIntent pendingIntent) {
        this.BR = 3;
        this.mPendingIntent = pendingIntent;
    }

    private boolean a(t tVar) {
        return com.google.android.gms.common.internal.m.equal(this.mPendingIntent, tVar.mPendingIntent);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof t) && a((t) that));
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
        u.a(this, parcel, flags);
    }
}
