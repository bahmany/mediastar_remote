package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class j implements SafeParcelable {
    public static final Parcelable.Creator<j> CREATOR = new k();
    private final int BR;
    String auy;
    String tG;

    j() {
        this.BR = 1;
    }

    j(int i, String str, String str2) {
        this.BR = i;
        this.auy = str;
        this.tG = str2;
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
        k.a(this, dest, flags);
    }
}
