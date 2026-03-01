package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.DrivePreferences;

/* loaded from: classes.dex */
public class OnDrivePreferencesResponse implements SafeParcelable {
    public static final Parcelable.Creator<OnDrivePreferencesResponse> CREATOR = new al();
    final int BR;
    DrivePreferences Pj;

    OnDrivePreferencesResponse(int versionCode, DrivePreferences prefs) {
        this.BR = versionCode;
        this.Pj = prefs;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        al.a(this, dest, flags);
    }
}
