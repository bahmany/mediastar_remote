package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.BleDevice;

/* loaded from: classes.dex */
public class b implements SafeParcelable {
    public static final Parcelable.Creator<b> CREATOR = new c();
    private final int BR;
    private final String TX;
    private final BleDevice TY;

    b(int i, String str, BleDevice bleDevice) {
        this.BR = i;
        this.TX = str;
        this.TY = bleDevice;
    }

    public b(BleDevice bleDevice) {
        this(2, bleDevice.getAddress(), bleDevice);
    }

    public b(String str) {
        this(2, str, null);
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

    public BleDevice iW() {
        return this.TY;
    }

    public String toString() {
        return String.format("ClaimBleDeviceRequest{%s %s}", this.TX, this.TY);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        c.a(this, parcel, flags);
    }
}
