package org.teleal.cling.model.types.csv;

import org.teleal.cling.model.types.InvalidValueException;

/* loaded from: classes.dex */
public class CSVDouble extends CSV<Double> {
    public CSVDouble() {
    }

    public CSVDouble(String s) throws InvalidValueException {
        super(s);
    }
}
