package org.teleal.cling.model.types.csv;

import org.teleal.cling.model.types.InvalidValueException;

/* loaded from: classes.dex */
public class CSVBytes extends CSV<Byte[]> {
    public CSVBytes() {
    }

    public CSVBytes(String s) throws InvalidValueException {
        super(s);
    }
}
