package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.LaunchOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.d;
import com.google.android.gms.internal.in;
import com.google.android.gms.internal.io;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: classes.dex */
public final class ij extends com.google.android.gms.common.internal.d<in> {
    private final Cast.Listener EO;
    private double FA;
    private boolean FB;
    private boolean GA;
    private int GB;
    private int GC;
    private final AtomicLong GD;
    private String GE;
    private String GF;
    private Bundle GG;
    private Map<Long, BaseImplementation.b<Status>> GH;
    private b GI;
    private BaseImplementation.b<Cast.ApplicationConnectionResult> GJ;
    private BaseImplementation.b<Status> GK;
    private ApplicationMetadata Gs;
    private final CastDevice Gt;
    private final Map<String, Cast.MessageReceivedCallback> Gu;
    private final long Gv;
    private c Gw;
    private String Gx;
    private boolean Gy;
    private boolean Gz;
    private final Handler mHandler;
    private static final ip Gr = new ip("CastClientImpl");
    private static final Object GL = new Object();
    private static final Object GM = new Object();

    private static final class a implements Cast.ApplicationConnectionResult {
        private final Status CM;
        private final ApplicationMetadata GN;
        private final String GO;
        private final boolean GP;
        private final String vL;

        public a(Status status) {
            this(status, null, null, null, false);
        }

        public a(Status status, ApplicationMetadata applicationMetadata, String str, String str2, boolean z) {
            this.CM = status;
            this.GN = applicationMetadata;
            this.GO = str;
            this.vL = str2;
            this.GP = z;
        }

        @Override // com.google.android.gms.cast.Cast.ApplicationConnectionResult
        public ApplicationMetadata getApplicationMetadata() {
            return this.GN;
        }

        @Override // com.google.android.gms.cast.Cast.ApplicationConnectionResult
        public String getApplicationStatus() {
            return this.GO;
        }

        @Override // com.google.android.gms.cast.Cast.ApplicationConnectionResult
        public String getSessionId() {
            return this.vL;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }

        @Override // com.google.android.gms.cast.Cast.ApplicationConnectionResult
        public boolean getWasLaunched() {
            return this.GP;
        }
    }

    private class b implements GoogleApiClient.OnConnectionFailedListener {
        private b() {
        }

        @Override // com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener
        public void onConnectionFailed(ConnectionResult result) {
            ij.this.fG();
        }
    }

    private class c extends io.a {
        private AtomicBoolean GR;

        private c() {
            this.GR = new AtomicBoolean(false);
        }

        private boolean ag(int i) {
            synchronized (ij.GM) {
                if (ij.this.GK == null) {
                    return false;
                }
                ij.this.GK.b(new Status(i));
                ij.this.GK = null;
                return true;
            }
        }

        private void c(long j, int i) {
            BaseImplementation.b bVar;
            synchronized (ij.this.GH) {
                bVar = (BaseImplementation.b) ij.this.GH.remove(Long.valueOf(j));
            }
            if (bVar != null) {
                bVar.b(new Status(i));
            }
        }

        @Override // com.google.android.gms.internal.io
        public void a(ApplicationMetadata applicationMetadata, String str, String str2, boolean z) {
            if (this.GR.get()) {
                return;
            }
            ij.this.Gs = applicationMetadata;
            ij.this.GE = applicationMetadata.getApplicationId();
            ij.this.GF = str2;
            synchronized (ij.GL) {
                if (ij.this.GJ != null) {
                    ij.this.GJ.b(new a(new Status(0), applicationMetadata, str, str2, z));
                    ij.this.GJ = null;
                }
            }
        }

        @Override // com.google.android.gms.internal.io
        public void a(String str, double d, boolean z) {
            ij.Gr.b("Deprecated callback: \"onStatusreceived\"", new Object[0]);
        }

        @Override // com.google.android.gms.internal.io
        public void a(String str, long j) {
            if (this.GR.get()) {
                return;
            }
            c(j, 0);
        }

        @Override // com.google.android.gms.internal.io
        public void a(String str, long j, int i) {
            if (this.GR.get()) {
                return;
            }
            c(j, i);
        }

        @Override // com.google.android.gms.internal.io
        public void ac(int i) {
            if (fL()) {
                ij.Gr.b("ICastDeviceControllerListener.onDisconnected: %d", Integer.valueOf(i));
                if (i != 0) {
                    ij.this.aA(2);
                }
            }
        }

        @Override // com.google.android.gms.internal.io
        public void ad(int i) {
            if (this.GR.get()) {
                return;
            }
            synchronized (ij.GL) {
                if (ij.this.GJ != null) {
                    ij.this.GJ.b(new a(new Status(i)));
                    ij.this.GJ = null;
                }
            }
        }

        @Override // com.google.android.gms.internal.io
        public void ae(int i) {
            if (this.GR.get()) {
                return;
            }
            ag(i);
        }

        @Override // com.google.android.gms.internal.io
        public void af(int i) {
            if (this.GR.get()) {
                return;
            }
            ag(i);
        }

        @Override // com.google.android.gms.internal.io
        public void b(final ig igVar) {
            if (this.GR.get()) {
                return;
            }
            ij.Gr.b("onApplicationStatusChanged", new Object[0]);
            ij.this.mHandler.post(new Runnable() { // from class: com.google.android.gms.internal.ij.c.3
                @Override // java.lang.Runnable
                public void run() {
                    ij.this.a(igVar);
                }
            });
        }

        @Override // com.google.android.gms.internal.io
        public void b(final il ilVar) {
            if (this.GR.get()) {
                return;
            }
            ij.Gr.b("onDeviceStatusChanged", new Object[0]);
            ij.this.mHandler.post(new Runnable() { // from class: com.google.android.gms.internal.ij.c.2
                @Override // java.lang.Runnable
                public void run() {
                    ij.this.a(ilVar);
                }
            });
        }

        @Override // com.google.android.gms.internal.io
        public void b(String str, byte[] bArr) {
            if (this.GR.get()) {
                return;
            }
            ij.Gr.b("IGNORING: Receive (type=binary, ns=%s) <%d bytes>", str, Integer.valueOf(bArr.length));
        }

        public boolean fL() {
            if (this.GR.getAndSet(true)) {
                return false;
            }
            ij.this.fC();
            return true;
        }

        public boolean fM() {
            return this.GR.get();
        }

        @Override // com.google.android.gms.internal.io
        public void k(final String str, final String str2) {
            if (this.GR.get()) {
                return;
            }
            ij.Gr.b("Receive (type=text, ns=%s) %s", str, str2);
            ij.this.mHandler.post(new Runnable() { // from class: com.google.android.gms.internal.ij.c.4
                @Override // java.lang.Runnable
                public void run() {
                    Cast.MessageReceivedCallback messageReceivedCallback;
                    synchronized (ij.this.Gu) {
                        messageReceivedCallback = (Cast.MessageReceivedCallback) ij.this.Gu.get(str);
                    }
                    if (messageReceivedCallback != null) {
                        messageReceivedCallback.onMessageReceived(ij.this.Gt, str, str2);
                    } else {
                        ij.Gr.b("Discarded message for unknown namespace '%s'", str);
                    }
                }
            });
        }

        @Override // com.google.android.gms.internal.io
        public void onApplicationDisconnected(final int statusCode) {
            if (this.GR.get()) {
                return;
            }
            ij.this.GE = null;
            ij.this.GF = null;
            ag(statusCode);
            if (ij.this.EO != null) {
                ij.this.mHandler.post(new Runnable() { // from class: com.google.android.gms.internal.ij.c.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (ij.this.EO != null) {
                            ij.this.EO.onApplicationDisconnected(statusCode);
                        }
                    }
                });
            }
        }
    }

    public ij(Context context, Looper looper, CastDevice castDevice, long j, Cast.Listener listener, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, connectionCallbacks, onConnectionFailedListener, (String[]) null);
        this.Gt = castDevice;
        this.EO = listener;
        this.Gv = j;
        this.mHandler = new Handler(looper);
        this.Gu = new HashMap();
        this.GD = new AtomicLong(0L);
        this.GH = new HashMap();
        fC();
        this.GI = new b();
        registerConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this.GI);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(ig igVar) {
        boolean z;
        String strFz = igVar.fz();
        if (ik.a(strFz, this.Gx)) {
            z = false;
        } else {
            this.Gx = strFz;
            z = true;
        }
        Gr.b("hasChanged=%b, mFirstApplicationStatusUpdate=%b", Boolean.valueOf(z), Boolean.valueOf(this.Gy));
        if (this.EO != null && (z || this.Gy)) {
            this.EO.onApplicationStatusChanged();
        }
        this.Gy = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(il ilVar) {
        boolean z;
        boolean z2;
        boolean z3;
        this.Gs = ilVar.getApplicationMetadata();
        double dFF = ilVar.fF();
        if (dFF == Double.NaN || dFF == this.FA) {
            z = false;
        } else {
            this.FA = dFF;
            z = true;
        }
        boolean zFN = ilVar.fN();
        if (zFN != this.FB) {
            this.FB = zFN;
            z = true;
        }
        Gr.b("hasVolumeChanged=%b, mFirstDeviceStatusUpdate=%b", Boolean.valueOf(z), Boolean.valueOf(this.Gz));
        if (this.EO != null && (z || this.Gz)) {
            this.EO.onVolumeChanged();
        }
        int iFO = ilVar.fO();
        if (iFO != this.GB) {
            this.GB = iFO;
            z2 = true;
        } else {
            z2 = false;
        }
        Gr.b("hasActiveInputChanged=%b, mFirstDeviceStatusUpdate=%b", Boolean.valueOf(z2), Boolean.valueOf(this.Gz));
        if (this.EO != null && (z2 || this.Gz)) {
            this.EO.W(this.GB);
        }
        int iFP = ilVar.fP();
        if (iFP != this.GC) {
            this.GC = iFP;
            z3 = true;
        } else {
            z3 = false;
        }
        Gr.b("hasStandbyStateChanged=%b, mFirstDeviceStatusUpdate=%b", Boolean.valueOf(z3), Boolean.valueOf(this.Gz));
        if (this.EO != null && (z3 || this.Gz)) {
            this.EO.X(this.GC);
        }
        this.Gz = false;
    }

    private void c(BaseImplementation.b<Cast.ApplicationConnectionResult> bVar) {
        synchronized (GL) {
            if (this.GJ != null) {
                this.GJ.b(new a(new Status(2002)));
            }
            this.GJ = bVar;
        }
    }

    private void e(BaseImplementation.b<Status> bVar) {
        synchronized (GM) {
            if (this.GK != null) {
                bVar.b(new Status(2001));
            } else {
                this.GK = bVar;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fC() {
        this.GA = false;
        this.GB = -1;
        this.GC = -1;
        this.Gs = null;
        this.Gx = null;
        this.FA = 0.0d;
        this.FB = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fG() {
        Gr.b("removing all MessageReceivedCallbacks", new Object[0]);
        synchronized (this.Gu) {
            this.Gu.clear();
        }
    }

    private void fH() throws IllegalStateException {
        if (!this.GA || this.Gw == null || this.Gw.fM()) {
            throw new IllegalStateException("Not connected to a device");
        }
    }

    public void G(boolean z) throws IllegalStateException, RemoteException {
        gS().a(z, this.FA, this.FB);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.d
    /* renamed from: L, reason: merged with bridge method [inline-methods] */
    public in j(IBinder iBinder) {
        return in.a.M(iBinder);
    }

    public void a(double d) throws IllegalStateException, RemoteException, IllegalArgumentException {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            throw new IllegalArgumentException("Volume cannot be " + d);
        }
        gS().a(d, this.FA, this.FB);
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(int i, IBinder iBinder, Bundle bundle) {
        Gr.b("in onPostInitHandler; statusCode=%d", Integer.valueOf(i));
        if (i == 0 || i == 1001) {
            this.GA = true;
            this.Gy = true;
            this.Gz = true;
        } else {
            this.GA = false;
        }
        if (i == 1001) {
            this.GG = new Bundle();
            this.GG.putBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING, true);
            i = 0;
        }
        super.a(i, iBinder, bundle);
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(com.google.android.gms.common.internal.k kVar, d.e eVar) throws RemoteException {
        Bundle bundle = new Bundle();
        Gr.b("getServiceFromBroker(): mLastApplicationId=%s, mLastSessionId=%s", this.GE, this.GF);
        this.Gt.putInBundle(bundle);
        bundle.putLong("com.google.android.gms.cast.EXTRA_CAST_FLAGS", this.Gv);
        if (this.GE != null) {
            bundle.putString("last_application_id", this.GE);
            if (this.GF != null) {
                bundle.putString("last_session_id", this.GF);
            }
        }
        this.Gw = new c();
        kVar.a(eVar, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, getContext().getPackageName(), this.Gw.asBinder(), bundle);
    }

    public void a(String str, Cast.MessageReceivedCallback messageReceivedCallback) throws IllegalStateException, RemoteException, IllegalArgumentException {
        ik.aF(str);
        aE(str);
        if (messageReceivedCallback != null) {
            synchronized (this.Gu) {
                this.Gu.put(str, messageReceivedCallback);
            }
            gS().aI(str);
        }
    }

    public void a(String str, LaunchOptions launchOptions, BaseImplementation.b<Cast.ApplicationConnectionResult> bVar) throws IllegalStateException, RemoteException {
        c(bVar);
        gS().a(str, launchOptions);
    }

    public void a(String str, BaseImplementation.b<Status> bVar) throws IllegalStateException, RemoteException {
        e(bVar);
        gS().aH(str);
    }

    public void a(String str, String str2, BaseImplementation.b<Status> bVar) throws IllegalStateException, RemoteException, IllegalArgumentException {
        if (TextUtils.isEmpty(str2)) {
            throw new IllegalArgumentException("The message payload cannot be null or empty");
        }
        if (str2.length() > 65536) {
            throw new IllegalArgumentException("Message exceeds maximum size");
        }
        ik.aF(str);
        fH();
        long jIncrementAndGet = this.GD.incrementAndGet();
        try {
            this.GH.put(Long.valueOf(jIncrementAndGet), bVar);
            gS().a(str, str2, jIncrementAndGet);
        } catch (Throwable th) {
            this.GH.remove(Long.valueOf(jIncrementAndGet));
            throw th;
        }
    }

    public void a(String str, boolean z, BaseImplementation.b<Cast.ApplicationConnectionResult> bVar) throws IllegalStateException, RemoteException {
        c(bVar);
        gS().f(str, z);
    }

    public void aE(String str) throws RemoteException, IllegalArgumentException {
        Cast.MessageReceivedCallback messageReceivedCallbackRemove;
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Channel namespace cannot be null or empty");
        }
        synchronized (this.Gu) {
            messageReceivedCallbackRemove = this.Gu.remove(str);
        }
        if (messageReceivedCallbackRemove != null) {
            try {
                gS().aJ(str);
            } catch (IllegalStateException e) {
                Gr.a(e, "Error unregistering namespace (%s): %s", str, e.getMessage());
            }
        }
    }

    public void b(String str, String str2, BaseImplementation.b<Cast.ApplicationConnectionResult> bVar) throws IllegalStateException, RemoteException {
        c(bVar);
        gS().l(str, str2);
    }

    public void d(BaseImplementation.b<Status> bVar) throws IllegalStateException, RemoteException {
        e(bVar);
        gS().fQ();
    }

    @Override // com.google.android.gms.common.internal.d, com.google.android.gms.common.api.Api.a
    public void disconnect() {
        Gr.b("disconnect(); ServiceListener=%s, isConnected=%b", this.Gw, Boolean.valueOf(isConnected()));
        c cVar = this.Gw;
        this.Gw = null;
        if (cVar == null || !cVar.fL()) {
            Gr.b("already disposed, so short-circuiting", new Object[0]);
            return;
        }
        fG();
        try {
            if (isConnected() || isConnecting()) {
                gS().disconnect();
            }
        } catch (RemoteException e) {
            Gr.a(e, "Error while disconnecting the controller interface: %s", e.getMessage());
        } finally {
            super.disconnect();
        }
    }

    @Override // com.google.android.gms.common.internal.d, com.google.android.gms.common.internal.e.b
    public Bundle fD() {
        if (this.GG == null) {
            return super.fD();
        }
        Bundle bundle = this.GG;
        this.GG = null;
        return bundle;
    }

    public void fE() throws IllegalStateException, RemoteException {
        gS().fE();
    }

    public double fF() throws IllegalStateException {
        fH();
        return this.FA;
    }

    public ApplicationMetadata getApplicationMetadata() throws IllegalStateException {
        fH();
        return this.Gs;
    }

    public String getApplicationStatus() throws IllegalStateException {
        fH();
        return this.Gx;
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return "com.google.android.gms.cast.internal.ICastDeviceController";
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return "com.google.android.gms.cast.service.BIND_CAST_DEVICE_CONTROLLER_SERVICE";
    }

    public boolean isMute() throws IllegalStateException {
        fH();
        return this.FB;
    }
}
