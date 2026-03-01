package org.cybergarage.upnp;

import java.util.Vector;
import org.cybergarage.util.Mutex;

/* loaded from: classes.dex */
public class DeviceList extends Vector {
    public static final String ELEM_NAME = "deviceList";
    private Mutex mutex = new Mutex();

    public Device getDevice(int n) {
        if (n < size()) {
            return (Device) get(n);
        }
        return null;
    }

    public void lock() {
        this.mutex.lock();
    }

    public void unlock() {
        this.mutex.unlock();
    }
}
