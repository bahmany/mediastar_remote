package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.DriveId;

/* loaded from: classes.dex */
public class OpenContentsRequest implements SafeParcelable {
    public static final Parcelable.Creator<OpenContentsRequest> CREATOR = new au();
    final int BR;
    final int MN;
    final DriveId NV;
    final int Pp;

    OpenContentsRequest(int versionCode, DriveId id, int mode, int baseRequestId) {
        this.BR = versionCode;
        this.NV = id;
        this.MN = mode;
        this.Pp = baseRequestId;
    }

    public OpenContentsRequest(DriveId id, int mode, int baseRequestId) {
        this(1, id, mode, baseRequestId);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        au.a(this, dest, flags);
    }
}
