package com.google.android.gms.common.api;

import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.e;
import com.google.android.gms.common.internal.n;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
final class b implements GoogleApiClient {
    private final Looper IB;
    private final e IQ;
    private final int IR;
    private ConnectionResult IT;
    private int IU;
    private volatile int IW;
    private int IY;
    final Handler Ja;
    private final List<String> Jd;
    private boolean Je;
    private final Lock IO = new ReentrantLock();
    private final Condition IP = this.IO.newCondition();
    final Queue<c<?>> IS = new LinkedList();
    private volatile int IV = 4;
    private boolean IX = false;
    private long IZ = 5000;
    private final Bundle Jb = new Bundle();
    private final Map<Api.c<?>, Api.a> Jc = new HashMap();
    private final Set<com.google.android.gms.common.api.c<?>> Jf = Collections.newSetFromMap(new WeakHashMap());
    final Set<c<?>> Jg = Collections.newSetFromMap(new ConcurrentHashMap());
    private final a Iu = new a() { // from class: com.google.android.gms.common.api.b.1
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.b.a
        public void b(c<?> cVar) {
            b.this.Jg.remove(cVar);
        }
    };
    private final GoogleApiClient.ConnectionCallbacks Jh = new GoogleApiClient.ConnectionCallbacks() { // from class: com.google.android.gms.common.api.b.2
        AnonymousClass2() {
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
        public void onConnected(Bundle connectionHint) {
            b.this.IO.lock();
            try {
                if (b.this.IV == 1) {
                    if (connectionHint != null) {
                        b.this.Jb.putAll(connectionHint);
                    }
                    b.this.gn();
                }
            } finally {
                b.this.IO.unlock();
            }
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
        public void onConnectionSuspended(int cause) {
            b.this.IO.lock();
            try {
                b.this.aj(cause);
                switch (cause) {
                    case 1:
                        if (!b.this.gp()) {
                            b.this.IW = 2;
                            b.this.Ja.sendMessageDelayed(b.this.Ja.obtainMessage(1), b.this.IZ);
                            break;
                        } else {
                            return;
                        }
                    case 2:
                        b.this.connect();
                        break;
                }
            } finally {
                b.this.IO.unlock();
            }
        }
    };
    private final e.b Ji = new e.b() { // from class: com.google.android.gms.common.api.b.3
        AnonymousClass3() {
        }

        @Override // com.google.android.gms.common.internal.e.b
        public Bundle fD() {
            return null;
        }

        @Override // com.google.android.gms.common.internal.e.b
        public boolean gr() {
            return b.this.Je;
        }

        @Override // com.google.android.gms.common.internal.e.b
        public boolean isConnected() {
            return b.this.isConnected();
        }
    };

    /* renamed from: com.google.android.gms.common.api.b$1 */
    class AnonymousClass1 implements a {
        AnonymousClass1() {
        }

        @Override // com.google.android.gms.common.api.b.a
        public void b(c<?> cVar) {
            b.this.Jg.remove(cVar);
        }
    }

    /* renamed from: com.google.android.gms.common.api.b$2 */
    class AnonymousClass2 implements GoogleApiClient.ConnectionCallbacks {
        AnonymousClass2() {
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
        public void onConnected(Bundle connectionHint) {
            b.this.IO.lock();
            try {
                if (b.this.IV == 1) {
                    if (connectionHint != null) {
                        b.this.Jb.putAll(connectionHint);
                    }
                    b.this.gn();
                }
            } finally {
                b.this.IO.unlock();
            }
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
        public void onConnectionSuspended(int cause) {
            b.this.IO.lock();
            try {
                b.this.aj(cause);
                switch (cause) {
                    case 1:
                        if (!b.this.gp()) {
                            b.this.IW = 2;
                            b.this.Ja.sendMessageDelayed(b.this.Ja.obtainMessage(1), b.this.IZ);
                            break;
                        } else {
                            return;
                        }
                    case 2:
                        b.this.connect();
                        break;
                }
            } finally {
                b.this.IO.unlock();
            }
        }
    }

    /* renamed from: com.google.android.gms.common.api.b$3 */
    class AnonymousClass3 implements e.b {
        AnonymousClass3() {
        }

        @Override // com.google.android.gms.common.internal.e.b
        public Bundle fD() {
            return null;
        }

        @Override // com.google.android.gms.common.internal.e.b
        public boolean gr() {
            return b.this.Je;
        }

        @Override // com.google.android.gms.common.internal.e.b
        public boolean isConnected() {
            return b.this.isConnected();
        }
    }

    /* renamed from: com.google.android.gms.common.api.b$4 */
    class AnonymousClass4 implements GoogleApiClient.OnConnectionFailedListener {
        final /* synthetic */ Api.b Jk;

        AnonymousClass4(Api.b bVar) {
            bVar = bVar;
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener
        public void onConnectionFailed(ConnectionResult result) {
            b.this.IO.lock();
            try {
                if (b.this.IT == null || bVar.getPriority() < b.this.IU) {
                    b.this.IT = result;
                    b.this.IU = bVar.getPriority();
                }
                b.this.gn();
            } finally {
                b.this.IO.unlock();
            }
        }
    }

    interface a {
        void b(c<?> cVar);
    }

    /* renamed from: com.google.android.gms.common.api.b$b */
    class HandlerC0003b extends Handler {
        HandlerC0003b(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what != 1) {
                Log.wtf("GoogleApiClientImpl", "Don't know how to handle this message.");
                return;
            }
            b.this.IO.lock();
            try {
                if (b.this.isConnected() || b.this.isConnecting() || !b.this.gp()) {
                    return;
                }
                b.h(b.this);
                b.this.connect();
            } finally {
                b.this.IO.unlock();
            }
        }
    }

    interface c<A extends Api.a> {
        void a(a aVar);

        void b(A a) throws DeadObjectException;

        void cancel();

        Api.c<A> gf();

        int gk();

        void m(Status status);
    }

    public b(Context context, Looper looper, ClientSettings clientSettings, Map<Api<?>, Api.ApiOptions> map, Set<GoogleApiClient.ConnectionCallbacks> set, Set<GoogleApiClient.OnConnectionFailedListener> set2, int i) {
        this.IQ = new e(context, looper, this.Ji);
        this.IB = looper;
        this.Ja = new HandlerC0003b(looper);
        this.IR = i;
        Iterator<GoogleApiClient.ConnectionCallbacks> it = set.iterator();
        while (it.hasNext()) {
            this.IQ.registerConnectionCallbacks(it.next());
        }
        Iterator<GoogleApiClient.OnConnectionFailedListener> it2 = set2.iterator();
        while (it2.hasNext()) {
            this.IQ.registerConnectionFailedListener(it2.next());
        }
        for (Api<?> api : map.keySet()) {
            Api.b<?, O> bVarGd = api.gd();
            this.Jc.put(api.gf(), a(bVarGd, map.get(api), context, looper, clientSettings, this.Jh, new GoogleApiClient.OnConnectionFailedListener() { // from class: com.google.android.gms.common.api.b.4
                final /* synthetic */ Api.b Jk;

                AnonymousClass4(Api.b bVarGd2) {
                    bVar = bVarGd2;
                }

                @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener
                public void onConnectionFailed(ConnectionResult result) {
                    b.this.IO.lock();
                    try {
                        if (b.this.IT == null || bVar.getPriority() < b.this.IU) {
                            b.this.IT = result;
                            b.this.IU = bVar.getPriority();
                        }
                        b.this.gn();
                    } finally {
                        b.this.IO.unlock();
                    }
                }
            }));
        }
        this.Jd = Collections.unmodifiableList(clientSettings.getScopes());
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static <C extends Api.a, O> C a(Api.b<C, O> bVar, Object obj, Context context, Looper looper, ClientSettings clientSettings, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        return (C) bVar.a(context, looper, clientSettings, obj, connectionCallbacks, onConnectionFailedListener);
    }

    private <A extends Api.a> void a(c<A> cVar) throws DeadObjectException {
        this.IO.lock();
        try {
            n.b(cVar.gf() != null, "This task can not be executed or enqueued (it's probably a Batch or malformed)");
            this.Jg.add(cVar);
            cVar.a(this.Iu);
            if (gp()) {
                cVar.m(new Status(8));
            } else {
                cVar.b(a(cVar.gf()));
            }
        } finally {
            this.IO.unlock();
        }
    }

    public void aj(int i) {
        this.IO.lock();
        try {
            if (this.IV != 3) {
                if (i == -1) {
                    if (isConnecting()) {
                        Iterator<c<?>> it = this.IS.iterator();
                        while (it.hasNext()) {
                            c<?> next = it.next();
                            if (next.gk() != 1) {
                                next.cancel();
                                it.remove();
                            }
                        }
                    } else {
                        this.IS.clear();
                    }
                    Iterator<c<?>> it2 = this.Jg.iterator();
                    while (it2.hasNext()) {
                        it2.next().cancel();
                    }
                    this.Jg.clear();
                    Iterator<com.google.android.gms.common.api.c<?>> it3 = this.Jf.iterator();
                    while (it3.hasNext()) {
                        it3.next().clear();
                    }
                    this.Jf.clear();
                    if (this.IT == null && !this.IS.isEmpty()) {
                        this.IX = true;
                        return;
                    }
                }
                boolean zIsConnecting = isConnecting();
                boolean zIsConnected = isConnected();
                this.IV = 3;
                if (zIsConnecting) {
                    if (i == -1) {
                        this.IT = null;
                    }
                    this.IP.signalAll();
                }
                this.Je = false;
                for (Api.a aVar : this.Jc.values()) {
                    if (aVar.isConnected()) {
                        aVar.disconnect();
                    }
                }
                this.Je = true;
                this.IV = 4;
                if (zIsConnected) {
                    if (i != -1) {
                        this.IQ.aB(i);
                    }
                    this.Je = false;
                }
            }
        } finally {
            this.IO.unlock();
        }
    }

    public void gn() {
        this.IY--;
        if (this.IY == 0) {
            if (this.IT != null) {
                this.IX = false;
                aj(3);
                if (gp()) {
                    this.Ja.sendMessageDelayed(this.Ja.obtainMessage(1), this.IZ);
                } else {
                    this.IQ.b(this.IT);
                }
                this.Je = false;
                return;
            }
            this.IV = 2;
            gq();
            this.IP.signalAll();
            go();
            if (!this.IX) {
                this.IQ.d(this.Jb.isEmpty() ? null : this.Jb);
            } else {
                this.IX = false;
                aj(-1);
            }
        }
    }

    private void go() {
        this.IO.lock();
        try {
            n.a(isConnected() || gp(), "GoogleApiClient is not connected yet.");
            while (!this.IS.isEmpty()) {
                try {
                    a(this.IS.remove());
                } catch (DeadObjectException e) {
                    Log.w("GoogleApiClientImpl", "Service died while flushing queue", e);
                }
            }
        } finally {
            this.IO.unlock();
        }
    }

    public boolean gp() {
        return this.IW != 0;
    }

    private void gq() {
        this.IO.lock();
        try {
            this.IW = 0;
            this.Ja.removeMessages(1);
        } finally {
            this.IO.unlock();
        }
    }

    static /* synthetic */ int h(b bVar) {
        int i = bVar.IW;
        bVar.IW = i - 1;
        return i;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public <C extends Api.a> C a(Api.c<C> cVar) {
        C c2 = (C) this.Jc.get(cVar);
        n.b(c2, "Appropriate Api was not requested.");
        return c2;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public <A extends Api.a, R extends Result, T extends BaseImplementation.a<R, A>> T a(T t) {
        this.IO.lock();
        try {
            t.a(new BaseImplementation.CallbackHandler<>(getLooper()));
            if (isConnected()) {
                b((b) t);
            } else {
                this.IS.add(t);
            }
            return t;
        } finally {
            this.IO.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public boolean a(Scope scope) {
        return this.Jd.contains(scope.gt());
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public <A extends Api.a, T extends BaseImplementation.a<? extends Result, A>> T b(T t) {
        n.a(isConnected() || gp(), "GoogleApiClient is not connected yet.");
        go();
        try {
            a((c) t);
        } catch (DeadObjectException e) {
            aj(1);
        }
        return t;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public ConnectionResult blockingConnect() {
        ConnectionResult connectionResult;
        n.a(Looper.myLooper() != Looper.getMainLooper(), "blockingConnect must not be called on the UI thread");
        this.IO.lock();
        try {
            connect();
            while (isConnecting()) {
                this.IP.await();
            }
            connectionResult = isConnected() ? ConnectionResult.HE : this.IT != null ? this.IT : new ConnectionResult(13, null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            connectionResult = new ConnectionResult(15, null);
        } finally {
            this.IO.unlock();
        }
        return connectionResult;
    }

    /* JADX WARN: Code restructure failed: missing block: B:48:0x002e, code lost:
    
        r0 = new com.google.android.gms.common.ConnectionResult(14, null);
     */
    @Override // com.google.android.gms.common.api.GoogleApiClient
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.google.android.gms.common.ConnectionResult blockingConnect(long r6, java.util.concurrent.TimeUnit r8) {
        /*
            r5 = this;
            android.os.Looper r0 = android.os.Looper.myLooper()
            android.os.Looper r1 = android.os.Looper.getMainLooper()
            if (r0 == r1) goto L3c
            r0 = 1
        Lb:
            java.lang.String r1 = "blockingConnect must not be called on the UI thread"
            com.google.android.gms.common.internal.n.a(r0, r1)
            java.util.concurrent.locks.Lock r0 = r5.IO
            r0.lock()
            r5.connect()     // Catch: java.lang.Throwable -> L7c
            long r0 = r8.toNanos(r6)     // Catch: java.lang.Throwable -> L7c
        L1c:
            boolean r2 = r5.isConnecting()     // Catch: java.lang.Throwable -> L7c
            if (r2 == 0) goto L54
            java.util.concurrent.locks.Condition r2 = r5.IP     // Catch: java.lang.InterruptedException -> L3e java.lang.Throwable -> L7c
            long r0 = r2.awaitNanos(r0)     // Catch: java.lang.InterruptedException -> L3e java.lang.Throwable -> L7c
            r2 = 0
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 > 0) goto L1c
            com.google.android.gms.common.ConnectionResult r0 = new com.google.android.gms.common.ConnectionResult     // Catch: java.lang.InterruptedException -> L3e java.lang.Throwable -> L7c
            r1 = 14
            r2 = 0
            r0.<init>(r1, r2)     // Catch: java.lang.InterruptedException -> L3e java.lang.Throwable -> L7c
            java.util.concurrent.locks.Lock r1 = r5.IO
            r1.unlock()
        L3b:
            return r0
        L3c:
            r0 = 0
            goto Lb
        L3e:
            r0 = move-exception
            java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch: java.lang.Throwable -> L7c
            r0.interrupt()     // Catch: java.lang.Throwable -> L7c
            com.google.android.gms.common.ConnectionResult r0 = new com.google.android.gms.common.ConnectionResult     // Catch: java.lang.Throwable -> L7c
            r1 = 15
            r2 = 0
            r0.<init>(r1, r2)     // Catch: java.lang.Throwable -> L7c
            java.util.concurrent.locks.Lock r1 = r5.IO
            r1.unlock()
            goto L3b
        L54:
            boolean r0 = r5.isConnected()     // Catch: java.lang.Throwable -> L7c
            if (r0 == 0) goto L62
            com.google.android.gms.common.ConnectionResult r0 = com.google.android.gms.common.ConnectionResult.HE     // Catch: java.lang.Throwable -> L7c
            java.util.concurrent.locks.Lock r1 = r5.IO
            r1.unlock()
            goto L3b
        L62:
            com.google.android.gms.common.ConnectionResult r0 = r5.IT     // Catch: java.lang.Throwable -> L7c
            if (r0 == 0) goto L6e
            com.google.android.gms.common.ConnectionResult r0 = r5.IT     // Catch: java.lang.Throwable -> L7c
            java.util.concurrent.locks.Lock r1 = r5.IO
            r1.unlock()
            goto L3b
        L6e:
            com.google.android.gms.common.ConnectionResult r0 = new com.google.android.gms.common.ConnectionResult     // Catch: java.lang.Throwable -> L7c
            r1 = 13
            r2 = 0
            r0.<init>(r1, r2)     // Catch: java.lang.Throwable -> L7c
            java.util.concurrent.locks.Lock r1 = r5.IO
            r1.unlock()
            goto L3b
        L7c:
            r0 = move-exception
            java.util.concurrent.locks.Lock r1 = r5.IO
            r1.unlock()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.common.api.b.blockingConnect(long, java.util.concurrent.TimeUnit):com.google.android.gms.common.ConnectionResult");
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public <L> com.google.android.gms.common.api.c<L> c(L l) {
        n.b(l, "Listener must not be null");
        this.IO.lock();
        try {
            com.google.android.gms.common.api.c<L> cVar = new com.google.android.gms.common.api.c<>(this.IB, l);
            this.Jf.add(cVar);
            return cVar;
        } finally {
            this.IO.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public void connect() {
        this.IO.lock();
        try {
            this.IX = false;
            if (isConnected() || isConnecting()) {
                return;
            }
            this.Je = true;
            this.IT = null;
            this.IV = 1;
            this.Jb.clear();
            this.IY = this.Jc.size();
            Iterator<Api.a> it = this.Jc.values().iterator();
            while (it.hasNext()) {
                it.next().connect();
            }
        } finally {
            this.IO.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public void disconnect() {
        gq();
        aj(-1);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public Looper getLooper() {
        return this.IB;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public boolean isConnected() {
        return this.IV == 2;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public boolean isConnecting() {
        return this.IV == 1;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public boolean isConnectionCallbacksRegistered(GoogleApiClient.ConnectionCallbacks listener) {
        return this.IQ.isConnectionCallbacksRegistered(listener);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public boolean isConnectionFailedListenerRegistered(GoogleApiClient.OnConnectionFailedListener listener) {
        return this.IQ.isConnectionFailedListenerRegistered(listener);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public void reconnect() {
        disconnect();
        connect();
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public void registerConnectionCallbacks(GoogleApiClient.ConnectionCallbacks listener) {
        this.IQ.registerConnectionCallbacks(listener);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public void registerConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener listener) {
        this.IQ.registerConnectionFailedListener(listener);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public void stopAutoManage(FragmentActivity lifecycleActivity) {
        n.a(this.IR >= 0, "Called stopAutoManage but automatic lifecycle management is not enabled.");
        d.a(lifecycleActivity).al(this.IR);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public void unregisterConnectionCallbacks(GoogleApiClient.ConnectionCallbacks listener) {
        this.IQ.unregisterConnectionCallbacks(listener);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public void unregisterConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener listener) {
        this.IQ.unregisterConnectionFailedListener(listener);
    }
}
