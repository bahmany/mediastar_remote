package org.teleal.cling.model.types;

/* loaded from: classes.dex */
public class CustomDatatype extends AbstractDatatype<String> {
    private String name;

    public CustomDatatype(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public String valueOf(String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        return s;
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype
    public String toString() {
        return "(" + getClass().getSimpleName() + ") '" + getName() + "'";
    }
}
