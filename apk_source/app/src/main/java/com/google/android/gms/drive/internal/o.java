package com.google.android.gms.drive.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.CreateFileActivityBuilder;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.internal.p;
import com.google.android.gms.drive.query.Query;

/* loaded from: classes.dex */
public class o implements DriveApi {

    /* renamed from: com.google.android.gms.drive.internal.o$1 */
    class AnonymousClass1 extends i {
        final /* synthetic */ Query Ok;

        AnonymousClass1(Query query) {
            query = query;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(q qVar) throws RemoteException {
            qVar.hY().a(new QueryRequest(query), new l(this));
        }
    }

    /* renamed from: com.google.android.gms.drive.internal.o$2 */
    class AnonymousClass2 extends b {
        AnonymousClass2() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(q qVar) throws RemoteException {
            qVar.hY().a(new CreateContentsRequest(536870912), new j(this));
        }
    }

    /* renamed from: com.google.android.gms.drive.internal.o$3 */
    class AnonymousClass3 extends d {
        final /* synthetic */ int Om;

        AnonymousClass3(int i) {
            i = i;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(q qVar) throws RemoteException {
            qVar.hY().a(new CreateContentsRequest(i), new k(this));
        }
    }

    /* renamed from: com.google.android.gms.drive.internal.o$4 */
    class AnonymousClass4 extends p.a {
        final /* synthetic */ Contents On;

        AnonymousClass4(Contents contents) {
            contents = contents;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(q qVar) throws RemoteException {
            qVar.hY().a(new CloseContentsRequest(contents, false), new bb(this));
        }
    }

    /* renamed from: com.google.android.gms.drive.internal.o$5 */
    class AnonymousClass5 extends g {
        final /* synthetic */ String Oo;

        AnonymousClass5(String str) {
            str = str;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(q qVar) throws RemoteException {
            qVar.hY().a(new GetMetadataRequest(DriveId.bg(str)), new e(this));
        }
    }

    /* renamed from: com.google.android.gms.drive.internal.o$6 */
    class AnonymousClass6 extends p.a {
        AnonymousClass6() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(q qVar) throws RemoteException {
            qVar.hY().a(new bb(this));
        }
    }

    static class a implements DriveApi.ContentsResult {
        private final Status CM;
        private final Contents Op;

        public a(Status status, Contents contents) {
            this.CM = status;
            this.Op = contents;
        }

        @Override // com.google.android.gms.drive.DriveApi.ContentsResult
        public Contents getContents() {
            return this.Op;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    static abstract class b extends p<DriveApi.ContentsResult> {
        b() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: p */
        public DriveApi.ContentsResult c(Status status) {
            return new a(status, null);
        }
    }

    static class c implements DriveApi.DriveContentsResult {
        private final Status CM;
        private final DriveContents MT;

        public c(Status status, DriveContents driveContents) {
            this.CM = status;
            this.MT = driveContents;
        }

        @Override // com.google.android.gms.drive.DriveApi.DriveContentsResult
        public DriveContents getDriveContents() {
            return this.MT;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    static abstract class d extends p<DriveApi.DriveContentsResult> {
        d() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: q */
        public DriveApi.DriveContentsResult c(Status status) {
            return new c(status, null);
        }
    }

    static class e extends com.google.android.gms.drive.internal.c {
        private final BaseImplementation.b<DriveApi.DriveIdResult> De;

        public e(BaseImplementation.b<DriveApi.DriveIdResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void a(OnDriveIdResponse onDriveIdResponse) throws RemoteException {
            this.De.b(new f(Status.Jo, onDriveIdResponse.getDriveId()));
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void a(OnMetadataResponse onMetadataResponse) throws RemoteException {
            this.De.b(new f(Status.Jo, new com.google.android.gms.drive.internal.l(onMetadataResponse.il()).getDriveId()));
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void o(Status status) throws RemoteException {
            this.De.b(new f(status, null));
        }
    }

    private static class f implements DriveApi.DriveIdResult {
        private final Status CM;
        private final DriveId MO;

        public f(Status status, DriveId driveId) {
            this.CM = status;
            this.MO = driveId;
        }

        @Override // com.google.android.gms.drive.DriveApi.DriveIdResult
        public DriveId getDriveId() {
            return this.MO;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    static abstract class g extends p<DriveApi.DriveIdResult> {
        g() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: r */
        public DriveApi.DriveIdResult c(Status status) {
            return new f(status, null);
        }
    }

    static class h implements DriveApi.MetadataBufferResult {
        private final Status CM;
        private final MetadataBuffer Oq;
        private final boolean Or;

        public h(Status status, MetadataBuffer metadataBuffer, boolean z) {
            this.CM = status;
            this.Oq = metadataBuffer;
            this.Or = z;
        }

        @Override // com.google.android.gms.drive.DriveApi.MetadataBufferResult
        public MetadataBuffer getMetadataBuffer() {
            return this.Oq;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    static abstract class i extends p<DriveApi.MetadataBufferResult> {
        i() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: s */
        public DriveApi.MetadataBufferResult c(Status status) {
            return new h(status, null, false);
        }
    }

    private static class j extends com.google.android.gms.drive.internal.c {
        private final BaseImplementation.b<DriveApi.ContentsResult> De;

        public j(BaseImplementation.b<DriveApi.ContentsResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void a(OnContentsResponse onContentsResponse) throws RemoteException {
            this.De.b(new a(Status.Jo, onContentsResponse.id()));
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void o(Status status) throws RemoteException {
            this.De.b(new a(status, null));
        }
    }

    private static class k extends com.google.android.gms.drive.internal.c {
        private final BaseImplementation.b<DriveApi.DriveContentsResult> De;

        public k(BaseImplementation.b<DriveApi.DriveContentsResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void a(OnContentsResponse onContentsResponse) throws RemoteException {
            this.De.b(new c(Status.Jo, new r(onContentsResponse.id())));
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void o(Status status) throws RemoteException {
            this.De.b(new c(status, null));
        }
    }

    private static class l extends com.google.android.gms.drive.internal.c {
        private final BaseImplementation.b<DriveApi.MetadataBufferResult> De;

        public l(BaseImplementation.b<DriveApi.MetadataBufferResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void a(OnListEntriesResponse onListEntriesResponse) throws RemoteException {
            this.De.b(new h(Status.Jo, new MetadataBuffer(onListEntriesResponse.ii(), null), onListEntriesResponse.ij()));
        }

        @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
        public void o(Status status) throws RemoteException {
            this.De.b(new h(status, null, false));
        }
    }

    static class m extends p.a {
        m(GoogleApiClient googleApiClient, Status status) {
            a(new BaseImplementation.CallbackHandler(((q) googleApiClient.a(Drive.CU)).getLooper()));
            b((m) status);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(q qVar) {
        }
    }

    public PendingResult<DriveApi.DriveContentsResult> a(GoogleApiClient googleApiClient, int i2) {
        return googleApiClient.a((GoogleApiClient) new d() { // from class: com.google.android.gms.drive.internal.o.3
            final /* synthetic */ int Om;

            AnonymousClass3(int i22) {
                i = i22;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new CreateContentsRequest(i), new k(this));
            }
        });
    }

    @Override // com.google.android.gms.drive.DriveApi
    public PendingResult<Status> discardContents(GoogleApiClient apiClient, Contents contents) {
        if (contents.hK()) {
            throw new IllegalStateException("DriveContents already closed.");
        }
        contents.hJ();
        return apiClient.b(new p.a() { // from class: com.google.android.gms.drive.internal.o.4
            final /* synthetic */ Contents On;

            AnonymousClass4(Contents contents2) {
                contents = contents2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new CloseContentsRequest(contents, false), new bb(this));
            }
        });
    }

    @Override // com.google.android.gms.drive.DriveApi
    public PendingResult<DriveApi.DriveIdResult> fetchDriveId(GoogleApiClient apiClient, String resourceId) {
        return apiClient.a((GoogleApiClient) new g() { // from class: com.google.android.gms.drive.internal.o.5
            final /* synthetic */ String Oo;

            AnonymousClass5(String resourceId2) {
                str = resourceId2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new GetMetadataRequest(DriveId.bg(str)), new e(this));
            }
        });
    }

    @Override // com.google.android.gms.drive.DriveApi
    public DriveFolder getAppFolder(GoogleApiClient apiClient) {
        if (!apiClient.isConnected()) {
            throw new IllegalStateException("Client must be connected");
        }
        DriveId driveIdIa = ((q) apiClient.a(Drive.CU)).ia();
        if (driveIdIa != null) {
            return new u(driveIdIa);
        }
        return null;
    }

    @Override // com.google.android.gms.drive.DriveApi
    public DriveFile getFile(GoogleApiClient apiClient, DriveId id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must be provided.");
        }
        if (apiClient.isConnected()) {
            return new s(id);
        }
        throw new IllegalStateException("Client must be connected");
    }

    @Override // com.google.android.gms.drive.DriveApi
    public DriveFolder getFolder(GoogleApiClient apiClient, DriveId id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must be provided.");
        }
        if (apiClient.isConnected()) {
            return new u(id);
        }
        throw new IllegalStateException("Client must be connected");
    }

    @Override // com.google.android.gms.drive.DriveApi
    public DriveFolder getRootFolder(GoogleApiClient apiClient) {
        if (apiClient.isConnected()) {
            return new u(((q) apiClient.a(Drive.CU)).hZ());
        }
        throw new IllegalStateException("Client must be connected");
    }

    @Override // com.google.android.gms.drive.DriveApi
    public PendingResult<DriveApi.ContentsResult> newContents(GoogleApiClient apiClient) {
        return apiClient.a((GoogleApiClient) new b() { // from class: com.google.android.gms.drive.internal.o.2
            AnonymousClass2() {
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new CreateContentsRequest(536870912), new j(this));
            }
        });
    }

    @Override // com.google.android.gms.drive.DriveApi
    public CreateFileActivityBuilder newCreateFileActivityBuilder() {
        return new CreateFileActivityBuilder();
    }

    @Override // com.google.android.gms.drive.DriveApi
    public PendingResult<DriveApi.DriveContentsResult> newDriveContents(GoogleApiClient apiClient) {
        return a(apiClient, 536870912);
    }

    @Override // com.google.android.gms.drive.DriveApi
    public OpenFileActivityBuilder newOpenFileActivityBuilder() {
        return new OpenFileActivityBuilder();
    }

    @Override // com.google.android.gms.drive.DriveApi
    public PendingResult<DriveApi.MetadataBufferResult> query(GoogleApiClient apiClient, Query query) {
        if (query == null) {
            throw new IllegalArgumentException("Query must be provided.");
        }
        return apiClient.a((GoogleApiClient) new i() { // from class: com.google.android.gms.drive.internal.o.1
            final /* synthetic */ Query Ok;

            AnonymousClass1(Query query2) {
                query = query2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new QueryRequest(query), new l(this));
            }
        });
    }

    @Override // com.google.android.gms.drive.DriveApi
    public PendingResult<Status> requestSync(GoogleApiClient apiClient) {
        return apiClient.b(new p.a() { // from class: com.google.android.gms.drive.internal.o.6
            AnonymousClass6() {
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new bb(this));
            }
        });
    }
}
