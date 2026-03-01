package com.google.android.gms.fitness.request;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.request.a;
import com.google.android.gms.fitness.request.k;

/* loaded from: classes.dex */
public class ac implements SafeParcelable {
    public static final Parcelable.Creator<ac> CREATOR = new ad();
    private final int BR;
    private final k UF;

    ac(int i, IBinder iBinder) {
        this.BR = i;
        this.UF = k.a.ay(iBinder);
    }

    public ac(BleScanCallback bleScanCallback) {
        this.BR = 1;
        this.UF = a.C0033a.iV().b(bleScanCallback);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    int getVersionCode() {
        return this.BR;
    }

    public IBinder jz() {
        return this.UF.asBinder();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        ad.a(this, parcel, flags);
    }
}
