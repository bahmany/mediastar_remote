package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class jb implements SafeParcelable {
    public static final jc CREATOR = new jc();
    final int BR;
    public final String Mi;
    public final int Mj;

    public jb(int i, String str, int i2) {
        this.BR = i;
        this.Mi = str;
        this.Mj = i2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        jc.a(this, out, flags);
    }
}
