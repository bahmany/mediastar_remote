package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class lr implements SafeParcelable {
    public static final ls CREATOR = new ls();
    private final int BR;
    public final String packageName;
    public final int uid;

    lr(int i, int i2, String str) {
        this.BR = i;
        this.uid = i2;
        this.packageName = str;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (!(o instanceof lr)) {
            return false;
        }
        lr lrVar = (lr) o;
        return lrVar.uid == this.uid && com.google.android.gms.common.internal.m.equal(lrVar.packageName, this.packageName);
    }

    int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return this.uid;
    }

    public String toString() {
        return String.format("%d:%s", Integer.valueOf(this.uid), this.packageName);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        ls.a(this, parcel, flags);
    }
}
