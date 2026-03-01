package org.teleal.cling.model.types.csv;

import java.net.URI;
import org.teleal.cling.model.types.InvalidValueException;

/* loaded from: classes.dex */
public class CSVURI extends CSV<URI> {
    public CSVURI() {
    }

    public CSVURI(String s) throws InvalidValueException {
        super(s);
    }
}
