package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@ez
/* loaded from: classes.dex */
public class dx extends gg implements ServiceConnection {
    private Context mContext;
    private final Object mw = new Object();
    private boolean sl = false;
    private el sm;
    private dw sn;
    private ec so;
    private List<ea> sp;
    private ee sq;

    public dx(Context context, el elVar, ee eeVar) {
        this.sp = null;
        this.mContext = context;
        this.sm = elVar;
        this.sq = eeVar;
        this.sn = new dw(context);
        this.so = ec.j(this.mContext);
        this.sp = this.so.d(10L);
    }

    private void a(final ea eaVar, String str, String str2) {
        final Intent intent = new Intent();
        intent.putExtra("RESPONSE_CODE", 0);
        intent.putExtra("INAPP_PURCHASE_DATA", str);
        intent.putExtra("INAPP_DATA_SIGNATURE", str2);
        gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.dx.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (dx.this.sq.a(eaVar.sB, -1, intent)) {
                        dx.this.sm.a(new eb(dx.this.mContext, eaVar.sC, true, -1, intent, eaVar));
                    } else {
                        dx.this.sm.a(new eb(dx.this.mContext, eaVar.sC, false, -1, intent, eaVar));
                    }
                } catch (RemoteException e) {
                    gs.W("Fail to verify and dispatch pending transaction");
                }
            }
        });
    }

    private void b(long j) {
        do {
            if (!c(j)) {
                gs.W("Timeout waiting for pending transaction to be processed.");
            }
        } while (!this.sl);
    }

    private boolean c(long j) throws InterruptedException {
        long jElapsedRealtime = 60000 - (SystemClock.elapsedRealtime() - j);
        if (jElapsedRealtime <= 0) {
            return false;
        }
        try {
            this.mw.wait(jElapsedRealtime);
        } catch (InterruptedException e) {
            gs.W("waitWithTimeout_lock interrupted");
        }
        return true;
    }

    private void cq() throws ClassNotFoundException {
        if (this.sp.isEmpty()) {
            return;
        }
        HashMap map = new HashMap();
        for (ea eaVar : this.sp) {
            map.put(eaVar.sC, eaVar);
        }
        String str = null;
        while (true) {
            Bundle bundleD = this.sn.d(this.mContext.getPackageName(), str);
            if (bundleD == null || ed.b(bundleD) != 0) {
                break;
            }
            ArrayList<String> stringArrayList = bundleD.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            ArrayList<String> stringArrayList2 = bundleD.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            ArrayList<String> stringArrayList3 = bundleD.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
            String string = bundleD.getString("INAPP_CONTINUATION_TOKEN");
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= stringArrayList.size()) {
                    break;
                }
                if (map.containsKey(stringArrayList.get(i2))) {
                    String str2 = stringArrayList.get(i2);
                    String str3 = stringArrayList2.get(i2);
                    String str4 = stringArrayList3.get(i2);
                    ea eaVar2 = (ea) map.get(str2);
                    if (eaVar2.sB.equals(ed.D(str3))) {
                        a(eaVar2, str3, str4);
                        map.remove(str2);
                    }
                }
                i = i2 + 1;
            }
            if (string == null || map.isEmpty()) {
                break;
            } else {
                str = string;
            }
        }
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            this.so.a((ea) map.get((String) it.next()));
        }
    }

    @Override // com.google.android.gms.internal.gg
    public void cp() {
        synchronized (this.mw) {
            Context context = this.mContext;
            Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
            Context context2 = this.mContext;
            context.bindService(intent, this, 1);
            b(SystemClock.elapsedRealtime());
            this.mContext.unbindService(this);
            this.sn.destroy();
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) {
        synchronized (this.mw) {
            this.sn.r(service);
            cq();
            this.sl = true;
            this.mw.notify();
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
        gs.U("In-app billing service disconnected.");
        this.sn.destroy();
    }

    @Override // com.google.android.gms.internal.gg
    public void onStop() {
        synchronized (this.mw) {
            this.mContext.unbindService(this);
            this.sn.destroy();
        }
    }
}
