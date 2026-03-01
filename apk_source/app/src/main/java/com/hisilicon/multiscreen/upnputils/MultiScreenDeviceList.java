package com.hisilicon.multiscreen.upnputils;

import org.cybergarage.upnp.DeviceList;
import org.cybergarage.util.Mutex;

/* loaded from: classes.dex */
public class MultiScreenDeviceList extends DeviceList {
    private static final long serialVersionUID = 2739688970776699423L;
    private Mutex mutex = new Mutex();

    @Override // org.cybergarage.upnp.DeviceList
    public void lock() {
        this.mutex.lock();
    }

    @Override // org.cybergarage.upnp.DeviceList
    public void unlock() {
        this.mutex.unlock();
    }
}
