package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class am implements SafeParcelable {
    public static final Parcelable.Creator<am> CREATOR = new an();
    public final long avC;
    public final String label;
    public final String packageName;
    public final int versionCode;

    am(int i, String str, String str2, long j) {
        this.versionCode = i;
        this.packageName = str;
        this.label = str2;
        this.avC = j;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        an.a(this, out, flags);
    }
}
