package org.teleal.cling.model.message.discovery;

import org.teleal.cling.model.Location;
import org.teleal.cling.model.message.header.UDNHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.types.NotificationSubtype;

/* loaded from: classes.dex */
public class OutgoingNotificationRequestUDN extends OutgoingNotificationRequest {
    public OutgoingNotificationRequestUDN(Location location, LocalDevice device, NotificationSubtype type) {
        super(location, device, type);
        getHeaders().add(UpnpHeader.Type.NT, new UDNHeader(device.getIdentity().getUdn()));
        getHeaders().add(UpnpHeader.Type.USN, new UDNHeader(device.getIdentity().getUdn()));
    }
}
