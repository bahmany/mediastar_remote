package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.DriveId;

/* loaded from: classes.dex */
public class LoadRealtimeRequest implements SafeParcelable {
    public static final Parcelable.Creator<LoadRealtimeRequest> CREATOR = new ag();
    final int BR;
    final DriveId MO;
    final boolean Pc;

    LoadRealtimeRequest(int versionCode, DriveId driveId, boolean useTestMode) {
        this.BR = versionCode;
        this.MO = driveId;
        this.Pc = useTestMode;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ag.a(this, dest, flags);
    }
}
