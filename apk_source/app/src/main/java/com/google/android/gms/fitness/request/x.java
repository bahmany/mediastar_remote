package com.google.android.gms.fitness.request;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class x implements SafeParcelable {
    public static final Parcelable.Creator<x> CREATOR = new y();
    private final int BR;
    private final String Tf;
    private final String mName;

    public static class a {
        private String Tf;
        private String mName;

        public a br(String str) {
            this.mName = str;
            return this;
        }

        public a bs(String str) {
            this.Tf = str;
            return this;
        }

        public x jy() {
            return new x(this);
        }
    }

    x(int i, String str, String str2) {
        this.BR = i;
        this.mName = str;
        this.Tf = str2;
    }

    private x(a aVar) {
        this.BR = 1;
        this.mName = aVar.mName;
        this.Tf = aVar.Tf;
    }

    private boolean a(x xVar) {
        return com.google.android.gms.common.internal.m.equal(this.mName, xVar.mName) && com.google.android.gms.common.internal.m.equal(this.Tf, xVar.Tf);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        return o == this || ((o instanceof x) && a((x) o));
    }

    public String getIdentifier() {
        return this.Tf;
    }

    public String getName() {
        return this.mName;
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return com.google.android.gms.common.internal.m.hashCode(this.mName, this.Tf);
    }

    public String toString() {
        return com.google.android.gms.common.internal.m.h(this).a("name", this.mName).a("identifier", this.Tf).toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        y.a(this, dest, flags);
    }
}
