package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.data.Session;

/* loaded from: classes.dex */
public class v implements SafeParcelable {
    public static final Parcelable.Creator<v> CREATOR = new w();
    private final int BR;
    private final Session Sk;

    public static class a {
        private Session Sk;

        public a b(Session session) {
            com.google.android.gms.common.internal.n.b(session.getEndTimeMillis() == 0, "Cannot start a session which has already ended");
            this.Sk = session;
            return this;
        }

        public v jx() {
            return new v(this);
        }
    }

    v(int i, Session session) {
        this.BR = i;
        this.Sk = session;
    }

    private v(a aVar) {
        this.BR = 1;
        this.Sk = aVar.Sk;
    }

    private boolean a(v vVar) {
        return com.google.android.gms.common.internal.m.equal(this.Sk, vVar.Sk);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return o == this || ((o instanceof v) && a((v) o));
    }

    public Session getSession() {
        return this.Sk;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.Sk);
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("session", this.Sk).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        w.a(this, dest, flags);
    }
}
