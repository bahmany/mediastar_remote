package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class ao implements SafeParcelable {
    public static final Parcelable.Creator<ao> CREATOR = new ap();
    public final m avp;
    public final int statusCode;
    public final int versionCode;

    ao(int i, int i2, m mVar) {
        this.versionCode = i;
        this.statusCode = i2;
        this.avp = mVar;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ap.a(this, dest, flags);
    }
}
