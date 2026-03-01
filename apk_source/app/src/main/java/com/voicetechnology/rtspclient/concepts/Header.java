package com.voicetechnology.rtspclient.concepts;

import com.voicetechnology.rtspclient.HeaderMismatchException;

/* loaded from: classes.dex */
public class Header {
    private String name;
    private String value;

    public Header(String header) {
        int colon = header.indexOf(58);
        if (colon == -1) {
            this.name = header;
        } else {
            this.name = header.substring(0, colon);
            this.value = header.substring(colon + 1).trim();
        }
    }

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getRawValue() {
        return this.value;
    }

    public void setRawValue(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.name) + ": " + this.value;
    }

    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof String) {
            return getName().equals(obj);
        }
        if (obj instanceof Header) {
            return getName().equals(((Header) obj).getName());
        }
        return false;
    }

    protected final void checkName(String expected) {
        if (!expected.equalsIgnoreCase(getName())) {
            throw new HeaderMismatchException(expected, getName());
        }
    }

    protected final void setName(String name) {
        this.value = this.name;
        this.name = name;
    }
}
