package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.MessageApi;

/* loaded from: classes.dex */
public final class ag implements MessageApi {

    /* renamed from: com.google.android.gms.wearable.internal.ag$1 */
    class AnonymousClass1 extends d<MessageApi.SendMessageResult> {
        final /* synthetic */ byte[] CY;
        final /* synthetic */ String avs;
        final /* synthetic */ String avt;

        AnonymousClass1(String str, String str2, byte[] bArr) {
            str = str;
            str = str2;
            bArr = bArr;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.a(this, str, str, bArr);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: aJ */
        public MessageApi.SendMessageResult c(Status status) {
            return new a(status, -1);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.ag$2 */
    class AnonymousClass2 extends d<Status> {
        final /* synthetic */ IntentFilter[] avg;
        final /* synthetic */ MessageApi.MessageListener avv;

        AnonymousClass2(MessageApi.MessageListener messageListener, IntentFilter[] intentFilterArr) {
            messageListener = messageListener;
            intentFilterArr = intentFilterArr;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.a(this, messageListener, intentFilterArr);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return new Status(13);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.ag$3 */
    class AnonymousClass3 extends d<Status> {
        final /* synthetic */ MessageApi.MessageListener avv;

        AnonymousClass3(MessageApi.MessageListener messageListener) {
            messageListener = messageListener;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.a(this, messageListener);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return new Status(13);
        }
    }

    public static class a implements MessageApi.SendMessageResult {
        private final Status CM;
        private final int uQ;

        public a(Status status, int i) {
            this.CM = status;
            this.uQ = i;
        }

        @Override // com.google.android.gms.wearable.MessageApi.SendMessageResult
        public int getRequestId() {
            return this.uQ;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    private PendingResult<Status> a(GoogleApiClient googleApiClient, MessageApi.MessageListener messageListener, IntentFilter[] intentFilterArr) {
        return googleApiClient.a((GoogleApiClient) new d<Status>() { // from class: com.google.android.gms.wearable.internal.ag.2
            final /* synthetic */ IntentFilter[] avg;
            final /* synthetic */ MessageApi.MessageListener avv;

            AnonymousClass2(MessageApi.MessageListener messageListener2, IntentFilter[] intentFilterArr2) {
                messageListener = messageListener2;
                intentFilterArr = intentFilterArr2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.a(this, messageListener, intentFilterArr);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: d */
            public Status c(Status status) {
                return new Status(13);
            }
        });
    }

    @Override // com.google.android.gms.wearable.MessageApi
    public PendingResult<Status> addListener(GoogleApiClient client, MessageApi.MessageListener listener) {
        return a(client, listener, null);
    }

    @Override // com.google.android.gms.wearable.MessageApi
    public PendingResult<Status> removeListener(GoogleApiClient client, MessageApi.MessageListener listener) {
        return client.a((GoogleApiClient) new d<Status>() { // from class: com.google.android.gms.wearable.internal.ag.3
            final /* synthetic */ MessageApi.MessageListener avv;

            AnonymousClass3(MessageApi.MessageListener listener2) {
                messageListener = listener2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.a(this, messageListener);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: d */
            public Status c(Status status) {
                return new Status(13);
            }
        });
    }

    @Override // com.google.android.gms.wearable.MessageApi
    public PendingResult<MessageApi.SendMessageResult> sendMessage(GoogleApiClient client, String nodeId, String action, byte[] data) {
        return client.a((GoogleApiClient) new d<MessageApi.SendMessageResult>() { // from class: com.google.android.gms.wearable.internal.ag.1
            final /* synthetic */ byte[] CY;
            final /* synthetic */ String avs;
            final /* synthetic */ String avt;

            AnonymousClass1(String nodeId2, String action2, byte[] data2) {
                str = nodeId2;
                str = action2;
                bArr = data2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.a(this, str, str, bArr);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: aJ */
            public MessageApi.SendMessageResult c(Status status) {
                return new a(status, -1);
            }
        });
    }
}
