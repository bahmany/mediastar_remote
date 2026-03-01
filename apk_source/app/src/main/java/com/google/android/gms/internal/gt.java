package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

@ez
/* loaded from: classes.dex */
public final class gt implements SafeParcelable {
    public static final gu CREATOR = new gu();
    public final int versionCode;
    public String wD;
    public int wE;
    public int wF;
    public boolean wG;

    public gt(int i, int i2, boolean z) {
        this(1, "afma-sdk-a-v" + i + "." + i2 + "." + (z ? "0" : "1"), i, i2, z);
    }

    gt(int i, String str, int i2, int i3, boolean z) {
        this.versionCode = i;
        this.wD = str;
        this.wE = i2;
        this.wF = i3;
        this.wG = z;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        gu.a(this, out, flags);
    }
}
