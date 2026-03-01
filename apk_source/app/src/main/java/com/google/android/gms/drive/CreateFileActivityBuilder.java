package com.google.android.gms.drive;

import android.content.IntentSender;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.drive.internal.r;
import com.google.android.gms.internal.jy;
import java.io.IOException;

/* loaded from: classes.dex */
public class CreateFileActivityBuilder {
    public static final String EXTRA_RESPONSE_DRIVE_ID = "response_drive_id";
    private final com.google.android.gms.drive.internal.h MS = new com.google.android.gms.drive.internal.h(0);
    private DriveContents MT;

    public IntentSender build(GoogleApiClient apiClient) throws IOException {
        n.b(this.MT, "Must provide initial contents to CreateFileActivityBuilder.");
        n.b(apiClient.a(Drive.SCOPE_FILE) || apiClient.a(Drive.MU), "The apiClient must have suitable scope to create files");
        jy.a(this.MT.getParcelFileDescriptor());
        this.MT.getContents().hJ();
        return this.MS.build(apiClient);
    }

    public CreateFileActivityBuilder setActivityStartFolder(DriveId folder) {
        this.MS.a(folder);
        return this;
    }

    public CreateFileActivityBuilder setActivityTitle(String title) {
        this.MS.bi(title);
        return this;
    }

    @Deprecated
    public CreateFileActivityBuilder setInitialContents(Contents contents) {
        return setInitialDriveContents(new r(contents));
    }

    public CreateFileActivityBuilder setInitialDriveContents(DriveContents driveContents) {
        if (driveContents == null) {
            throw new IllegalArgumentException("DriveContents must be provided.");
        }
        if (!(driveContents instanceof r)) {
            throw new IllegalArgumentException("Only DriveContents obtained from the Drive API are accepted.");
        }
        if (driveContents.getDriveId() != null) {
            throw new IllegalArgumentException("Only DriveContents obtained through DriveApi.newDriveContents are accepted for file creation.");
        }
        if (driveContents.getContents().hK()) {
            throw new IllegalArgumentException("DriveContents are already closed.");
        }
        this.MS.bk(driveContents.getContents().getRequestId());
        this.MT = driveContents;
        return this;
    }

    public CreateFileActivityBuilder setInitialMetadata(MetadataChangeSet metadataChangeSet) {
        this.MS.a(metadataChangeSet);
        return this;
    }
}
