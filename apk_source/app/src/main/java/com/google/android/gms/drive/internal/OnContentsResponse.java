package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.Contents;

/* loaded from: classes.dex */
public class OnContentsResponse implements SafeParcelable {
    public static final Parcelable.Creator<OnContentsResponse> CREATOR = new ai();
    final int BR;
    final Contents Op;
    final boolean Pg;

    OnContentsResponse(int versionCode, Contents contents, boolean outOfDate) {
        this.BR = versionCode;
        this.Op = contents;
        this.Pg = outOfDate;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Contents id() {
        return this.Op;
    }

    public boolean ie() {
        return this.Pg;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ai.a(this, dest, flags);
    }
}
