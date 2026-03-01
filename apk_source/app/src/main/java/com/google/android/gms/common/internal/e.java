package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class e {
    private final b LE;
    private final ArrayList<GoogleApiClient.ConnectionCallbacks> LF = new ArrayList<>();
    final ArrayList<GoogleApiClient.ConnectionCallbacks> LG = new ArrayList<>();
    private boolean LH = false;
    private final ArrayList<GooglePlayServicesClient.OnConnectionFailedListener> LI = new ArrayList<>();
    private final Handler mHandler;

    final class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what != 1) {
                Log.wtf("GmsClientEvents", "Don't know how to handle this message.");
                return;
            }
            synchronized (e.this.LF) {
                if (e.this.LE.gr() && e.this.LE.isConnected() && e.this.LF.contains(msg.obj)) {
                    ((GoogleApiClient.ConnectionCallbacks) msg.obj).onConnected(e.this.LE.fD());
                }
            }
        }
    }

    public interface b {
        Bundle fD();

        boolean gr();

        boolean isConnected();
    }

    public e(Context context, Looper looper, b bVar) {
        this.LE = bVar;
        this.mHandler = new a(looper);
    }

    public void aB(int i) {
        this.mHandler.removeMessages(1);
        synchronized (this.LF) {
            this.LH = true;
            Iterator it = new ArrayList(this.LF).iterator();
            while (it.hasNext()) {
                GoogleApiClient.ConnectionCallbacks connectionCallbacks = (GoogleApiClient.ConnectionCallbacks) it.next();
                if (!this.LE.gr()) {
                    break;
                } else if (this.LF.contains(connectionCallbacks)) {
                    connectionCallbacks.onConnectionSuspended(i);
                }
            }
            this.LH = false;
        }
    }

    public void b(ConnectionResult connectionResult) {
        this.mHandler.removeMessages(1);
        synchronized (this.LI) {
            Iterator it = new ArrayList(this.LI).iterator();
            while (it.hasNext()) {
                GooglePlayServicesClient.OnConnectionFailedListener onConnectionFailedListener = (GooglePlayServicesClient.OnConnectionFailedListener) it.next();
                if (!this.LE.gr()) {
                    return;
                }
                if (this.LI.contains(onConnectionFailedListener)) {
                    onConnectionFailedListener.onConnectionFailed(connectionResult);
                }
            }
        }
    }

    public void d(Bundle bundle) {
        synchronized (this.LF) {
            n.I(!this.LH);
            this.mHandler.removeMessages(1);
            this.LH = true;
            n.I(this.LG.size() == 0);
            Iterator it = new ArrayList(this.LF).iterator();
            while (it.hasNext()) {
                GoogleApiClient.ConnectionCallbacks connectionCallbacks = (GoogleApiClient.ConnectionCallbacks) it.next();
                if (!this.LE.gr() || !this.LE.isConnected()) {
                    break;
                } else if (!this.LG.contains(connectionCallbacks)) {
                    connectionCallbacks.onConnected(bundle);
                }
            }
            this.LG.clear();
            this.LH = false;
        }
    }

    protected void dM() {
        synchronized (this.LF) {
            d(this.LE.fD());
        }
    }

    public boolean isConnectionCallbacksRegistered(GoogleApiClient.ConnectionCallbacks listener) {
        boolean zContains;
        n.i(listener);
        synchronized (this.LF) {
            zContains = this.LF.contains(listener);
        }
        return zContains;
    }

    public boolean isConnectionFailedListenerRegistered(GooglePlayServicesClient.OnConnectionFailedListener listener) {
        boolean zContains;
        n.i(listener);
        synchronized (this.LI) {
            zContains = this.LI.contains(listener);
        }
        return zContains;
    }

    public void registerConnectionCallbacks(GoogleApiClient.ConnectionCallbacks listener) {
        n.i(listener);
        synchronized (this.LF) {
            if (this.LF.contains(listener)) {
                Log.w("GmsClientEvents", "registerConnectionCallbacks(): listener " + listener + " is already registered");
            } else {
                this.LF.add(listener);
            }
        }
        if (this.LE.isConnected()) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, listener));
        }
    }

    public void registerConnectionFailedListener(GooglePlayServicesClient.OnConnectionFailedListener listener) {
        n.i(listener);
        synchronized (this.LI) {
            if (this.LI.contains(listener)) {
                Log.w("GmsClientEvents", "registerConnectionFailedListener(): listener " + listener + " is already registered");
            } else {
                this.LI.add(listener);
            }
        }
    }

    public void unregisterConnectionCallbacks(GoogleApiClient.ConnectionCallbacks listener) {
        n.i(listener);
        synchronized (this.LF) {
            if (this.LF != null) {
                if (!this.LF.remove(listener)) {
                    Log.w("GmsClientEvents", "unregisterConnectionCallbacks(): listener " + listener + " not found");
                } else if (this.LH) {
                    this.LG.add(listener);
                }
            }
        }
    }

    public void unregisterConnectionFailedListener(GooglePlayServicesClient.OnConnectionFailedListener listener) {
        n.i(listener);
        synchronized (this.LI) {
            if (this.LI != null && !this.LI.remove(listener)) {
                Log.w("GmsClientEvents", "unregisterConnectionFailedListener(): listener " + listener + " not found");
            }
        }
    }
}
