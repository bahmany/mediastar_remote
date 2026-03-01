package org.teleal.cling.model.types;

import android.support.v4.media.TransportMediator;
import android.support.v7.internal.widget.ActivityChooserView;

/* loaded from: classes.dex */
public class IntegerDatatype extends AbstractDatatype<Integer> {
    private int byteSize;

    public IntegerDatatype(int byteSize) {
        this.byteSize = byteSize;
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public boolean isHandlingJavaType(Class type) {
        return type == Integer.TYPE || Integer.class.isAssignableFrom(type);
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public Integer valueOf(String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        try {
            Integer value = Integer.valueOf(Integer.parseInt(s));
            if (!isValid(value)) {
                throw new InvalidValueException("Not a " + getByteSize() + " byte(s) integer: " + s);
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new InvalidValueException("Can't convert string to number: " + s, ex);
        }
    }

    @Override // org.teleal.cling.model.types.AbstractDatatype, org.teleal.cling.model.types.Datatype
    public boolean isValid(Integer value) {
        return value == null || (value.intValue() >= getMinValue() && value.intValue() <= getMaxValue());
    }

    public int getMinValue() {
        switch (getByteSize()) {
            case 1:
                return -128;
            case 2:
                return -32768;
            case 3:
            default:
                throw new IllegalArgumentException("Invalid integer byte size: " + getByteSize());
            case 4:
                return Integer.MIN_VALUE;
        }
    }

    public int getMaxValue() {
        switch (getByteSize()) {
            case 1:
                return TransportMediator.KEYCODE_MEDIA_PAUSE;
            case 2:
                return 32767;
            case 3:
            default:
                throw new IllegalArgumentException("Invalid integer byte size: " + getByteSize());
            case 4:
                return ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    }

    public int getByteSize() {
        return this.byteSize;
    }
}
