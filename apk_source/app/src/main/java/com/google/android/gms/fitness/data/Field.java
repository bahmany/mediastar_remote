package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class Field implements SafeParcelable {
    public static final Parcelable.Creator<Field> CREATOR = new j();
    public static final int FORMAT_FLOAT = 2;
    public static final int FORMAT_INT32 = 1;
    private final int BR;
    private final int ST;
    private final String mName;

    Field(int versionCode, String name, int format) {
        this.BR = versionCode;
        this.mName = name;
        this.ST = format;
    }

    public Field(String name, int format) {
        this(1, name, format);
    }

    private boolean a(Field field) {
        return this.mName.equals(field.mName) && this.ST == field.ST;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof Field) && a((Field) that));
    }

    public int getFormat() {
        return this.ST;
    }

    public String getName() {
        return this.mName;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return this.mName.hashCode();
    }

    public String toString() {
        Object[] objArr = new Object[2];
        objArr[0] = this.mName;
        objArr[1] = this.ST == 1 ? "i" : "f";
        return String.format("%s(%s)", objArr);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        j.a(this, dest, flags);
    }
}
