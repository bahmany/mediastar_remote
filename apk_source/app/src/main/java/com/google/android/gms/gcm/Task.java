package com.google.android.gms.gcm;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.dex */
public abstract class Task implements Parcelable {
    private final String adq = null;
    private final String mTag = null;
    private final boolean adr = false;
    private final boolean ads = false;

    Task() {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getServiceName() {
        return this.adq;
    }

    public String getTag() {
        return this.mTag;
    }

    public boolean isPersisted() {
        return this.ads;
    }

    public boolean isUpdateCurrent() {
        return this.adr;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.adq);
        parcel.writeString(this.mTag);
        parcel.writeInt(this.adr ? 1 : 0);
        parcel.writeInt(this.ads ? 1 : 0);
    }
}
