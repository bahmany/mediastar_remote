package org.cybergarage.upnp.device;

import org.cybergarage.upnp.Device;

/* loaded from: classes.dex */
public interface DeviceChangeListener {
    void deviceAdded(Device device);

    void deviceRefreshed(Device device);

    void deviceRemoved(Device device);
}
