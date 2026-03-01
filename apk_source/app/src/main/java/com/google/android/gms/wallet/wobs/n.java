package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class n implements SafeParcelable {
    public static final Parcelable.Creator<n> CREATOR = new o();
    private final int BR;
    String auB;
    String description;

    n() {
        this.BR = 1;
    }

    n(int i, String str, String str2) {
        this.BR = i;
        this.auB = str;
        this.description = str2;
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
        o.a(this, dest, flags);
    }
}
