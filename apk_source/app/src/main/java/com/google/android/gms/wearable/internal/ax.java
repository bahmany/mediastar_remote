package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.internal.ae;

/* loaded from: classes.dex */
public class ax extends ae.a {
    private final DataApi.DataListener avM;
    private final MessageApi.MessageListener avN;
    private final NodeApi.NodeListener avO;
    private final IntentFilter[] avP;

    public ax(DataApi.DataListener dataListener, MessageApi.MessageListener messageListener, NodeApi.NodeListener nodeListener, IntentFilter[] intentFilterArr) {
        this.avM = dataListener;
        this.avN = messageListener;
        this.avO = nodeListener;
        this.avP = intentFilterArr;
    }

    public static ax a(DataApi.DataListener dataListener, IntentFilter[] intentFilterArr) {
        return new ax(dataListener, null, null, intentFilterArr);
    }

    public static ax a(MessageApi.MessageListener messageListener, IntentFilter[] intentFilterArr) {
        return new ax(null, messageListener, null, intentFilterArr);
    }

    public static ax a(NodeApi.NodeListener nodeListener) {
        return new ax(null, null, nodeListener, null);
    }

    @Override // com.google.android.gms.wearable.internal.ae
    public void Z(DataHolder dataHolder) {
        if (this.avM != null) {
            try {
                this.avM.onDataChanged(new DataEventBuffer(dataHolder));
            } finally {
                dataHolder.close();
            }
        }
    }

    @Override // com.google.android.gms.wearable.internal.ae
    public void a(ah ahVar) {
        if (this.avN != null) {
            this.avN.onMessageReceived(ahVar);
        }
    }

    @Override // com.google.android.gms.wearable.internal.ae
    public void a(ak akVar) {
        if (this.avO != null) {
            this.avO.onPeerConnected(akVar);
        }
    }

    @Override // com.google.android.gms.wearable.internal.ae
    public void b(ak akVar) {
        if (this.avO != null) {
            this.avO.onPeerDisconnected(akVar);
        }
    }

    public IntentFilter[] pZ() {
        return this.avP;
    }
}
