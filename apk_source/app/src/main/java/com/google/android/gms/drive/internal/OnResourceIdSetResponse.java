package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.List;

/* loaded from: classes.dex */
public class OnResourceIdSetResponse implements SafeParcelable {
    public static final Parcelable.Creator<OnResourceIdSetResponse> CREATOR = new ar();
    private final int BR;
    private final List<String> NU;

    OnResourceIdSetResponse(int versionCode, List<String> resourceIds) {
        this.BR = versionCode;
        this.NU = resourceIds;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public List<String> hX() {
        return this.NU;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ar.a(this, dest, flags);
    }
}
