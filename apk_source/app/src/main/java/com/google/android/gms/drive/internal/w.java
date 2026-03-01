package com.google.android.gms.drive.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.ChangeListener;
import com.google.android.gms.drive.events.DriveEvent;
import com.google.android.gms.drive.internal.o;
import com.google.android.gms.drive.internal.p;
import java.util.ArrayList;
import java.util.Set;

/* loaded from: classes.dex */
public class w implements DriveResource {
    protected final DriveId MO;

    private static class a extends com.google.android.gms.drive.internal.c {
        private final BaseImplementation.b<DriveApi.MetadataBufferResult> De;

        public a(BaseImplementation.b<DriveApi.MetadataBufferResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void a(OnListParentsResponse onListParentsResponse) throws RemoteException {
            this.De.b(new o.h(Status.Jo, new MetadataBuffer(onListParentsResponse.ik(), null), false));
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void o(Status status) throws RemoteException {
            this.De.b(new o.h(status, null, false));
        }
    }

    private static class b extends com.google.android.gms.drive.internal.c {
        private final BaseImplementation.b<DriveResource.MetadataResult> De;

        public b(BaseImplementation.b<DriveResource.MetadataResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void a(OnMetadataResponse onMetadataResponse) throws RemoteException {
            this.De.b(new c(Status.Jo, new l(onMetadataResponse.il())));
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void o(Status status) throws RemoteException {
            this.De.b(new c(status, null));
        }
    }

    private static class c implements DriveResource.MetadataResult {
        private final Status CM;
        private final Metadata OV;

        public c(Status status, Metadata metadata) {
            this.CM = status;
            this.OV = metadata;
        }

        @Override // com.google.android.gms.drive.DriveResource.MetadataResult
        public Metadata getMetadata() {
            return this.OV;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    private abstract class d extends p<DriveResource.MetadataResult> {
        private d() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: v, reason: merged with bridge method [inline-methods] */
        public DriveResource.MetadataResult c(Status status) {
            return new c(status, null);
        }
    }

    protected w(DriveId driveId) {
        this.MO = driveId;
    }

    @Override // com.google.android.gms.drive.DriveResource
    public PendingResult<Status> addChangeListener(GoogleApiClient apiClient, ChangeListener listener) {
        return ((q) apiClient.a(Drive.CU)).a(apiClient, this.MO, 1, listener);
    }

    @Override // com.google.android.gms.drive.DriveResource
    public PendingResult<Status> addChangeListener(GoogleApiClient apiClient, DriveEvent.Listener<ChangeEvent> listener) {
        return ((q) apiClient.a(Drive.CU)).a(apiClient, this.MO, 1, listener);
    }

    @Override // com.google.android.gms.drive.DriveResource
    public PendingResult<Status> addChangeSubscription(GoogleApiClient apiClient) {
        return ((q) apiClient.a(Drive.CU)).a(apiClient, this.MO, 1);
    }

    @Override // com.google.android.gms.drive.DriveResource
    public DriveId getDriveId() {
        return this.MO;
    }

    @Override // com.google.android.gms.drive.DriveResource
    public PendingResult<DriveResource.MetadataResult> getMetadata(GoogleApiClient apiClient) {
        return apiClient.a((GoogleApiClient) new d() { // from class: com.google.android.gms.drive.internal.w.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new GetMetadataRequest(w.this.MO), new b(this));
            }
        });
    }

    @Override // com.google.android.gms.drive.DriveResource
    public PendingResult<DriveApi.MetadataBufferResult> listParents(GoogleApiClient apiClient) {
        return apiClient.a((GoogleApiClient) new o.i() { // from class: com.google.android.gms.drive.internal.w.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new ListParentsRequest(w.this.MO), new a(this));
            }
        });
    }

    @Override // com.google.android.gms.drive.DriveResource
    public PendingResult<Status> removeChangeListener(GoogleApiClient apiClient, ChangeListener listener) {
        return ((q) apiClient.a(Drive.CU)).b(apiClient, this.MO, 1, listener);
    }

    @Override // com.google.android.gms.drive.DriveResource
    public PendingResult<Status> removeChangeListener(GoogleApiClient apiClient, DriveEvent.Listener<ChangeEvent> listener) {
        return ((q) apiClient.a(Drive.CU)).b(apiClient, this.MO, 1, listener);
    }

    @Override // com.google.android.gms.drive.DriveResource
    public PendingResult<Status> removeChangeSubscription(GoogleApiClient apiClient) {
        return ((q) apiClient.a(Drive.CU)).b(apiClient, this.MO, 1);
    }

    @Override // com.google.android.gms.drive.DriveResource
    public PendingResult<Status> setParents(GoogleApiClient apiClient, Set<DriveId> parentIds) {
        if (parentIds == null) {
            throw new IllegalArgumentException("ParentIds must be provided.");
        }
        if (parentIds.isEmpty()) {
            throw new IllegalArgumentException("ParentIds must contain at least one parent.");
        }
        final ArrayList arrayList = new ArrayList(parentIds);
        return apiClient.b(new p.a() { // from class: com.google.android.gms.drive.internal.w.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new SetResourceParentsRequest(w.this.MO, arrayList), new bb(this));
            }
        });
    }

    @Override // com.google.android.gms.drive.DriveResource
    public PendingResult<DriveResource.MetadataResult> updateMetadata(GoogleApiClient apiClient, final MetadataChangeSet changeSet) {
        if (changeSet == null) {
            throw new IllegalArgumentException("ChangeSet must be provided.");
        }
        return apiClient.b(new d() { // from class: com.google.android.gms.drive.internal.w.4
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                changeSet.hS().setContext(qVar.getContext());
                qVar.hY().a(new UpdateMetadataRequest(w.this.MO, changeSet.hS()), new b(this));
            }
        });
    }
}
