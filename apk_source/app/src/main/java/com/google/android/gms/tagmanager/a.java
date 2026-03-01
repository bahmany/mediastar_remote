package com.google.android.gms.tagmanager;

import android.content.Context;
import android.os.Process;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.internal.ju;
import com.google.android.gms.internal.jw;
import java.io.IOException;

/* loaded from: classes.dex */
class a {
    private static a anF;
    private static Object xz = new Object();
    private volatile long anB;
    private volatile long anC;
    private volatile long anD;
    private InterfaceC0141a anE;
    private volatile boolean mClosed;
    private final Context mContext;
    private final Thread wf;
    private volatile AdvertisingIdClient.Info xB;
    private final ju yD;

    /* renamed from: com.google.android.gms.tagmanager.a$a, reason: collision with other inner class name */
    public interface InterfaceC0141a {
        AdvertisingIdClient.Info nK();
    }

    private a(Context context) {
        this(context, null, jw.hA());
    }

    a(Context context, InterfaceC0141a interfaceC0141a, ju juVar) {
        this.anB = 900000L;
        this.anC = 30000L;
        this.mClosed = false;
        this.anE = new InterfaceC0141a() { // from class: com.google.android.gms.tagmanager.a.1
            @Override // com.google.android.gms.tagmanager.a.InterfaceC0141a
            public AdvertisingIdClient.Info nK() {
                try {
                    return AdvertisingIdClient.getAdvertisingIdInfo(a.this.mContext);
                } catch (GooglePlayServicesNotAvailableException e) {
                    bh.W("GooglePlayServicesNotAvailableException getting Advertising Id Info");
                    return null;
                } catch (GooglePlayServicesRepairableException e2) {
                    bh.W("GooglePlayServicesRepairableException getting Advertising Id Info");
                    return null;
                } catch (IOException e3) {
                    bh.W("IOException getting Ad Id Info");
                    return null;
                } catch (IllegalStateException e4) {
                    bh.W("IllegalStateException getting Advertising Id Info");
                    return null;
                } catch (Exception e5) {
                    bh.W("Unknown exception. Could not get the Advertising Id Info.");
                    return null;
                }
            }
        };
        this.yD = juVar;
        if (context != null) {
            this.mContext = context.getApplicationContext();
        } else {
            this.mContext = context;
        }
        if (interfaceC0141a != null) {
            this.anE = interfaceC0141a;
        }
        this.wf = new Thread(new Runnable() { // from class: com.google.android.gms.tagmanager.a.2
            @Override // java.lang.Runnable
            public void run() throws InterruptedException, SecurityException, IllegalArgumentException {
                a.this.nI();
            }
        });
    }

    static a V(Context context) {
        if (anF == null) {
            synchronized (xz) {
                if (anF == null) {
                    anF = new a(context);
                    anF.start();
                }
            }
        }
        return anF;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void nI() throws InterruptedException, SecurityException, IllegalArgumentException {
        Process.setThreadPriority(10);
        while (!this.mClosed) {
            try {
                this.xB = this.anE.nK();
                Thread.sleep(this.anB);
            } catch (InterruptedException e) {
                bh.U("sleep interrupted in AdvertiserDataPoller thread; continuing");
            }
        }
    }

    private void nJ() {
        if (this.yD.currentTimeMillis() - this.anD < this.anC) {
            return;
        }
        interrupt();
        this.anD = this.yD.currentTimeMillis();
    }

    void interrupt() {
        this.wf.interrupt();
    }

    public boolean isLimitAdTrackingEnabled() {
        nJ();
        if (this.xB == null) {
            return true;
        }
        return this.xB.isLimitAdTrackingEnabled();
    }

    public String nH() {
        nJ();
        if (this.xB == null) {
            return null;
        }
        return this.xB.getId();
    }

    void start() {
        this.wf.start();
    }
}
