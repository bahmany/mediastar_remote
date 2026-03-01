package com.google.android.gms.fitness.request;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.a;
import com.google.android.gms.fitness.request.k;
import com.google.android.gms.internal.jr;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class StartBleScanRequest implements SafeParcelable {
    public static final Parcelable.Creator<StartBleScanRequest> CREATOR = new ab();
    private final int BR;
    private final List<DataType> Su;
    private final k UF;
    private final int UG;

    public static class Builder {
        private k UF;
        private DataType[] Un = new DataType[0];
        private int UG = 10;

        public Builder a(k kVar) {
            this.UF = kVar;
            return this;
        }

        public StartBleScanRequest build() {
            com.google.android.gms.common.internal.n.a(this.UF != null, "Must set BleScanCallback");
            return new StartBleScanRequest(this);
        }

        public Builder setBleScanCallback(BleScanCallback bleScanCallback) {
            a(a.C0033a.iV().a(bleScanCallback));
            return this;
        }

        public Builder setDataTypes(DataType... dataTypes) {
            this.Un = dataTypes;
            return this;
        }

        public Builder setTimeoutSecs(int stopTimeSecs) {
            com.google.android.gms.common.internal.n.b(stopTimeSecs > 0, "Stop time must be greater than zero");
            com.google.android.gms.common.internal.n.b(stopTimeSecs <= 60, "Stop time must be less than 1 minute");
            this.UG = stopTimeSecs;
            return this;
        }
    }

    StartBleScanRequest(int versionCode, List<DataType> dataTypes, IBinder bleScanCallback, int timeoutSecs) {
        this.BR = versionCode;
        this.Su = dataTypes;
        this.UF = k.a.ay(bleScanCallback);
        this.UG = timeoutSecs;
    }

    private StartBleScanRequest(Builder builder) {
        this.BR = 2;
        this.Su = jr.b(builder.Un);
        this.UF = builder.UF;
        this.UG = builder.UG;
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

    public int jA() {
        return this.UG;
    }

    public IBinder jz() {
        return this.UF.asBinder();
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("dataTypes", this.Su).a("timeoutSecs", Integer.valueOf(this.UG)).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        ab.a(this, parcel, flags);
    }
}
