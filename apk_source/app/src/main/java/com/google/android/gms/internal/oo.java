package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class oo implements SafeParcelable {
    public static final Parcelable.Creator<oo> CREATOR = new op();
    private final int BR;
    String[] atD;
    byte[][] atE;

    oo() {
        this(1, new String[0], new byte[0][]);
    }

    oo(int i, String[] strArr, byte[][] bArr) {
        this.BR = i;
        this.atD = strArr;
        this.atE = bArr;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getVersionCode() {
        return this.BR;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        op.a(this, out, flags);
    }
}
