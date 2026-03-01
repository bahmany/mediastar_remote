package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;

/* loaded from: classes.dex */
public class ah implements SafeParcelable {
    public static final Parcelable.Creator<ah> CREATOR = new ai();
    private final int BR;
    private final DataType SF;
    private final DataSource Sh;

    public static class a {
        private DataType SF;
        private DataSource Sh;

        public a d(DataSource dataSource) {
            this.Sh = dataSource;
            return this;
        }

        public a d(DataType dataType) {
            this.SF = dataType;
            return this;
        }

        public ah jE() {
            if (this.SF == null || this.Sh == null) {
                return new ah(this);
            }
            throw new IllegalArgumentException("Cannot specify both dataType and dataSource");
        }
    }

    ah(int i, DataType dataType, DataSource dataSource) {
        this.BR = i;
        this.SF = dataType;
        this.Sh = dataSource;
    }

    private ah(a aVar) {
        this.BR = 1;
        this.SF = aVar.SF;
        this.Sh = aVar.Sh;
    }

    private boolean a(ah ahVar) {
        return com.google.android.gms.common.internal.m.equal(this.Sh, ahVar.Sh) && com.google.android.gms.common.internal.m.equal(this.SF, ahVar.SF);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return this == o || ((o instanceof ah) && a((ah) o));
    }

    public DataSource getDataSource() {
        return this.Sh;
    }

    public DataType getDataType() {
        return this.SF;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.Sh, this.SF);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        ai.a(this, parcel, flags);
    }
}
