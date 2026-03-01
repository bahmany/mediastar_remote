package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class b implements SafeParcelable {
    public static final Parcelable.Creator<b> CREATOR = new c();
    private final int BR;
    String label;
    String value;

    b() {
        this.BR = 1;
    }

    b(int i, String str, String str2) {
        this.BR = i;
        this.label = str;
        this.value = str2;
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
        c.a(this, dest, flags);
    }
}
