package com.google.android.gms.tagmanager;

import android.content.Context;
import com.google.android.gms.internal.c;
import com.google.android.gms.tagmanager.o;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
class cp implements o.e {
    private final String anR;
    private String aon;
    private bg<c.j> aqi;
    private r aqj;
    private final ScheduledExecutorService aql;
    private final a aqm;
    private ScheduledFuture<?> aqn;
    private boolean mClosed;
    private final Context mContext;

    interface a {
        co a(r rVar);
    }

    interface b {
        ScheduledExecutorService oO();
    }

    public cp(Context context, String str, r rVar) {
        this(context, str, rVar, null, null);
    }

    cp(Context context, String str, r rVar, b bVar, a aVar) {
        this.aqj = rVar;
        this.mContext = context;
        this.anR = str;
        this.aql = (bVar == null ? new b() { // from class: com.google.android.gms.tagmanager.cp.1
            @Override // com.google.android.gms.tagmanager.cp.b
            public ScheduledExecutorService oO() {
                return Executors.newSingleThreadScheduledExecutor();
            }
        } : bVar).oO();
        if (aVar == null) {
            this.aqm = new a() { // from class: com.google.android.gms.tagmanager.cp.2
                @Override // com.google.android.gms.tagmanager.cp.a
                public co a(r rVar2) {
                    return new co(cp.this.mContext, cp.this.anR, rVar2);
                }
            };
        } else {
            this.aqm = aVar;
        }
    }

    private co cH(String str) {
        co coVarA = this.aqm.a(this.aqj);
        coVarA.a(this.aqi);
        coVarA.cr(this.aon);
        coVarA.cG(str);
        return coVarA;
    }

    private synchronized void oN() {
        if (this.mClosed) {
            throw new IllegalStateException("called method after closed");
        }
    }

    @Override // com.google.android.gms.tagmanager.o.e
    public synchronized void a(bg<c.j> bgVar) {
        oN();
        this.aqi = bgVar;
    }

    @Override // com.google.android.gms.tagmanager.o.e
    public synchronized void cr(String str) {
        oN();
        this.aon = str;
    }

    @Override // com.google.android.gms.tagmanager.o.e
    public synchronized void e(long j, String str) {
        bh.V("loadAfterDelay: containerId=" + this.anR + " delay=" + j);
        oN();
        if (this.aqi == null) {
            throw new IllegalStateException("callback must be set before loadAfterDelay() is called.");
        }
        if (this.aqn != null) {
            this.aqn.cancel(false);
        }
        this.aqn = this.aql.schedule(cH(str), j, TimeUnit.MILLISECONDS);
    }

    @Override // com.google.android.gms.common.api.Releasable
    public synchronized void release() {
        oN();
        if (this.aqn != null) {
            this.aqn.cancel(false);
        }
        this.aql.shutdown();
        this.mClosed = true;
    }
}
