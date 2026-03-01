package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;

/* loaded from: classes.dex */
public class OnMetadataResponse implements SafeParcelable {
    public static final Parcelable.Creator<OnMetadataResponse> CREATOR = new ap();
    final int BR;
    final MetadataBundle Od;

    OnMetadataResponse(int versionCode, MetadataBundle metadata) {
        this.BR = versionCode;
        this.Od = metadata;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public MetadataBundle il() {
        return this.Od;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ap.a(this, dest, flags);
    }
}
