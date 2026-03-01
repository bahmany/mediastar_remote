package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.jr;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public final class DataType implements SafeParcelable {
    public static final Parcelable.Creator<DataType> CREATOR = new h();
    private final int BR;
    private final List<Field> SN;
    private final String mName;

    DataType(int versionCode, String name, List<Field> fields) {
        this.BR = versionCode;
        this.mName = name;
        this.SN = Collections.unmodifiableList(fields);
    }

    public DataType(String name, Field... fields) {
        this(1, name, jr.b(fields));
    }

    private boolean a(DataType dataType) {
        return this.mName.equals(dataType.mName) && this.SN.equals(dataType.SN);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof DataType) && a((DataType) that));
    }

    public List<Field> getFields() {
        return this.SN;
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

    public String iL() {
        return this.mName.startsWith("com.google.") ? this.mName.substring(11) : this.mName;
    }

    public int indexOf(Field field) {
        if (this.SN.contains(field)) {
            return this.SN.indexOf(field);
        }
        throw new IllegalArgumentException(String.format("%s not a field of %s", field, this));
    }

    public String toString() {
        return String.format("DataType{%s%s}", this.mName, this.SN);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        h.a(this, dest, flags);
    }
}
