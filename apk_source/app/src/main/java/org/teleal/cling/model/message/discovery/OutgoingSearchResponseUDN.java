package org.teleal.cling.model.message.discovery;

import org.teleal.cling.model.Location;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.header.UDNHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.LocalDevice;

/* loaded from: classes.dex */
public class OutgoingSearchResponseUDN extends OutgoingSearchResponse {
    public OutgoingSearchResponseUDN(IncomingDatagramMessage request, Location location, LocalDevice device) {
        super(request, location, device);
        getHeaders().add(UpnpHeader.Type.ST, new UDNHeader(device.getIdentity().getUdn()));
        getHeaders().add(UpnpHeader.Type.USN, new UDNHeader(device.getIdentity().getUdn()));
    }
}
