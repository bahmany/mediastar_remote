package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.content.ContentProviderClient;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.a;
import java.util.HashMap;

/* loaded from: classes.dex */
public class lx {
    private final md<lw> Dh;
    private ContentProviderClient aeG = null;
    private boolean aeH = false;
    private HashMap<LocationListener, b> aeI = new HashMap<>();
    private final Context mContext;

    private static class a extends Handler {
        private final LocationListener aeJ;

        public a(LocationListener locationListener) {
            this.aeJ = locationListener;
        }

        public a(LocationListener locationListener, Looper looper) {
            super(looper);
            this.aeJ = locationListener;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    this.aeJ.onLocationChanged(new Location((Location) msg.obj));
                    break;
                default:
                    Log.e("LocationClientHelper", "unknown message in LocationHandler.handleMessage");
                    break;
            }
        }
    }

    private static class b extends a.AbstractBinderC0096a {
        private Handler aeK;

        b(LocationListener locationListener, Looper looper) {
            this.aeK = looper == null ? new a(locationListener) : new a(locationListener, looper);
        }

        @Override // com.google.android.gms.location.a
        public void onLocationChanged(Location location) {
            if (this.aeK == null) {
                Log.e("LocationClientHelper", "Received a location in client after calling removeLocationUpdates.");
                return;
            }
            Message messageObtain = Message.obtain();
            messageObtain.what = 1;
            messageObtain.obj = location;
            this.aeK.sendMessage(messageObtain);
        }

        public void release() {
            this.aeK = null;
        }
    }

    public lx(Context context, md<lw> mdVar) {
        this.mContext = context;
        this.Dh = mdVar;
    }

    private b a(LocationListener locationListener, Looper looper) {
        b bVar;
        if (looper == null) {
            com.google.android.gms.common.internal.n.b(Looper.myLooper(), "Can't create handler inside thread that has not called Looper.prepare()");
        }
        synchronized (this.aeI) {
            bVar = this.aeI.get(locationListener);
            if (bVar == null) {
                bVar = new b(locationListener, looper);
            }
            this.aeI.put(locationListener, bVar);
        }
        return bVar;
    }

    public void a(lz lzVar, LocationListener locationListener, Looper looper) throws RemoteException {
        this.Dh.dK();
        ((lw) this.Dh.gS()).a(lzVar, a(locationListener, looper));
    }

    public void b(lz lzVar, PendingIntent pendingIntent) throws RemoteException {
        this.Dh.dK();
        ((lw) this.Dh.gS()).a(lzVar, pendingIntent);
    }

    public Location getLastLocation() {
        this.Dh.dK();
        try {
            return ((lw) this.Dh.gS()).bT(this.mContext.getPackageName());
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    public void lW() {
        if (this.aeH) {
            try {
                setMockMode(false);
            } catch (RemoteException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public void removeAllListeners() {
        try {
            synchronized (this.aeI) {
                for (b bVar : this.aeI.values()) {
                    if (bVar != null) {
                        ((lw) this.Dh.gS()).a(bVar);
                    }
                }
                this.aeI.clear();
            }
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    public void removeLocationUpdates(PendingIntent callbackIntent) throws RemoteException {
        this.Dh.dK();
        ((lw) this.Dh.gS()).a(callbackIntent);
    }

    public void removeLocationUpdates(LocationListener listener) throws RemoteException {
        this.Dh.dK();
        com.google.android.gms.common.internal.n.b(listener, "Invalid null listener");
        synchronized (this.aeI) {
            b bVarRemove = this.aeI.remove(listener);
            if (this.aeG != null && this.aeI.isEmpty()) {
                this.aeG.release();
                this.aeG = null;
            }
            if (bVarRemove != null) {
                bVarRemove.release();
                ((lw) this.Dh.gS()).a(bVarRemove);
            }
        }
    }

    public void requestLocationUpdates(LocationRequest request, PendingIntent callbackIntent) throws RemoteException {
        this.Dh.dK();
        ((lw) this.Dh.gS()).a(request, callbackIntent);
    }

    public void requestLocationUpdates(LocationRequest request, LocationListener listener, Looper looper) throws RemoteException {
        this.Dh.dK();
        ((lw) this.Dh.gS()).a(request, a(listener, looper));
    }

    public void setMockLocation(Location mockLocation) throws RemoteException {
        this.Dh.dK();
        ((lw) this.Dh.gS()).setMockLocation(mockLocation);
    }

    public void setMockMode(boolean isMockMode) throws RemoteException {
        this.Dh.dK();
        ((lw) this.Dh.gS()).setMockMode(isMockMode);
        this.aeH = isMockMode;
    }
}
