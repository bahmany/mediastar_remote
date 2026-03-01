package com.google.android.gms.fitness.result;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class BleDevicesResult implements Result, SafeParcelable {
    public static final Parcelable.Creator<BleDevicesResult> CREATOR = new a();
    private final int BR;
    private final Status CM;
    private final List<BleDevice> UJ;

    BleDevicesResult(int versionCode, List<BleDevice> bleDevices, Status status) {
        this.BR = versionCode;
        this.UJ = Collections.unmodifiableList(bleDevices);
        this.CM = status;
    }

    public BleDevicesResult(List<BleDevice> bleDevices, Status status) {
        this.BR = 3;
        this.UJ = Collections.unmodifiableList(bleDevices);
        this.CM = status;
    }

    public static BleDevicesResult D(Status status) {
        return new BleDevicesResult(Collections.emptyList(), status);
    }

    private boolean b(BleDevicesResult bleDevicesResult) {
        return this.CM.equals(bleDevicesResult.CM) && m.equal(this.UJ, bleDevicesResult.UJ);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof BleDevicesResult) && b((BleDevicesResult) that));
    }

    public List<BleDevice> getClaimedBleDevices() {
        return this.UJ;
    }

    public List<BleDevice> getClaimedBleDevices(DataType dataType) {
        ArrayList arrayList = new ArrayList();
        for (BleDevice bleDevice : this.UJ) {
            if (bleDevice.getDataTypes().contains(dataType)) {
                arrayList.add(bleDevice);
            }
        }
        return Collections.unmodifiableList(arrayList);
    }

    @Override // com.google.android.gms.common.api.Result
    public Status getStatus() {
        return this.CM;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(this.CM, this.UJ);
    }

    public String toString() {
        return m.h(this).a("status", this.CM).a("bleDevices", this.UJ).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        a.a(this, dest, flags);
    }
}
