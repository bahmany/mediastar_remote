package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

@Deprecated
/* loaded from: classes.dex */
public class r implements SafeParcelable {
    public static final Parcelable.Creator<r> CREATOR = new s();
    public final com.google.android.gms.wearable.c avm;
    public final int statusCode;
    public final int versionCode;

    r(int i, int i2, com.google.android.gms.wearable.c cVar) {
        this.versionCode = i;
        this.statusCode = i2;
        this.avm = cVar;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        s.a(this, dest, flags);
    }
}
