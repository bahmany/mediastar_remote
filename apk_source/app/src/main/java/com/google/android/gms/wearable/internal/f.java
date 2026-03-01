package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.PutDataRequest;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public final class f implements DataApi {

    /* renamed from: com.google.android.gms.wearable.internal.f$1 */
    class AnonymousClass1 extends d<DataApi.DataItemResult> {
        final /* synthetic */ PutDataRequest avb;

        AnonymousClass1(PutDataRequest putDataRequest) {
            putDataRequest = putDataRequest;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws IOException, RemoteException {
            awVar.a(this, putDataRequest);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: aF */
        public DataApi.DataItemResult c(Status status) {
            return new a(status, null);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.f$2 */
    class AnonymousClass2 extends d<DataApi.DataItemResult> {
        final /* synthetic */ Uri akn;

        AnonymousClass2(Uri uri) {
            uri = uri;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.a(this, uri);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: aF */
        public DataApi.DataItemResult c(Status status) {
            return new a(status, null);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.f$3 */
    class AnonymousClass3 extends d<DataItemBuffer> {
        AnonymousClass3() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.o(this);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: aG */
        public DataItemBuffer c(Status status) {
            return new DataItemBuffer(DataHolder.as(status.getStatusCode()));
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.f$4 */
    class AnonymousClass4 extends d<DataItemBuffer> {
        final /* synthetic */ Uri akn;

        AnonymousClass4(Uri uri) {
            uri = uri;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.b(this, uri);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: aG */
        public DataItemBuffer c(Status status) {
            return new DataItemBuffer(DataHolder.as(status.getStatusCode()));
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.f$5 */
    class AnonymousClass5 extends d<DataApi.DeleteDataItemsResult> {
        final /* synthetic */ Uri akn;

        AnonymousClass5(Uri uri) {
            uri = uri;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.c(this, uri);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: aH */
        public DataApi.DeleteDataItemsResult c(Status status) {
            return new b(status, 0);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.f$6 */
    class AnonymousClass6 extends d<DataApi.GetFdForAssetResult> {
        final /* synthetic */ Asset avd;

        AnonymousClass6(Asset asset) {
            asset = asset;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.a(this, asset);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: aI */
        public DataApi.GetFdForAssetResult c(Status status) {
            return new c(status, null);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.f$7 */
    class AnonymousClass7 extends d<DataApi.GetFdForAssetResult> {
        final /* synthetic */ DataItemAsset ave;

        AnonymousClass7(DataItemAsset dataItemAsset) {
            dataItemAsset = dataItemAsset;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.a(this, dataItemAsset);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: aI */
        public DataApi.GetFdForAssetResult c(Status status) {
            return new c(status, null);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.f$8 */
    class AnonymousClass8 extends d<Status> {
        final /* synthetic */ DataApi.DataListener avf;
        final /* synthetic */ IntentFilter[] avg;

        AnonymousClass8(DataApi.DataListener dataListener, IntentFilter[] intentFilterArr) {
            dataListener = dataListener;
            intentFilterArr = intentFilterArr;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.a(this, dataListener, intentFilterArr);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return new Status(13);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.f$9 */
    class AnonymousClass9 extends d<Status> {
        final /* synthetic */ DataApi.DataListener avf;

        AnonymousClass9(DataApi.DataListener dataListener) {
            dataListener = dataListener;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.a(this, dataListener);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return new Status(13);
        }
    }

    public static class a implements DataApi.DataItemResult {
        private final Status CM;
        private final DataItem avh;

        public a(Status status, DataItem dataItem) {
            this.CM = status;
            this.avh = dataItem;
        }

        @Override // com.google.android.gms.wearable.DataApi.DataItemResult
        public DataItem getDataItem() {
            return this.avh;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    public static class b implements DataApi.DeleteDataItemsResult {
        private final Status CM;
        private final int avi;

        public b(Status status, int i) {
            this.CM = status;
            this.avi = i;
        }

        @Override // com.google.android.gms.wearable.DataApi.DeleteDataItemsResult
        public int getNumDeleted() {
            return this.avi;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    public static class c implements DataApi.GetFdForAssetResult {
        private final Status CM;
        private volatile InputStream XM;
        private volatile ParcelFileDescriptor avj;
        private volatile boolean mClosed = false;

        public c(Status status, ParcelFileDescriptor parcelFileDescriptor) {
            this.CM = status;
            this.avj = parcelFileDescriptor;
        }

        @Override // com.google.android.gms.wearable.DataApi.GetFdForAssetResult
        public ParcelFileDescriptor getFd() {
            if (this.mClosed) {
                throw new IllegalStateException("Cannot access the file descriptor after release().");
            }
            return this.avj;
        }

        @Override // com.google.android.gms.wearable.DataApi.GetFdForAssetResult
        public InputStream getInputStream() {
            if (this.mClosed) {
                throw new IllegalStateException("Cannot access the input stream after release().");
            }
            if (this.avj == null) {
                return null;
            }
            if (this.XM == null) {
                this.XM = new ParcelFileDescriptor.AutoCloseInputStream(this.avj);
            }
            return this.XM;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }

        @Override // com.google.android.gms.common.api.Releasable
        public void release() throws IOException {
            if (this.avj == null) {
                return;
            }
            if (this.mClosed) {
                throw new IllegalStateException("releasing an already released result.");
            }
            try {
                if (this.XM != null) {
                    this.XM.close();
                } else {
                    this.avj.close();
                }
                this.mClosed = true;
                this.avj = null;
                this.XM = null;
            } catch (IOException e) {
            }
        }
    }

    private PendingResult<Status> a(GoogleApiClient googleApiClient, DataApi.DataListener dataListener, IntentFilter[] intentFilterArr) {
        return googleApiClient.a((GoogleApiClient) new d<Status>() { // from class: com.google.android.gms.wearable.internal.f.8
            final /* synthetic */ DataApi.DataListener avf;
            final /* synthetic */ IntentFilter[] avg;

            AnonymousClass8(DataApi.DataListener dataListener2, IntentFilter[] intentFilterArr2) {
                dataListener = dataListener2;
                intentFilterArr = intentFilterArr2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.a(this, dataListener, intentFilterArr);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: d */
            public Status c(Status status) {
                return new Status(13);
            }
        });
    }

    private void a(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("asset is null");
        }
        if (asset.getDigest() == null) {
            throw new IllegalArgumentException("invalid asset");
        }
        if (asset.getData() != null) {
            throw new IllegalArgumentException("invalid asset");
        }
    }

    @Override // com.google.android.gms.wearable.DataApi
    public PendingResult<Status> addListener(GoogleApiClient client, DataApi.DataListener listener) {
        return a(client, listener, null);
    }

    @Override // com.google.android.gms.wearable.DataApi
    public PendingResult<DataApi.DeleteDataItemsResult> deleteDataItems(GoogleApiClient client, Uri uri) {
        return client.a((GoogleApiClient) new d<DataApi.DeleteDataItemsResult>() { // from class: com.google.android.gms.wearable.internal.f.5
            final /* synthetic */ Uri akn;

            AnonymousClass5(Uri uri2) {
                uri = uri2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.c(this, uri);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: aH */
            public DataApi.DeleteDataItemsResult c(Status status) {
                return new b(status, 0);
            }
        });
    }

    @Override // com.google.android.gms.wearable.DataApi
    public PendingResult<DataApi.DataItemResult> getDataItem(GoogleApiClient client, Uri uri) {
        return client.a((GoogleApiClient) new d<DataApi.DataItemResult>() { // from class: com.google.android.gms.wearable.internal.f.2
            final /* synthetic */ Uri akn;

            AnonymousClass2(Uri uri2) {
                uri = uri2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.a(this, uri);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: aF */
            public DataApi.DataItemResult c(Status status) {
                return new a(status, null);
            }
        });
    }

    @Override // com.google.android.gms.wearable.DataApi
    public PendingResult<DataItemBuffer> getDataItems(GoogleApiClient client) {
        return client.a((GoogleApiClient) new d<DataItemBuffer>() { // from class: com.google.android.gms.wearable.internal.f.3
            AnonymousClass3() {
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.o(this);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: aG */
            public DataItemBuffer c(Status status) {
                return new DataItemBuffer(DataHolder.as(status.getStatusCode()));
            }
        });
    }

    @Override // com.google.android.gms.wearable.DataApi
    public PendingResult<DataItemBuffer> getDataItems(GoogleApiClient client, Uri uri) {
        return client.a((GoogleApiClient) new d<DataItemBuffer>() { // from class: com.google.android.gms.wearable.internal.f.4
            final /* synthetic */ Uri akn;

            AnonymousClass4(Uri uri2) {
                uri = uri2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.b(this, uri);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: aG */
            public DataItemBuffer c(Status status) {
                return new DataItemBuffer(DataHolder.as(status.getStatusCode()));
            }
        });
    }

    @Override // com.google.android.gms.wearable.DataApi
    public PendingResult<DataApi.GetFdForAssetResult> getFdForAsset(GoogleApiClient client, Asset asset) {
        a(asset);
        return client.a((GoogleApiClient) new d<DataApi.GetFdForAssetResult>() { // from class: com.google.android.gms.wearable.internal.f.6
            final /* synthetic */ Asset avd;

            AnonymousClass6(Asset asset2) {
                asset = asset2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.a(this, asset);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: aI */
            public DataApi.GetFdForAssetResult c(Status status) {
                return new c(status, null);
            }
        });
    }

    @Override // com.google.android.gms.wearable.DataApi
    public PendingResult<DataApi.GetFdForAssetResult> getFdForAsset(GoogleApiClient client, DataItemAsset asset) {
        return client.a((GoogleApiClient) new d<DataApi.GetFdForAssetResult>() { // from class: com.google.android.gms.wearable.internal.f.7
            final /* synthetic */ DataItemAsset ave;

            AnonymousClass7(DataItemAsset asset2) {
                dataItemAsset = asset2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.a(this, dataItemAsset);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: aI */
            public DataApi.GetFdForAssetResult c(Status status) {
                return new c(status, null);
            }
        });
    }

    @Override // com.google.android.gms.wearable.DataApi
    public PendingResult<DataApi.DataItemResult> putDataItem(GoogleApiClient client, PutDataRequest request) {
        return client.a((GoogleApiClient) new d<DataApi.DataItemResult>() { // from class: com.google.android.gms.wearable.internal.f.1
            final /* synthetic */ PutDataRequest avb;

            AnonymousClass1(PutDataRequest request2) {
                putDataRequest = request2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws IOException, RemoteException {
                awVar.a(this, putDataRequest);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: aF */
            public DataApi.DataItemResult c(Status status) {
                return new a(status, null);
            }
        });
    }

    @Override // com.google.android.gms.wearable.DataApi
    public PendingResult<Status> removeListener(GoogleApiClient client, DataApi.DataListener listener) {
        return client.a((GoogleApiClient) new d<Status>() { // from class: com.google.android.gms.wearable.internal.f.9
            final /* synthetic */ DataApi.DataListener avf;

            AnonymousClass9(DataApi.DataListener listener2) {
                dataListener = listener2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.a(this, dataListener);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: d */
            public Status c(Status status) {
                return new Status(13);
            }
        });
    }
}
