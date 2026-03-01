package org.teleal.cling.model.types.csv;

import org.teleal.cling.model.types.InvalidValueException;

/* loaded from: classes.dex */
public class CSVBoolean extends CSV<Boolean> {
    public CSVBoolean() {
    }

    public CSVBoolean(String s) throws InvalidValueException {
        super(s);
    }
}
