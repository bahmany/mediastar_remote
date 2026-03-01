package com.github.mikephil.charting.utils;

/* loaded from: classes.dex */
public final class FSize {
    public final float height;
    public final float width;

    public FSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FSize)) {
            return false;
        }
        FSize other = (FSize) obj;
        return this.width == other.width && this.height == other.height;
    }

    public String toString() {
        return String.valueOf(this.width) + "x" + this.height;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.width) ^ Float.floatToIntBits(this.height);
    }
}
