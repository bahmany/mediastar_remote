package com.hisilicon.multiscreen.mybox;

import org.cybergarage.upnp.Device;

/* loaded from: classes.dex */
public interface IOriginalDeviceListListener {
    void deviceAdd(Device device);

    void deviceRemoved(Device device);
}
