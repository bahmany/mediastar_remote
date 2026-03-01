package com.google.android.gms.analytics;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.google.android.gms.analytics.t;

/* loaded from: classes.dex */
class q extends ae {
    private static final Object yc = new Object();
    private static q yo;
    private Context mContext;
    private Handler mHandler;
    private d yd;
    private volatile f ye;
    private boolean yh;
    private String yi;
    private p ym;
    private int yf = 1800;
    private boolean yg = true;
    private boolean yj = true;
    private boolean yk = true;
    private e yl = new e() { // from class: com.google.android.gms.analytics.q.1
        @Override // com.google.android.gms.analytics.e
        public void z(boolean z) {
            q.this.a(z, q.this.yj);
        }
    };
    private boolean yn = false;

    private q() {
    }

    public static q ea() {
        if (yo == null) {
            yo = new q();
        }
        return yo;
    }

    private void eb() {
        this.ym = new p(this);
        this.ym.z(this.mContext);
    }

    private void ec() {
        this.mHandler = new Handler(this.mContext.getMainLooper(), new Handler.Callback() { // from class: com.google.android.gms.analytics.q.2
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                if (1 == msg.what && q.yc.equals(msg.obj)) {
                    t.eq().B(true);
                    q.this.dispatchLocalHits();
                    t.eq().B(false);
                    if (q.this.yf > 0 && !q.this.yn) {
                        q.this.mHandler.sendMessageDelayed(q.this.mHandler.obtainMessage(1, q.yc), q.this.yf * 1000);
                    }
                }
                return true;
            }
        });
        if (this.yf > 0) {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, yc), this.yf * 1000);
        }
    }

    @Override // com.google.android.gms.analytics.ae
    synchronized void A(boolean z) {
        a(this.yn, z);
    }

    synchronized void a(Context context, f fVar) {
        if (this.mContext == null) {
            this.mContext = context.getApplicationContext();
            if (this.ye == null) {
                this.ye = fVar;
                if (this.yg) {
                    dispatchLocalHits();
                    this.yg = false;
                }
                if (this.yh) {
                    dO();
                    this.yh = false;
                }
            }
        }
    }

    synchronized void a(boolean z, boolean z2) {
        if (this.yn != z || this.yj != z2) {
            if ((z || !z2) && this.yf > 0) {
                this.mHandler.removeMessages(1, yc);
            }
            if (!z && z2 && this.yf > 0) {
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, yc), this.yf * 1000);
            }
            z.V("PowerSaveMode " + ((z || !z2) ? "initiated." : "terminated."));
            this.yn = z;
            this.yj = z2;
        }
    }

    void dO() {
        if (this.ye == null) {
            z.V("setForceLocalDispatch() queued. It will be called once initialization is complete.");
            this.yh = true;
        } else {
            t.eq().a(t.a.SET_FORCE_LOCAL_DISPATCH);
            this.ye.dO();
        }
    }

    @Override // com.google.android.gms.analytics.ae
    synchronized void dispatchLocalHits() {
        if (this.ye == null) {
            z.V("Dispatch call queued. Dispatch will run once initialization is complete.");
            this.yg = true;
        } else {
            t.eq().a(t.a.DISPATCH);
            this.ye.dispatch();
        }
    }

    synchronized d ed() {
        if (this.yd == null) {
            if (this.mContext == null) {
                throw new IllegalStateException("Cant get a store unless we have a context");
            }
            this.yd = new ab(this.yl, this.mContext);
            if (this.yi != null) {
                this.yd.dN().af(this.yi);
                this.yi = null;
            }
        }
        if (this.mHandler == null) {
            ec();
        }
        if (this.ym == null && this.yk) {
            eb();
        }
        return this.yd;
    }

    @Override // com.google.android.gms.analytics.ae
    synchronized void ee() {
        if (!this.yn && this.yj && this.yf > 0) {
            this.mHandler.removeMessages(1, yc);
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, yc));
        }
    }

    @Override // com.google.android.gms.analytics.ae
    synchronized void setLocalDispatchPeriod(int dispatchPeriodInSeconds) {
        if (this.mHandler == null) {
            z.V("Dispatch period set with null handler. Dispatch will run once initialization is complete.");
            this.yf = dispatchPeriodInSeconds;
        } else {
            t.eq().a(t.a.SET_DISPATCH_PERIOD);
            if (!this.yn && this.yj && this.yf > 0) {
                this.mHandler.removeMessages(1, yc);
            }
            this.yf = dispatchPeriodInSeconds;
            if (dispatchPeriodInSeconds > 0 && !this.yn && this.yj) {
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, yc), dispatchPeriodInSeconds * 1000);
            }
        }
    }
}
