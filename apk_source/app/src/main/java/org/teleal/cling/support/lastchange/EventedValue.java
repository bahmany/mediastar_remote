package org.teleal.cling.support.lastchange;

import java.util.Map;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.support.shared.AbstractMap;

/* loaded from: classes.dex */
public abstract class EventedValue<V> {
    protected final V value;

    protected abstract Datatype getDatatype();

    public EventedValue(V value) {
        this.value = value;
    }

    public EventedValue(Map.Entry<String, String>[] entryArr) {
        try {
            this.value = valueOf(entryArr);
        } catch (InvalidValueException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public V getValue() {
        return this.value;
    }

    public Map.Entry<String, String>[] getAttributes() {
        return new Map.Entry[]{new AbstractMap.SimpleEntry("val", toString())};
    }

    protected V valueOf(Map.Entry<String, String>[] entryArr) throws InvalidValueException {
        V v = null;
        for (Map.Entry<String, String> attribute : entryArr) {
            if (attribute.getKey().equals("val")) {
                v = valueOf(attribute.getValue());
            }
        }
        return v;
    }

    protected V valueOf(String str) throws InvalidValueException {
        return (V) getDatatype().valueOf(str);
    }

    public String toString() {
        return getDatatype().getString(getValue());
    }
}
