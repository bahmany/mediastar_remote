package com.google.android.gms.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.d;
import com.google.android.gms.internal.lv;
import com.google.android.gms.internal.lw;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;
import java.util.List;

/* loaded from: classes.dex */
public class ly extends com.google.android.gms.common.internal.d<lw> {
    private final md<lw> Dh;
    private final lx aeL;
    private final mv aeM;
    private final lo aeN;
    private final ie aeO;
    private final String aeP;

    private final class a extends com.google.android.gms.common.internal.d<lw>.b<LocationClient.OnAddGeofencesResultListener> {
        private final int HF;
        private final String[] aeQ;

        public a(LocationClient.OnAddGeofencesResultListener onAddGeofencesResultListener, int i, String[] strArr) {
            super(onAddGeofencesResultListener);
            this.HF = LocationStatusCodes.ee(i);
            this.aeQ = strArr;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: a, reason: merged with bridge method [inline-methods] */
        public void g(LocationClient.OnAddGeofencesResultListener onAddGeofencesResultListener) {
            if (onAddGeofencesResultListener != null) {
                onAddGeofencesResultListener.onAddGeofencesResult(this.HF, this.aeQ);
            }
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    private static final class b extends lv.a {
        private LocationClient.OnAddGeofencesResultListener aeS;
        private LocationClient.OnRemoveGeofencesResultListener aeT;
        private ly aeU;

        public b(LocationClient.OnAddGeofencesResultListener onAddGeofencesResultListener, ly lyVar) {
            this.aeS = onAddGeofencesResultListener;
            this.aeT = null;
            this.aeU = lyVar;
        }

        public b(LocationClient.OnRemoveGeofencesResultListener onRemoveGeofencesResultListener, ly lyVar) {
            this.aeT = onRemoveGeofencesResultListener;
            this.aeS = null;
            this.aeU = lyVar;
        }

        @Override // com.google.android.gms.internal.lv
        public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) throws RemoteException {
            if (this.aeU == null) {
                Log.wtf("LocationClientImpl", "onAddGeofenceResult called multiple times");
                return;
            }
            ly lyVar = this.aeU;
            ly lyVar2 = this.aeU;
            lyVar2.getClass();
            lyVar.a(lyVar2.new a(this.aeS, statusCode, geofenceRequestIds));
            this.aeU = null;
            this.aeS = null;
            this.aeT = null;
        }

        @Override // com.google.android.gms.internal.lv
        public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent pendingIntent) {
            if (this.aeU == null) {
                Log.wtf("LocationClientImpl", "onRemoveGeofencesByPendingIntentResult called multiple times");
                return;
            }
            ly lyVar = this.aeU;
            ly lyVar2 = this.aeU;
            lyVar2.getClass();
            lyVar.a(lyVar2.new d(1, this.aeT, statusCode, pendingIntent));
            this.aeU = null;
            this.aeS = null;
            this.aeT = null;
        }

        @Override // com.google.android.gms.internal.lv
        public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceRequestIds) {
            if (this.aeU == null) {
                Log.wtf("LocationClientImpl", "onRemoveGeofencesByRequestIdsResult called multiple times");
                return;
            }
            ly lyVar = this.aeU;
            ly lyVar2 = this.aeU;
            lyVar2.getClass();
            lyVar.a(lyVar2.new d(2, this.aeT, statusCode, geofenceRequestIds));
            this.aeU = null;
            this.aeS = null;
            this.aeT = null;
        }
    }

    private final class c implements md<lw> {
        private c() {
        }

        @Override // com.google.android.gms.internal.md
        public void dK() {
            ly.this.dK();
        }

        @Override // com.google.android.gms.internal.md
        /* renamed from: lX, reason: merged with bridge method [inline-methods] */
        public lw gS() {
            return ly.this.gS();
        }
    }

    private final class d extends com.google.android.gms.common.internal.d<lw>.b<LocationClient.OnRemoveGeofencesResultListener> {
        private final int HF;
        private final String[] aeQ;
        private final int aeV;
        private final PendingIntent mPendingIntent;

        public d(int i, LocationClient.OnRemoveGeofencesResultListener onRemoveGeofencesResultListener, int i2, PendingIntent pendingIntent) {
            super(onRemoveGeofencesResultListener);
            com.google.android.gms.common.internal.a.I(i == 1);
            this.aeV = i;
            this.HF = LocationStatusCodes.ee(i2);
            this.mPendingIntent = pendingIntent;
            this.aeQ = null;
        }

        public d(int i, LocationClient.OnRemoveGeofencesResultListener onRemoveGeofencesResultListener, int i2, String[] strArr) {
            super(onRemoveGeofencesResultListener);
            com.google.android.gms.common.internal.a.I(i == 2);
            this.aeV = i;
            this.HF = LocationStatusCodes.ee(i2);
            this.aeQ = strArr;
            this.mPendingIntent = null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.android.gms.common.internal.d.b
        /* renamed from: a, reason: merged with bridge method [inline-methods] */
        public void g(LocationClient.OnRemoveGeofencesResultListener onRemoveGeofencesResultListener) {
            if (onRemoveGeofencesResultListener != null) {
                switch (this.aeV) {
                    case 1:
                        onRemoveGeofencesResultListener.onRemoveGeofencesByPendingIntentResult(this.HF, this.mPendingIntent);
                        break;
                    case 2:
                        onRemoveGeofencesResultListener.onRemoveGeofencesByRequestIdsResult(this.HF, this.aeQ);
                        break;
                    default:
                        Log.wtf("LocationClientImpl", "Unsupported action: " + this.aeV);
                        break;
                }
            }
        }

        @Override // com.google.android.gms.common.internal.d.b
        protected void gT() {
        }
    }

    public ly(Context context, Looper looper, String str, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener, String str2) {
        this(context, looper, str, connectionCallbacks, onConnectionFailedListener, str2, null);
    }

    public ly(Context context, Looper looper, String str, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener, String str2, String str3) {
        this(context, looper, str, connectionCallbacks, onConnectionFailedListener, str2, str3, null);
    }

    public ly(Context context, Looper looper, String str, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener, String str2, String str3, String str4) {
        super(context, looper, connectionCallbacks, onConnectionFailedListener, new String[0]);
        this.Dh = new c();
        this.aeL = new lx(context, this.Dh);
        this.aeP = str2;
        this.aeM = new mv(str, this.Dh, str3);
        this.aeN = lo.a(context, str3, str4, this.Dh);
        this.aeO = ie.a(context, this.Dh);
    }

    public ly(Context context, GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener onConnectionFailedListener, String str) {
        super(context, connectionCallbacks, onConnectionFailedListener, new String[0]);
        this.Dh = new c();
        this.aeL = new lx(context, this.Dh);
        this.aeP = str;
        this.aeM = new mv(context.getPackageName(), this.Dh, null);
        this.aeN = lo.a(context, null, null, this.Dh);
        this.aeO = ie.a(context, this.Dh);
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(com.google.android.gms.common.internal.k kVar, d.e eVar) throws RemoteException {
        Bundle bundle = new Bundle();
        bundle.putString("client_name", this.aeP);
        kVar.e(eVar, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, getContext().getPackageName(), bundle);
    }

    public void a(lz lzVar, LocationListener locationListener) throws RemoteException {
        a(lzVar, locationListener, (Looper) null);
    }

    public void a(lz lzVar, LocationListener locationListener, Looper looper) throws RemoteException {
        synchronized (this.aeL) {
            this.aeL.a(lzVar, locationListener, looper);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.d
    /* renamed from: aL, reason: merged with bridge method [inline-methods] */
    public lw j(IBinder iBinder) {
        return lw.a.aK(iBinder);
    }

    public void addGeofences(List<mb> geofences, PendingIntent pendingIntent, LocationClient.OnAddGeofencesResultListener listener) throws RemoteException {
        dK();
        com.google.android.gms.common.internal.n.b(geofences != null && geofences.size() > 0, "At least one geofence must be specified.");
        com.google.android.gms.common.internal.n.b(pendingIntent, "PendingIntent must be specified.");
        com.google.android.gms.common.internal.n.b(listener, "OnAddGeofencesResultListener not provided.");
        gS().a(geofences, pendingIntent, listener == null ? null : new b(listener, this), getContext().getPackageName());
    }

    public void b(lz lzVar, PendingIntent pendingIntent) throws RemoteException {
        this.aeL.b(lzVar, pendingIntent);
    }

    @Override // com.google.android.gms.common.internal.d, com.google.android.gms.common.api.Api.a
    public void disconnect() {
        synchronized (this.aeL) {
            if (isConnected()) {
                this.aeL.removeAllListeners();
                this.aeL.lW();
            }
            super.disconnect();
        }
    }

    public Location getLastLocation() {
        return this.aeL.getLastLocation();
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return "com.google.android.gms.location.internal.IGoogleLocationManagerService";
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return "com.google.android.location.internal.GoogleLocationManagerService.START";
    }

    public void removeActivityUpdates(PendingIntent callbackIntent) throws RemoteException {
        dK();
        com.google.android.gms.common.internal.n.i(callbackIntent);
        gS().removeActivityUpdates(callbackIntent);
    }

    public void removeGeofences(PendingIntent pendingIntent, LocationClient.OnRemoveGeofencesResultListener listener) throws RemoteException {
        dK();
        com.google.android.gms.common.internal.n.b(pendingIntent, "PendingIntent must be specified.");
        com.google.android.gms.common.internal.n.b(listener, "OnRemoveGeofencesResultListener not provided.");
        gS().a(pendingIntent, listener == null ? null : new b(listener, this), getContext().getPackageName());
    }

    public void removeGeofences(List<String> geofenceRequestIds, LocationClient.OnRemoveGeofencesResultListener listener) throws RemoteException {
        dK();
        com.google.android.gms.common.internal.n.b(geofenceRequestIds != null && geofenceRequestIds.size() > 0, "geofenceRequestIds can't be null nor empty.");
        com.google.android.gms.common.internal.n.b(listener, "OnRemoveGeofencesResultListener not provided.");
        gS().a((String[]) geofenceRequestIds.toArray(new String[0]), listener == null ? null : new b(listener, this), getContext().getPackageName());
    }

    public void removeLocationUpdates(PendingIntent callbackIntent) throws RemoteException {
        this.aeL.removeLocationUpdates(callbackIntent);
    }

    public void removeLocationUpdates(LocationListener listener) throws RemoteException {
        this.aeL.removeLocationUpdates(listener);
    }

    public void requestActivityUpdates(long detectionIntervalMillis, PendingIntent callbackIntent) throws RemoteException {
        dK();
        com.google.android.gms.common.internal.n.i(callbackIntent);
        com.google.android.gms.common.internal.n.b(detectionIntervalMillis >= 0, "detectionIntervalMillis must be >= 0");
        gS().a(detectionIntervalMillis, true, callbackIntent);
    }

    public void requestLocationUpdates(LocationRequest request, PendingIntent callbackIntent) throws RemoteException {
        this.aeL.requestLocationUpdates(request, callbackIntent);
    }

    public void requestLocationUpdates(LocationRequest request, LocationListener listener, Looper looper) throws RemoteException {
        synchronized (this.aeL) {
            this.aeL.requestLocationUpdates(request, listener, looper);
        }
    }

    public void setMockLocation(Location mockLocation) throws RemoteException {
        this.aeL.setMockLocation(mockLocation);
    }

    public void setMockMode(boolean isMockMode) throws RemoteException {
        this.aeL.setMockMode(isMockMode);
    }
}
