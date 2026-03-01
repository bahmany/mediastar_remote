package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.ExecutionOptions;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;

/* loaded from: classes.dex */
public class CloseContentsAndUpdateMetadataRequest implements SafeParcelable {
    public static final Parcelable.Creator<CloseContentsAndUpdateMetadataRequest> CREATOR = new e();
    final int BR;
    final DriveId NV;
    final MetadataBundle NW;
    final Contents NX;
    final int NY;
    final String Nf;
    final boolean Ng;

    CloseContentsAndUpdateMetadataRequest(int versionCode, DriveId id, MetadataBundle metadataChangeSet, Contents contentsReference, boolean notifyOnCompletion, String trackingTag, int commitStrategy) {
        this.BR = versionCode;
        this.NV = id;
        this.NW = metadataChangeSet;
        this.NX = contentsReference;
        this.Ng = notifyOnCompletion;
        this.Nf = trackingTag;
        this.NY = commitStrategy;
    }

    public CloseContentsAndUpdateMetadataRequest(DriveId id, MetadataBundle metadataChangeSet, Contents contentsReference, ExecutionOptions executionOptions) {
        this(1, id, metadataChangeSet, contentsReference, executionOptions.hP(), executionOptions.hO(), executionOptions.hQ());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        e.a(this, dest, flags);
    }
}
