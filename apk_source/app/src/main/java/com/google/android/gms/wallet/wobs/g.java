package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class g implements SafeParcelable {
    public static final Parcelable.Creator<g> CREATOR = new h();
    private final int BR;
    int aus;
    String aut;
    double auu;
    String auv;
    long auw;
    int aux;

    g() {
        this.BR = 1;
        this.aux = -1;
        this.aus = -1;
        this.auu = -1.0d;
    }

    g(int i, int i2, String str, double d, String str2, long j, int i3) {
        this.BR = i;
        this.aus = i2;
        this.aut = str;
        this.auu = d;
        this.auv = str2;
        this.auw = j;
        this.aux = i3;
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
        h.a(this, dest, flags);
    }
}
