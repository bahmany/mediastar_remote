package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.internal.ff;

@ez
/* loaded from: classes.dex */
public abstract class fg extends gg {
    private final fi pQ;
    private final ff.a tu;

    @ez
    public static final class a extends fg {
        private final Context mContext;

        public a(Context context, fi fiVar, ff.a aVar) {
            super(fiVar, aVar);
            this.mContext = context;
        }

        @Override // com.google.android.gms.internal.fg
        public void cD() {
        }

        @Override // com.google.android.gms.internal.fg
        public fm cE() {
            Bundle bundleBD = gb.bD();
            return fr.a(this.mContext, new bm(bundleBD.getString("gads:sdk_core_location"), bundleBD.getString("gads:sdk_core_experiment_id"), bundleBD.getString("gads:block_autoclicks_experiment_id")), new cj(), new fy());
        }
    }

    @ez
    public static final class b extends fg implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
        private final Object mw;
        private final ff.a tu;

        /* renamed from: tv, reason: collision with root package name */
        private final fh f2tv;

        public b(Context context, fi fiVar, ff.a aVar) {
            super(fiVar, aVar);
            this.mw = new Object();
            this.tu = aVar;
            this.f2tv = new fh(context, this, this, fiVar.lD.wF);
            this.f2tv.connect();
        }

        @Override // com.google.android.gms.internal.fg
        public void cD() {
            synchronized (this.mw) {
                if (this.f2tv.isConnected() || this.f2tv.isConnecting()) {
                    this.f2tv.disconnect();
                }
            }
        }

        @Override // com.google.android.gms.internal.fg
        public fm cE() {
            fm fmVarCF;
            synchronized (this.mw) {
                try {
                    fmVarCF = this.f2tv.cF();
                } catch (IllegalStateException e) {
                    fmVarCF = null;
                }
            }
            return fmVarCF;
        }

        @Override // com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks
        public void onConnected(Bundle connectionHint) {
            start();
        }

        @Override // com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener
        public void onConnectionFailed(ConnectionResult result) {
            this.tu.a(new fk(0));
        }

        @Override // com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks
        public void onDisconnected() {
            gs.S("Disconnected from remote ad request service.");
        }
    }

    public fg(fi fiVar, ff.a aVar) {
        this.pQ = fiVar;
        this.tu = aVar;
    }

    private static fk a(fm fmVar, fi fiVar) {
        try {
            return fmVar.b(fiVar);
        } catch (RemoteException e) {
            gs.d("Could not fetch ad response from ad request service.", e);
            return null;
        } catch (NullPointerException e2) {
            gs.d("Could not fetch ad response from ad request service due to an Exception.", e2);
            return null;
        } catch (SecurityException e3) {
            gs.d("Could not fetch ad response from ad request service due to an Exception.", e3);
            return null;
        } catch (Throwable th) {
            gb.e(th);
            return null;
        }
    }

    public abstract void cD();

    public abstract fm cE();

    @Override // com.google.android.gms.internal.gg
    public final void cp() {
        fk fkVarA;
        try {
            fm fmVarCE = cE();
            if (fmVarCE == null || (fkVarA = a(fmVarCE, this.pQ)) == null) {
                fkVarA = new fk(0);
            }
            cD();
            this.tu.a(fkVarA);
        } catch (Throwable th) {
            cD();
            throw th;
        }
    }

    @Override // com.google.android.gms.internal.gg
    public final void onStop() {
        cD();
    }
}
