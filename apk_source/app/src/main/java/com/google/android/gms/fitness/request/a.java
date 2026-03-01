package com.google.android.gms.fitness.request;

import android.os.RemoteException;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.request.k;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class a extends k.a {
    private final BleScanCallback TU;

    /* renamed from: com.google.android.gms.fitness.request.a$a */
    public static class C0033a {
        private static final C0033a TV = new C0033a();
        private final Map<BleScanCallback, a> TW = new HashMap();

        private C0033a() {
        }

        public static C0033a iV() {
            return TV;
        }

        public a a(BleScanCallback bleScanCallback) {
            a aVar;
            synchronized (this.TW) {
                aVar = this.TW.get(bleScanCallback);
                if (aVar == null) {
                    aVar = new a(bleScanCallback);
                    this.TW.put(bleScanCallback, aVar);
                }
            }
            return aVar;
        }

        public a b(BleScanCallback bleScanCallback) {
            a aVar;
            synchronized (this.TW) {
                aVar = this.TW.get(bleScanCallback);
                if (aVar == null) {
                    aVar = new a(bleScanCallback);
                }
            }
            return aVar;
        }
    }

    private a(BleScanCallback bleScanCallback) {
        this.TU = (BleScanCallback) com.google.android.gms.common.internal.n.i(bleScanCallback);
    }

    /* synthetic */ a(BleScanCallback bleScanCallback, AnonymousClass1 anonymousClass1) {
        this(bleScanCallback);
    }

    @Override // com.google.android.gms.fitness.request.k
    public void onDeviceFound(BleDevice device) throws RemoteException {
        this.TU.onDeviceFound(device);
    }

    @Override // com.google.android.gms.fitness.request.k
    public void onScanStopped() throws RemoteException {
        this.TU.onScanStopped();
    }
}
