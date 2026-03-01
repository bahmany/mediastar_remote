package com.google.android.gms.fitness.result;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.q;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class SessionReadResult implements Result, SafeParcelable {
    public static final Parcelable.Creator<SessionReadResult> CREATOR = new f();
    private final int BR;
    private final Status CM;
    private final List<q> UO;
    private final List<Session> Ua;

    SessionReadResult(int versionCode, List<Session> sessions, List<q> sessionDataSets, Status status) {
        this.BR = versionCode;
        this.Ua = sessions;
        this.UO = Collections.unmodifiableList(sessionDataSets);
        this.CM = status;
    }

    public SessionReadResult(List<Session> sessions, List<q> sessionDataSets, Status status) {
        this.BR = 3;
        this.Ua = sessions;
        this.UO = Collections.unmodifiableList(sessionDataSets);
        this.CM = status;
    }

    public static SessionReadResult H(Status status) {
        return new SessionReadResult(new ArrayList(), new ArrayList(), status);
    }

    private boolean b(SessionReadResult sessionReadResult) {
        return this.CM.equals(sessionReadResult.CM) && m.equal(this.Ua, sessionReadResult.Ua) && m.equal(this.UO, sessionReadResult.UO);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object that) {
        return this == that || ((that instanceof SessionReadResult) && b((SessionReadResult) that));
    }

    public List<DataSet> getDataSet(Session session) {
        n.b(this.Ua.contains(session), "Attempting to read data for session %s which was not returned", session);
        ArrayList arrayList = new ArrayList();
        for (q qVar : this.UO) {
            if (m.equal(session, qVar.getSession())) {
                arrayList.add(qVar.iP());
            }
        }
        return arrayList;
    }

    public List<DataSet> getDataSet(Session session, DataType dataType) {
        n.b(this.Ua.contains(session), "Attempting to read data for session %s which was not returned", session);
        ArrayList arrayList = new ArrayList();
        for (q qVar : this.UO) {
            if (m.equal(session, qVar.getSession()) && dataType.equals(qVar.iP().getDataType())) {
                arrayList.add(qVar.iP());
            }
        }
        return arrayList;
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
        return m.hashCode(this.CM, this.Ua, this.UO);
    }

    public List<q> jJ() {
        return this.UO;
    }

    public String toString() {
        return m.h(this).a("status", this.CM).a("sessions", this.Ua).a("sessionDataSets", this.UO).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        f.a(this, dest, flags);
    }
}
