package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class DataDeleteRequest implements SafeParcelable {
    public static final Parcelable.Creator<DataDeleteRequest> CREATOR = new d();
    private final int BR;
    private final long KL;
    private final long Si;
    private final List<DataType> Su;
    private final List<DataSource> TZ;
    private final List<Session> Ua;
    private final boolean Ub;
    private final boolean Uc;

    public static class Builder {
        private long KL;
        private long Si;
        private List<DataSource> TZ = new ArrayList();
        private List<DataType> Su = new ArrayList();
        private List<Session> Ua = new ArrayList();
        private boolean Ub = false;
        private boolean Uc = false;

        private void iZ() {
            if (this.Ua.isEmpty()) {
                return;
            }
            for (Session session : this.Ua) {
                com.google.android.gms.common.internal.n.a(session.getStartTimeMillis() >= this.KL && session.getEndTimeMillis() <= this.Si, "Session %s is outside the time interval [%d, %d]", session, Long.valueOf(this.KL), Long.valueOf(this.Si));
            }
        }

        public Builder addDataSource(DataSource dataSource) {
            com.google.android.gms.common.internal.n.b(!this.Ub, "All data is already marked for deletion");
            com.google.android.gms.common.internal.n.b(dataSource != null, "Must specify a valid data source");
            if (!this.TZ.contains(dataSource)) {
                this.TZ.add(dataSource);
            }
            return this;
        }

        public Builder addDataType(DataType dataType) {
            com.google.android.gms.common.internal.n.b(!this.Ub, "All data is already marked for deletion");
            com.google.android.gms.common.internal.n.b(dataType != null, "Must specify a valid data type");
            if (!this.Su.contains(dataType)) {
                this.Su.add(dataType);
            }
            return this;
        }

        public Builder addSession(Session session) {
            com.google.android.gms.common.internal.n.b(!this.Uc, "All sessions already marked for deletion");
            com.google.android.gms.common.internal.n.b(session != null, "Must specify a valid session");
            com.google.android.gms.common.internal.n.b(session.getEndTimeMillis() > 0, "Must specify a session that has already ended");
            this.Ua.add(session);
            return this;
        }

        public DataDeleteRequest build() {
            com.google.android.gms.common.internal.n.a(this.KL > 0 && this.Si > this.KL, "Must specify a valid time interval");
            com.google.android.gms.common.internal.n.a((this.Ub || !this.TZ.isEmpty() || !this.Su.isEmpty()) || (this.Uc || !this.Ua.isEmpty()), "No data or session marked for deletion");
            iZ();
            return new DataDeleteRequest(this);
        }

        public Builder deleteAllData() {
            com.google.android.gms.common.internal.n.b(this.Su.isEmpty() && this.TZ.isEmpty(), "Specific data source/type already specified for deletion. DataSources: %s DataTypes: %s", this.TZ, this.Su);
            this.Ub = true;
            return this;
        }

        public Builder deleteAllSessions() {
            com.google.android.gms.common.internal.n.b(this.Ua.isEmpty(), "Specific sessions already added for deletion: %s", this.Ua);
            this.Uc = true;
            return this;
        }

        public Builder setTimeInterval(long startTime, long endTime, TimeUnit timeUnit) {
            com.google.android.gms.common.internal.n.b(startTime > 0, "Invalid start time :%d", Long.valueOf(startTime));
            com.google.android.gms.common.internal.n.b(endTime > startTime, "Invalid end time :%d", Long.valueOf(endTime));
            this.KL = timeUnit.toMillis(startTime);
            this.Si = timeUnit.toMillis(endTime);
            return this;
        }
    }

    DataDeleteRequest(int versionCode, long startTimeMillis, long endTimeMillis, List<DataSource> dataSources, List<DataType> dataTypes, List<Session> sessions, boolean deleteAllData, boolean deleteAllSessions) {
        this.BR = versionCode;
        this.KL = startTimeMillis;
        this.Si = endTimeMillis;
        this.TZ = Collections.unmodifiableList(dataSources);
        this.Su = Collections.unmodifiableList(dataTypes);
        this.Ua = sessions;
        this.Ub = deleteAllData;
        this.Uc = deleteAllSessions;
    }

    private DataDeleteRequest(Builder builder) {
        this.BR = 1;
        this.KL = builder.KL;
        this.Si = builder.Si;
        this.TZ = Collections.unmodifiableList(builder.TZ);
        this.Su = Collections.unmodifiableList(builder.Su);
        this.Ua = Collections.unmodifiableList(builder.Ua);
        this.Ub = builder.Ub;
        this.Uc = builder.Uc;
    }

    private boolean a(DataDeleteRequest dataDeleteRequest) {
        return this.KL == dataDeleteRequest.KL && this.Si == dataDeleteRequest.Si && com.google.android.gms.common.internal.m.equal(this.TZ, dataDeleteRequest.TZ) && com.google.android.gms.common.internal.m.equal(this.Su, dataDeleteRequest.Su) && com.google.android.gms.common.internal.m.equal(this.Ua, dataDeleteRequest.Ua) && this.Ub == dataDeleteRequest.Ub && this.Uc == dataDeleteRequest.Uc;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return o == this || ((o instanceof DataDeleteRequest) && a((DataDeleteRequest) o));
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

    public List<Session> getSessions() {
        return this.Ua;
    }

    public long getStartTimeMillis() {
        return this.KL;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(Long.valueOf(this.KL), Long.valueOf(this.Si));
    }

    public boolean iX() {
        return this.Ub;
    }

    public boolean iY() {
        return this.Uc;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("startTimeMillis", Long.valueOf(this.KL)).a("endTimeMillis", Long.valueOf(this.Si)).a("dataSources", this.TZ).a("dateTypes", this.Su).a("sessions", this.Ua).a("deleteAllData", Boolean.valueOf(this.Ub)).a("deleteAllSessions", Boolean.valueOf(this.Uc)).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        d.a(this, dest, flags);
    }
}
