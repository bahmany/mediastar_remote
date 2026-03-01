package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class ab implements SafeParcelable {
    public static final Parcelable.Creator<ab> CREATOR = new ac();
    public final ak avr;
    public final int statusCode;
    public final int versionCode;

    ab(int i, int i2, ak akVar) {
        this.versionCode = i;
        this.statusCode = i2;
        this.avr = akVar;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ac.a(this, dest, flags);
    }
}
