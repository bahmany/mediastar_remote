package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class i implements SafeParcelable {
    public static final Parcelable.Creator<i> CREATOR = new j();
    private final int BR;
    private final String mName;

    i(int i, String str) {
        this.BR = i;
        this.mName = str;
    }

    public i(String str) {
        this.BR = 1;
        this.mName = str;
    }

    private boolean a(i iVar) {
        return com.google.android.gms.common.internal.m.equal(this.mName, iVar.mName);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return o == this || ((o instanceof i) && a((i) o));
    }

    public String getName() {
        return this.mName;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.mName);
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("name", this.mName).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        j.a(this, dest, flags);
    }
}
