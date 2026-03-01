package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class ProxyCard implements SafeParcelable {
    public static final Parcelable.Creator<ProxyCard> CREATOR = new o();
    private final int BR;
    String atu;
    String atv;
    int atw;
    int atx;

    ProxyCard(int versionCode, String pan, String cvn, int expirationMonth, int expirationYear) {
        this.BR = versionCode;
        this.atu = pan;
        this.atv = cvn;
        this.atw = expirationMonth;
        this.atx = expirationYear;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getCvn() {
        return this.atv;
    }

    public int getExpirationMonth() {
        return this.atw;
    }

    public int getExpirationYear() {
        return this.atx;
    }

    public String getPan() {
        return this.atu;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        o.a(this, out, flags);
    }
}
