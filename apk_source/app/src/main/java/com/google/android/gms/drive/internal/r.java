package com.google.android.gms.drive.internal;

import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.ExecutionOptions;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.internal.o;
import com.google.android.gms.drive.internal.p;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class r implements DriveContents {
    private final Contents Op;

    public r(Contents contents) {
        this.Op = (Contents) com.google.android.gms.common.internal.n.i(contents);
    }

    private PendingResult<Status> a(GoogleApiClient googleApiClient, final MetadataChangeSet metadataChangeSet, final ExecutionOptions executionOptions) {
        if (this.Op.getMode() == 268435456) {
            throw new IllegalStateException("Cannot commit contents opened with MODE_READ_ONLY");
        }
        if (ExecutionOptions.aV(executionOptions.hQ()) && !this.Op.hL()) {
            throw new IllegalStateException("DriveContents must be valid for conflict detection.");
        }
        ExecutionOptions.a(googleApiClient, executionOptions);
        if (this.Op.hK()) {
            throw new IllegalStateException("DriveContents already closed.");
        }
        if (getDriveId() == null) {
            throw new IllegalStateException("Only DriveContents obtained through DriveFile.open can be committed.");
        }
        if (metadataChangeSet == null) {
            metadataChangeSet = MetadataChangeSet.Nl;
        }
        this.Op.hJ();
        return googleApiClient.b(new p.a() { // from class: com.google.android.gms.drive.internal.r.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                metadataChangeSet.hS().setContext(qVar.getContext());
                qVar.hY().a(new CloseContentsAndUpdateMetadataRequest(r.this.Op.getDriveId(), metadataChangeSet.hS(), r.this.Op, executionOptions), new bb(this));
            }
        });
    }

    @Override // com.google.android.gms.drive.DriveContents
    public PendingResult<Status> commit(GoogleApiClient apiClient, MetadataChangeSet changeSet) {
        return a(apiClient, changeSet, new ExecutionOptions.Builder().build());
    }

    @Override // com.google.android.gms.drive.DriveContents
    public PendingResult<Status> commit(GoogleApiClient apiClient, MetadataChangeSet changeSet, ExecutionOptions executionOptions) {
        return a(apiClient, changeSet, executionOptions);
    }

    @Override // com.google.android.gms.drive.DriveContents
    public void discard(GoogleApiClient apiClient) {
        if (this.Op.hK()) {
            throw new IllegalStateException("DriveContents already closed.");
        }
        this.Op.hJ();
        ((AnonymousClass3) apiClient.b(new p.a() { // from class: com.google.android.gms.drive.internal.r.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new CloseContentsRequest(r.this.Op, false), new bb(this));
            }
        })).setResultCallback(new ResultCallback<Status>() { // from class: com.google.android.gms.drive.internal.r.2
            @Override // com.google.android.gms.common.api.ResultCallback
            /* renamed from: k, reason: merged with bridge method [inline-methods] */
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    v.n("DriveContentsImpl", "Contents discarded");
                } else {
                    v.q("DriveContentsImpl", "Error discarding contents");
                }
            }
        });
    }

    @Override // com.google.android.gms.drive.DriveContents
    public Contents getContents() {
        return this.Op;
    }

    @Override // com.google.android.gms.drive.DriveContents
    public DriveId getDriveId() {
        return this.Op.getDriveId();
    }

    @Override // com.google.android.gms.drive.DriveContents
    public InputStream getInputStream() {
        return this.Op.getInputStream();
    }

    @Override // com.google.android.gms.drive.DriveContents
    public int getMode() {
        return this.Op.getMode();
    }

    @Override // com.google.android.gms.drive.DriveContents
    public OutputStream getOutputStream() {
        return this.Op.getOutputStream();
    }

    @Override // com.google.android.gms.drive.DriveContents
    public ParcelFileDescriptor getParcelFileDescriptor() {
        return this.Op.getParcelFileDescriptor();
    }

    @Override // com.google.android.gms.drive.DriveContents
    public PendingResult<DriveApi.DriveContentsResult> reopenForWrite(GoogleApiClient apiClient) {
        if (this.Op.hK()) {
            throw new IllegalStateException("DriveContents already closed.");
        }
        if (this.Op.getMode() != 268435456) {
            throw new IllegalStateException("reopenForWrite can only be used with DriveContents opened with MODE_READ_ONLY.");
        }
        this.Op.hJ();
        return apiClient.a((GoogleApiClient) new o.d() { // from class: com.google.android.gms.drive.internal.r.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new OpenContentsRequest(r.this.getDriveId(), 536870912, r.this.Op.getRequestId()), new av(this, null));
            }
        });
    }
}
