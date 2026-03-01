package org.teleal.cling.model.message.header;

import java.net.URI;
import org.teleal.cling.model.types.ServiceType;

/* loaded from: classes.dex */
public class ServiceTypeHeader extends UpnpHeader<ServiceType> {
    public ServiceTypeHeader() {
    }

    public ServiceTypeHeader(URI uri) throws InvalidHeaderException {
        setString(uri.toString());
    }

    public ServiceTypeHeader(ServiceType value) {
        setValue(value);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String s) throws InvalidHeaderException {
        try {
            setValue(ServiceType.valueOf(s));
        } catch (RuntimeException ex) {
            throw new InvalidHeaderException("Invalid service type header value, " + ex.getMessage());
        }
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue().toString();
    }
}
