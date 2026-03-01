package org.teleal.cling.model.message.header;

import java.net.URI;
import org.teleal.cling.model.types.DeviceType;

/* loaded from: classes.dex */
public class DeviceTypeHeader extends UpnpHeader<DeviceType> {
    public DeviceTypeHeader() {
    }

    public DeviceTypeHeader(URI uri) throws InvalidHeaderException {
        setString(uri.toString());
    }

    public DeviceTypeHeader(DeviceType value) {
        setValue(value);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String s) throws InvalidHeaderException {
        try {
            setValue(DeviceType.valueOf(s));
        } catch (RuntimeException ex) {
            throw new InvalidHeaderException("Invalid device type header value, " + ex.getMessage());
        }
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue().toString();
    }
}
