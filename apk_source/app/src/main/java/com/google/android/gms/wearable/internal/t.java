package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class t implements SafeParcelable {
    public static final Parcelable.Creator<t> CREATOR = new u();
    public final com.google.android.gms.wearable.c[] avn;
    public final int statusCode;
    public final int versionCode;

    t(int i, int i2, com.google.android.gms.wearable.c[] cVarArr) {
        this.versionCode = i;
        this.statusCode = i2;
        this.avn = cVarArr;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        u.a(this, dest, flags);
    }
}
