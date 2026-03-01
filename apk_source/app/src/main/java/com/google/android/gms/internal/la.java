package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.RecordingApi;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.request.ae;
import com.google.android.gms.fitness.request.ah;
import com.google.android.gms.fitness.request.l;
import com.google.android.gms.fitness.result.ListSubscriptionsResult;
import com.google.android.gms.internal.kj;
import com.google.android.gms.internal.kp;

/* loaded from: classes.dex */
public class la implements RecordingApi {

    private static class a extends kp.a {
        private final BaseImplementation.b<ListSubscriptionsResult> De;

        private a(BaseImplementation.b<ListSubscriptionsResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.internal.kp
        public void a(ListSubscriptionsResult listSubscriptionsResult) {
            this.De.b(listSubscriptionsResult);
        }
    }

    public PendingResult<Status> a(GoogleApiClient googleApiClient, final com.google.android.gms.fitness.request.ae aeVar) {
        return googleApiClient.a((GoogleApiClient) new kj.c() { // from class: com.google.android.gms.internal.la.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(aeVar, new kj.b(this), kjVar.getContext().getPackageName());
            }
        });
    }

    public PendingResult<Status> a(GoogleApiClient googleApiClient, final com.google.android.gms.fitness.request.ah ahVar) {
        return googleApiClient.b(new kj.c() { // from class: com.google.android.gms.internal.la.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(ahVar, new kj.b(this), kjVar.getContext().getPackageName());
            }
        });
    }

    public PendingResult<ListSubscriptionsResult> a(GoogleApiClient googleApiClient, final com.google.android.gms.fitness.request.l lVar) {
        return googleApiClient.a((GoogleApiClient) new kj.a<ListSubscriptionsResult>() { // from class: com.google.android.gms.internal.la.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(lVar, new a(this), kjVar.getContext().getPackageName());
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: z, reason: merged with bridge method [inline-methods] */
            public ListSubscriptionsResult c(Status status) {
                return ListSubscriptionsResult.G(status);
            }
        });
    }

    @Override // com.google.android.gms.fitness.RecordingApi
    public PendingResult<ListSubscriptionsResult> listSubscriptions(GoogleApiClient client) {
        return a(client, new l.a().jk());
    }

    @Override // com.google.android.gms.fitness.RecordingApi
    public PendingResult<ListSubscriptionsResult> listSubscriptions(GoogleApiClient client, DataType dataType) {
        return a(client, new l.a().c(dataType).jk());
    }

    @Override // com.google.android.gms.fitness.RecordingApi
    public PendingResult<Status> subscribe(GoogleApiClient client, DataSource dataSource) {
        return a(client, new ae.a().b(new Subscription.a().b(dataSource).iR()).jD());
    }

    @Override // com.google.android.gms.fitness.RecordingApi
    public PendingResult<Status> subscribe(GoogleApiClient client, DataType dataType) {
        return a(client, new ae.a().b(new Subscription.a().b(dataType).iR()).jD());
    }

    @Override // com.google.android.gms.fitness.RecordingApi
    public PendingResult<Status> unsubscribe(GoogleApiClient client, DataSource dataSource) {
        return a(client, new ah.a().d(dataSource).jE());
    }

    @Override // com.google.android.gms.fitness.RecordingApi
    public PendingResult<Status> unsubscribe(GoogleApiClient client, DataType dataType) {
        return a(client, new ah.a().d(dataType).jE());
    }

    @Override // com.google.android.gms.fitness.RecordingApi
    public PendingResult<Status> unsubscribe(GoogleApiClient client, Subscription subscription) {
        return subscription.getDataType() == null ? unsubscribe(client, subscription.getDataSource()) : unsubscribe(client, subscription.getDataType());
    }
}
