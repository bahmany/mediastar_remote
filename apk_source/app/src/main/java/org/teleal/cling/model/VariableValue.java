package org.teleal.cling.model;

import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.InvalidValueException;

/* loaded from: classes.dex */
public class VariableValue {
    private final Datatype datatype;
    private final Object value;

    public VariableValue(Datatype datatype, Object value) throws InvalidValueException {
        this.datatype = datatype;
        this.value = value instanceof String ? datatype.valueOf((String) value) : value;
        if (!ModelUtil.ANDROID_RUNTIME) {
            if (!getDatatype().isValid(getValue())) {
                throw new InvalidValueException("Invalid value for " + getDatatype() + ": " + getValue());
            }
            if (!isValidXMLString(toString())) {
                throw new InvalidValueException("Invalid characters in string value (XML 1.0, section 2.2) produced by " + getDatatype());
            }
        }
    }

    public Datatype getDatatype() {
        return this.datatype;
    }

    public Object getValue() {
        return this.value;
    }

    protected boolean isValidXMLString(String s) {
        int i = 0;
        while (i < s.length()) {
            int cp = s.codePointAt(i);
            if (cp != 9 && cp != 10 && cp != 13 && ((cp < 32 || cp > 55295) && ((cp < 57344 || cp > 65533) && (cp < 65536 || cp > 1114111)))) {
                return false;
            }
            i += Character.charCount(cp);
        }
        return true;
    }

    public String toString() {
        return getDatatype().getString(getValue());
    }
}
