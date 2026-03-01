package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class UnclaimBleDeviceRequest implements SafeParcelable {
    public static final Parcelable.Creator<UnclaimBleDeviceRequest> CREATOR = new ag();
    private final int BR;
    private final String TX;

    UnclaimBleDeviceRequest(int versionCode, String deviceAddress) {
        this.BR = versionCode;
        this.TX = deviceAddress;
    }

    public UnclaimBleDeviceRequest(String deviceAddress) {
        this(3, deviceAddress);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getDeviceAddress() {
        return this.TX;
    }

    int getVersionCode() {
        return this.BR;
    }

    public String toString() {
        return String.format("UnclaimBleDeviceRequest{%s}", this.TX);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        ag.a(this, parcel, flags);
    }
}
