package org.teleal.cling.model.types;

import org.teleal.common.util.ByteArray;
import org.teleal.common.util.HexBin;

/* loaded from: classes.dex */
public class BinHexDatatype extends AbstractDatatype<Byte[]> {
    @Override // org.teleal.cling.model.types.AbstractDatatype
    public Class<Byte[]> getValueType() {
        return Byte[].class;
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public Byte[] valueOf(String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        try {
            return ByteArray.toWrapper(HexBin.stringToBytes(s));
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public String getString(Byte[] value) throws InvalidValueException {
        if (value == null) {
            return "";
        }
        try {
            return HexBin.bytesToString(ByteArray.toPrimitive(value));
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }
}
