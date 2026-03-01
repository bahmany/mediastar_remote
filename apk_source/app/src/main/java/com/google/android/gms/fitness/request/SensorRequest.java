package com.google.android.gms.fitness.request;

import android.os.SystemClock;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.location.LocationRequest;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class SensorRequest {
    public static final int ACCURACY_MODE_DEFAULT = 2;
    public static final int ACCURACY_MODE_HIGH = 3;
    public static final int ACCURACY_MODE_LOW = 1;
    private final DataType SF;
    private final DataSource Sh;
    private final long Ti;
    private final int Tj;
    private final long Us;
    private final long Ut;
    private final LocationRequest Ux;
    private final long Uy;

    public static class Builder {
        private DataType SF;
        private DataSource Sh;
        private long Ti = -1;
        private long Ut = 0;
        private long Us = 0;
        private boolean Uz = false;
        private int Tj = 2;
        private long Uy = Long.MAX_VALUE;

        public SensorRequest build() {
            com.google.android.gms.common.internal.n.a((this.Sh == null && this.SF == null) ? false : true, "Must call setDataSource() or setDataType()");
            com.google.android.gms.common.internal.n.a(this.SF == null || this.Sh == null || this.SF.equals(this.Sh.getDataType()), "Specified data type is incompatible with specified data source");
            return new SensorRequest(this);
        }

        public Builder setAccuracyMode(int accuracyMode) {
            this.Tj = SensorRequest.da(accuracyMode);
            return this;
        }

        public Builder setDataSource(DataSource dataSource) {
            this.Sh = dataSource;
            return this;
        }

        public Builder setDataType(DataType dataType) {
            this.SF = dataType;
            return this;
        }

        public Builder setFastestRate(int fastestInterval, TimeUnit unit) {
            com.google.android.gms.common.internal.n.b(fastestInterval >= 0, "Cannot use a negative interval");
            this.Uz = true;
            this.Ut = unit.toMicros(fastestInterval);
            return this;
        }

        public Builder setMaxDeliveryLatency(int interval, TimeUnit unit) {
            com.google.android.gms.common.internal.n.b(interval >= 0, "Cannot use a negative delivery interval");
            this.Us = unit.toMicros(interval);
            return this;
        }

        public Builder setSamplingRate(long interval, TimeUnit unit) {
            com.google.android.gms.common.internal.n.b(interval >= 0, "Cannot use a negative sampling interval");
            this.Ti = unit.toMicros(interval);
            if (!this.Uz) {
                this.Ut = this.Ti / 2;
            }
            return this;
        }

        public Builder setTimeout(long timeout, TimeUnit timeUnit) {
            com.google.android.gms.common.internal.n.b(timeout > 0, "Invalid time out value specified: %d", Long.valueOf(timeout));
            com.google.android.gms.common.internal.n.b(timeUnit != null, "Invalid time unit specified");
            this.Uy = timeUnit.toMicros(timeout);
            return this;
        }
    }

    private SensorRequest(DataSource dataSource, LocationRequest locationRequest) {
        this.Ux = locationRequest;
        this.Ti = TimeUnit.MILLISECONDS.toMicros(locationRequest.getInterval());
        this.Ut = TimeUnit.MILLISECONDS.toMicros(locationRequest.getFastestInterval());
        this.Us = this.Ti;
        this.SF = dataSource.getDataType();
        this.Tj = a(locationRequest);
        this.Sh = dataSource;
        long expirationTime = locationRequest.getExpirationTime();
        if (expirationTime == Long.MAX_VALUE) {
            this.Uy = Long.MAX_VALUE;
        } else {
            this.Uy = TimeUnit.MILLISECONDS.toMicros(expirationTime - SystemClock.elapsedRealtime());
        }
    }

    private SensorRequest(Builder builder) {
        this.Sh = builder.Sh;
        this.SF = builder.SF;
        this.Ti = builder.Ti;
        this.Ut = builder.Ut;
        this.Us = builder.Us;
        this.Tj = builder.Tj;
        this.Ux = null;
        this.Uy = builder.Uy;
    }

    private static int a(LocationRequest locationRequest) {
        switch (locationRequest.getPriority()) {
            case 100:
                return 3;
            case 104:
                return 1;
            default:
                return 2;
        }
    }

    private boolean a(SensorRequest sensorRequest) {
        return com.google.android.gms.common.internal.m.equal(this.Sh, sensorRequest.Sh) && com.google.android.gms.common.internal.m.equal(this.SF, sensorRequest.SF) && this.Ti == sensorRequest.Ti && this.Ut == sensorRequest.Ut && this.Us == sensorRequest.Us && this.Tj == sensorRequest.Tj && com.google.android.gms.common.internal.m.equal(this.Ux, sensorRequest.Ux) && this.Uy == sensorRequest.Uy;
    }

    public static int da(int i) {
        switch (i) {
            case 1:
            case 3:
                return i;
            case 2:
            default:
                return 2;
        }
    }

    public static SensorRequest fromLocationRequest(DataSource dataSource, LocationRequest locationRequest) {
        return new SensorRequest(dataSource, locationRequest);
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof SensorRequest) && a((SensorRequest) that));
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

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.Sh, this.SF, Long.valueOf(this.Ti), Long.valueOf(this.Ut), Long.valueOf(this.Us), Integer.valueOf(this.Tj), this.Ux, Long.valueOf(this.Uy));
    }

    public int iQ() {
        return this.Tj;
    }

    public long jm() {
        return this.Ut;
    }

    public long jn() {
        return this.Us;
    }

    public long jr() {
        return this.Uy;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("dataSource", this.Sh).a("dataType", this.SF).a("samplingRateMicros", Long.valueOf(this.Ti)).a("deliveryLatencyMicros", Long.valueOf(this.Us)).a("timeOutMicros", Long.valueOf(this.Uy)).toString();
    }
}
