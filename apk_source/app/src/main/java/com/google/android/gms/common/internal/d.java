package com.google.android.gms.common.internal;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.e;
import com.google.android.gms.common.internal.j;
import com.google.android.gms.common.internal.k;
import java.util.ArrayList;

/* loaded from: classes.dex */
public abstract class d<T extends IInterface> implements Api.a, e.b {
    public static final String[] Lw = {"service_esmobile", "service_googleme"};
    private final String[] Ds;
    private final Looper IB;
    private final com.google.android.gms.common.internal.e IQ;
    private T Lr;
    private final ArrayList<d<T>.b<?>> Ls;
    private d<T>.f Lt;
    private volatile int Lu;
    boolean Lv;
    private final Context mContext;
    final Handler mHandler;

    final class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1 && !d.this.isConnecting()) {
                b bVar = (b) msg.obj;
                bVar.gT();
                bVar.unregister();
                return;
            }
            if (msg.what == 3) {
                d.this.IQ.b(new ConnectionResult(((Integer) msg.obj).intValue(), null));
                return;
            }
            if (msg.what == 4) {
                d.this.az(1);
                d.this.Lr = null;
                d.this.IQ.aB(((Integer) msg.obj).intValue());
            } else if (msg.what == 2 && !d.this.isConnected()) {
                b bVar2 = (b) msg.obj;
                bVar2.gT();
                bVar2.unregister();
            } else if (msg.what == 2 || msg.what == 1) {
                ((b) msg.obj).gU();
            } else {
                Log.wtf("GmsClient", "Don't know how to handle this message.");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract class b<TListener> {
        private boolean Ly = false;
        private TListener mListener;

        public b(TListener tlistener) {
            this.mListener = tlistener;
        }

        protected abstract void g(TListener tlistener);

        protected abstract void gT();

        public void gU() {
            TListener tlistener;
            synchronized (this) {
                tlistener = this.mListener;
                if (this.Ly) {
                    Log.w("GmsClient", "Callback proxy " + this + " being reused. This is not safe.");
                }
            }
            if (tlistener != null) {
                try {
                    g(tlistener);
                } catch (RuntimeException e) {
                    gT();
                    throw e;
                }
            } else {
                gT();
            }
            synchronized (this) {
                this.Ly = true;
            }
            unregister();
        }

        public void gV() {
            synchronized (this) {
                this.mListener = null;
            }
        }

        public void unregister() {
            gV();
            synchronized (d.this.Ls) {
                d.this.Ls.remove(this);
            }
        }
    }

    public static final class c implements GoogleApiClient.ConnectionCallbacks {
        private final GooglePlayServicesClient.ConnectionCallbacks Lz;

        public c(GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks) {
            this.Lz = connectionCallbacks;
        }

        public boolean equals(Object other) {
            return other instanceof c ? this.Lz.equals(((c) other).Lz) : this.Lz.equals(other);
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
        public void onConnected(Bundle connectionHint) {
            this.Lz.onConnected(connectionHint);
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
        public void onConnectionSuspended(int cause) {
            this.Lz.onDisconnected();
        }
    }

    /* renamed from: com.google.android.gms.common.internal.d$d, reason: collision with other inner class name */
    public abstract class AbstractC0005d<TListener> extends d<T>.b<TListener> {
        private final DataHolder IC;

        public AbstractC0005d(TListener tlistener, DataHolder dataHolder) {
            super(tlistener);
            this.IC = dataHolder;
        }

        protected abstract void a(TListener tlistener, DataHolder dataHolder);

        @Override // com.google.android.gms.common.internal.d.b
        protected final void g(TListener tlistener) {
            a(tlistener, this.IC);
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
            if (this.IC != null) {
                this.IC.close();
            }
        }

        @Override // com.google.android.gms.common.internal.d.b
        public /* bridge */ /* synthetic */ void gU() {
            super.gU();
        }

        @Override // com.google.android.gms.common.internal.d.b
        public /* bridge */ /* synthetic */ void gV() {
            super.gV();
        }

        @Override // com.google.android.gms.common.internal.d.b
        public /* bridge */ /* synthetic */ void unregister() {
            super.unregister();
        }
    }

    public static final class e extends j.a {
        private d LA;

        public e(d dVar) {
            this.LA = dVar;
        }

        @Override // com.google.android.gms.common.internal.j
        public void b(int i, IBinder iBinder, Bundle bundle) {
            n.b("onPostInitComplete can be called only once per call to getServiceFromBroker", (Object) this.LA);
            this.LA.a(i, iBinder, bundle);
            this.LA = null;
        }
    }

    final class f implements ServiceConnection {
        f() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName component, IBinder binder) {
            d.this.N(binder);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName component) {
            d.this.mHandler.sendMessage(d.this.mHandler.obtainMessage(4, 1));
        }
    }

    public static final class g implements GoogleApiClient.OnConnectionFailedListener {
        private final GooglePlayServicesClient.OnConnectionFailedListener LB;

        public g(GooglePlayServicesClient.OnConnectionFailedListener onConnectionFailedListener) {
            this.LB = onConnectionFailedListener;
        }

        public boolean equals(Object other) {
            return other instanceof g ? this.LB.equals(((g) other).LB) : this.LB.equals(other);
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener
        public void onConnectionFailed(ConnectionResult result) {
            this.LB.onConnectionFailed(result);
        }
    }

    protected final class h extends d<T>.b<Boolean> {
        public final Bundle LC;
        public final IBinder LD;
        public final int statusCode;

        public h(int i, IBinder iBinder, Bundle bundle) {
            super(true);
            this.statusCode = i;
            this.LD = iBinder;
            this.LC = bundle;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: b, reason: merged with bridge method [inline-methods] */
        public void g(Boolean bool) {
            if (bool == null) {
                d.this.az(1);
                return;
            }
            switch (this.statusCode) {
                case 0:
                    try {
                        if (d.this.getServiceDescriptor().equals(this.LD.getInterfaceDescriptor())) {
                            d.this.Lr = d.this.j(this.LD);
                            if (d.this.Lr != null) {
                                d.this.az(3);
                                d.this.IQ.dM();
                                return;
                            }
                        }
                    } catch (RemoteException e) {
                    }
                    com.google.android.gms.common.internal.f.J(d.this.mContext).b(d.this.getStartServiceAction(), d.this.Lt);
                    d.this.Lt = null;
                    d.this.az(1);
                    d.this.Lr = null;
                    d.this.IQ.b(new ConnectionResult(8, null));
                    return;
                case 10:
                    d.this.az(1);
                    throw new IllegalStateException("A fatal developer error has occurred. Check the logs for further information.");
                default:
                    PendingIntent pendingIntent = this.LC != null ? (PendingIntent) this.LC.getParcelable("pendingIntent") : null;
                    if (d.this.Lt != null) {
                        com.google.android.gms.common.internal.f.J(d.this.mContext).b(d.this.getStartServiceAction(), d.this.Lt);
                        d.this.Lt = null;
                    }
                    d.this.az(1);
                    d.this.Lr = null;
                    d.this.IQ.b(new ConnectionResult(this.statusCode, pendingIntent));
                    return;
            }
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    protected d(Context context, Looper looper, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener, String... strArr) {
        this.Ls = new ArrayList<>();
        this.Lu = 1;
        this.Lv = false;
        this.mContext = (Context) n.i(context);
        this.IB = (Looper) n.b(looper, "Looper must not be null");
        this.IQ = new com.google.android.gms.common.internal.e(context, looper, this);
        this.mHandler = new a(looper);
        c(strArr);
        this.Ds = strArr;
        registerConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) n.i(connectionCallbacks));
        registerConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) n.i(onConnectionFailedListener));
    }

    @Deprecated
    protected d(Context context, GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener onConnectionFailedListener, String... strArr) {
        this(context, context.getMainLooper(), new c(connectionCallbacks), new g(onConnectionFailedListener), strArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void az(int i) {
        int i2 = this.Lu;
        this.Lu = i;
        if (i2 != i) {
            if (i == 3) {
                onConnected();
            } else if (i2 == 3 && i == 1) {
                onDisconnected();
            }
        }
    }

    protected final void N(IBinder iBinder) {
        try {
            a(k.a.Q(iBinder), new e(this));
        } catch (RemoteException e2) {
            Log.w("GmsClient", "service died");
        }
    }

    protected void a(int i, IBinder iBinder, Bundle bundle) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, new h(i, iBinder, bundle)));
    }

    @Deprecated
    public final void a(d<T>.b<?> bVar) {
        synchronized (this.Ls) {
            this.Ls.add(bVar);
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(2, bVar));
    }

    protected abstract void a(k kVar, e eVar) throws RemoteException;

    public void aA(int i) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, Integer.valueOf(i)));
    }

    protected void c(String... strArr) {
    }

    @Override // com.google.android.gms.common.api.Api.a
    public void connect() throws PackageManager.NameNotFoundException {
        this.Lv = true;
        az(2);
        int iIsGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.mContext);
        if (iIsGooglePlayServicesAvailable != 0) {
            az(1);
            this.mHandler.sendMessage(this.mHandler.obtainMessage(3, Integer.valueOf(iIsGooglePlayServicesAvailable)));
            return;
        }
        if (this.Lt != null) {
            Log.e("GmsClient", "Calling connect() while still connected, missing disconnect().");
            this.Lr = null;
            com.google.android.gms.common.internal.f.J(this.mContext).b(getStartServiceAction(), this.Lt);
        }
        this.Lt = new f();
        if (com.google.android.gms.common.internal.f.J(this.mContext).a(getStartServiceAction(), this.Lt)) {
            return;
        }
        Log.e("GmsClient", "unable to connect to service: " + getStartServiceAction());
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, 9));
    }

    protected final void dK() {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
        }
    }

    @Override // com.google.android.gms.common.api.Api.a
    public void disconnect() {
        this.Lv = false;
        synchronized (this.Ls) {
            int size = this.Ls.size();
            for (int i = 0; i < size; i++) {
                this.Ls.get(i).gV();
            }
            this.Ls.clear();
        }
        az(1);
        this.Lr = null;
        if (this.Lt != null) {
            com.google.android.gms.common.internal.f.J(this.mContext).b(getStartServiceAction(), this.Lt);
            this.Lt = null;
        }
    }

    @Override // com.google.android.gms.common.internal.e.b
    public Bundle fD() {
        return null;
    }

    public final String[] gR() {
        return this.Ds;
    }

    public final T gS() {
        dK();
        return this.Lr;
    }

    public final Context getContext() {
        return this.mContext;
    }

    @Override // com.google.android.gms.common.api.Api.a
    public final Looper getLooper() {
        return this.IB;
    }

    protected abstract String getServiceDescriptor();

    protected abstract String getStartServiceAction();

    @Override // com.google.android.gms.common.internal.e.b
    public boolean gr() {
        return this.Lv;
    }

    @Override // com.google.android.gms.common.api.Api.a, com.google.android.gms.common.internal.e.b
    public boolean isConnected() {
        return this.Lu == 3;
    }

    public boolean isConnecting() {
        return this.Lu == 2;
    }

    @Deprecated
    public boolean isConnectionCallbacksRegistered(GooglePlayServicesClient.ConnectionCallbacks listener) {
        return this.IQ.isConnectionCallbacksRegistered(new c(listener));
    }

    @Deprecated
    public boolean isConnectionFailedListenerRegistered(GooglePlayServicesClient.OnConnectionFailedListener listener) {
        return this.IQ.isConnectionFailedListenerRegistered(listener);
    }

    protected abstract T j(IBinder iBinder);

    protected void onConnected() {
    }

    protected void onDisconnected() {
    }

    @Deprecated
    public void registerConnectionCallbacks(GooglePlayServicesClient.ConnectionCallbacks listener) {
        this.IQ.registerConnectionCallbacks(new c(listener));
    }

    public void registerConnectionCallbacks(GoogleApiClient.ConnectionCallbacks listener) {
        this.IQ.registerConnectionCallbacks(listener);
    }

    @Deprecated
    public void registerConnectionFailedListener(GooglePlayServicesClient.OnConnectionFailedListener listener) {
        this.IQ.registerConnectionFailedListener(listener);
    }

    public void registerConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener listener) {
        this.IQ.registerConnectionFailedListener(listener);
    }

    @Deprecated
    public void unregisterConnectionCallbacks(GooglePlayServicesClient.ConnectionCallbacks listener) {
        this.IQ.unregisterConnectionCallbacks(new c(listener));
    }

    @Deprecated
    public void unregisterConnectionFailedListener(GooglePlayServicesClient.OnConnectionFailedListener listener) {
        this.IQ.unregisterConnectionFailedListener(listener);
    }
}
