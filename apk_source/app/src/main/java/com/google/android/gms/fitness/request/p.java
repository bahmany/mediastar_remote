package com.google.android.gms.fitness.request;

import android.app.PendingIntent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.k;

/* loaded from: classes.dex */
public class p implements SafeParcelable {
    public static final Parcelable.Creator<p> CREATOR = new q();
    private final int BR;
    private final com.google.android.gms.fitness.data.k Up;
    private final PendingIntent mPendingIntent;

    p(int i, IBinder iBinder, PendingIntent pendingIntent) {
        this.BR = i;
        this.Up = iBinder == null ? null : k.a.an(iBinder);
        this.mPendingIntent = pendingIntent;
    }

    public p(com.google.android.gms.fitness.data.k kVar, PendingIntent pendingIntent) {
        this.BR = 2;
        this.Up = kVar;
        this.mPendingIntent = pendingIntent;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    int getVersionCode() {
        return this.BR;
    }

    public PendingIntent jl() {
        return this.mPendingIntent;
    }

    IBinder jq() {
        if (this.Up == null) {
            return null;
        }
        return this.Up.asBinder();
    }

    public String toString() {
        return String.format("SensorUnregistrationRequest{%s}", this.Up);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        q.a(this, parcel, flags);
    }
}
