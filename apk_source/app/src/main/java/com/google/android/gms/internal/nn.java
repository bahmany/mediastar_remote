package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.d;
import com.google.android.gms.internal.ng;
import com.google.android.gms.internal.ni;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class nn extends com.google.android.gms.common.internal.d<ng> {
    private final String BZ;
    private final nk akL;
    private final ni akM;
    private boolean akN;
    private final Object mw;

    public nn(Context context, nk nkVar) {
        super(context, nkVar, nkVar, new String[0]);
        this.BZ = context.getPackageName();
        this.akL = (nk) com.google.android.gms.common.internal.n.i(nkVar);
        this.akL.a(this);
        this.akM = new ni();
        this.mw = new Object();
        this.akN = true;
    }

    private void c(nl nlVar, nh nhVar) {
        this.akM.a(nlVar, nhVar);
    }

    private void d(nl nlVar, nh nhVar) {
        try {
            mW();
            gS().a(this.BZ, nlVar, nhVar);
        } catch (RemoteException e) {
            Log.e("PlayLoggerImpl", "Couldn't send log event.  Will try caching.");
            c(nlVar, nhVar);
        } catch (IllegalStateException e2) {
            Log.e("PlayLoggerImpl", "Service was disconnected.  Will try caching.");
            c(nlVar, nhVar);
        }
    }

    private void mW() {
        nl nlVar;
        com.google.android.gms.common.internal.a.I(!this.akN);
        if (this.akM.isEmpty()) {
            return;
        }
        nl nlVar2 = null;
        try {
            ArrayList arrayList = new ArrayList();
            Iterator<ni.a> it = this.akM.mU().iterator();
            while (it.hasNext()) {
                ni.a next = it.next();
                if (next.akD != null) {
                    gS().a(this.BZ, next.akB, pm.f(next.akD));
                } else {
                    if (next.akB.equals(nlVar2)) {
                        arrayList.add(next.akC);
                        nlVar = nlVar2;
                    } else {
                        if (!arrayList.isEmpty()) {
                            gS().a(this.BZ, nlVar2, arrayList);
                            arrayList.clear();
                        }
                        nl nlVar3 = next.akB;
                        arrayList.add(next.akC);
                        nlVar = nlVar3;
                    }
                    nlVar2 = nlVar;
                }
            }
            if (!arrayList.isEmpty()) {
                gS().a(this.BZ, nlVar2, arrayList);
            }
            this.akM.clear();
        } catch (RemoteException e) {
            Log.e("PlayLoggerImpl", "Couldn't send cached log events to AndroidLog service.  Retaining in memory cache.");
        }
    }

    void S(boolean z) {
        synchronized (this.mw) {
            boolean z2 = this.akN;
            this.akN = z;
            if (z2 && !this.akN) {
                mW();
            }
        }
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(com.google.android.gms.common.internal.k kVar, d.e eVar) throws RemoteException {
        kVar.f(eVar, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, getContext().getPackageName(), new Bundle());
    }

    public void b(nl nlVar, nh nhVar) {
        synchronized (this.mw) {
            if (this.akN) {
                c(nlVar, nhVar);
            } else {
                d(nlVar, nhVar);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.d
    /* renamed from: bD, reason: merged with bridge method [inline-methods] */
    public ng j(IBinder iBinder) {
        return ng.a.bC(iBinder);
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return "com.google.android.gms.playlog.internal.IPlayLogService";
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return "com.google.android.gms.playlog.service.START";
    }

    public void start() {
        synchronized (this.mw) {
            if (isConnecting() || isConnected()) {
                return;
            }
            this.akL.R(true);
            connect();
        }
    }

    public void stop() {
        synchronized (this.mw) {
            this.akL.R(false);
            disconnect();
        }
    }
}
