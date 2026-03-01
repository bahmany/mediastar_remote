package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class DataTypeCreateRequest implements SafeParcelable {
    public static final Parcelable.Creator<DataTypeCreateRequest> CREATOR = new h();
    private final int BR;
    private final List<Field> SN;
    private final String mName;

    public static class Builder {
        private List<Field> SN = new ArrayList();
        private String mName;

        public Builder addField(Field field) {
            if (!this.SN.contains(field)) {
                this.SN.add(field);
            }
            return this;
        }

        public Builder addField(String name, int format) {
            com.google.android.gms.common.internal.n.b((name == null && name.isEmpty()) ? false : true, "Invalid name specified");
            return addField(new Field(name, format));
        }

        public DataTypeCreateRequest build() {
            com.google.android.gms.common.internal.n.a(this.mName != null, "Must set the name");
            com.google.android.gms.common.internal.n.a(this.SN.isEmpty() ? false : true, "Must specify the data fields");
            return new DataTypeCreateRequest(this);
        }

        public Builder setName(String name) {
            this.mName = name;
            return this;
        }
    }

    DataTypeCreateRequest(int versionCode, String name, List<Field> fields) {
        this.BR = versionCode;
        this.mName = name;
        this.SN = Collections.unmodifiableList(fields);
    }

    private DataTypeCreateRequest(Builder builder) {
        this.BR = 1;
        this.mName = builder.mName;
        this.SN = Collections.unmodifiableList(builder.SN);
    }

    private boolean a(DataTypeCreateRequest dataTypeCreateRequest) {
        return com.google.android.gms.common.internal.m.equal(this.mName, dataTypeCreateRequest.mName) && com.google.android.gms.common.internal.m.equal(this.SN, dataTypeCreateRequest.SN);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return o == this || ((o instanceof DataTypeCreateRequest) && a((DataTypeCreateRequest) o));
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
        return com.google.android.gms.common.internal.m.hashCode(this.mName, this.SN);
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("name", this.mName).a("fields", this.SN).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        h.a(this, dest, flags);
    }
}
