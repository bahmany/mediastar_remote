package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class InstrumentInfo implements SafeParcelable {
    public static final Parcelable.Creator<InstrumentInfo> CREATOR = new h();
    private final int BR;
    private String asC;
    private String asD;

    InstrumentInfo(int versionCode, String instrumentType, String instrumentDetails) {
        this.BR = versionCode;
        this.asC = instrumentType;
        this.asD = instrumentDetails;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getInstrumentDetails() {
        return this.asD;
    }

    public String getInstrumentType() {
        return this.asC;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        h.a(this, out, flags);
    }
}
