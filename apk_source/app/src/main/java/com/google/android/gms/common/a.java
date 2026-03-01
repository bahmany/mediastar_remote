package com.google.android.gms.common;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/* loaded from: classes.dex */
public class a implements ServiceConnection {
    boolean HC = false;
    private final BlockingQueue<IBinder> HD = new LinkedBlockingQueue();

    public IBinder fX() throws InterruptedException {
        if (this.HC) {
            throw new IllegalStateException();
        }
        this.HC = true;
        return this.HD.take();
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.HD.add(service);
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
    }
}
