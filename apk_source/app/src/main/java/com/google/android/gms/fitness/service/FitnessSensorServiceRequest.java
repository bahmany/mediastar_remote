package com.google.android.gms.fitness.service;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.k;

/* loaded from: classes.dex */
public class FitnessSensorServiceRequest implements SafeParcelable {
    public static final Parcelable.Creator<FitnessSensorServiceRequest> CREATOR = new a();
    public static final int UNSPECIFIED = -1;
    private final int BR;
    private final DataSource Sh;
    private final long UR;
    private final long US;
    private final k Up;

    FitnessSensorServiceRequest(int versionCode, DataSource dataSource, IBinder listenerBinder, long samplingRateMicros, long batchIntervalMicros) {
        this.BR = versionCode;
        this.Sh = dataSource;
        this.Up = k.a.an(listenerBinder);
        this.UR = samplingRateMicros;
        this.US = batchIntervalMicros;
    }

    private boolean a(FitnessSensorServiceRequest fitnessSensorServiceRequest) {
        return m.equal(this.Sh, fitnessSensorServiceRequest.Sh) && this.UR == fitnessSensorServiceRequest.UR && this.US == fitnessSensorServiceRequest.US;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof FitnessSensorServiceRequest) && a((FitnessSensorServiceRequest) that));
    }

    public long getBatchIntervalMicros() {
        return this.US;
    }

    public DataSource getDataSource() {
        return this.Sh;
    }

    public SensorEventDispatcher getDispatcher() {
        return new b(this.Up);
    }

    public long getSamplingRateMicros() {
        return this.UR;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(this.Sh, Long.valueOf(this.UR), Long.valueOf(this.US));
    }

    IBinder jq() {
        return this.Up.asBinder();
    }

    public String toString() {
        return String.format("FitnessSensorServiceRequest{%s}", this.Sh);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        a.a(this, parcel, flags);
    }
}
