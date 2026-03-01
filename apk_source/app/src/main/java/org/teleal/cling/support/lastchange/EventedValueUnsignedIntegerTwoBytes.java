package org.teleal.cling.support.lastchange;

import java.util.Map;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytes;

/* loaded from: classes.dex */
public class EventedValueUnsignedIntegerTwoBytes extends EventedValue<UnsignedIntegerTwoBytes> {
    public EventedValueUnsignedIntegerTwoBytes(UnsignedIntegerTwoBytes value) {
        super(value);
    }

    public EventedValueUnsignedIntegerTwoBytes(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    protected Datatype getDatatype() {
        return Datatype.Builtin.UI2.getDatatype();
    }
}
