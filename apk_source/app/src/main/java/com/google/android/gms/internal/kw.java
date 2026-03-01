package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.BleApi;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.fitness.request.StartBleScanRequest;
import com.google.android.gms.fitness.request.UnclaimBleDeviceRequest;
import com.google.android.gms.fitness.result.BleDevicesResult;
import com.google.android.gms.internal.kj;
import com.google.android.gms.internal.le;

/* loaded from: classes.dex */
public class kw implements BleApi {

    private static class a extends le.a {
        private final BaseImplementation.b<BleDevicesResult> De;

        private a(BaseImplementation.b<BleDevicesResult> bVar) {
            this.De = bVar;
        }

        @Override // com.google.android.gms.internal.le
        public void a(BleDevicesResult bleDevicesResult) {
            this.De.b(bleDevicesResult);
        }
    }

    @Override // com.google.android.gms.fitness.BleApi
    public PendingResult<Status> claimBleDevice(GoogleApiClient client, final BleDevice bleDevice) {
        return client.a((GoogleApiClient) new kj.c() { // from class: com.google.android.gms.internal.kw.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(new com.google.android.gms.fitness.request.b(bleDevice), new kj.b(this), kjVar.getContext().getPackageName());
            }
        });
    }

    @Override // com.google.android.gms.fitness.BleApi
    public PendingResult<Status> claimBleDevice(GoogleApiClient client, final String deviceAddress) {
        return client.a((GoogleApiClient) new kj.c() { // from class: com.google.android.gms.internal.kw.3
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(new com.google.android.gms.fitness.request.b(deviceAddress), new kj.b(this), kjVar.getContext().getPackageName());
            }
        });
    }

    @Override // com.google.android.gms.fitness.BleApi
    public PendingResult<BleDevicesResult> listClaimedBleDevices(GoogleApiClient client) {
        return client.a((GoogleApiClient) new kj.a<BleDevicesResult>() { // from class: com.google.android.gms.internal.kw.6
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(new a(this), kjVar.getContext().getPackageName());
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: w, reason: merged with bridge method [inline-methods] */
            public BleDevicesResult c(Status status) {
                return BleDevicesResult.D(status);
            }
        });
    }

    @Override // com.google.android.gms.fitness.BleApi
    public PendingResult<Status> startBleScan(GoogleApiClient client, final StartBleScanRequest request) {
        return client.a((GoogleApiClient) new kj.c() { // from class: com.google.android.gms.internal.kw.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(request, new kj.b(this), kjVar.getContext().getPackageName());
            }
        });
    }

    @Override // com.google.android.gms.fitness.BleApi
    public PendingResult<Status> stopBleScan(GoogleApiClient client, final BleScanCallback requestCallback) {
        return client.a((GoogleApiClient) new kj.c() { // from class: com.google.android.gms.internal.kw.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kj.b bVar = new kj.b(this);
                String packageName = kjVar.getContext().getPackageName();
                kjVar.iT().a(new com.google.android.gms.fitness.request.ac(requestCallback), bVar, packageName);
            }
        });
    }

    @Override // com.google.android.gms.fitness.BleApi
    public PendingResult<Status> unclaimBleDevice(GoogleApiClient client, BleDevice bleDevice) {
        return unclaimBleDevice(client, bleDevice.getAddress());
    }

    @Override // com.google.android.gms.fitness.BleApi
    public PendingResult<Status> unclaimBleDevice(GoogleApiClient client, final String deviceAddress) {
        return client.a((GoogleApiClient) new kj.c() { // from class: com.google.android.gms.internal.kw.5
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(kj kjVar) throws RemoteException {
                kjVar.iT().a(new UnclaimBleDeviceRequest(deviceAddress), new kj.b(this), kjVar.getContext().getPackageName());
            }
        });
    }
}
