package org.teleal.cling.model.types;

/* loaded from: classes.dex */
public class BooleanDatatype extends AbstractDatatype<Boolean> {
    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public boolean isHandlingJavaType(Class type) {
        return type == Boolean.TYPE || Boolean.class.isAssignableFrom(type);
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public Boolean valueOf(String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        if (s.equals("1") || s.toUpperCase().equals("YES") || s.toUpperCase().equals("TRUE")) {
            return true;
        }
        if (s.equals("0") || s.toUpperCase().equals("NO") || s.toUpperCase().equals("FALSE")) {
            return false;
        }
        throw new InvalidValueException("Invalid boolean value string: " + s);
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public String getString(Boolean value) throws InvalidValueException {
        return value == null ? "" : value.booleanValue() ? "1" : "0";
    }
}
