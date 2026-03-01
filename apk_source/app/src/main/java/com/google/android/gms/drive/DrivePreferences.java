package com.google.android.gms.drive;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public class DrivePreferences implements SafeParcelable {
    public static final Parcelable.Creator<DrivePreferences> CREATOR = new d();
    final int BR;
    final boolean Ne;

    DrivePreferences(int versionCode, boolean syncOverWifiOnly) {
        this.BR = versionCode;
        this.Ne = syncOverWifiOnly;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        d.a(this, parcel, flags);
    }
}
