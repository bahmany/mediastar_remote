package com.google.android.gms.fitness.request;

import com.google.android.gms.fitness.data.BleDevice;

/* loaded from: classes.dex */
public interface BleScanCallback {
    void onDeviceFound(BleDevice bleDevice);

    void onScanStopped();
}
