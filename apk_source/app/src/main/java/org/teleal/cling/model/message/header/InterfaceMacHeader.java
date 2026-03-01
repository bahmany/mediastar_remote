package org.teleal.cling.model.message.header;

import org.teleal.common.util.HexBin;

/* loaded from: classes.dex */
public class InterfaceMacHeader extends UpnpHeader<byte[]> {
    public InterfaceMacHeader() {
    }

    public InterfaceMacHeader(byte[] value) {
        setValue(value);
    }

    public InterfaceMacHeader(String s) throws InvalidHeaderException {
        setString(s);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String s) throws InvalidHeaderException {
        byte[] bytes = HexBin.stringToBytes(s, ":");
        setValue(bytes);
        if (bytes.length != 6) {
            throw new InvalidHeaderException("Invalid MAC address: " + s);
        }
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return HexBin.bytesToString(getValue(), ":");
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String toString() {
        return "(" + getClass().getSimpleName() + ") '" + getString() + "'";
    }
}
