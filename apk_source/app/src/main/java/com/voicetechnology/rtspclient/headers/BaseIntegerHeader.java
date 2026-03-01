package com.voicetechnology.rtspclient.headers;

import com.voicetechnology.rtspclient.concepts.Header;

/* loaded from: classes.dex */
public class BaseIntegerHeader extends Header {
    private int value;

    public BaseIntegerHeader(String name) {
        super(name);
        String text = getRawValue();
        if (text != null) {
            this.value = Integer.parseInt(text);
        }
    }

    public BaseIntegerHeader(String name, int value) {
        super(name);
        setValue(value);
    }

    public BaseIntegerHeader(String name, String header) {
        super(header);
        checkName(name);
        this.value = Integer.parseInt(getRawValue());
    }

    public final void setValue(int newValue) {
        this.value = newValue;
        setRawValue(String.valueOf(this.value));
    }

    public final int getValue() {
        return this.value;
    }
}
