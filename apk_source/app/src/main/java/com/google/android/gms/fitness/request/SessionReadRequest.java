package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class SessionReadRequest implements SafeParcelable {
    public static final Parcelable.Creator<SessionReadRequest> CREATOR = new s();
    private final int BR;
    private final long KL;
    private final long Si;
    private final List<DataType> Su;
    private final List<DataSource> TZ;
    private final String UC;
    private boolean UD;
    private final List<String> UE;
    private final boolean Uk;
    private final String vL;

    public static class Builder {
        private String UC;
        private String vL;
        private long KL = 0;
        private long Si = 0;
        private List<DataType> Su = new ArrayList();
        private List<DataSource> TZ = new ArrayList();
        private boolean UD = false;
        private boolean Uk = false;
        private List<String> UE = new ArrayList();

        public SessionReadRequest build() {
            com.google.android.gms.common.internal.n.b(this.KL > 0, "Invalid start time: %s", Long.valueOf(this.KL));
            com.google.android.gms.common.internal.n.b(this.Si > 0 && this.Si > this.KL, "Invalid end time: %s", Long.valueOf(this.Si));
            return new SessionReadRequest(this);
        }

        public Builder enableServerQueries() {
            this.Uk = true;
            return this;
        }

        public Builder excludePackage(String appPackageName) {
            com.google.android.gms.common.internal.n.b(appPackageName, (Object) "Attempting to use a null package name");
            if (!this.UE.contains(appPackageName)) {
                this.UE.add(appPackageName);
            }
            return this;
        }

        public Builder read(DataSource dataSource) {
            com.google.android.gms.common.internal.n.b(dataSource, "Attempting to add a null data source");
            if (!this.TZ.contains(dataSource)) {
                this.TZ.add(dataSource);
            }
            return this;
        }

        public Builder read(DataType dataType) {
            com.google.android.gms.common.internal.n.b(dataType, "Attempting to use a null data type");
            if (!this.Su.contains(dataType)) {
                this.Su.add(dataType);
            }
            return this;
        }

        public Builder readSessionsFromAllApps() {
            this.UD = true;
            return this;
        }

        public Builder setSessionId(String sessionId) {
            this.vL = sessionId;
            return this;
        }

        public Builder setSessionName(String sessionName) {
            this.UC = sessionName;
            return this;
        }

        public Builder setTimeInterval(long startTimeMillis, long endTimeMillis) {
            this.KL = startTimeMillis;
            this.Si = endTimeMillis;
            return this;
        }

        public Builder setTimeInterval(long startTime, long endTime, TimeUnit timeUnit) {
            return setTimeInterval(timeUnit.toMillis(startTime), timeUnit.toMillis(endTime));
        }
    }

    SessionReadRequest(int versionCode, String sessionName, String sessionId, long startTimeMillis, long endTimeMillis, List<DataType> dataTypes, List<DataSource> dataSources, boolean getSessionsFromAllApps, boolean serverQueriesEnabled, List<String> excludedPackages) {
        this.BR = versionCode;
        this.UC = sessionName;
        this.vL = sessionId;
        this.KL = startTimeMillis;
        this.Si = endTimeMillis;
        this.Su = Collections.unmodifiableList(dataTypes);
        this.TZ = Collections.unmodifiableList(dataSources);
        this.UD = getSessionsFromAllApps;
        this.Uk = serverQueriesEnabled;
        this.UE = excludedPackages;
    }

    private SessionReadRequest(Builder builder) {
        this.BR = 3;
        this.UC = builder.UC;
        this.vL = builder.vL;
        this.KL = builder.KL;
        this.Si = builder.Si;
        this.Su = Collections.unmodifiableList(builder.Su);
        this.TZ = Collections.unmodifiableList(builder.TZ);
        this.UD = builder.UD;
        this.Uk = builder.Uk;
        this.UE = builder.UE;
    }

    private boolean a(SessionReadRequest sessionReadRequest) {
        return com.google.android.gms.common.internal.m.equal(this.UC, sessionReadRequest.UC) && this.vL.equals(sessionReadRequest.vL) && this.KL == sessionReadRequest.KL && this.Si == sessionReadRequest.Si && com.google.android.gms.common.internal.m.equal(this.Su, sessionReadRequest.Su) && com.google.android.gms.common.internal.m.equal(this.TZ, sessionReadRequest.TZ) && this.UD == sessionReadRequest.UD && this.UE.equals(sessionReadRequest.UE) && this.Uk == sessionReadRequest.Uk;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return this == o || ((o instanceof SessionReadRequest) && a((SessionReadRequest) o));
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

    public String getSessionId() {
        return this.vL;
    }

    public long getStartTimeMillis() {
        return this.KL;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.UC, this.vL, Long.valueOf(this.KL), Long.valueOf(this.Si));
    }

    public boolean jg() {
        return this.Uk;
    }

    public String ju() {
        return this.UC;
    }

    public boolean jv() {
        return this.UD;
    }

    public List<String> jw() {
        return this.UE;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("sessionName", this.UC).a("sessionId", this.vL).a("startTimeMillis", Long.valueOf(this.KL)).a("endTimeMillis", Long.valueOf(this.Si)).a("dataTypes", this.Su).a("dataSources", this.TZ).a("sessionsFromAllApps", Boolean.valueOf(this.UD)).a("excludedPackages", this.UE).a("useServer", Boolean.valueOf(this.Uk)).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        s.a(this, dest, flags);
    }
}
