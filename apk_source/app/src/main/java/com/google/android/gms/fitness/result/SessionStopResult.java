package com.google.android.gms.fitness.result;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.Session;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class SessionStopResult implements Result, SafeParcelable {
    public static final Parcelable.Creator<SessionStopResult> CREATOR = new g();
    private final int BR;
    private final Status CM;
    private final List<Session> Ua;

    SessionStopResult(int versionCode, Status status, List<Session> sessions) {
        this.BR = versionCode;
        this.CM = status;
        this.Ua = Collections.unmodifiableList(sessions);
    }

    public SessionStopResult(Status status, List<Session> sessions) {
        this.BR = 3;
        this.CM = status;
        this.Ua = Collections.unmodifiableList(sessions);
    }

    public static SessionStopResult I(Status status) {
        return new SessionStopResult(status, Collections.emptyList());
    }

    private boolean b(SessionStopResult sessionStopResult) {
        return this.CM.equals(sessionStopResult.CM) && m.equal(this.Ua, sessionStopResult.Ua);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return this == o || ((o instanceof SessionStopResult) && b((SessionStopResult) o));
    }

    public List<Session> getSessions() {
        return this.Ua;
    }

    @Override // com.google.android.gms.common.api.Result
    public Status getStatus() {
        return this.CM;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return m.hashCode(this.CM, this.Ua);
    }

    public String toString() {
        return m.h(this).a("status", this.CM).a("sessions", this.Ua).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        g.a(this, dest, flags);
    }
}
