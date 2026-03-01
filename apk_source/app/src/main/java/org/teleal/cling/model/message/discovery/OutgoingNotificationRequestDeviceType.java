package org.teleal.cling.model.message.discovery;

import org.teleal.cling.model.Location;
import org.teleal.cling.model.message.header.DeviceTypeHeader;
import org.teleal.cling.model.message.header.DeviceUSNHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.types.NotificationSubtype;

/* loaded from: classes.dex */
public class OutgoingNotificationRequestDeviceType extends OutgoingNotificationRequest {
    public OutgoingNotificationRequestDeviceType(Location location, LocalDevice device, NotificationSubtype type) {
        super(location, device, type);
        getHeaders().add(UpnpHeader.Type.NT, new DeviceTypeHeader(device.getType()));
        getHeaders().add(UpnpHeader.Type.USN, new DeviceUSNHeader(device.getIdentity().getUdn(), device.getType()));
    }
}
