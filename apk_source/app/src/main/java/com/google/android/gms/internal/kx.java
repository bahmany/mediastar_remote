package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.ConfigApi;
import com.google.android.gms.fitness.request.DataTypeCreateRequest;
import com.google.android.gms.fitness.result.DataTypeResult;
import com.google.android.gms.internal.kj;
import com.google.android.gms.internal.kn;

/* loaded from: classes.dex */
public class kx implements ConfigApi {

    private static class a extends kn.a {
        private final BaseImplementation.b<DataTypeResult> De;

        private a(BaseImplementation.b<DataTypeResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.internal.kn
        public void a(DataTypeResult dataTypeResult) {
            this.De.b(dataTypeResult);
        }
    }

    @Override // com.google.android.gms.fitness.ConfigApi
    public PendingResult<DataTypeResult> createCustomDataType(GoogleApiClient client, final DataTypeCreateRequest request) {
        return client.b(new kj.a<DataTypeResult>() { // from class: com.google.android.gms.internal.kx.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(request, new a(this), kjVar.getContext().getPackageName());
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: x, reason: merged with bridge method [inline-methods] */
            public DataTypeResult c(Status status) {
                return DataTypeResult.F(status);
            }
        });
    }

    @Override // com.google.android.gms.fitness.ConfigApi
    public PendingResult<Status> disableFit(GoogleApiClient client) {
        return client.b(new kj.c() { // from class: com.google.android.gms.internal.kx.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(new kj.b(this), kjVar.getContext().getPackageName());
            }
        });
    }

    @Override // com.google.android.gms.fitness.ConfigApi
    public PendingResult<DataTypeResult> readDataType(GoogleApiClient client, String dataTypeName) {
        final com.google.android.gms.fitness.request.i iVar = new com.google.android.gms.fitness.request.i(dataTypeName);
        return client.a((GoogleApiClient) new kj.a<DataTypeResult>() { // from class: com.google.android.gms.internal.kx.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(iVar, new a(this), kjVar.getContext().getPackageName());
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: x, reason: merged with bridge method [inline-methods] */
            public DataTypeResult c(Status status) {
                return DataTypeResult.F(status);
            }
        });
    }
}
