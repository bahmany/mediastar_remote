package com.hisilicon.multiscreen.upnputils;

import org.cybergarage.upnp.Device;

/* loaded from: classes.dex */
public interface IUpnpControlPointListener {
    void originalListAdd(Device device);

    void originalListRemoved(Device device);

    void reavedNotify();

    void stbLeaveNotify();

    void stbSuspendNotify();
}
