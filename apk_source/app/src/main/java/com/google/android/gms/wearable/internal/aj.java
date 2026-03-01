package com.google.android.gms.wearable.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import java.util.List;

/* loaded from: classes.dex */
public final class aj implements NodeApi {

    /* renamed from: com.google.android.gms.wearable.internal.aj$1 */
    class AnonymousClass1 extends d<NodeApi.GetLocalNodeResult> {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.p(this);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: aK */
        public NodeApi.GetLocalNodeResult c(Status status) {
            return new b(status, null);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aj$2 */
    class AnonymousClass2 extends d<NodeApi.GetConnectedNodesResult> {
        AnonymousClass2() {
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.q(this);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: aL */
        public NodeApi.GetConnectedNodesResult c(Status status) {
            return new a(status, null);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aj$3 */
    class AnonymousClass3 extends d<Status> {
        final /* synthetic */ NodeApi.NodeListener avz;

        AnonymousClass3(NodeApi.NodeListener nodeListener) {
            nodeListener = nodeListener;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.a(this, nodeListener);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return new Status(13);
        }
    }

    /* renamed from: com.google.android.gms.wearable.internal.aj$4 */
    class AnonymousClass4 extends d<Status> {
        final /* synthetic */ NodeApi.NodeListener avz;

        AnonymousClass4(NodeApi.NodeListener nodeListener) {
            nodeListener = nodeListener;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(aw awVar) throws RemoteException {
            awVar.b(this, nodeListener);
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
        /* renamed from: d */
        public Status c(Status status) {
            return new Status(13);
        }
    }

    public static class a implements NodeApi.GetConnectedNodesResult {
        private final Status CM;
        private final List<Node> avA;

        public a(Status status, List<Node> list) {
            this.CM = status;
            this.avA = list;
        }

        @Override // com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult
        public List<Node> getNodes() {
            return this.avA;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    public static class b implements NodeApi.GetLocalNodeResult {
        private final Status CM;
        private final Node avB;

        public b(Status status, Node node) {
            this.CM = status;
            this.avB = node;
        }

        @Override // com.google.android.gms.wearable.NodeApi.GetLocalNodeResult
        public Node getNode() {
            return this.avB;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    @Override // com.google.android.gms.wearable.NodeApi
    public PendingResult<Status> addListener(GoogleApiClient client, NodeApi.NodeListener listener) {
        return client.a((GoogleApiClient) new d<Status>() { // from class: com.google.android.gms.wearable.internal.aj.3
            final /* synthetic */ NodeApi.NodeListener avz;

            AnonymousClass3(NodeApi.NodeListener listener2) {
                nodeListener = listener2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.a(this, nodeListener);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: d */
            public Status c(Status status) {
                return new Status(13);
            }
        });
    }

    @Override // com.google.android.gms.wearable.NodeApi
    public PendingResult<NodeApi.GetConnectedNodesResult> getConnectedNodes(GoogleApiClient client) {
        return client.a((GoogleApiClient) new d<NodeApi.GetConnectedNodesResult>() { // from class: com.google.android.gms.wearable.internal.aj.2
            AnonymousClass2() {
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.q(this);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: aL */
            public NodeApi.GetConnectedNodesResult c(Status status) {
                return new a(status, null);
            }
        });
    }

    @Override // com.google.android.gms.wearable.NodeApi
    public PendingResult<NodeApi.GetLocalNodeResult> getLocalNode(GoogleApiClient client) {
        return client.a((GoogleApiClient) new d<NodeApi.GetLocalNodeResult>() { // from class: com.google.android.gms.wearable.internal.aj.1
            AnonymousClass1() {
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.p(this);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: aK */
            public NodeApi.GetLocalNodeResult c(Status status) {
                return new b(status, null);
            }
        });
    }

    @Override // com.google.android.gms.wearable.NodeApi
    public PendingResult<Status> removeListener(GoogleApiClient client, NodeApi.NodeListener listener) {
        return client.a((GoogleApiClient) new d<Status>() { // from class: com.google.android.gms.wearable.internal.aj.4
            final /* synthetic */ NodeApi.NodeListener avz;

            AnonymousClass4(NodeApi.NodeListener listener2) {
                nodeListener = listener2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(aw awVar) throws RemoteException {
                awVar.b(this, nodeListener);
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.AbstractPendingResult
            /* renamed from: d */
            public Status c(Status status) {
                return new Status(13);
            }
        });
    }
}
