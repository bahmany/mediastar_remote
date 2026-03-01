package org.teleal.cling.model.message.header;

/* loaded from: classes.dex */
public class RootDeviceHeader extends UpnpHeader<String> {
    public RootDeviceHeader() {
        setValue("upnp:rootdevice");
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String s) throws InvalidHeaderException {
        if (!s.toLowerCase().equals(getValue())) {
            throw new InvalidHeaderException("Invalid root device NT header value: " + s);
        }
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue();
    }
}
