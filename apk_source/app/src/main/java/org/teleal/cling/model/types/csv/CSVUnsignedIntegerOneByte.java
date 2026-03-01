package org.teleal.cling.model.types.csv;

import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UnsignedIntegerOneByte;

/* loaded from: classes.dex */
public class CSVUnsignedIntegerOneByte extends CSV<UnsignedIntegerOneByte> {
    public CSVUnsignedIntegerOneByte() {
    }

    public CSVUnsignedIntegerOneByte(String s) throws InvalidValueException {
        super(s);
    }
}
