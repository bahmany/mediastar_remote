package com.google.android.gms.drive.realtime.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class ParcelableIndexReference implements SafeParcelable {
    public static final Parcelable.Creator<ParcelableIndexReference> CREATOR = new q();
    final int BR;
    final String Rh;
    final boolean Ri;
    final int mIndex;

    ParcelableIndexReference(int versionCode, String objectId, int index, boolean canBeDeleted) {
        this.BR = versionCode;
        this.Rh = objectId;
        this.mIndex = index;
        this.Ri = canBeDeleted;
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
