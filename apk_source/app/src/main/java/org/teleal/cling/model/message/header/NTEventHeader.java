package org.teleal.cling.model.message.header;

import org.cybergarage.upnp.device.NT;

/* loaded from: classes.dex */
public class NTEventHeader extends UpnpHeader<String> {
    public NTEventHeader() {
        setValue(NT.EVENT);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String s) throws InvalidHeaderException {
        if (!s.toLowerCase().equals(getValue())) {
            throw new InvalidHeaderException("Invalid event NT header value: " + s);
        }
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue();
    }
}
