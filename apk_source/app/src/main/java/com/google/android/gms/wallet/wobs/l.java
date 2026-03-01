package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class l implements SafeParcelable {
    public static final Parcelable.Creator<l> CREATOR = new m();
    private final int BR;
    long auA;
    long auz;

    l() {
        this.BR = 1;
    }

    l(int i, long j, long j2) {
        this.BR = i;
        this.auz = j;
        this.auA = j2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        m.a(this, dest, flags);
    }
}
