package com.google.android.gms.fitness.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class q implements SafeParcelable {
    public static final Parcelable.Creator<q> CREATOR = new r();
    final int BR;
    private final Session Sk;
    private final DataSet Th;

    q(int i, Session session, DataSet dataSet) {
        this.BR = i;
        this.Sk = session;
        this.Th = dataSet;
    }

    private boolean a(q qVar) {
        return com.google.android.gms.common.internal.m.equal(this.Sk, qVar.Sk) && com.google.android.gms.common.internal.m.equal(this.Th, qVar.Th);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return o == this || ((o instanceof q) && a((q) o));
    }

    public Session getSession() {
        return this.Sk;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.Sk, this.Th);
    }

    public DataSet iP() {
        return this.Th;
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("session", this.Sk).a("dataSet", this.Th).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        r.a(this, dest, flags);
    }
}
