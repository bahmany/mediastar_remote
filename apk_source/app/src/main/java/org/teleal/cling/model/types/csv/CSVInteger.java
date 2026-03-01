package org.teleal.cling.model.types.csv;

import org.teleal.cling.model.types.InvalidValueException;

/* loaded from: classes.dex */
public class CSVInteger extends CSV<Integer> {
    public CSVInteger() {
    }

    public CSVInteger(String s) throws InvalidValueException {
        super(s);
    }
}
