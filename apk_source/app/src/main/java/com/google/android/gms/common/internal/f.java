package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.d;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class f implements Handler.Callback {
    private static final Object LK = new Object();
    private static f LL;
    private final HashMap<String, a> LM = new HashMap<>();
    private final Context mD;
    private final Handler mHandler;

    final class a {
        private final String LN;
        private boolean LQ;
        private IBinder LR;
        private ComponentName LS;
        private final ServiceConnectionC0006a LO = new ServiceConnectionC0006a();
        private final HashSet<d<?>.f> LP = new HashSet<>();
        private int mState = 0;

        /* renamed from: com.google.android.gms.common.internal.f$a$a */
        public class ServiceConnectionC0006a implements ServiceConnection {
            public ServiceConnectionC0006a() {
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName component, IBinder binder) {
                synchronized (f.this.LM) {
                    a.this.LR = binder;
                    a.this.LS = component;
                    Iterator it = a.this.LP.iterator();
                    while (it.hasNext()) {
                        ((d.f) it.next()).onServiceConnected(component, binder);
                    }
                    a.this.mState = 1;
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName component) {
                synchronized (f.this.LM) {
                    a.this.LR = null;
                    a.this.LS = component;
                    Iterator it = a.this.LP.iterator();
                    while (it.hasNext()) {
                        ((d.f) it.next()).onServiceDisconnected(component);
                    }
                    a.this.mState = 2;
                }
            }
        }

        public a(String str) {
            this.LN = str;
        }

        public void J(boolean z) {
            this.LQ = z;
        }

        public void a(d<?>.f fVar) {
            this.LP.add(fVar);
        }

        public void b(d<?>.f fVar) {
            this.LP.remove(fVar);
        }

        public boolean c(d<?>.f fVar) {
            return this.LP.contains(fVar);
        }

        public ServiceConnectionC0006a gW() {
            return this.LO;
        }

        public String gX() {
            return this.LN;
        }

        public boolean gY() {
            return this.LP.isEmpty();
        }

        public IBinder getBinder() {
            return this.LR;
        }

        public ComponentName getComponentName() {
            return this.LS;
        }

        public int getState() {
            return this.mState;
        }

        public boolean isBound() {
            return this.LQ;
        }
    }

    private f(Context context) {
        this.mHandler = new Handler(context.getMainLooper(), this);
        this.mD = context.getApplicationContext();
    }

    public static f J(Context context) {
        synchronized (LK) {
            if (LL == null) {
                LL = new f(context.getApplicationContext());
            }
        }
        return LL;
    }

    public boolean a(String str, d<?>.f fVar) {
        boolean zIsBound;
        synchronized (this.LM) {
            a aVar = this.LM.get(str);
            if (aVar != null) {
                this.mHandler.removeMessages(0, aVar);
                if (!aVar.c(fVar)) {
                    aVar.a(fVar);
                    switch (aVar.getState()) {
                        case 1:
                            fVar.onServiceConnected(aVar.getComponentName(), aVar.getBinder());
                            break;
                        case 2:
                            aVar.J(this.mD.bindService(new Intent(str).setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE), aVar.gW(), 129));
                            break;
                    }
                } else {
                    throw new IllegalStateException("Trying to bind a GmsServiceConnection that was already connected before.  startServiceAction=" + str);
                }
            } else {
                aVar = new a(str);
                aVar.a(fVar);
                aVar.J(this.mD.bindService(new Intent(str).setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE), aVar.gW(), 129));
                this.LM.put(str, aVar);
            }
            zIsBound = aVar.isBound();
        }
        return zIsBound;
    }

    public void b(String str, d<?>.f fVar) {
        synchronized (this.LM) {
            a aVar = this.LM.get(str);
            if (aVar == null) {
                throw new IllegalStateException("Nonexistent connection status for service action: " + str);
            }
            if (!aVar.c(fVar)) {
                throw new IllegalStateException("Trying to unbind a GmsServiceConnection  that was not bound before.  startServiceAction=" + str);
            }
            aVar.b(fVar);
            if (aVar.gY()) {
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0, aVar), 5000L);
            }
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                a aVar = (a) msg.obj;
                synchronized (this.LM) {
                    if (aVar.gY()) {
                        this.mD.unbindService(aVar.gW());
                        this.LM.remove(aVar.gX());
                    }
                }
                return true;
            default:
                return false;
        }
    }
}
