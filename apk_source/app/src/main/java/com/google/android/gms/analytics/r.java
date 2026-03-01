package com.google.android.gms.analytics;

import android.content.Context;
import android.content.Intent;
import com.google.android.gms.analytics.c;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.hb;
import com.google.android.gms.internal.ju;
import com.google.android.gms.internal.jw;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

/* loaded from: classes.dex */
class r implements af, c.b, c.InterfaceC0001c {
    private final Context mContext;
    private boolean yA;
    private boolean yB;
    private boolean yC;
    private ju yD;
    private long yE;
    private com.google.android.gms.analytics.d yd;
    private final f ye;
    private boolean yg;
    private volatile long yq;
    private volatile a yr;
    private volatile com.google.android.gms.analytics.b ys;
    private com.google.android.gms.analytics.d yt;
    private final GoogleAnalytics yu;
    private final Queue<d> yv;
    private volatile int yw;
    private volatile Timer yx;
    private volatile Timer yy;
    private volatile Timer yz;

    private enum a {
        CONNECTING,
        CONNECTED_SERVICE,
        CONNECTED_LOCAL,
        BLOCKED,
        PENDING_CONNECTION,
        PENDING_DISCONNECT,
        DISCONNECTED
    }

    private class b extends TimerTask {
        private b() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            if (r.this.yr != a.CONNECTED_SERVICE || !r.this.yv.isEmpty() || r.this.yq + r.this.yE >= r.this.yD.elapsedRealtime()) {
                r.this.yz.schedule(r.this.new b(), r.this.yE);
            } else {
                z.V("Disconnecting due to inactivity");
                r.this.cD();
            }
        }
    }

    private class c extends TimerTask {
        private c() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            if (r.this.yr == a.CONNECTING) {
                r.this.ek();
            }
        }
    }

    private static class d {
        private final Map<String, String> yP;
        private final long yQ;
        private final String yR;
        private final List<hb> yS;

        public d(Map<String, String> map, long j, String str, List<hb> list) {
            this.yP = map;
            this.yQ = j;
            this.yR = str;
            this.yS = list;
        }

        public Map<String, String> en() {
            return this.yP;
        }

        public long eo() {
            return this.yQ;
        }

        public List<hb> ep() {
            return this.yS;
        }

        public String getPath() {
            return this.yR;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("PATH: ");
            sb.append(this.yR);
            if (this.yP != null) {
                sb.append("  PARAMS: ");
                for (Map.Entry<String, String> entry : this.yP.entrySet()) {
                    sb.append(entry.getKey());
                    sb.append("=");
                    sb.append(entry.getValue());
                    sb.append(",  ");
                }
            }
            return sb.toString();
        }
    }

    private class e extends TimerTask {
        private e() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            r.this.el();
        }
    }

    r(Context context, f fVar) {
        this(context, fVar, null, GoogleAnalytics.getInstance(context));
    }

    r(Context context, f fVar, com.google.android.gms.analytics.d dVar, GoogleAnalytics googleAnalytics) {
        this.yv = new ConcurrentLinkedQueue();
        this.yE = 300000L;
        this.yt = dVar;
        this.mContext = context;
        this.ye = fVar;
        this.yu = googleAnalytics;
        this.yD = jw.hA();
        this.yw = 0;
        this.yr = a.DISCONNECTED;
    }

    private Timer a(Timer timer) {
        if (timer == null) {
            return null;
        }
        timer.cancel();
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void cD() {
        if (this.ys != null && this.yr == a.CONNECTED_SERVICE) {
            this.yr = a.PENDING_DISCONNECT;
            this.ys.disconnect();
        }
    }

    private void eg() {
        this.yx = a(this.yx);
        this.yy = a(this.yy);
        this.yz = a(this.yz);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public synchronized void ei() {
        if (Thread.currentThread().equals(this.ye.getThread())) {
            if (this.yA) {
                dI();
            }
            switch (this.yr) {
                case CONNECTED_LOCAL:
                    while (!this.yv.isEmpty()) {
                        d dVarPoll = this.yv.poll();
                        z.V("Sending hit to store  " + dVarPoll);
                        this.yd.a(dVarPoll.en(), dVarPoll.eo(), dVarPoll.getPath(), dVarPoll.ep());
                    }
                    if (this.yg) {
                        ej();
                        break;
                    }
                    break;
                case CONNECTED_SERVICE:
                    while (!this.yv.isEmpty()) {
                        d dVarPeek = this.yv.peek();
                        z.V("Sending hit to service   " + dVarPeek);
                        if (this.yu.isDryRunEnabled()) {
                            z.V("Dry run enabled. Hit not actually sent to service.");
                        } else {
                            this.ys.a(dVarPeek.en(), dVarPeek.eo(), dVarPeek.getPath(), dVarPeek.ep());
                        }
                        this.yv.poll();
                    }
                    this.yq = this.yD.elapsedRealtime();
                    break;
                case DISCONNECTED:
                    z.V("Need to reconnect");
                    if (!this.yv.isEmpty()) {
                        el();
                        break;
                    }
                    break;
                case BLOCKED:
                    z.V("Blocked. Dropping hits.");
                    this.yv.clear();
                    break;
            }
        } else {
            this.ye.dP().add(new Runnable() { // from class: com.google.android.gms.analytics.r.1
                @Override // java.lang.Runnable
                public void run() {
                    r.this.ei();
                }
            });
        }
    }

    private void ej() {
        this.yd.dispatch();
        this.yg = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void ek() {
        if (this.yr != a.CONNECTED_LOCAL) {
            if (this.mContext == null || !GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE.equals(this.mContext.getPackageName())) {
                eg();
                z.V("falling back to local store");
                if (this.yt != null) {
                    this.yd = this.yt;
                } else {
                    q qVarEa = q.ea();
                    qVarEa.a(this.mContext, this.ye);
                    this.yd = qVarEa.ed();
                }
                this.yr = a.CONNECTED_LOCAL;
                ei();
            } else {
                this.yr = a.BLOCKED;
                this.ys.disconnect();
                z.W("Attempted to fall back to local store from service.");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void el() {
        if (this.yC || this.ys == null || this.yr == a.CONNECTED_LOCAL) {
            z.W("client not initialized.");
            ek();
        } else {
            try {
                this.yw++;
                a(this.yy);
                this.yr = a.CONNECTING;
                this.yy = new Timer("Failed Connect");
                this.yy.schedule(new c(), 3000L);
                z.V("connecting to Analytics service");
                this.ys.connect();
            } catch (SecurityException e2) {
                z.W("security exception on connectToService");
                ek();
            }
        }
    }

    private void em() {
        this.yx = a(this.yx);
        this.yx = new Timer("Service Reconnect");
        this.yx.schedule(new e(), 5000L);
    }

    @Override // com.google.android.gms.analytics.c.InterfaceC0001c
    public synchronized void a(int i, Intent intent) {
        this.yr = a.PENDING_CONNECTION;
        if (this.yw < 2) {
            z.W("Service unavailable (code=" + i + "), will retry.");
            em();
        } else {
            z.W("Service unavailable (code=" + i + "), using local store.");
            ek();
        }
    }

    @Override // com.google.android.gms.analytics.af
    public void b(Map<String, String> map, long j, String str, List<hb> list) {
        z.V("putHit called");
        this.yv.add(new d(map, j, str, list));
        ei();
    }

    @Override // com.google.android.gms.analytics.af
    public void dI() {
        z.V("clearHits called");
        this.yv.clear();
        switch (this.yr) {
            case CONNECTED_LOCAL:
                this.yd.l(0L);
                this.yA = false;
                break;
            case CONNECTED_SERVICE:
                this.ys.dI();
                this.yA = false;
                break;
            default:
                this.yA = true;
                break;
        }
    }

    @Override // com.google.android.gms.analytics.af
    public synchronized void dO() {
        if (!this.yC) {
            z.V("setForceLocalDispatch called.");
            this.yC = true;
            switch (this.yr) {
                case CONNECTED_SERVICE:
                    cD();
                    break;
                case CONNECTING:
                    this.yB = true;
                    break;
            }
        }
    }

    @Override // com.google.android.gms.analytics.af
    public void dispatch() {
        switch (this.yr) {
            case CONNECTED_LOCAL:
                ej();
                break;
            case CONNECTED_SERVICE:
                break;
            default:
                this.yg = true;
                break;
        }
    }

    @Override // com.google.android.gms.analytics.af
    public void eh() {
        if (this.ys != null) {
            return;
        }
        this.ys = new com.google.android.gms.analytics.c(this.mContext, this, this);
        el();
    }

    @Override // com.google.android.gms.analytics.c.b
    public synchronized void onConnected() {
        this.yy = a(this.yy);
        this.yw = 0;
        z.V("Connected to service");
        this.yr = a.CONNECTED_SERVICE;
        if (this.yB) {
            cD();
            this.yB = false;
        } else {
            ei();
            this.yz = a(this.yz);
            this.yz = new Timer("disconnect check");
            this.yz.schedule(new b(), this.yE);
        }
    }

    @Override // com.google.android.gms.analytics.c.b
    public synchronized void onDisconnected() {
        if (this.yr == a.BLOCKED) {
            z.V("Service blocked.");
            eg();
        } else if (this.yr == a.PENDING_DISCONNECT) {
            z.V("Disconnected from service");
            eg();
            this.yr = a.DISCONNECTED;
        } else {
            z.V("Unexpected disconnect.");
            this.yr = a.PENDING_CONNECTION;
            if (this.yw < 2) {
                em();
            } else {
                ek();
            }
        }
    }
}
