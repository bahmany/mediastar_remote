package org.teleal.cling.model.types.csv;

import org.teleal.cling.model.types.InvalidValueException;

/* loaded from: classes.dex */
public class CSVString extends CSV<String> {
    public CSVString() {
    }

    public CSVString(String s) throws InvalidValueException {
        super(s);
    }
}
