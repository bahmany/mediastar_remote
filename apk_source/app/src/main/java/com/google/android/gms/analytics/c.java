package com.google.android.gms.analytics;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.hb;
import com.google.android.gms.internal.hc;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
class c implements com.google.android.gms.analytics.b {
    private Context mContext;
    private ServiceConnection xG;
    private b xH;
    private InterfaceC0001c xI;
    private hc xJ;

    final class a implements ServiceConnection {
        a() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName component, IBinder binder) {
            z.V("service connected, binder: " + binder);
            try {
                if ("com.google.android.gms.analytics.internal.IAnalyticsService".equals(binder.getInterfaceDescriptor())) {
                    z.V("bound to service");
                    c.this.xJ = hc.a.E(binder);
                    c.this.dL();
                    return;
                }
            } catch (RemoteException e) {
            }
            try {
                c.this.mContext.unbindService(this);
            } catch (IllegalArgumentException e2) {
            }
            c.this.xG = null;
            c.this.xI.a(2, null);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName component) {
            z.V("service disconnected: " + component);
            c.this.xG = null;
            c.this.xH.onDisconnected();
        }
    }

    public interface b {
        void onConnected();

        void onDisconnected();
    }

    /* renamed from: com.google.android.gms.analytics.c$c, reason: collision with other inner class name */
    public interface InterfaceC0001c {
        void a(int i, Intent intent);
    }

    public c(Context context, b bVar, InterfaceC0001c interfaceC0001c) {
        this.mContext = context;
        if (bVar == null) {
            throw new IllegalArgumentException("onConnectedListener cannot be null");
        }
        this.xH = bVar;
        if (interfaceC0001c == null) {
            throw new IllegalArgumentException("onConnectionFailedListener cannot be null");
        }
        this.xI = interfaceC0001c;
    }

    private hc dJ() {
        dK();
        return this.xJ;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dL() {
        dM();
    }

    private void dM() {
        this.xH.onConnected();
    }

    @Override // com.google.android.gms.analytics.b
    public void a(Map<String, String> map, long j, String str, List<hb> list) {
        try {
            dJ().a(map, j, str, list);
        } catch (RemoteException e) {
            z.T("sendHit failed: " + e);
        }
    }

    @Override // com.google.android.gms.analytics.b
    public void connect() {
        Intent intent = new Intent("com.google.android.gms.analytics.service.START");
        intent.setComponent(new ComponentName(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE, "com.google.android.gms.analytics.service.AnalyticsService"));
        intent.putExtra("app_package_name", this.mContext.getPackageName());
        if (this.xG != null) {
            z.T("Calling connect() while still connected, missing disconnect().");
            return;
        }
        this.xG = new a();
        boolean zBindService = this.mContext.bindService(intent, this.xG, 129);
        z.V("connect: bindService returned " + zBindService + " for " + intent);
        if (zBindService) {
            return;
        }
        this.xG = null;
        this.xI.a(1, null);
    }

    @Override // com.google.android.gms.analytics.b
    public void dI() {
        try {
            dJ().dI();
        } catch (RemoteException e) {
            z.T("clear hits failed: " + e);
        }
    }

    protected void dK() {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
        }
    }

    @Override // com.google.android.gms.analytics.b
    public void disconnect() {
        this.xJ = null;
        if (this.xG != null) {
            try {
                this.mContext.unbindService(this.xG);
            } catch (IllegalArgumentException e) {
            } catch (IllegalStateException e2) {
            }
            this.xG = null;
            this.xH.onDisconnected();
        }
    }

    public boolean isConnected() {
        return this.xJ != null;
    }
}
