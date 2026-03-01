package org.teleal.cling.model.message.discovery;

import org.teleal.cling.model.Location;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.header.RootDeviceHeader;
import org.teleal.cling.model.message.header.UDNHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.LocalDevice;

/* loaded from: classes.dex */
public class OutgoingSearchResponseRootDeviceUDN extends OutgoingSearchResponse {
    public OutgoingSearchResponseRootDeviceUDN(IncomingDatagramMessage request, Location location, LocalDevice device) {
        super(request, location, device);
        getHeaders().add(UpnpHeader.Type.ST, new RootDeviceHeader());
        getHeaders().add(UpnpHeader.Type.USN, new UDNHeader(device.getIdentity().getUdn()));
    }
}
