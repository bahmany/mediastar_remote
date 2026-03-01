package org.teleal.cling.model.message.header;

import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.NamedDeviceType;
import org.teleal.cling.model.types.UDN;

/* loaded from: classes.dex */
public class DeviceUSNHeader extends UpnpHeader<NamedDeviceType> {
    public DeviceUSNHeader() {
    }

    public DeviceUSNHeader(UDN udn, DeviceType deviceType) {
        setValue(new NamedDeviceType(udn, deviceType));
    }

    public DeviceUSNHeader(NamedDeviceType value) {
        setValue(value);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String s) throws InvalidHeaderException {
        try {
            setValue(NamedDeviceType.valueOf(s));
        } catch (Exception ex) {
            throw new InvalidHeaderException("Invalid device USN header value, " + ex.getMessage());
        }
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue().toString();
    }
}
