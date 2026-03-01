package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class Subscription implements SafeParcelable {
    public static final Parcelable.Creator<Subscription> CREATOR = new s();
    private final int BR;
    private final DataType SF;
    private final DataSource Sh;
    private final long Ti;
    private final int Tj;

    public static class a {
        private DataType SF;
        private DataSource Sh;
        private long Ti = -1;
        private int Tj = 2;

        public a b(DataSource dataSource) {
            this.Sh = dataSource;
            return this;
        }

        public a b(DataType dataType) {
            this.SF = dataType;
            return this;
        }

        public Subscription iR() {
            com.google.android.gms.common.internal.n.a((this.Sh == null && this.SF == null) ? false : true, "Must call setDataSource() or setDataType()");
            com.google.android.gms.common.internal.n.a(this.SF == null || this.Sh == null || this.SF.equals(this.Sh.getDataType()), "Specified data type is incompatible with specified data source");
            return new Subscription(this);
        }
    }

    Subscription(int versionCode, DataSource dataSource, DataType dataType, long samplingIntervalMicros, int accuracyMode) {
        this.BR = versionCode;
        this.Sh = dataSource;
        this.SF = dataType;
        this.Ti = samplingIntervalMicros;
        this.Tj = accuracyMode;
    }

    private Subscription(a builder) {
        this.BR = 1;
        this.SF = builder.SF;
        this.Sh = builder.Sh;
        this.Ti = builder.Ti;
        this.Tj = builder.Tj;
    }

    /* synthetic */ Subscription(a x0, AnonymousClass1 x1) {
        this(x0);
    }

    private boolean a(Subscription subscription) {
        return com.google.android.gms.common.internal.m.equal(this.Sh, subscription.Sh) && com.google.android.gms.common.internal.m.equal(this.SF, subscription.SF) && this.Ti == subscription.Ti && this.Tj == subscription.Tj;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof Subscription) && a((Subscription) that));
    }

    public DataSource getDataSource() {
        return this.Sh;
    }

    public DataType getDataType() {
        return this.SF;
    }

    public long getSamplingRateMicros() {
        return this.Ti;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.Sh, this.Sh, Long.valueOf(this.Ti), Integer.valueOf(this.Tj));
    }

    public int iQ() {
        return this.Tj;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("dataSource", this.Sh).a("dataType", this.SF).a("samplingIntervalMicros", Long.valueOf(this.Ti)).a("accuracyMode", Integer.valueOf(this.Tj)).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        s.a(this, dest, flags);
    }
}
