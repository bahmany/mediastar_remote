package com.google.android.gms.drive.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.c;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.internal.o;

/* loaded from: classes.dex */
public class s extends w implements DriveFile {

    private static class a implements DriveFile.DownloadProgressListener {
        private final com.google.android.gms.common.api.c<DriveFile.DownloadProgressListener> OI;

        public a(com.google.android.gms.common.api.c<DriveFile.DownloadProgressListener> cVar) {
            this.OI = cVar;
        }

        @Override // com.google.android.gms.drive.DriveFile.DownloadProgressListener
        public void onProgress(final long bytesDownloaded, final long bytesExpected) {
            this.OI.a(new c.b<DriveFile.DownloadProgressListener>() { // from class: com.google.android.gms.drive.internal.s.a.1
                @Override // com.google.android.gms.common.api.c.b
                /* renamed from: a, reason: merged with bridge method [inline-methods] */
                public void d(DriveFile.DownloadProgressListener downloadProgressListener) {
                    downloadProgressListener.onProgress(bytesDownloaded, bytesExpected);
                }

                @Override // com.google.android.gms.common.api.c.b
                public void gs() {
                }
            });
        }
    }

    private static class b extends c {
        private final BaseImplementation.b<DriveApi.ContentsResult> De;
        private final DriveFile.DownloadProgressListener OM;

        public b(BaseImplementation.b<DriveApi.ContentsResult> bVar, DriveFile.DownloadProgressListener downloadProgressListener) {
            this.De = bVar;
            this.OM = downloadProgressListener;
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void a(OnContentsResponse onContentsResponse) throws RemoteException {
            this.De.b(new o.a(onContentsResponse.ie() ? new Status(-1) : Status.Jo, onContentsResponse.id()));
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void a(OnDownloadProgressResponse onDownloadProgressResponse) throws RemoteException {
            if (this.OM != null) {
                this.OM.onProgress(onDownloadProgressResponse.m4if(), onDownloadProgressResponse.ig());
            }
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void o(Status status) throws RemoteException {
            this.De.b(new o.a(status, null));
        }
    }

    public s(DriveId driveId) {
        super(driveId);
    }

    private static DriveFile.DownloadProgressListener a(GoogleApiClient googleApiClient, DriveFile.DownloadProgressListener downloadProgressListener) {
        if (downloadProgressListener == null) {
            return null;
        }
        return new a(googleApiClient.c(downloadProgressListener));
    }

    @Override // com.google.android.gms.drive.DriveFile
    public PendingResult<Status> commitAndCloseContents(GoogleApiClient client, Contents contents) {
        return new r(contents).commit(client, null);
    }

    @Override // com.google.android.gms.drive.DriveFile
    public PendingResult<Status> commitAndCloseContents(GoogleApiClient client, Contents contents, MetadataChangeSet changeSet) {
        return new r(contents).commit(client, changeSet);
    }

    @Override // com.google.android.gms.drive.DriveFile
    public PendingResult<Status> discardContents(GoogleApiClient apiClient, Contents contents) {
        return Drive.DriveApi.discardContents(apiClient, contents);
    }

    @Override // com.google.android.gms.drive.DriveFile
    public PendingResult<DriveApi.DriveContentsResult> open(GoogleApiClient apiClient, final int mode, DriveFile.DownloadProgressListener listener) {
        if (mode != 268435456 && mode != 536870912 && mode != 805306368) {
            throw new IllegalArgumentException("Invalid mode provided.");
        }
        final DriveFile.DownloadProgressListener downloadProgressListenerA = a(apiClient, listener);
        return apiClient.a((GoogleApiClient) new o.d() { // from class: com.google.android.gms.drive.internal.s.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new OpenContentsRequest(s.this.getDriveId(), mode, 0), new av(this, downloadProgressListenerA));
            }
        });
    }

    @Override // com.google.android.gms.drive.DriveFile
    public PendingResult<DriveApi.ContentsResult> openContents(GoogleApiClient apiClient, final int mode, DriveFile.DownloadProgressListener listener) {
        if (mode != 268435456 && mode != 536870912 && mode != 805306368) {
            throw new IllegalArgumentException("Invalid mode provided.");
        }
        final DriveFile.DownloadProgressListener downloadProgressListenerA = a(apiClient, listener);
        return apiClient.a((GoogleApiClient) new o.b() { // from class: com.google.android.gms.drive.internal.s.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new OpenContentsRequest(s.this.getDriveId(), mode, 0), new b(this, downloadProgressListenerA));
            }
        });
    }
}
