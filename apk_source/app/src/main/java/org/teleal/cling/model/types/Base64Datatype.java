package org.teleal.cling.model.types;

import org.teleal.common.util.Base64Coder;
import org.teleal.common.util.ByteArray;

/* loaded from: classes.dex */
public class Base64Datatype extends AbstractDatatype<Byte[]> {
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
            return ByteArray.toWrapper(Base64Coder.decode(s));
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
            return new String(Base64Coder.encode(ByteArray.toPrimitive(value)));
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }
}
