package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class Value implements SafeParcelable {
    public static final Parcelable.Creator<Value> CREATOR = new u();
    private final int BR;
    private final int ST;
    private boolean Tk;
    private float Tl;

    Value(int format) {
        this(1, format, false, 0.0f);
    }

    Value(int versionCode, int format, boolean isSet, float value) {
        this.BR = versionCode;
        this.ST = format;
        this.Tk = isSet;
        this.Tl = value;
    }

    private boolean a(Value value) {
        if (this.ST != value.ST || this.Tk != value.Tk) {
            return false;
        }
        switch (this.ST) {
            case 1:
                if (asInt() != value.asInt()) {
                    break;
                }
                break;
            case 2:
                if (asFloat() != value.asFloat()) {
                    break;
                }
                break;
            default:
                if (this.Tl != value.Tl) {
                    break;
                }
                break;
        }
        return true;
    }

    public float asFloat() {
        com.google.android.gms.common.internal.n.a(this.ST == 2, "Value is not in float format");
        return this.Tl;
    }

    public int asInt() {
        com.google.android.gms.common.internal.n.a(this.ST == 1, "Value is not in int format");
        return Float.floatToRawIntBits(this.Tl);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return this == o || ((o instanceof Value) && a((Value) o));
    }

    public int getFormat() {
        return this.ST;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(Float.valueOf(this.Tl), Integer.valueOf(this.ST), Boolean.valueOf(this.Tk));
    }

    float iS() {
        return this.Tl;
    }

    public boolean isSet() {
        return this.Tk;
    }

    public void setFloat(float value) {
        com.google.android.gms.common.internal.n.a(this.ST == 2, "Attempting to set an float value to a field that is not in FLOAT format.  Please check the data type definition and use the right format.");
        this.Tk = true;
        this.Tl = value;
    }

    public void setInt(int value) {
        com.google.android.gms.common.internal.n.a(this.ST == 1, "Attempting to set an int value to a field that is not in INT32 format.  Please check the data type definition and use the right format.");
        this.Tk = true;
        this.Tl = Float.intBitsToFloat(value);
    }

    public String toString() {
        switch (this.ST) {
            case 1:
                return Integer.toString(asInt());
            case 2:
                return Float.toString(asFloat());
            default:
                return "unknown";
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        u.a(this, dest, flags);
    }
}
