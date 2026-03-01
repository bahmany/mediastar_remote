package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class SessionInsertRequest implements SafeParcelable {
    public static final Parcelable.Creator<SessionInsertRequest> CREATOR = new r();
    private final int BR;
    private final Session Sk;
    private final List<DataSet> Sw;
    private final List<DataPoint> UA;

    public static class Builder {
        private Session Sk;
        private List<DataSet> Sw = new ArrayList();
        private List<DataPoint> UA = new ArrayList();
        private List<DataSource> UB = new ArrayList();

        private void c(DataPoint dataPoint) {
            long nanos = TimeUnit.MILLISECONDS.toNanos(this.Sk.getStartTimeMillis());
            long nanos2 = TimeUnit.MILLISECONDS.toNanos(this.Sk.getEndTimeMillis());
            long timestampNanos = dataPoint.getTimestampNanos();
            if (timestampNanos != 0) {
                com.google.android.gms.common.internal.n.a(timestampNanos >= nanos && timestampNanos <= nanos2, "Data point %s has time stamp outside session interval [%d, %d]", dataPoint, Long.valueOf(nanos), Long.valueOf(nanos2));
            }
            long startTimeNanos = dataPoint.getStartTimeNanos();
            long endTimeNanos = dataPoint.getEndTimeNanos();
            if (startTimeNanos == 0 || endTimeNanos == 0) {
                return;
            }
            com.google.android.gms.common.internal.n.a(startTimeNanos >= nanos && endTimeNanos <= nanos2, "Data point %s has start and end times outside session interval [%d, %d]", dataPoint, Long.valueOf(nanos), Long.valueOf(nanos2));
        }

        private void jt() {
            Iterator<DataSet> it = this.Sw.iterator();
            while (it.hasNext()) {
                Iterator<DataPoint> it2 = it.next().getDataPoints().iterator();
                while (it2.hasNext()) {
                    c(it2.next());
                }
            }
            Iterator<DataPoint> it3 = this.UA.iterator();
            while (it3.hasNext()) {
                c(it3.next());
            }
        }

        public Builder addAggregateDataPoint(DataPoint aggregateDataPoint) {
            com.google.android.gms.common.internal.n.b(aggregateDataPoint != null, "Must specify a valid aggregate data point.");
            long startTimeNanos = aggregateDataPoint.getStartTimeNanos();
            com.google.android.gms.common.internal.n.b(startTimeNanos > 0 && aggregateDataPoint.getEndTimeNanos() > startTimeNanos, "Aggregate data point should have valid start and end times: %s", aggregateDataPoint);
            DataSource dataSource = aggregateDataPoint.getDataSource();
            com.google.android.gms.common.internal.n.a(!this.UB.contains(dataSource), "Data set/Aggregate data point for this data source %s is already added.", dataSource);
            this.UB.add(dataSource);
            this.UA.add(aggregateDataPoint);
            return this;
        }

        public Builder addDataSet(DataSet dataSet) {
            com.google.android.gms.common.internal.n.b(dataSet != null, "Must specify a valid data set.");
            DataSource dataSource = dataSet.getDataSource();
            com.google.android.gms.common.internal.n.a(!this.UB.contains(dataSource), "Data set for this data source %s is already added.", dataSource);
            com.google.android.gms.common.internal.n.b(dataSet.getDataPoints().isEmpty() ? false : true, "No data points specified in the input data set.");
            this.UB.add(dataSource);
            this.Sw.add(dataSet);
            return this;
        }

        public SessionInsertRequest build() {
            com.google.android.gms.common.internal.n.a(this.Sk != null, "Must specify a valid session.");
            com.google.android.gms.common.internal.n.a(this.Sk.getEndTimeMillis() != 0, "Must specify a valid end time, cannot insert a continuing session.");
            jt();
            return new SessionInsertRequest(this);
        }

        public Builder setSession(Session session) {
            this.Sk = session;
            return this;
        }
    }

    SessionInsertRequest(int versionCode, Session session, List<DataSet> dataSets, List<DataPoint> aggregateDataPoints) {
        this.BR = versionCode;
        this.Sk = session;
        this.Sw = Collections.unmodifiableList(dataSets);
        this.UA = Collections.unmodifiableList(aggregateDataPoints);
    }

    private SessionInsertRequest(Builder builder) {
        this.BR = 1;
        this.Sk = builder.Sk;
        this.Sw = Collections.unmodifiableList(builder.Sw);
        this.UA = Collections.unmodifiableList(builder.UA);
    }

    private boolean a(SessionInsertRequest sessionInsertRequest) {
        return com.google.android.gms.common.internal.m.equal(this.Sk, sessionInsertRequest.Sk) && com.google.android.gms.common.internal.m.equal(this.Sw, sessionInsertRequest.Sw) && com.google.android.gms.common.internal.m.equal(this.UA, sessionInsertRequest.UA);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return o == this || ((o instanceof SessionInsertRequest) && a((SessionInsertRequest) o));
    }

    public List<DataSet> getDataSets() {
        return this.Sw;
    }

    public Session getSession() {
        return this.Sk;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.Sk, this.Sw, this.UA);
    }

    public List<DataPoint> js() {
        return this.UA;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("session", this.Sk).a("dataSets", this.Sw).a("aggregateDataPoints", this.UA).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        r.a(this, dest, flags);
    }
}
