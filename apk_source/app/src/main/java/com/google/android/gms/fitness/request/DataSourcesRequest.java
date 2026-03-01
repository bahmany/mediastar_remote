package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.internal.jr;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class DataSourcesRequest implements SafeParcelable {
    public static final Parcelable.Creator<DataSourcesRequest> CREATOR = new g();
    private final int BR;
    private final List<DataType> Su;
    private final List<Integer> Ul;
    private final boolean Um;

    public static class Builder {
        private DataType[] Un = new DataType[0];
        private int[] Uo = {0, 1};
        private boolean Um = false;

        public DataSourcesRequest build() {
            com.google.android.gms.common.internal.n.a(this.Un.length > 0, "Must add at least one data type");
            com.google.android.gms.common.internal.n.a(this.Uo.length > 0, "Must add at least one data source type");
            return new DataSourcesRequest(this);
        }

        public Builder setDataSourceTypes(int... dataSourceTypes) {
            this.Uo = dataSourceTypes;
            return this;
        }

        public Builder setDataTypes(DataType... dataTypes) {
            this.Un = dataTypes;
            return this;
        }
    }

    DataSourcesRequest(int versionCode, List<DataType> dataTypes, List<Integer> dataSourceTypes, boolean includeDbOnlySources) {
        this.BR = versionCode;
        this.Su = dataTypes;
        this.Ul = dataSourceTypes;
        this.Um = includeDbOnlySources;
    }

    private DataSourcesRequest(Builder builder) {
        this.BR = 2;
        this.Su = jr.b(builder.Un);
        this.Ul = Arrays.asList(jr.a(builder.Uo));
        this.Um = builder.Um;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public List<DataType> getDataTypes() {
        return Collections.unmodifiableList(this.Su);
    }

    int getVersionCode() {
        return this.BR;
    }

    public boolean ji() {
        return this.Um;
    }

    List<Integer> jj() {
        return this.Ul;
    }

    public String toString() {
        m.a aVarA = com.google.android.gms.common.internal.m.h(this).a("dataTypes", this.Su).a("sourceTypes", this.Ul);
        if (this.Um) {
            aVarA.a("includeDbOnlySources", "true");
        }
        return aVarA.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        g.a(this, parcel, flags);
    }
}
