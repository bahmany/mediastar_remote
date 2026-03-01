package org.teleal.cling.model.message.header;

import java.net.URI;
import org.teleal.cling.model.types.UDAServiceType;

/* loaded from: classes.dex */
public class UDAServiceTypeHeader extends ServiceTypeHeader {
    public UDAServiceTypeHeader() {
    }

    public UDAServiceTypeHeader(URI uri) {
        super(uri);
    }

    public UDAServiceTypeHeader(UDAServiceType value) {
        super(value);
    }

    @Override // org.teleal.cling.model.message.header.ServiceTypeHeader, org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String s) throws InvalidHeaderException {
        try {
            setValue(UDAServiceType.m10valueOf(s));
        } catch (Exception ex) {
            throw new InvalidHeaderException("Invalid UDA service type header value, " + ex.getMessage());
        }
    }
}
