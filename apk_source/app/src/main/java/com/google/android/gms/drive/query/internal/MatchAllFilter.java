package com.google.android.gms.drive.query.internal;

import android.os.Parcel;

/* loaded from: classes.dex */
public class MatchAllFilter extends AbstractFilter {
    public static final j CREATOR = new j();
    final int BR;

    public MatchAllFilter() {
        this(1);
    }

    MatchAllFilter(int versionCode) {
        this.BR = versionCode;
    }

    @Override // com.google.android.gms.drive.query.Filter
    public <F> F a(f<F> fVar) {
        return fVar.is();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        j.a(this, out, flags);
    }
}
