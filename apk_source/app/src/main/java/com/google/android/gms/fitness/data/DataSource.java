package com.google.android.gms.fitness.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.kv;

/* loaded from: classes.dex */
public class DataSource implements SafeParcelable {
    public static final Parcelable.Creator<DataSource> CREATOR = new g();
    public static final int TYPE_DERIVED = 1;
    public static final int TYPE_RAW = 0;
    private final int BR;
    private final int FD;
    private final DataType SF;
    private final Device SI;
    private final a SJ;
    private final String SK;
    private final boolean SL;
    private final String SM;
    private final String mName;

    public static final class Builder {
        private DataType SF;
        private Device SI;
        private a SJ;
        private String mName;
        private int FD = -1;
        private String SK = "";
        private boolean SL = false;

        public DataSource build() {
            com.google.android.gms.common.internal.n.a(this.SF != null, "Must set data type");
            com.google.android.gms.common.internal.n.a(this.FD >= 0, "Must set data source type");
            return new DataSource(this);
        }

        public Builder setAppPackageName(Context appContext) {
            return setAppPackageName(appContext.getPackageName());
        }

        public Builder setAppPackageName(String packageName) {
            this.SJ = new a(packageName, null, null);
            return this;
        }

        public Builder setDataType(DataType dataType) {
            this.SF = dataType;
            return this;
        }

        public Builder setDevice(Device device) {
            this.SI = device;
            return this;
        }

        public Builder setName(String name) {
            this.mName = name;
            return this;
        }

        public Builder setObfuscated(boolean isObfuscated) {
            this.SL = isObfuscated;
            return this;
        }

        public Builder setStreamName(String streamName) {
            com.google.android.gms.common.internal.n.b(streamName != null, "Must specify a valid stream name");
            this.SK = streamName;
            return this;
        }

        public Builder setType(int type) {
            this.FD = type;
            return this;
        }
    }

    DataSource(int versionCode, DataType dataType, String name, int type, Device device, a application, String streamName, boolean isObfuscated) {
        this.BR = versionCode;
        this.SF = dataType;
        this.FD = type;
        this.mName = name;
        this.SI = device;
        this.SJ = application;
        this.SK = streamName;
        this.SL = isObfuscated;
        this.SM = iI();
    }

    private DataSource(Builder builder) {
        this.BR = 3;
        this.SF = builder.SF;
        this.FD = builder.FD;
        this.mName = builder.mName;
        this.SI = builder.SI;
        this.SJ = builder.SJ;
        this.SK = builder.SK;
        this.SL = builder.SL;
        this.SM = iI();
    }

    /* synthetic */ DataSource(Builder x0, AnonymousClass1 x1) {
        this(x0);
    }

    private boolean a(DataSource dataSource) {
        return this.SF.equals(dataSource.SF) && this.FD == dataSource.FD && com.google.android.gms.common.internal.m.equal(this.mName, dataSource.mName) && com.google.android.gms.common.internal.m.equal(this.SI, dataSource.SI) && com.google.android.gms.common.internal.m.equal(this.SK, dataSource.SK) && com.google.android.gms.common.internal.m.equal(this.SJ, dataSource.SJ);
    }

    private String getTypeString() {
        switch (this.FD) {
            case 0:
                return "raw";
            case 1:
                return "derived";
            default:
                throw new IllegalArgumentException("invalid type value");
        }
    }

    private String iI() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTypeString());
        sb.append(":").append(this.SF.getName());
        if (this.SJ != null) {
            sb.append(":").append(this.SJ.getPackageName());
        }
        if (this.SI != null) {
            sb.append(":").append(this.SI.getStreamIdentifier());
        }
        if (this.SK != null) {
            sb.append(":").append(this.SK);
        }
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof DataSource) && a((DataSource) that));
    }

    public String getAppPackageName() {
        if (this.SJ == null) {
            return null;
        }
        return this.SJ.getPackageName();
    }

    public DataType getDataType() {
        return this.SF;
    }

    public Device getDevice() {
        return this.SI;
    }

    public String getName() {
        return this.mName;
    }

    public String getStreamIdentifier() {
        return this.SM;
    }

    public String getStreamName() {
        return this.SK;
    }

    public int getType() {
        return this.FD;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return this.SM.hashCode();
    }

    public a iH() {
        return this.SJ;
    }

    public boolean iJ() {
        return this.SL;
    }

    public DataSource iK() {
        return new DataSource(3, this.SF, this.mName, this.FD, this.SI == null ? null : this.SI.iM(), this.SJ == null ? null : this.SJ.iA(), kv.bq(this.SK), this.SL);
    }

    public String toDebugString() {
        return (this.FD == 0 ? "r" : "d") + ":" + this.SF.iL() + (this.SJ == null ? "" : this.SJ.equals(a.Sp) ? ":gms" : ":" + this.SJ.getPackageName()) + (this.SI != null ? ":" + this.SI.getModel() : "") + (this.SK != null ? ":" + this.SK : "");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("DataSource{");
        sb.append(getTypeString());
        if (this.mName != null) {
            sb.append(":").append(this.mName);
        }
        if (this.SJ != null) {
            sb.append(":").append(this.SJ);
        }
        if (this.SI != null) {
            sb.append(":").append(this.SI);
        }
        if (this.SK != null) {
            sb.append(":").append(this.SK);
        }
        sb.append(":").append(this.SF);
        return sb.append("}").toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        g.a(kv.c(this), parcel, flags);
    }
}
