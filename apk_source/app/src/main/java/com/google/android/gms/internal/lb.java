package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.os.RemoteException;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.SensorsApi;
import com.google.android.gms.fitness.data.l;
import com.google.android.gms.fitness.request.DataSourceListener;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.internal.kj;
import com.google.android.gms.internal.km;
import com.google.android.gms.internal.ks;

/* loaded from: classes.dex */
public class lb implements SensorsApi {

    private static abstract class a<R extends Result> extends BaseImplementation.a<R, kj> {
        public a() {
            super(Fitness.CU);
        }
    }

    private static class b extends km.a {
        private final BaseImplementation.b<DataSourcesResult> De;

        private b(BaseImplementation.b<DataSourcesResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.internal.km
        public void a(DataSourcesResult dataSourcesResult) {
            this.De.b(dataSourcesResult);
        }
    }

    private static class c extends ks.a {
        private final BaseImplementation.b<Status> De;
        private final DataSourceListener TM;

        private c(BaseImplementation.b<Status> bVar, DataSourceListener dataSourceListener) {
            this.De = bVar;
            this.TM = dataSourceListener;
        }

        @Override // com.google.android.gms.internal.ks
        public void k(Status status) {
            if (this.TM != null && status.isSuccess()) {
                l.a.iO().c(this.TM);
            }
            this.De.b(status);
        }
    }

    private PendingResult<Status> a(GoogleApiClient googleApiClient, final com.google.android.gms.fitness.request.n nVar) {
        return googleApiClient.a((GoogleApiClient) new a<Status>() { // from class: com.google.android.gms.internal.lb.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(nVar, new kj.b(this), kjVar.getContext().getPackageName());
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: d, reason: merged with bridge method [inline-methods] */
            public Status c(Status status) {
                return status;
            }
        });
    }

    private PendingResult<Status> a(GoogleApiClient googleApiClient, final com.google.android.gms.fitness.request.p pVar, final DataSourceListener dataSourceListener) {
        return googleApiClient.b(new a<Status>() { // from class: com.google.android.gms.internal.lb.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(pVar, new c(this, dataSourceListener), kjVar.getContext().getPackageName());
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: d, reason: merged with bridge method [inline-methods] */
            public Status c(Status status) {
                return status;
            }
        });
    }

    @Override // com.google.android.gms.fitness.SensorsApi
    public PendingResult<DataSourcesResult> findDataSources(GoogleApiClient client, final DataSourcesRequest request) {
        return client.a((GoogleApiClient) new a<DataSourcesResult>() { // from class: com.google.android.gms.internal.lb.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: A, reason: merged with bridge method [inline-methods] */
            public DataSourcesResult c(Status status) {
                return DataSourcesResult.E(status);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(request, new b(this), kjVar.getContext().getPackageName());
            }
        });
    }

    @Override // com.google.android.gms.fitness.SensorsApi
    public PendingResult<Status> register(GoogleApiClient client, SensorRequest request, PendingIntent intent) {
        return a(client, new com.google.android.gms.fitness.request.n(request, null, intent));
    }

    @Override // com.google.android.gms.fitness.SensorsApi
    public PendingResult<Status> register(GoogleApiClient client, SensorRequest request, DataSourceListener listener) {
        return a(client, new com.google.android.gms.fitness.request.n(request, l.a.iO().a(listener), null));
    }

    @Override // com.google.android.gms.fitness.SensorsApi
    public PendingResult<Status> unregister(GoogleApiClient client, PendingIntent pendingIntent) {
        return a(client, new com.google.android.gms.fitness.request.p(null, pendingIntent), null);
    }

    @Override // com.google.android.gms.fitness.SensorsApi
    public PendingResult<Status> unregister(GoogleApiClient client, DataSourceListener listener) {
        com.google.android.gms.fitness.data.l lVarB = l.a.iO().b(listener);
        return lVarB == null ? new kt(Status.Jo) : a(client, new com.google.android.gms.fitness.request.p(lVarB, null), listener);
    }
}
