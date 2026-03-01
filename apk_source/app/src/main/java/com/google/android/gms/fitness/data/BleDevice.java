package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.ki;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class BleDevice implements SafeParcelable {
    public static final Parcelable.Creator<BleDevice> CREATOR = new c();
    private final int BR;
    private final String Ss;
    private final List<String> St;
    private final List<DataType> Su;
    private final String mName;

    BleDevice(int versionCode, String address, String name, List<String> profiles, List<DataType> dataTypes) {
        this.BR = versionCode;
        this.Ss = address;
        this.mName = name;
        this.St = Collections.unmodifiableList(profiles);
        this.Su = Collections.unmodifiableList(dataTypes);
    }

    private boolean a(BleDevice bleDevice) {
        return this.mName.equals(bleDevice.mName) && this.Ss.equals(bleDevice.Ss) && ki.a(bleDevice.St, this.St) && ki.a(this.Su, bleDevice.Su);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return o == this || ((o instanceof BleDevice) && a((BleDevice) o));
    }

    public String getAddress() {
        return this.Ss;
    }

    public List<DataType> getDataTypes() {
        return this.Su;
    }

    public String getName() {
        return this.mName;
    }

    public List<String> getSupportedProfiles() {
        return this.St;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.mName, this.Ss, this.St, this.Su);
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("name", this.mName).a("address", this.Ss).a("dataTypes", this.Su).a("supportedProfiles", this.St).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        c.a(this, parcel, flags);
    }
}
