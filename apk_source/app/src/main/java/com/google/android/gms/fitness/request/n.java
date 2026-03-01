package com.google.android.gms.fitness.request;

import android.app.PendingIntent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.k;
import com.google.android.gms.location.LocationRequest;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class n implements SafeParcelable {
    public static final Parcelable.Creator<n> CREATOR = new o();
    private final int BR;
    private final DataType SF;
    private final DataSource Sh;
    private final long Ti;
    private final int Tj;
    private com.google.android.gms.fitness.data.k Up;
    int Uq;
    int Ur;
    private final long Us;
    private final long Ut;
    private final List<LocationRequest> Uu;
    private final long Uv;
    private final List Uw;
    private final PendingIntent mPendingIntent;

    n(int i, DataSource dataSource, DataType dataType, IBinder iBinder, int i2, int i3, long j, long j2, PendingIntent pendingIntent, long j3, int i4, List<LocationRequest> list, long j4) {
        this.BR = i;
        this.Sh = dataSource;
        this.SF = dataType;
        this.Up = iBinder == null ? null : k.a.an(iBinder);
        this.Ti = j == 0 ? i2 : j;
        this.Ut = j3;
        this.Us = j2 == 0 ? i3 : j2;
        this.Uu = list;
        this.mPendingIntent = pendingIntent;
        this.Tj = i4;
        this.Uw = Collections.emptyList();
        this.Uv = j4;
    }

    private n(DataSource dataSource, DataType dataType, com.google.android.gms.fitness.data.k kVar, PendingIntent pendingIntent, long j, long j2, long j3, int i, List list, List list2, long j4) {
        this.BR = 4;
        this.Sh = dataSource;
        this.SF = dataType;
        this.Up = kVar;
        this.mPendingIntent = pendingIntent;
        this.Ti = j;
        this.Ut = j2;
        this.Us = j3;
        this.Tj = i;
        this.Uu = list;
        this.Uw = list2;
        this.Uv = j4;
    }

    public n(SensorRequest sensorRequest, com.google.android.gms.fitness.data.k kVar, PendingIntent pendingIntent) {
        this(sensorRequest.getDataSource(), sensorRequest.getDataType(), kVar, pendingIntent, sensorRequest.getSamplingRateMicros(), sensorRequest.jm(), sensorRequest.jn(), sensorRequest.iQ(), null, Collections.emptyList(), sensorRequest.jr());
    }

    private boolean a(n nVar) {
        return com.google.android.gms.common.internal.m.equal(this.Sh, nVar.Sh) && com.google.android.gms.common.internal.m.equal(this.SF, nVar.SF) && this.Ti == nVar.Ti && this.Ut == nVar.Ut && this.Us == nVar.Us && this.Tj == nVar.Tj && com.google.android.gms.common.internal.m.equal(this.Uu, nVar.Uu);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof n) && a((n) that));
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
        return com.google.android.gms.common.internal.m.hashCode(this.Sh, this.SF, this.Up, Long.valueOf(this.Ti), Long.valueOf(this.Ut), Long.valueOf(this.Us), Integer.valueOf(this.Tj), this.Uu);
    }

    public int iQ() {
        return this.Tj;
    }

    public PendingIntent jl() {
        return this.mPendingIntent;
    }

    public long jm() {
        return this.Ut;
    }

    public long jn() {
        return this.Us;
    }

    public List<LocationRequest> jo() {
        return this.Uu;
    }

    public long jp() {
        return this.Uv;
    }

    IBinder jq() {
        if (this.Up == null) {
            return null;
        }
        return this.Up.asBinder();
    }

    public String toString() {
        return String.format("SensorRegistrationRequest{type %s source %s interval %s fastest %s latency %s}", this.SF, this.Sh, Long.valueOf(this.Ti), Long.valueOf(this.Ut), Long.valueOf(this.Us));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        o.a(this, parcel, flags);
    }
}
