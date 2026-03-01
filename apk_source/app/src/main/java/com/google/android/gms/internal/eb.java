package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.google.android.gms.internal.ek;

@ez
/* loaded from: classes.dex */
public final class eb extends ek.a implements ServiceConnection {
    private Context mContext;
    private boolean sD;
    private int sE;
    private Intent sF;
    private dw sn;
    private String su;
    private ea sy;

    public eb(Context context, String str, boolean z, int i, Intent intent, ea eaVar) {
        this.sD = false;
        this.su = str;
        this.sE = i;
        this.sF = intent;
        this.sD = z;
        this.mContext = context;
        this.sy = eaVar;
    }

    @Override // com.google.android.gms.internal.ek
    public void finishPurchase() {
        int iD = ed.d(this.sF);
        if (this.sE == -1 && iD == 0) {
            this.sn = new dw(this.mContext);
            Context context = this.mContext;
            Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
            Context context2 = this.mContext;
            context.bindService(intent, this, 1);
        }
    }

    @Override // com.google.android.gms.internal.ek
    public String getProductId() {
        return this.su;
    }

    @Override // com.google.android.gms.internal.ek
    public Intent getPurchaseData() {
        return this.sF;
    }

    @Override // com.google.android.gms.internal.ek
    public int getResultCode() {
        return this.sE;
    }

    @Override // com.google.android.gms.internal.ek
    public boolean isVerified() {
        return this.sD;
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) {
        gs.U("In-app billing service connected.");
        this.sn.r(service);
        String strE = ed.E(ed.e(this.sF));
        if (strE == null) {
            return;
        }
        if (this.sn.c(this.mContext.getPackageName(), strE) == 0) {
            ec.j(this.mContext).a(this.sy);
        }
        this.mContext.unbindService(this);
        this.sn.destroy();
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
        gs.U("In-app billing service disconnected.");
        this.sn.destroy();
    }
}
