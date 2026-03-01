package org.teleal.cling.model.types;

/* loaded from: classes.dex */
public class StringDatatype extends AbstractDatatype<String> {
    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public String valueOf(String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        return s;
    }
}
