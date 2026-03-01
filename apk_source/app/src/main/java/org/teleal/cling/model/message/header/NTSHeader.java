package org.teleal.cling.model.message.header;

import org.teleal.cling.model.types.NotificationSubtype;

/* loaded from: classes.dex */
public class NTSHeader extends UpnpHeader<NotificationSubtype> {
    public NTSHeader() {
    }

    public NTSHeader(NotificationSubtype type) {
        setValue(type);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String s) throws InvalidHeaderException {
        NotificationSubtype[] notificationSubtypeArrValuesCustom = NotificationSubtype.valuesCustom();
        int length = notificationSubtypeArrValuesCustom.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            NotificationSubtype type = notificationSubtypeArrValuesCustom[i];
            if (!s.equals(type.getHeaderString())) {
                i++;
            } else {
                setValue(type);
                break;
            }
        }
        if (getValue() == null) {
            throw new InvalidHeaderException("Invalid NTS header value: " + s);
        }
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue().getHeaderString();
    }
}
