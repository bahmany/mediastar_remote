package com.google.android.gms.internal;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.ads.purchase.InAppPurchaseActivity;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.ei;

@ez
/* loaded from: classes.dex */
public class dz extends ei.a implements ServiceConnection {
    private final Activity nr;
    private el sm;
    private dw sn;
    private final ec so;
    private ee sq;
    private Context sw;
    private eg sx;
    private ea sy;
    private String sz = null;

    public dz(Activity activity) {
        this.nr = activity;
        this.so = ec.j(this.nr.getApplicationContext());
    }

    public static void a(Context context, boolean z, dv dvVar) {
        Intent intent = new Intent();
        intent.setClassName(context, InAppPurchaseActivity.CLASS_NAME);
        intent.putExtra("com.google.android.gms.ads.internal.purchase.useClientJar", z);
        dv.a(intent, dvVar);
        context.startActivity(intent);
    }

    private void a(String str, boolean z, int i, Intent intent) {
        try {
            this.sm.a(new eb(this.sw, str, z, i, intent, this.sy));
        } catch (RemoteException e) {
            gs.W("Fail to invoke PlayStorePurchaseListener.");
        }
    }

    @Override // com.google.android.gms.internal.ei
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 1001) {
                int iD = ed.d(data);
                if (resultCode != -1 || iD != 0) {
                    this.so.a(this.sy);
                    a(this.sx.getProductId(), false, resultCode, data);
                } else if (this.sq.a(this.sz, resultCode, data)) {
                    a(this.sx.getProductId(), true, resultCode, data);
                } else {
                    a(this.sx.getProductId(), false, resultCode, data);
                }
                this.sx.recordPlayBillingResolution(iD);
            }
        } catch (RemoteException e) {
            gs.W("Fail to process purchase result.");
        } finally {
            this.sz = null;
            this.nr.finish();
        }
    }

    @Override // com.google.android.gms.internal.ei
    public void onCreate() {
        dv dvVarC = dv.c(this.nr.getIntent());
        this.sm = dvVarC.lM;
        this.sq = dvVarC.lT;
        this.sx = dvVarC.si;
        this.sn = new dw(this.nr.getApplicationContext());
        this.sw = dvVarC.sj;
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_STORE_PACKAGE);
        this.nr.bindService(intent, this, 1);
    }

    @Override // com.google.android.gms.internal.ei
    public void onDestroy() {
        this.nr.unbindService(this);
        this.sn.destroy();
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) throws IntentSender.SendIntentException {
        this.sn.r(service);
        try {
            this.sz = this.sq.cu();
            Bundle bundleA = this.sn.a(this.nr.getPackageName(), this.sx.getProductId(), this.sz);
            PendingIntent pendingIntent = (PendingIntent) bundleA.getParcelable("BUY_INTENT");
            if (pendingIntent == null) {
                int iB = ed.b(bundleA);
                this.sx.recordPlayBillingResolution(iB);
                a(this.sx.getProductId(), false, iB, null);
                this.nr.finish();
            } else {
                this.sy = new ea(this.sx.getProductId(), this.sz);
                this.so.b(this.sy);
                Integer num = 0;
                Integer num2 = 0;
                Integer num3 = 0;
                this.nr.startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), num.intValue(), num2.intValue(), num3.intValue());
            }
        } catch (IntentSender.SendIntentException | RemoteException e) {
            gs.d("Error when connecting in-app billing service", e);
            this.nr.finish();
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
        gs.U("In-app billing service disconnected.");
        this.sn.destroy();
    }
}
