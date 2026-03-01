package com.google.android.gms.wearable;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.internal.ae;
import com.google.android.gms.wearable.internal.ah;
import com.google.android.gms.wearable.internal.ak;

/* loaded from: classes.dex */
public abstract class WearableListenerService extends Service implements DataApi.DataListener, MessageApi.MessageListener, NodeApi.NodeListener {
    public static final String BIND_LISTENER_INTENT_ACTION = "com.google.android.gms.wearable.BIND_LISTENER";
    private String BZ;
    private IBinder LR;
    private Handler auR;
    private boolean auT;
    private volatile int NP = -1;
    private Object auS = new Object();

    private class a extends ae.a {
        private a() {
        }

        @Override // com.google.android.gms.wearable.internal.ae
        public void Z(final DataHolder dataHolder) throws SecurityException {
            if (Log.isLoggable("WearableLS", 3)) {
                Log.d("WearableLS", "onDataItemChanged: " + WearableListenerService.this.BZ + ": " + dataHolder);
            }
            WearableListenerService.this.pS();
            synchronized (WearableListenerService.this.auS) {
                if (WearableListenerService.this.auT) {
                    dataHolder.close();
                } else {
                    WearableListenerService.this.auR.post(new Runnable() { // from class: com.google.android.gms.wearable.WearableListenerService.a.1
                        @Override // java.lang.Runnable
                        public void run() {
                            DataEventBuffer dataEventBuffer = new DataEventBuffer(dataHolder);
                            try {
                                WearableListenerService.this.onDataChanged(dataEventBuffer);
                            } finally {
                                dataEventBuffer.release();
                            }
                        }
                    });
                }
            }
        }

        @Override // com.google.android.gms.wearable.internal.ae
        public void a(final ah ahVar) throws SecurityException {
            if (Log.isLoggable("WearableLS", 3)) {
                Log.d("WearableLS", "onMessageReceived: " + ahVar);
            }
            WearableListenerService.this.pS();
            synchronized (WearableListenerService.this.auS) {
                if (WearableListenerService.this.auT) {
                    return;
                }
                WearableListenerService.this.auR.post(new Runnable() { // from class: com.google.android.gms.wearable.WearableListenerService.a.2
                    @Override // java.lang.Runnable
                    public void run() {
                        WearableListenerService.this.onMessageReceived(ahVar);
                    }
                });
            }
        }

        @Override // com.google.android.gms.wearable.internal.ae
        public void a(final ak akVar) throws SecurityException {
            if (Log.isLoggable("WearableLS", 3)) {
                Log.d("WearableLS", "onPeerConnected: " + WearableListenerService.this.BZ + ": " + akVar);
            }
            WearableListenerService.this.pS();
            synchronized (WearableListenerService.this.auS) {
                if (WearableListenerService.this.auT) {
                    return;
                }
                WearableListenerService.this.auR.post(new Runnable() { // from class: com.google.android.gms.wearable.WearableListenerService.a.3
                    @Override // java.lang.Runnable
                    public void run() {
                        WearableListenerService.this.onPeerConnected(akVar);
                    }
                });
            }
        }

        @Override // com.google.android.gms.wearable.internal.ae
        public void b(final ak akVar) throws SecurityException {
            if (Log.isLoggable("WearableLS", 3)) {
                Log.d("WearableLS", "onPeerDisconnected: " + WearableListenerService.this.BZ + ": " + akVar);
            }
            WearableListenerService.this.pS();
            synchronized (WearableListenerService.this.auS) {
                if (WearableListenerService.this.auT) {
                    return;
                }
                WearableListenerService.this.auR.post(new Runnable() { // from class: com.google.android.gms.wearable.WearableListenerService.a.4
                    @Override // java.lang.Runnable
                    public void run() {
                        WearableListenerService.this.onPeerDisconnected(akVar);
                    }
                });
            }
        }
    }

    private boolean bc(int i) {
        String[] packagesForUid = getPackageManager().getPackagesForUid(i);
        if (packagesForUid == null) {
            return false;
        }
        for (String str : packagesForUid) {
            if (GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pS() throws SecurityException {
        int callingUid = Binder.getCallingUid();
        if (callingUid == this.NP) {
            return;
        }
        if (!GooglePlayServicesUtil.b(getPackageManager(), GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE) || !bc(callingUid)) {
            throw new SecurityException("Caller is not GooglePlayServices");
        }
        this.NP = callingUid;
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (BIND_LISTENER_INTENT_ACTION.equals(intent.getAction())) {
            return this.LR;
        }
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        if (Log.isLoggable("WearableLS", 3)) {
            Log.d("WearableLS", "onCreate: " + getPackageName());
        }
        this.BZ = getPackageName();
        HandlerThread handlerThread = new HandlerThread("WearableListenerService");
        handlerThread.start();
        this.auR = new Handler(handlerThread.getLooper());
        this.LR = new a();
    }

    @Override // com.google.android.gms.wearable.DataApi.DataListener
    public void onDataChanged(DataEventBuffer dataEvents) {
    }

    @Override // android.app.Service
    public void onDestroy() {
        synchronized (this.auS) {
            this.auT = true;
            this.auR.getLooper().quit();
        }
        super.onDestroy();
    }

    @Override // com.google.android.gms.wearable.MessageApi.MessageListener
    public void onMessageReceived(MessageEvent messageEvent) {
    }

    @Override // com.google.android.gms.wearable.NodeApi.NodeListener
    public void onPeerConnected(Node peer) {
    }

    @Override // com.google.android.gms.wearable.NodeApi.NodeListener
    public void onPeerDisconnected(Node peer) {
    }
}
