package com.google.android.gms.drive.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.internal.o;

/* loaded from: classes.dex */
class av extends c {
    private final BaseImplementation.b<DriveApi.DriveContentsResult> De;
    private final DriveFile.DownloadProgressListener OM;

    av(BaseImplementation.b<DriveApi.DriveContentsResult> bVar, DriveFile.DownloadProgressListener downloadProgressListener) {
        this.De = bVar;
        this.OM = downloadProgressListener;
    }

    @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
    public void a(OnContentsResponse onContentsResponse) throws RemoteException {
        this.De.b(new o.c(onContentsResponse.ie() ? new Status(-1) : Status.Jo, new r(onContentsResponse.id())));
    }

    @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
    public void a(OnDownloadProgressResponse onDownloadProgressResponse) throws RemoteException {
        if (this.OM != null) {
            this.OM.onProgress(onDownloadProgressResponse.m4if(), onDownloadProgressResponse.ig());
        }
    }

    @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
    public void o(Status status) throws RemoteException {
        this.De.b(new o.c(status, null));
    }
}
