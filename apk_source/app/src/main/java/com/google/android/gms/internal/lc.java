package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.os.RemoteException;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.SessionsApi;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.request.v;
import com.google.android.gms.fitness.request.x;
import com.google.android.gms.fitness.result.SessionReadResult;
import com.google.android.gms.fitness.result.SessionStopResult;
import com.google.android.gms.internal.kj;
import com.google.android.gms.internal.kq;
import com.google.android.gms.internal.kr;

/* loaded from: classes.dex */
public class lc implements SessionsApi {

    private static class a extends kq.a {
        private final BaseImplementation.b<SessionReadResult> De;

        private a(BaseImplementation.b<SessionReadResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.internal.kq
        public void a(SessionReadResult sessionReadResult) throws RemoteException {
            this.De.b(sessionReadResult);
        }
    }

    private static class b extends kr.a {
        private final BaseImplementation.b<SessionStopResult> De;

        private b(BaseImplementation.b<SessionStopResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.internal.kr
        public void a(SessionStopResult sessionStopResult) {
            this.De.b(sessionStopResult);
        }
    }

    @Override // com.google.android.gms.fitness.SessionsApi
    public PendingResult<Status> insertSession(GoogleApiClient client, final SessionInsertRequest request) {
        return client.a((GoogleApiClient) new kj.c() { // from class: com.google.android.gms.internal.lc.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(request, new kj.b(this), kjVar.getContext().getPackageName());
            }
        });
    }

    @Override // com.google.android.gms.fitness.SessionsApi
    public PendingResult<SessionReadResult> readSession(GoogleApiClient client, final SessionReadRequest request) {
        return client.a((GoogleApiClient) new kj.a<SessionReadResult>() { // from class: com.google.android.gms.internal.lc.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: C, reason: merged with bridge method [inline-methods] */
            public SessionReadResult c(Status status) {
                return SessionReadResult.H(status);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(request, new a(this), kjVar.getContext().getPackageName());
            }
        });
    }

    @Override // com.google.android.gms.fitness.SessionsApi
    public PendingResult<Status> registerForSessions(GoogleApiClient client, final PendingIntent intent) {
        return client.b(new kj.c() { // from class: com.google.android.gms.internal.lc.5
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kj.b bVar = new kj.b(this);
                kjVar.iT().a(new com.google.android.gms.fitness.request.t(intent), bVar, kjVar.getContext().getPackageName());
            }
        });
    }

    @Override // com.google.android.gms.fitness.SessionsApi
    public PendingResult<Status> startSession(GoogleApiClient client, final Session session) {
        return client.b(new kj.c() { // from class: com.google.android.gms.internal.lc.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(new v.a().b(session).jx(), new kj.b(this), kjVar.getContext().getPackageName());
            }
        });
    }

    @Override // com.google.android.gms.fitness.SessionsApi
    public PendingResult<SessionStopResult> stopSession(GoogleApiClient client, final String name, final String identifier) {
        return client.b(new kj.a<SessionStopResult>() { // from class: com.google.android.gms.internal.lc.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: B, reason: merged with bridge method [inline-methods] */
            public SessionStopResult c(Status status) {
                return SessionStopResult.I(status);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(new x.a().br(name).bs(identifier).jy(), new b(this), kjVar.getContext().getPackageName());
            }
        });
    }

    @Override // com.google.android.gms.fitness.SessionsApi
    public PendingResult<Status> unregisterForSessions(GoogleApiClient client, final PendingIntent intent) {
        return client.b(new kj.c() { // from class: com.google.android.gms.internal.lc.6
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kj.b bVar = new kj.b(this);
                kjVar.iT().a(new com.google.android.gms.fitness.request.z(intent), bVar, kjVar.getContext().getPackageName());
            }
        });
    }
}
