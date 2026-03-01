package org.teleal.cling.model.types.csv;

import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;

/* loaded from: classes.dex */
public class CSVUnsignedIntegerFourBytes extends CSV<UnsignedIntegerFourBytes> {
    public CSVUnsignedIntegerFourBytes() {
    }

    public CSVUnsignedIntegerFourBytes(String s) throws InvalidValueException {
        super(s);
    }
}
