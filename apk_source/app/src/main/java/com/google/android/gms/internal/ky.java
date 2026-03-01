package com.google.android.gms.internal;

import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.HistoryApi;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataInsertRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.internal.kj;
import com.google.android.gms.internal.kl;

/* loaded from: classes.dex */
public class ky implements HistoryApi {

    private static class a extends kl.a {
        private final BaseImplementation.b<DataReadResult> De;
        private int TB;
        private DataReadResult TC;

        private a(BaseImplementation.b<DataReadResult> bVar) {
            this.TB = 0;
            this.TC = null;
            this.De = bVar;
        }

        @Override // com.google.android.gms.internal.kl
        public void a(DataReadResult dataReadResult) {
            synchronized (this) {
                Log.v("Fitness", "Received batch result");
                if (this.TC == null) {
                    this.TC = dataReadResult;
                } else {
                    this.TC.b(dataReadResult);
                }
                this.TB++;
                if (this.TB == this.TC.jF()) {
                    this.De.b(this.TC);
                }
            }
        }
    }

    @Override // com.google.android.gms.fitness.HistoryApi
    public PendingResult<Status> deleteData(GoogleApiClient client, final DataDeleteRequest request) {
        return client.a((GoogleApiClient) new kj.c() { // from class: com.google.android.gms.internal.ky.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(request, new kj.b(this), kjVar.getContext().getPackageName());
            }
        });
    }

    @Override // com.google.android.gms.fitness.HistoryApi
    @Deprecated
    public PendingResult<Status> insert(GoogleApiClient client, final DataInsertRequest request) {
        return client.a((GoogleApiClient) new kj.c() { // from class: com.google.android.gms.internal.ky.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(request, new kj.b(this), kjVar.getContext().getPackageName());
            }
        });
    }

    @Override // com.google.android.gms.fitness.HistoryApi
    public PendingResult<Status> insertData(GoogleApiClient client, DataSet dataSet) {
        return insert(client, new DataInsertRequest.Builder().setDataSet(dataSet).build());
    }

    @Override // com.google.android.gms.fitness.HistoryApi
    public PendingResult<DataReadResult> readData(GoogleApiClient client, final DataReadRequest request) {
        return client.a((GoogleApiClient) new kj.a<DataReadResult>() { // from class: com.google.android.gms.internal.ky.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(request, new a(this), kjVar.getContext().getPackageName());
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: y, reason: merged with bridge method [inline-methods] */
            public DataReadResult c(Status status) {
                return DataReadResult.a(status, request);
            }
        });
    }
}
