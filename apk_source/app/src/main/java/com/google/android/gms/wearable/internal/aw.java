package com.google.android.gms.wearable.internal;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.d;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.internal.af;
import com.google.android.gms.wearable.internal.ag;
import com.google.android.gms.wearable.internal.aj;
import com.google.android.gms.wearable.internal.f;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/* loaded from: classes.dex */
public class aw extends com.google.android.gms.common.internal.d<af> {
    private final ExecutorService aqp;
    private final HashMap<DataApi.DataListener, ax> avF;
    private final HashMap<MessageApi.MessageListener, ax> avG;
    private final HashMap<NodeApi.NodeListener, ax> avH;

    /* renamed from: com.google.android.gms.wearable.internal.aw$1 */
    class AnonymousClass1 extends com.google.android.gms.wearable.internal.a {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void a(Status status) {
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aw$10 */
    class AnonymousClass10 extends com.google.android.gms.wearable.internal.a {
        final /* synthetic */ BaseImplementation.b avK;

        AnonymousClass10(BaseImplementation.b bVar) {
            bVar = bVar;
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void a(v vVar) {
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(vVar.avo);
            bVar.b(new aj.a(new Status(vVar.statusCode), arrayList));
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aw$11 */
    class AnonymousClass11 extends com.google.android.gms.wearable.internal.a {
        final /* synthetic */ BaseImplementation.b avK;
        final /* synthetic */ DataApi.DataListener avf;

        AnonymousClass11(DataApi.DataListener dataListener, BaseImplementation.b bVar) {
            dataListener = dataListener;
            bVar = bVar;
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void a(Status status) {
            if (!status.isSuccess()) {
                synchronized (aw.this.avF) {
                    aw.this.avF.remove(dataListener);
                }
            }
            bVar.b(status);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aw$12 */
    class AnonymousClass12 extends com.google.android.gms.wearable.internal.a {
        final /* synthetic */ BaseImplementation.b avK;
        final /* synthetic */ MessageApi.MessageListener avv;

        AnonymousClass12(MessageApi.MessageListener messageListener, BaseImplementation.b bVar) {
            messageListener = messageListener;
            bVar = bVar;
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void a(Status status) {
            if (!status.isSuccess()) {
                synchronized (aw.this.avG) {
                    aw.this.avG.remove(messageListener);
                }
            }
            bVar.b(status);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aw$13 */
    class AnonymousClass13 extends com.google.android.gms.wearable.internal.a {
        final /* synthetic */ BaseImplementation.b avK;
        final /* synthetic */ NodeApi.NodeListener avz;

        AnonymousClass13(NodeApi.NodeListener nodeListener, BaseImplementation.b bVar) {
            nodeListener = nodeListener;
            bVar = bVar;
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void a(Status status) {
            if (!status.isSuccess()) {
                synchronized (aw.this.avH) {
                    aw.this.avH.remove(nodeListener);
                }
            }
            bVar.b(status);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aw$14 */
    class AnonymousClass14 extends com.google.android.gms.wearable.internal.a {
        final /* synthetic */ BaseImplementation.b avK;

        AnonymousClass14(BaseImplementation.b bVar) {
            bVar = bVar;
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void a(Status status) {
            bVar.b(status);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aw$2 */
    class AnonymousClass2 implements Callable<Boolean> {
        final /* synthetic */ byte[] CY;
        final /* synthetic */ ParcelFileDescriptor avJ;

        AnonymousClass2(ParcelFileDescriptor parcelFileDescriptor, byte[] bArr) {
            parcelFileDescriptor = parcelFileDescriptor;
            bArr = bArr;
        }

        @Override // java.util.concurrent.Callable
        /* renamed from: pY */
        public Boolean call() throws IOException {
            if (Log.isLoggable("WearableClient", 3)) {
                Log.d("WearableClient", "processAssets: writing data to FD : " + parcelFileDescriptor);
            }
            ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor);
            try {
                try {
                    autoCloseOutputStream.write(bArr);
                    autoCloseOutputStream.flush();
                    if (Log.isLoggable("WearableClient", 3)) {
                        Log.d("WearableClient", "processAssets: wrote data: " + parcelFileDescriptor);
                    }
                    try {
                        if (Log.isLoggable("WearableClient", 3)) {
                            Log.d("WearableClient", "processAssets: closing: " + parcelFileDescriptor);
                        }
                        autoCloseOutputStream.close();
                        return true;
                    } catch (IOException e) {
                        return true;
                    }
                } catch (IOException e2) {
                    Log.w("WearableClient", "processAssets: writing data failed: " + parcelFileDescriptor);
                    return false;
                }
            } finally {
                try {
                    if (Log.isLoggable("WearableClient", 3)) {
                        Log.d("WearableClient", "processAssets: closing: " + parcelFileDescriptor);
                    }
                    autoCloseOutputStream.close();
                } catch (IOException e3) {
                }
            }
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aw$3 */
    class AnonymousClass3 extends com.google.android.gms.wearable.internal.a {
        final /* synthetic */ BaseImplementation.b avK;

        AnonymousClass3(BaseImplementation.b bVar) {
            bVar = bVar;
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void a(x xVar) {
            bVar.b(new f.a(new Status(xVar.statusCode), xVar.avp));
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aw$4 */
    class AnonymousClass4 extends com.google.android.gms.wearable.internal.a {
        final /* synthetic */ BaseImplementation.b avK;

        AnonymousClass4(BaseImplementation.b bVar) {
            bVar = bVar;
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void aa(DataHolder dataHolder) {
            bVar.b(new DataItemBuffer(dataHolder));
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aw$5 */
    class AnonymousClass5 extends com.google.android.gms.wearable.internal.a {
        final /* synthetic */ BaseImplementation.b avK;

        AnonymousClass5(BaseImplementation.b bVar) {
            bVar = bVar;
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void aa(DataHolder dataHolder) {
            bVar.b(new DataItemBuffer(dataHolder));
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aw$6 */
    class AnonymousClass6 extends com.google.android.gms.wearable.internal.a {
        final /* synthetic */ BaseImplementation.b avK;

        AnonymousClass6(BaseImplementation.b bVar) {
            bVar = bVar;
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void a(p pVar) {
            bVar.b(new f.b(new Status(pVar.statusCode), pVar.avl));
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aw$7 */
    class AnonymousClass7 extends com.google.android.gms.wearable.internal.a {
        final /* synthetic */ BaseImplementation.b avK;

        AnonymousClass7(BaseImplementation.b bVar) {
            bVar = bVar;
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void a(as asVar) {
            bVar.b(new ag.a(new Status(asVar.statusCode), asVar.avD));
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aw$8 */
    class AnonymousClass8 extends com.google.android.gms.wearable.internal.a {
        final /* synthetic */ BaseImplementation.b avK;

        AnonymousClass8(BaseImplementation.b bVar) {
            bVar = bVar;
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void a(z zVar) {
            bVar.b(new f.c(new Status(zVar.statusCode), zVar.avq));
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aw$9 */
    class AnonymousClass9 extends com.google.android.gms.wearable.internal.a {
        final /* synthetic */ BaseImplementation.b avK;

        AnonymousClass9(BaseImplementation.b bVar) {
            bVar = bVar;
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void a(ab abVar) {
            bVar.b(new aj.b(new Status(abVar.statusCode), abVar.avr));
        }
    }

    private static class a extends com.google.android.gms.wearable.internal.a {
        private final BaseImplementation.b<DataApi.DataItemResult> De;
        private final List<FutureTask<Boolean>> avL;

        a(BaseImplementation.b<DataApi.DataItemResult> bVar, List<FutureTask<Boolean>> list) {
            this.De = bVar;
            this.avL = list;
        }

        @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
        public void a(ao aoVar) {
            this.De.b(new f.a(new Status(aoVar.statusCode), aoVar.avp));
            if (aoVar.statusCode != 0) {
                Iterator<FutureTask<Boolean>> it = this.avL.iterator();
                while (it.hasNext()) {
                    it.next().cancel(true);
                }
            }
        }
    }

    public aw(Context context, Looper looper, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, connectionCallbacks, onConnectionFailedListener, new String[0]);
        this.aqp = Executors.newCachedThreadPool();
        this.avF = new HashMap<>();
        this.avG = new HashMap<>();
        this.avH = new HashMap<>();
    }

    private FutureTask<Boolean> a(ParcelFileDescriptor parcelFileDescriptor, byte[] bArr) {
        return new FutureTask<>(new Callable<Boolean>() { // from class: com.google.android.gms.wearable.internal.aw.2
            final /* synthetic */ byte[] CY;
            final /* synthetic */ ParcelFileDescriptor avJ;

            AnonymousClass2(ParcelFileDescriptor parcelFileDescriptor2, byte[] bArr2) {
                parcelFileDescriptor = parcelFileDescriptor2;
                bArr = bArr2;
            }

            @Override // java.util.concurrent.Callable
            /* renamed from: pY */
            public Boolean call() throws IOException {
                if (Log.isLoggable("WearableClient", 3)) {
                    Log.d("WearableClient", "processAssets: writing data to FD : " + parcelFileDescriptor);
                }
                ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor);
                try {
                    try {
                        autoCloseOutputStream.write(bArr);
                        autoCloseOutputStream.flush();
                        if (Log.isLoggable("WearableClient", 3)) {
                            Log.d("WearableClient", "processAssets: wrote data: " + parcelFileDescriptor);
                        }
                        try {
                            if (Log.isLoggable("WearableClient", 3)) {
                                Log.d("WearableClient", "processAssets: closing: " + parcelFileDescriptor);
                            }
                            autoCloseOutputStream.close();
                            return true;
                        } catch (IOException e) {
                            return true;
                        }
                    } catch (IOException e2) {
                        Log.w("WearableClient", "processAssets: writing data failed: " + parcelFileDescriptor);
                        return false;
                    }
                } finally {
                    try {
                        if (Log.isLoggable("WearableClient", 3)) {
                            Log.d("WearableClient", "processAssets: closing: " + parcelFileDescriptor);
                        }
                        autoCloseOutputStream.close();
                    } catch (IOException e3) {
                    }
                }
            }
        });
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(int i, IBinder iBinder, Bundle bundle) {
        if (Log.isLoggable("WearableClient", 2)) {
            Log.d("WearableClient", "onPostInitHandler: statusCode " + i);
        }
        if (i == 0) {
            try {
                AnonymousClass1 anonymousClass1 = new com.google.android.gms.wearable.internal.a() { // from class: com.google.android.gms.wearable.internal.aw.1
                    AnonymousClass1() {
                    }

                    @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
                    public void a(Status status) {
                    }
                };
                if (Log.isLoggable("WearableClient", 2)) {
                    Log.d("WearableClient", "onPostInitHandler: service " + iBinder);
                }
                af afVarBT = af.a.bT(iBinder);
                for (Map.Entry<DataApi.DataListener, ax> entry : this.avF.entrySet()) {
                    if (Log.isLoggable("WearableClient", 2)) {
                        Log.d("WearableClient", "onPostInitHandler: adding Data listener " + entry.getValue());
                    }
                    afVarBT.a(anonymousClass1, new b(entry.getValue()));
                }
                for (Map.Entry<MessageApi.MessageListener, ax> entry2 : this.avG.entrySet()) {
                    if (Log.isLoggable("WearableClient", 2)) {
                        Log.d("WearableClient", "onPostInitHandler: adding Message listener " + entry2.getValue());
                    }
                    afVarBT.a(anonymousClass1, new b(entry2.getValue()));
                }
                for (Map.Entry<NodeApi.NodeListener, ax> entry3 : this.avH.entrySet()) {
                    if (Log.isLoggable("WearableClient", 2)) {
                        Log.d("WearableClient", "onPostInitHandler: adding Node listener " + entry3.getValue());
                    }
                    afVarBT.a(anonymousClass1, new b(entry3.getValue()));
                }
            } catch (RemoteException e) {
                Log.d("WearableClient", "WearableClientImpl.onPostInitHandler: error while adding listener", e);
            }
        }
        Log.d("WearableClient", "WearableClientImpl.onPostInitHandler: done");
        super.a(i, iBinder, bundle);
    }

    public void a(BaseImplementation.b<DataApi.DataItemResult> bVar, Uri uri) throws RemoteException {
        gS().a(new com.google.android.gms.wearable.internal.a() { // from class: com.google.android.gms.wearable.internal.aw.3
            final /* synthetic */ BaseImplementation.b avK;

            AnonymousClass3(BaseImplementation.b bVar2) {
                bVar = bVar2;
            }

            @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
            public void a(x xVar) {
                bVar.b(new f.a(new Status(xVar.statusCode), xVar.avp));
            }
        }, uri);
    }

    public void a(BaseImplementation.b<DataApi.GetFdForAssetResult> bVar, Asset asset) throws RemoteException {
        gS().a(new com.google.android.gms.wearable.internal.a() { // from class: com.google.android.gms.wearable.internal.aw.8
            final /* synthetic */ BaseImplementation.b avK;

            AnonymousClass8(BaseImplementation.b bVar2) {
                bVar = bVar2;
            }

            @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
            public void a(z zVar) {
                bVar.b(new f.c(new Status(zVar.statusCode), zVar.avq));
            }
        }, asset);
    }

    public void a(BaseImplementation.b<Status> bVar, DataApi.DataListener dataListener) throws RemoteException {
        ax axVarRemove;
        synchronized (this.avF) {
            axVarRemove = this.avF.remove(dataListener);
        }
        if (axVarRemove == null) {
            bVar.b(new Status(4002));
        } else {
            a(bVar, axVarRemove);
        }
    }

    public void a(BaseImplementation.b<Status> bVar, DataApi.DataListener dataListener, IntentFilter[] intentFilterArr) throws RemoteException {
        ax axVarA = ax.a(dataListener, intentFilterArr);
        synchronized (this.avF) {
            if (this.avF.get(dataListener) != null) {
                bVar.b(new Status(4001));
            } else {
                this.avF.put(dataListener, axVarA);
                gS().a(new com.google.android.gms.wearable.internal.a() { // from class: com.google.android.gms.wearable.internal.aw.11
                    final /* synthetic */ BaseImplementation.b avK;
                    final /* synthetic */ DataApi.DataListener avf;

                    AnonymousClass11(DataApi.DataListener dataListener2, BaseImplementation.b bVar2) {
                        dataListener = dataListener2;
                        bVar = bVar2;
                    }

                    @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
                    public void a(Status status) {
                        if (!status.isSuccess()) {
                            synchronized (aw.this.avF) {
                                aw.this.avF.remove(dataListener);
                            }
                        }
                        bVar.b(status);
                    }
                }, new b(axVarA));
            }
        }
    }

    public void a(BaseImplementation.b<DataApi.GetFdForAssetResult> bVar, DataItemAsset dataItemAsset) throws RemoteException {
        a(bVar, Asset.createFromRef(dataItemAsset.getId()));
    }

    public void a(BaseImplementation.b<Status> bVar, MessageApi.MessageListener messageListener) throws RemoteException {
        synchronized (this.avG) {
            ax axVarRemove = this.avG.remove(messageListener);
            if (axVarRemove == null) {
                bVar.b(new Status(4002));
            } else {
                a(bVar, axVarRemove);
            }
        }
    }

    public void a(BaseImplementation.b<Status> bVar, MessageApi.MessageListener messageListener, IntentFilter[] intentFilterArr) throws RemoteException {
        ax axVarA = ax.a(messageListener, intentFilterArr);
        synchronized (this.avG) {
            if (this.avG.get(messageListener) != null) {
                bVar.b(new Status(4001));
            } else {
                this.avG.put(messageListener, axVarA);
                gS().a(new com.google.android.gms.wearable.internal.a() { // from class: com.google.android.gms.wearable.internal.aw.12
                    final /* synthetic */ BaseImplementation.b avK;
                    final /* synthetic */ MessageApi.MessageListener avv;

                    AnonymousClass12(MessageApi.MessageListener messageListener2, BaseImplementation.b bVar2) {
                        messageListener = messageListener2;
                        bVar = bVar2;
                    }

                    @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
                    public void a(Status status) {
                        if (!status.isSuccess()) {
                            synchronized (aw.this.avG) {
                                aw.this.avG.remove(messageListener);
                            }
                        }
                        bVar.b(status);
                    }
                }, new b(axVarA));
            }
        }
    }

    public void a(BaseImplementation.b<Status> bVar, NodeApi.NodeListener nodeListener) throws RemoteException {
        ax axVarA = ax.a(nodeListener);
        synchronized (this.avH) {
            if (this.avH.get(nodeListener) != null) {
                bVar.b(new Status(4001));
            } else {
                this.avH.put(nodeListener, axVarA);
                gS().a(new com.google.android.gms.wearable.internal.a() { // from class: com.google.android.gms.wearable.internal.aw.13
                    final /* synthetic */ BaseImplementation.b avK;
                    final /* synthetic */ NodeApi.NodeListener avz;

                    AnonymousClass13(NodeApi.NodeListener nodeListener2, BaseImplementation.b bVar2) {
                        nodeListener = nodeListener2;
                        bVar = bVar2;
                    }

                    @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
                    public void a(Status status) {
                        if (!status.isSuccess()) {
                            synchronized (aw.this.avH) {
                                aw.this.avH.remove(nodeListener);
                            }
                        }
                        bVar.b(status);
                    }
                }, new b(axVarA));
            }
        }
    }

    public void a(BaseImplementation.b<DataApi.DataItemResult> bVar, PutDataRequest putDataRequest) throws IOException, RemoteException {
        Iterator<Map.Entry<String, Asset>> it = putDataRequest.getAssets().entrySet().iterator();
        while (it.hasNext()) {
            Asset value = it.next().getValue();
            if (value.getData() == null && value.getDigest() == null && value.getFd() == null && value.getUri() == null) {
                throw new IllegalArgumentException("Put for " + putDataRequest.getUri() + " contains invalid asset: " + value);
            }
        }
        PutDataRequest putDataRequestK = PutDataRequest.k(putDataRequest.getUri());
        putDataRequestK.setData(putDataRequest.getData());
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, Asset> entry : putDataRequest.getAssets().entrySet()) {
            Asset value2 = entry.getValue();
            if (value2.getData() == null) {
                putDataRequestK.putAsset(entry.getKey(), entry.getValue());
            } else {
                try {
                    ParcelFileDescriptor[] parcelFileDescriptorArrCreatePipe = ParcelFileDescriptor.createPipe();
                    if (Log.isLoggable("WearableClient", 3)) {
                        Log.d("WearableClient", "processAssets: replacing data with FD in asset: " + value2 + " read:" + parcelFileDescriptorArrCreatePipe[0] + " write:" + parcelFileDescriptorArrCreatePipe[1]);
                    }
                    putDataRequestK.putAsset(entry.getKey(), Asset.createFromFd(parcelFileDescriptorArrCreatePipe[0]));
                    FutureTask<Boolean> futureTaskA = a(parcelFileDescriptorArrCreatePipe[1], value2.getData());
                    arrayList.add(futureTaskA);
                    this.aqp.submit(futureTaskA);
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to create ParcelFileDescriptor for asset in request: " + putDataRequest, e);
                }
            }
        }
        try {
            gS().a(new a(bVar, arrayList), putDataRequestK);
        } catch (NullPointerException e2) {
            throw new IllegalStateException("Unable to putDataItem: " + putDataRequest, e2);
        }
    }

    public void a(BaseImplementation.b<Status> bVar, ae aeVar) throws RemoteException {
        gS().a(new com.google.android.gms.wearable.internal.a() { // from class: com.google.android.gms.wearable.internal.aw.14
            final /* synthetic */ BaseImplementation.b avK;

            AnonymousClass14(BaseImplementation.b bVar2) {
                bVar = bVar2;
            }

            @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
            public void a(Status status) {
                bVar.b(status);
            }
        }, new aq(aeVar));
    }

    public void a(BaseImplementation.b<MessageApi.SendMessageResult> bVar, String str, String str2, byte[] bArr) throws RemoteException {
        gS().a(new com.google.android.gms.wearable.internal.a() { // from class: com.google.android.gms.wearable.internal.aw.7
            final /* synthetic */ BaseImplementation.b avK;

            AnonymousClass7(BaseImplementation.b bVar2) {
                bVar = bVar2;
            }

            @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
            public void a(as asVar) {
                bVar.b(new ag.a(new Status(asVar.statusCode), asVar.avD));
            }
        }, str, str2, bArr);
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(com.google.android.gms.common.internal.k kVar, d.e eVar) throws RemoteException {
        kVar.e(eVar, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, getContext().getPackageName());
    }

    public void b(BaseImplementation.b<DataItemBuffer> bVar, Uri uri) throws RemoteException {
        gS().b(new com.google.android.gms.wearable.internal.a() { // from class: com.google.android.gms.wearable.internal.aw.5
            final /* synthetic */ BaseImplementation.b avK;

            AnonymousClass5(BaseImplementation.b bVar2) {
                bVar = bVar2;
            }

            @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
            public void aa(DataHolder dataHolder) {
                bVar.b(new DataItemBuffer(dataHolder));
            }
        }, uri);
    }

    public void b(BaseImplementation.b<Status> bVar, NodeApi.NodeListener nodeListener) throws RemoteException {
        synchronized (this.avH) {
            ax axVarRemove = this.avH.remove(nodeListener);
            if (axVarRemove == null) {
                bVar.b(new Status(4002));
            } else {
                a(bVar, axVarRemove);
            }
        }
    }

    @Override // com.google.android.gms.common.internal.d
    /* renamed from: bU */
    public af j(IBinder iBinder) {
        return af.a.bT(iBinder);
    }

    public void c(BaseImplementation.b<DataApi.DeleteDataItemsResult> bVar, Uri uri) throws RemoteException {
        gS().c(new com.google.android.gms.wearable.internal.a() { // from class: com.google.android.gms.wearable.internal.aw.6
            final /* synthetic */ BaseImplementation.b avK;

            AnonymousClass6(BaseImplementation.b bVar2) {
                bVar = bVar2;
            }

            @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
            public void a(p pVar) {
                bVar.b(new f.b(new Status(pVar.statusCode), pVar.avl));
            }
        }, uri);
    }

    @Override // com.google.android.gms.common.internal.d, com.google.android.gms.common.api.Api.a
    public void disconnect() {
        super.disconnect();
        this.avF.clear();
        this.avG.clear();
        this.avH.clear();
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return "com.google.android.gms.wearable.internal.IWearableService";
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return "com.google.android.gms.wearable.BIND";
    }

    public void o(BaseImplementation.b<DataItemBuffer> bVar) throws RemoteException {
        gS().b(new com.google.android.gms.wearable.internal.a() { // from class: com.google.android.gms.wearable.internal.aw.4
            final /* synthetic */ BaseImplementation.b avK;

            AnonymousClass4(BaseImplementation.b bVar2) {
                bVar = bVar2;
            }

            @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
            public void aa(DataHolder dataHolder) {
                bVar.b(new DataItemBuffer(dataHolder));
            }
        });
    }

    public void p(BaseImplementation.b<NodeApi.GetLocalNodeResult> bVar) throws RemoteException {
        gS().c(new com.google.android.gms.wearable.internal.a() { // from class: com.google.android.gms.wearable.internal.aw.9
            final /* synthetic */ BaseImplementation.b avK;

            AnonymousClass9(BaseImplementation.b bVar2) {
                bVar = bVar2;
            }

            @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
            public void a(ab abVar) {
                bVar.b(new aj.b(new Status(abVar.statusCode), abVar.avr));
            }
        });
    }

    public void q(BaseImplementation.b<NodeApi.GetConnectedNodesResult> bVar) throws RemoteException {
        gS().d(new com.google.android.gms.wearable.internal.a() { // from class: com.google.android.gms.wearable.internal.aw.10
            final /* synthetic */ BaseImplementation.b avK;

            AnonymousClass10(BaseImplementation.b bVar2) {
                bVar = bVar2;
            }

            @Override // com.google.android.gms.wearable.internal.a, com.google.android.gms.wearable.internal.ad
            public void a(v vVar) {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(vVar.avo);
                bVar.b(new aj.a(new Status(vVar.statusCode), arrayList));
            }
        });
    }
}
