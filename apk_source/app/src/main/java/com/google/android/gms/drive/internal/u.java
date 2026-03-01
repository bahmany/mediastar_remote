package com.google.android.gms.drive.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.ExecutionOptions;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

/* loaded from: classes.dex */
public class u extends w implements DriveFolder {

    private static class a extends com.google.android.gms.drive.internal.c {
        private final BaseImplementation.b<DriveFolder.DriveFileResult> De;

        public a(BaseImplementation.b<DriveFolder.DriveFileResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void a(OnDriveIdResponse onDriveIdResponse) throws RemoteException {
            this.De.b(new c(Status.Jo, new s(onDriveIdResponse.getDriveId())));
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void o(Status status) throws RemoteException {
            this.De.b(new c(status, null));
        }
    }

    private static class b extends com.google.android.gms.drive.internal.c {
        private final BaseImplementation.b<DriveFolder.DriveFolderResult> De;

        public b(BaseImplementation.b<DriveFolder.DriveFolderResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void a(OnDriveIdResponse onDriveIdResponse) throws RemoteException {
            this.De.b(new e(Status.Jo, new u(onDriveIdResponse.getDriveId())));
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void o(Status status) throws RemoteException {
            this.De.b(new e(status, null));
        }
    }

    private static class c implements DriveFolder.DriveFileResult {
        private final Status CM;
        private final DriveFile OQ;

        public c(Status status, DriveFile driveFile) {
            this.CM = status;
            this.OQ = driveFile;
        }

        @Override // com.google.android.gms.drive.DriveFolder.DriveFileResult
        public DriveFile getDriveFile() {
            return this.OQ;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    static abstract class d extends p<DriveFolder.DriveFileResult> {
        d() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: t, reason: merged with bridge method [inline-methods] */
        public DriveFolder.DriveFileResult c(Status status) {
            return new c(status, null);
        }
    }

    private static class e implements DriveFolder.DriveFolderResult {
        private final Status CM;
        private final DriveFolder OR;

        public e(Status status, DriveFolder driveFolder) {
            this.CM = status;
            this.OR = driveFolder;
        }

        @Override // com.google.android.gms.drive.DriveFolder.DriveFolderResult
        public DriveFolder getDriveFolder() {
            return this.OR;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    static abstract class f extends p<DriveFolder.DriveFolderResult> {
        f() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: u, reason: merged with bridge method [inline-methods] */
        public DriveFolder.DriveFolderResult c(Status status) {
            return new e(status, null);
        }
    }

    public u(DriveId driveId) {
        super(driveId);
    }

    private PendingResult<DriveFolder.DriveFileResult> a(GoogleApiClient googleApiClient, final MetadataChangeSet metadataChangeSet, final Contents contents, final int i, final ExecutionOptions executionOptions) {
        ExecutionOptions.a(googleApiClient, executionOptions);
        if (contents != null) {
            contents.hJ();
        }
        return googleApiClient.b(new d() { // from class: com.google.android.gms.drive.internal.u.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                metadataChangeSet.hS().setContext(qVar.getContext());
                qVar.hY().a(new CreateFileRequest(u.this.getDriveId(), metadataChangeSet.hS(), contents == null ? 0 : contents.getRequestId(), i, executionOptions), new a(this));
            }
        });
    }

    private PendingResult<DriveFolder.DriveFileResult> a(GoogleApiClient googleApiClient, MetadataChangeSet metadataChangeSet, DriveContents driveContents, ExecutionOptions executionOptions) {
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
        if (metadataChangeSet == null) {
            throw new IllegalArgumentException("MetadataChangeSet must be provided.");
        }
        if (DriveFolder.MIME_TYPE.equals(metadataChangeSet.getMimeType())) {
            throw new IllegalArgumentException("May not create folders (mimetype: application/vnd.google-apps.folder) using this method. Use DriveFolder.createFolder() instead.");
        }
        return a(googleApiClient, metadataChangeSet, driveContents.getContents(), 0, executionOptions);
    }

    @Override // com.google.android.gms.drive.DriveFolder
    public PendingResult<DriveFolder.DriveFileResult> createFile(GoogleApiClient apiClient, MetadataChangeSet changeSet, Contents contents) {
        return createFile(apiClient, changeSet, new r(contents));
    }

    @Override // com.google.android.gms.drive.DriveFolder
    public PendingResult<DriveFolder.DriveFileResult> createFile(GoogleApiClient apiClient, MetadataChangeSet changeSet, DriveContents driveContents) {
        return createFile(apiClient, changeSet, driveContents, null);
    }

    @Override // com.google.android.gms.drive.DriveFolder
    public PendingResult<DriveFolder.DriveFileResult> createFile(GoogleApiClient apiClient, MetadataChangeSet changeSet, DriveContents driveContents, ExecutionOptions executionOptions) {
        if (executionOptions == null) {
            executionOptions = new ExecutionOptions.Builder().build();
        }
        if (executionOptions.hQ() != 0) {
            throw new IllegalStateException("May not set a conflict strategy for calls to createFile.");
        }
        return a(apiClient, changeSet, driveContents, executionOptions);
    }

    @Override // com.google.android.gms.drive.DriveFolder
    public PendingResult<DriveFolder.DriveFolderResult> createFolder(GoogleApiClient apiClient, final MetadataChangeSet changeSet) {
        if (changeSet == null) {
            throw new IllegalArgumentException("MetadataChangeSet must be provided.");
        }
        if (changeSet.getMimeType() == null || changeSet.getMimeType().equals(DriveFolder.MIME_TYPE)) {
            return apiClient.b(new f() { // from class: com.google.android.gms.drive.internal.u.2
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // com.google.android.gms.common.api.BaseImplementation.a
                public void a(q qVar) throws RemoteException {
                    changeSet.hS().setContext(qVar.getContext());
                    qVar.hY().a(new CreateFolderRequest(u.this.getDriveId(), changeSet.hS()), new b(this));
                }
            });
        }
        throw new IllegalArgumentException("The mimetype must be of type application/vnd.google-apps.folder");
    }

    @Override // com.google.android.gms.drive.DriveFolder
    public PendingResult<DriveApi.MetadataBufferResult> listChildren(GoogleApiClient apiClient) {
        return queryChildren(apiClient, null);
    }

    @Override // com.google.android.gms.drive.DriveFolder
    public PendingResult<DriveApi.MetadataBufferResult> queryChildren(GoogleApiClient apiClient, Query query) {
        Query.Builder builderAddFilter = new Query.Builder().addFilter(Filters.in(SearchableField.PARENTS, getDriveId()));
        if (query != null) {
            if (query.getFilter() != null) {
                builderAddFilter.addFilter(query.getFilter());
            }
            builderAddFilter.setPageToken(query.getPageToken());
            builderAddFilter.setSortOrder(query.getSortOrder());
        }
        return new o().query(apiClient, builderAddFilter.build());
    }
}
