package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.AggregateDataTypes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.DataTypes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class DataReadRequest implements SafeParcelable {
    public static final Parcelable.Creator<DataReadRequest> CREATOR = new f();
    private final int BR;
    private final long KL;
    private final long Si;
    private final List<DataType> Su;
    private final int Sx;
    private final List<DataSource> TZ;
    private final List<DataType> Ud;
    private final List<DataSource> Ue;
    private final long Uf;
    private final DataSource Ug;
    private final int Uh;
    private final boolean Ui;
    private final boolean Uj;
    private final boolean Uk;

    public static class Builder {
        private long KL;
        private long Si;
        private DataSource Ug;
        private List<DataType> Su = new ArrayList();
        private List<DataSource> TZ = new ArrayList();
        private List<DataType> Ud = new ArrayList();
        private List<DataSource> Ue = new ArrayList();
        private int Sx = 0;
        private long Uf = 0;
        private int Uh = 0;
        private boolean Ui = false;
        private boolean Uj = false;
        private boolean Uk = false;

        public Builder aggregate(DataSource dataSource, DataType outputDataType) {
            com.google.android.gms.common.internal.n.b(dataSource, "Attempting to add a null data source");
            com.google.android.gms.common.internal.n.a(!this.TZ.contains(dataSource), "Cannot add the same data source for aggregated and detailed");
            DataType dataType = dataSource.getDataType();
            com.google.android.gms.common.internal.n.b(AggregateDataTypes.INPUT_TYPES.contains(dataType), "Unsupported input data type specified for aggregation: %s", dataType);
            com.google.android.gms.common.internal.n.b(AggregateDataTypes.getForInput(dataType).contains(outputDataType), "Invalid output aggregate data type specified: %s -> %s", dataType, outputDataType);
            if (!this.Ue.contains(dataSource)) {
                this.Ue.add(dataSource);
            }
            return this;
        }

        public Builder aggregate(DataType inputDataType, DataType outputDataType) {
            com.google.android.gms.common.internal.n.b(inputDataType, "Attempting to use a null data type");
            com.google.android.gms.common.internal.n.a(!this.Su.contains(inputDataType), "Cannot add the same data type as aggregated and detailed");
            com.google.android.gms.common.internal.n.b(AggregateDataTypes.INPUT_TYPES.contains(inputDataType), "Unsupported input data type specified for aggregation: %s", inputDataType);
            com.google.android.gms.common.internal.n.b(AggregateDataTypes.getForInput(inputDataType).contains(outputDataType), "Invalid output aggregate data type specified: %s -> %s", inputDataType, outputDataType);
            if (!this.Ud.contains(inputDataType)) {
                this.Ud.add(inputDataType);
            }
            return this;
        }

        public Builder bucketByActivitySegment(int minDuration, TimeUnit timeUnit) {
            com.google.android.gms.common.internal.n.b(this.Sx == 0, "Bucketing strategy already set to %s", Integer.valueOf(this.Sx));
            com.google.android.gms.common.internal.n.b(minDuration > 0, "Must specify a valid minimum duration for an activity segment: %d", Integer.valueOf(minDuration));
            this.Sx = 4;
            this.Uf = timeUnit.toMillis(minDuration);
            return this;
        }

        public Builder bucketByActivitySegment(int minDuration, TimeUnit timeUnit, DataSource activityDataSource) {
            com.google.android.gms.common.internal.n.b(this.Sx == 0, "Bucketing strategy already set to %s", Integer.valueOf(this.Sx));
            com.google.android.gms.common.internal.n.b(minDuration > 0, "Must specify a valid minimum duration for an activity segment: %d", Integer.valueOf(minDuration));
            com.google.android.gms.common.internal.n.b(activityDataSource != null, "Invalid activity data source specified");
            com.google.android.gms.common.internal.n.b(activityDataSource.getDataType().equals(DataTypes.ACTIVITY_SEGMENT), "Invalid activity data source specified: %s", activityDataSource);
            this.Ug = activityDataSource;
            this.Sx = 4;
            this.Uf = timeUnit.toMillis(minDuration);
            return this;
        }

        public Builder bucketByActivityType(int minDuration, TimeUnit timeUnit) {
            com.google.android.gms.common.internal.n.b(this.Sx == 0, "Bucketing strategy already set to %s", Integer.valueOf(this.Sx));
            com.google.android.gms.common.internal.n.b(minDuration > 0, "Must specify a valid minimum duration for an activity segment: %d", Integer.valueOf(minDuration));
            this.Sx = 3;
            this.Uf = timeUnit.toMillis(minDuration);
            return this;
        }

        public Builder bucketByActivityType(int minDuration, TimeUnit timeUnit, DataSource activityDataSource) {
            com.google.android.gms.common.internal.n.b(this.Sx == 0, "Bucketing strategy already set to %s", Integer.valueOf(this.Sx));
            com.google.android.gms.common.internal.n.b(minDuration > 0, "Must specify a valid minimum duration for an activity segment: %d", Integer.valueOf(minDuration));
            com.google.android.gms.common.internal.n.b(activityDataSource != null, "Invalid activity data source specified");
            com.google.android.gms.common.internal.n.b(activityDataSource.getDataType().equals(DataTypes.ACTIVITY_SEGMENT), "Invalid activity data source specified: %s", activityDataSource);
            this.Ug = activityDataSource;
            this.Sx = 3;
            this.Uf = timeUnit.toMillis(minDuration);
            return this;
        }

        public Builder bucketBySession(int minDuration, TimeUnit timeUnit) {
            com.google.android.gms.common.internal.n.b(this.Sx == 0, "Bucketing strategy already set to %s", Integer.valueOf(this.Sx));
            com.google.android.gms.common.internal.n.b(minDuration > 0, "Must specify a valid minimum duration for an activity segment: %d", Integer.valueOf(minDuration));
            this.Sx = 2;
            this.Uf = timeUnit.toMillis(minDuration);
            return this;
        }

        public Builder bucketByTime(int duration, TimeUnit timeUnit) {
            com.google.android.gms.common.internal.n.b(this.Sx == 0, "Bucketing strategy already set to %s", Integer.valueOf(this.Sx));
            com.google.android.gms.common.internal.n.b(duration > 0, "Must specify a valid minimum duration for an activity segment: %d", Integer.valueOf(duration));
            this.Sx = 1;
            this.Uf = timeUnit.toMillis(duration);
            return this;
        }

        public DataReadRequest build() {
            boolean z = true;
            com.google.android.gms.common.internal.n.a((this.TZ.isEmpty() && this.Su.isEmpty() && this.Ue.isEmpty() && this.Ud.isEmpty()) ? false : true, "Must add at least one data source (aggregated or detailed)");
            com.google.android.gms.common.internal.n.a(this.KL > 0, "Invalid start time: %s", Long.valueOf(this.KL));
            com.google.android.gms.common.internal.n.a(this.Si > 0 && this.Si > this.KL, "Invalid end time: %s", Long.valueOf(this.Si));
            boolean z2 = this.Ue.isEmpty() && this.Ud.isEmpty();
            if ((!z2 || this.Sx != 0) && (z2 || this.Sx == 0)) {
                z = false;
            }
            com.google.android.gms.common.internal.n.a(z, "Must specify a valid bucketing strategy while requesting aggregation");
            return new DataReadRequest(this);
        }

        public Builder enableServerQueries() {
            this.Uk = true;
            return this;
        }

        public Builder read(DataSource dataSource) {
            com.google.android.gms.common.internal.n.b(dataSource, "Attempting to add a null data source");
            com.google.android.gms.common.internal.n.b(!this.Ue.contains(dataSource), "Cannot add the same data source as aggregated and detailed");
            if (!this.TZ.contains(dataSource)) {
                this.TZ.add(dataSource);
            }
            return this;
        }

        public Builder read(DataType dataType) {
            com.google.android.gms.common.internal.n.b(dataType, "Attempting to use a null data type");
            com.google.android.gms.common.internal.n.a(!this.Ud.contains(dataType), "Cannot add the same data type as aggregated and detailed");
            if (!this.Su.contains(dataType)) {
                this.Su.add(dataType);
            }
            return this;
        }

        public Builder setTimeRange(long startTimeMillis, long endTimeMillis) {
            this.KL = startTimeMillis;
            this.Si = endTimeMillis;
            return this;
        }
    }

    DataReadRequest(int versionCode, List<DataType> dataTypes, List<DataSource> dataSources, long startTimeMillis, long endTimeMillis, List<DataType> aggregatedDataTypes, List<DataSource> aggregatedDataSources, int bucketType, long bucketDurationMillis, DataSource activityDataSource, int limit, boolean disableTransformations, boolean flushBeforeRead, boolean serverQueriesEnabled) {
        this.BR = versionCode;
        this.Su = Collections.unmodifiableList(dataTypes);
        this.TZ = Collections.unmodifiableList(dataSources);
        this.KL = startTimeMillis;
        this.Si = endTimeMillis;
        this.Ud = Collections.unmodifiableList(aggregatedDataTypes);
        this.Ue = Collections.unmodifiableList(aggregatedDataSources);
        this.Sx = bucketType;
        this.Uf = bucketDurationMillis;
        this.Ug = activityDataSource;
        this.Uh = limit;
        this.Ui = disableTransformations;
        this.Uj = flushBeforeRead;
        this.Uk = serverQueriesEnabled;
    }

    private DataReadRequest(Builder builder) {
        this.BR = 2;
        this.Su = Collections.unmodifiableList(builder.Su);
        this.TZ = Collections.unmodifiableList(builder.TZ);
        this.KL = builder.KL;
        this.Si = builder.Si;
        this.Ud = Collections.unmodifiableList(builder.Ud);
        this.Ue = Collections.unmodifiableList(builder.Ue);
        this.Sx = builder.Sx;
        this.Uf = builder.Uf;
        this.Ug = builder.Ug;
        this.Uh = builder.Uh;
        this.Ui = builder.Ui;
        this.Uj = builder.Uj;
        this.Uk = builder.Uk;
    }

    /* synthetic */ DataReadRequest(Builder x0, AnonymousClass1 x1) {
        this(x0);
    }

    private boolean a(DataReadRequest dataReadRequest) {
        return this.Su.equals(dataReadRequest.Su) && this.TZ.equals(dataReadRequest.TZ) && this.KL == dataReadRequest.KL && this.Si == dataReadRequest.Si && this.Sx == dataReadRequest.Sx && this.Ue.equals(dataReadRequest.Ue) && this.Ud.equals(dataReadRequest.Ud) && com.google.android.gms.common.internal.m.equal(this.Ug, dataReadRequest.Ug) && this.Uf == dataReadRequest.Uf && this.Uk == dataReadRequest.Uk;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof DataReadRequest) && a((DataReadRequest) that));
    }

    public int getBucketType() {
        return this.Sx;
    }

    public List<DataSource> getDataSources() {
        return this.TZ;
    }

    public List<DataType> getDataTypes() {
        return this.Su;
    }

    public long getEndTimeMillis() {
        return this.Si;
    }

    public long getStartTimeMillis() {
        return this.KL;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(Integer.valueOf(this.Sx), Long.valueOf(this.KL), Long.valueOf(this.Si));
    }

    public List<DataType> ja() {
        return this.Ud;
    }

    public List<DataSource> jb() {
        return this.Ue;
    }

    public long jc() {
        return this.Uf;
    }

    public DataSource jd() {
        return this.Ug;
    }

    public int je() {
        return this.Uh;
    }

    public boolean jf() {
        return this.Ui;
    }

    public boolean jg() {
        return this.Uk;
    }

    public boolean jh() {
        return this.Uj;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ReadDataRequest{");
        if (!this.Su.isEmpty()) {
            Iterator<DataType> it = this.Su.iterator();
            while (it.hasNext()) {
                sb.append(it.next().iL()).append(" ");
            }
        }
        if (!this.TZ.isEmpty()) {
            Iterator<DataSource> it2 = this.TZ.iterator();
            while (it2.hasNext()) {
                sb.append(it2.next().toDebugString()).append(" ");
            }
        }
        if (this.Sx != 0) {
            sb.append("bucket by ").append(Bucket.cz(this.Sx));
            if (this.Uf > 0) {
                sb.append(" >").append(this.Uf).append("ms");
            }
            sb.append(": ");
        }
        if (!this.Ud.isEmpty()) {
            Iterator<DataType> it3 = this.Ud.iterator();
            while (it3.hasNext()) {
                sb.append(it3.next().iL()).append(" ");
            }
        }
        if (!this.Ue.isEmpty()) {
            Iterator<DataSource> it4 = this.Ue.iterator();
            while (it4.hasNext()) {
                sb.append(it4.next().toDebugString()).append(" ");
            }
        }
        sb.append(String.format("(%tF %tT - %tF %tT)", Long.valueOf(this.KL), Long.valueOf(this.KL), Long.valueOf(this.Si), Long.valueOf(this.Si)));
        if (this.Ug != null) {
            sb.append("activities: ").append(this.Ug.toDebugString());
        }
        if (this.Uk) {
            sb.append(" +server");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        f.a(this, dest, flags);
    }
}
