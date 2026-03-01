package com.google.android.gms.drive.events;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.drive.internal.OnEventResponse;
import com.google.android.gms.drive.internal.ad;
import com.google.android.gms.drive.internal.v;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public abstract class DriveEventService extends Service implements ChangeListener, CompletionListener {
    public static final String ACTION_HANDLE_EVENT = "com.google.android.gms.drive.events.HANDLE_EVENT";
    private CountDownLatch NN;
    a NO;
    int NP;
    private final String mName;

    final class a extends Handler {
        a() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Message b(OnEventResponse onEventResponse) {
            return obtainMessage(1, onEventResponse);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Message hW() {
            return obtainMessage(2);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            v.n("DriveEventService", "handleMessage message type:" + msg.what);
            switch (msg.what) {
                case 1:
                    DriveEventService.this.a((OnEventResponse) msg.obj);
                    break;
                case 2:
                    getLooper().quit();
                    break;
                default:
                    v.p("DriveEventService", "Unexpected message type:" + msg.what);
                    break;
            }
        }
    }

    final class b extends ad.a {
        b() {
        }

        @Override // com.google.android.gms.drive.internal.ad
        public void c(OnEventResponse onEventResponse) throws RemoteException {
            synchronized (DriveEventService.this) {
                v.n("DriveEventService", "onEvent: " + onEventResponse);
                n.i(DriveEventService.this.NO);
                DriveEventService.this.hV();
                DriveEventService.this.NO.sendMessage(DriveEventService.this.NO.b(onEventResponse));
            }
        }
    }

    protected DriveEventService() {
        this("DriveEventService");
    }

    protected DriveEventService(String name) {
        this.NP = -1;
        this.mName = name;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(OnEventResponse onEventResponse) {
        DriveEvent driveEventIh = onEventResponse.ih();
        v.n("DriveEventService", "handleEventMessage: " + driveEventIh);
        try {
            switch (driveEventIh.getType()) {
                case 1:
                    onChange((ChangeEvent) driveEventIh);
                    break;
                case 2:
                    onCompletion((CompletionEvent) driveEventIh);
                    break;
                default:
                    v.p(this.mName, "Unhandled event: " + driveEventIh);
                    break;
            }
        } catch (Exception e) {
            v.a(this.mName, e, "Error handling event: " + driveEventIh);
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
    public void hV() throws SecurityException {
        int callingUid = getCallingUid();
        if (callingUid == this.NP) {
            return;
        }
        if (!GooglePlayServicesUtil.b(getPackageManager(), GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE) || !bc(callingUid)) {
            throw new SecurityException("Caller is not GooglePlayServices");
        }
        this.NP = callingUid;
    }

    protected int getCallingUid() {
        return Binder.getCallingUid();
    }

    /* JADX WARN: Type inference failed for: r1v3, types: [com.google.android.gms.drive.events.DriveEventService$1] */
    @Override // android.app.Service
    public final synchronized IBinder onBind(Intent intent) {
        IBinder iBinderAsBinder;
        if (ACTION_HANDLE_EVENT.equals(intent.getAction())) {
            if (this.NO == null) {
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                this.NN = new CountDownLatch(1);
                new Thread() { // from class: com.google.android.gms.drive.events.DriveEventService.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            Looper.prepare();
                            DriveEventService.this.NO = DriveEventService.this.new a();
                            countDownLatch.countDown();
                            v.n("DriveEventService", "Bound and starting loop");
                            Looper.loop();
                            v.n("DriveEventService", "Finished loop");
                        } finally {
                            DriveEventService.this.NN.countDown();
                        }
                    }
                }.start();
                try {
                    countDownLatch.await(5000L, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Unable to start event handler", e);
                }
            }
            iBinderAsBinder = new b().asBinder();
        } else {
            iBinderAsBinder = null;
        }
        return iBinderAsBinder;
    }

    @Override // com.google.android.gms.drive.events.ChangeListener
    public void onChange(ChangeEvent event) {
        v.p(this.mName, "Unhandled change event: " + event);
    }

    @Override // com.google.android.gms.drive.events.CompletionListener
    public void onCompletion(CompletionEvent event) {
        v.p(this.mName, "Unhandled completion event: " + event);
    }

    @Override // android.app.Service
    public synchronized void onDestroy() {
        v.n("DriveEventService", "onDestroy");
        if (this.NO != null) {
            this.NO.sendMessage(this.NO.hW());
            this.NO = null;
            try {
                this.NN.await(5000L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
            }
            this.NN = null;
        }
        super.onDestroy();
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        return true;
    }
}
