package org.teleal.cling.model.message.discovery;

import org.teleal.cling.model.Location;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.header.ServiceTypeHeader;
import org.teleal.cling.model.message.header.ServiceUSNHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.types.ServiceType;

/* loaded from: classes.dex */
public class OutgoingSearchResponseServiceType extends OutgoingSearchResponse {
    public OutgoingSearchResponseServiceType(IncomingDatagramMessage request, Location location, LocalDevice device, ServiceType serviceType) {
        super(request, location, device);
        getHeaders().add(UpnpHeader.Type.ST, new ServiceTypeHeader(serviceType));
        getHeaders().add(UpnpHeader.Type.USN, new ServiceUSNHeader(device.getIdentity().getUdn(), serviceType));
    }
}
