package com.google.android.gms.drive.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.ExecutionOptions;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;

/* loaded from: classes.dex */
public class CreateFileRequest implements SafeParcelable {
    public static final Parcelable.Creator<CreateFileRequest> CREATOR = new j();
    final int BR;
    final Contents NX;
    final String Nf;
    final MetadataBundle Od;
    final Integer Oe;
    final DriveId Of;
    final boolean Og;
    final int Oh;
    final int Oi;

    CreateFileRequest(int versionCode, DriveId parentDriveId, MetadataBundle metadata, Contents contentsReference, Integer fileType, boolean sendEventOnCompletion, String trackingTag, int createStrategy, int openContentsRequestId) {
        if (contentsReference != null && openContentsRequestId != 0) {
            com.google.android.gms.common.internal.n.b(contentsReference.getRequestId() == openContentsRequestId, "inconsistent contents reference");
        }
        if ((fileType == null || fileType.intValue() == 0) && contentsReference == null && openContentsRequestId == 0) {
            throw new IllegalArgumentException("Need a valid contents");
        }
        this.BR = versionCode;
        this.Of = (DriveId) com.google.android.gms.common.internal.n.i(parentDriveId);
        this.Od = (MetadataBundle) com.google.android.gms.common.internal.n.i(metadata);
        this.NX = contentsReference;
        this.Oe = fileType;
        this.Nf = trackingTag;
        this.Oh = createStrategy;
        this.Og = sendEventOnCompletion;
        this.Oi = openContentsRequestId;
    }

    public CreateFileRequest(DriveId parentDriveId, MetadataBundle metadata, int openContentsRequestId, int fileType, ExecutionOptions executionOptions) {
        this(2, parentDriveId, metadata, null, Integer.valueOf(fileType), executionOptions.hP(), executionOptions.hO(), executionOptions.hQ(), openContentsRequestId);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        j.a(this, dest, flags);
    }
}
