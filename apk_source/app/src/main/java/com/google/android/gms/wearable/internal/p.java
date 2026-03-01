package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class p implements SafeParcelable {
    public static final Parcelable.Creator<p> CREATOR = new q();
    public final int avl;
    public final int statusCode;
    public final int versionCode;

    p(int i, int i2, int i3) {
        this.versionCode = i;
        this.statusCode = i2;
        this.avl = i3;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        q.a(this, dest, flags);
    }
}
