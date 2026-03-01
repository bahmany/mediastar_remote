package com.google.android.gms.drive.internal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.d;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.events.DriveEventService;
import com.google.android.gms.drive.internal.ab;
import com.google.android.gms.drive.internal.o;
import com.google.android.gms.drive.internal.p;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class q extends com.google.android.gms.common.internal.d<ab> {
    private final String Dd;
    private final String IH;
    private final Bundle Os;
    private final boolean Ot;
    private DriveId Ou;
    private DriveId Ov;
    final GoogleApiClient.ConnectionCallbacks Ow;
    final Map<DriveId, Map<com.google.android.gms.drive.events.c, y>> Ox;

    /* renamed from: com.google.android.gms.drive.internal.q$1 */
    class AnonymousClass1 extends p.a {
        final /* synthetic */ y OA;
        final /* synthetic */ DriveId Oy;
        final /* synthetic */ int Oz;

        AnonymousClass1(DriveId driveId, int i, y yVar) {
            driveId = driveId;
            i = i;
            yVar = yVar;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(q qVar) throws RemoteException {
            qVar.hY().a(new AddEventListenerRequest(driveId, i), yVar, (String) null, new bb(this));
        }
    }

    /* renamed from: com.google.android.gms.drive.internal.q$2 */
    class AnonymousClass2 extends p.a {
        final /* synthetic */ y OC;
        final /* synthetic */ DriveId Oy;
        final /* synthetic */ int Oz;

        AnonymousClass2(DriveId driveId, int i, y yVar) {
            driveId = driveId;
            i = i;
            yVar = yVar;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(q qVar) throws RemoteException {
            qVar.hY().a(new RemoveEventListenerRequest(driveId, i), yVar, (String) null, new bb(this));
        }
    }

    /* renamed from: com.google.android.gms.drive.internal.q$3 */
    class AnonymousClass3 extends p.a {
        final /* synthetic */ DriveId Oy;
        final /* synthetic */ int Oz;

        AnonymousClass3(DriveId driveId, int i) {
            driveId = driveId;
            i = i;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(q qVar) throws RemoteException {
            qVar.hY().a(new AddEventListenerRequest(driveId, i), (ad) null, (String) null, new bb(this));
        }
    }

    /* renamed from: com.google.android.gms.drive.internal.q$4 */
    class AnonymousClass4 extends p.a {
        final /* synthetic */ DriveId Oy;
        final /* synthetic */ int Oz;

        AnonymousClass4(DriveId driveId, int i) {
            driveId = driveId;
            i = i;
        }

        @Override // com.google.android.gms.common.api.BaseImplementation.a
        public void a(q qVar) throws RemoteException {
            qVar.hY().a(new RemoveEventListenerRequest(driveId, i), (ad) null, (String) null, new bb(this));
        }
    }

    public q(Context context, Looper looper, ClientSettings clientSettings, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener, String[] strArr, Bundle bundle) {
        super(context, looper, connectionCallbacks, onConnectionFailedListener, strArr);
        this.Ox = new HashMap();
        this.Dd = (String) com.google.android.gms.common.internal.n.b(clientSettings.getAccountNameOrDefault(), (Object) "Must call Api.ClientBuilder.setAccountName()");
        this.IH = clientSettings.getRealClientPackageName();
        this.Ow = connectionCallbacks;
        this.Os = bundle;
        Intent intent = new Intent(DriveEventService.ACTION_HANDLE_EVENT);
        intent.setPackage(context.getPackageName());
        List<ResolveInfo> listQueryIntentServices = context.getPackageManager().queryIntentServices(intent, 0);
        switch (listQueryIntentServices.size()) {
            case 0:
                this.Ot = false;
                return;
            case 1:
                ServiceInfo serviceInfo = listQueryIntentServices.get(0).serviceInfo;
                if (!serviceInfo.exported) {
                    throw new IllegalStateException("Drive event service " + serviceInfo.name + " must be exported in AndroidManifest.xml");
                }
                this.Ot = true;
                return;
            default:
                throw new IllegalStateException("AndroidManifest.xml can only define one service that handles the " + intent.getAction() + " action");
        }
    }

    @Override // com.google.android.gms.common.internal.d
    /* renamed from: T */
    public ab j(IBinder iBinder) {
        return ab.a.U(iBinder);
    }

    PendingResult<Status> a(GoogleApiClient googleApiClient, DriveId driveId, int i) {
        com.google.android.gms.common.internal.n.b(com.google.android.gms.drive.events.d.a(i, driveId), "id");
        com.google.android.gms.common.internal.n.i("eventService");
        com.google.android.gms.common.internal.n.a(isConnected(), "Client must be connected");
        if (this.Ot) {
            return googleApiClient.b(new p.a() { // from class: com.google.android.gms.drive.internal.q.3
                final /* synthetic */ DriveId Oy;
                final /* synthetic */ int Oz;

                AnonymousClass3(DriveId driveId2, int i2) {
                    driveId = driveId2;
                    i = i2;
                }

                @Override // com.google.android.gms.common.api.BaseImplementation.a
                public void a(q qVar) throws RemoteException {
                    qVar.hY().a(new AddEventListenerRequest(driveId, i), (ad) null, (String) null, new bb(this));
                }
            });
        }
        throw new IllegalStateException("Application must define an exported DriveEventService subclass in AndroidManifest.xml to add event subscriptions");
    }

    PendingResult<Status> a(GoogleApiClient googleApiClient, DriveId driveId, int i, com.google.android.gms.drive.events.c cVar) {
        Map<com.google.android.gms.drive.events.c, y> map;
        PendingResult<Status> mVar;
        com.google.android.gms.common.internal.n.b(com.google.android.gms.drive.events.d.a(i, driveId), "id");
        com.google.android.gms.common.internal.n.b(cVar, "listener");
        com.google.android.gms.common.internal.n.a(isConnected(), "Client must be connected");
        synchronized (this.Ox) {
            Map<com.google.android.gms.drive.events.c, y> map2 = this.Ox.get(driveId);
            if (map2 == null) {
                HashMap map3 = new HashMap();
                this.Ox.put(driveId, map3);
                map = map3;
            } else {
                map = map2;
            }
            y yVar = map.get(cVar);
            if (yVar == null) {
                yVar = new y(getLooper(), getContext(), i, cVar);
                map.put(cVar, yVar);
            } else if (yVar.br(i)) {
                mVar = new o.m(googleApiClient, Status.Jo);
            }
            yVar.bq(i);
            mVar = googleApiClient.b(new p.a() { // from class: com.google.android.gms.drive.internal.q.1
                final /* synthetic */ y OA;
                final /* synthetic */ DriveId Oy;
                final /* synthetic */ int Oz;

                AnonymousClass1(DriveId driveId2, int i2, y yVar2) {
                    driveId = driveId2;
                    i = i2;
                    yVar = yVar2;
                }

                @Override // com.google.android.gms.common.api.BaseImplementation.a
                public void a(q qVar) throws RemoteException {
                    qVar.hY().a(new AddEventListenerRequest(driveId, i), yVar, (String) null, new bb(this));
                }
            });
        }
        return mVar;
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(int i, IBinder iBinder, Bundle bundle) {
        if (bundle != null) {
            bundle.setClassLoader(getClass().getClassLoader());
            this.Ou = (DriveId) bundle.getParcelable("com.google.android.gms.drive.root_id");
            this.Ov = (DriveId) bundle.getParcelable("com.google.android.gms.drive.appdata_id");
        }
        super.a(i, iBinder, bundle);
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(com.google.android.gms.common.internal.k kVar, d.e eVar) throws RemoteException {
        String packageName = getContext().getPackageName();
        com.google.android.gms.common.internal.n.i(eVar);
        com.google.android.gms.common.internal.n.i(packageName);
        com.google.android.gms.common.internal.n.i(gR());
        Bundle bundle = new Bundle();
        if (!packageName.equals(this.IH)) {
            bundle.putString("proxy_package_name", this.IH);
        }
        bundle.putAll(this.Os);
        kVar.a(eVar, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, packageName, gR(), this.Dd, bundle);
    }

    PendingResult<Status> b(GoogleApiClient googleApiClient, DriveId driveId, int i) {
        com.google.android.gms.common.internal.n.b(com.google.android.gms.drive.events.d.a(i, driveId), "id");
        com.google.android.gms.common.internal.n.i("eventService");
        com.google.android.gms.common.internal.n.a(isConnected(), "Client must be connected");
        return googleApiClient.b(new p.a() { // from class: com.google.android.gms.drive.internal.q.4
            final /* synthetic */ DriveId Oy;
            final /* synthetic */ int Oz;

            AnonymousClass4(DriveId driveId2, int i2) {
                driveId = driveId2;
                i = i2;
            }

            @Override // com.google.android.gms.common.api.BaseImplementation.a
            public void a(q qVar) throws RemoteException {
                qVar.hY().a(new RemoveEventListenerRequest(driveId, i), (ad) null, (String) null, new bb(this));
            }
        });
    }

    PendingResult<Status> b(GoogleApiClient googleApiClient, DriveId driveId, int i, com.google.android.gms.drive.events.c cVar) {
        PendingResult<Status> pendingResultB;
        com.google.android.gms.common.internal.n.b(com.google.android.gms.drive.events.d.a(i, driveId), "id");
        com.google.android.gms.common.internal.n.a(isConnected(), "Client must be connected");
        com.google.android.gms.common.internal.n.b(cVar, "listener");
        synchronized (this.Ox) {
            Map<com.google.android.gms.drive.events.c, y> map = this.Ox.get(driveId);
            if (map == null) {
                pendingResultB = new o.m(googleApiClient, Status.Jo);
            } else {
                y yVarRemove = map.remove(cVar);
                if (yVarRemove == null) {
                    pendingResultB = new o.m(googleApiClient, Status.Jo);
                } else {
                    if (map.isEmpty()) {
                        this.Ox.remove(driveId);
                    }
                    pendingResultB = googleApiClient.b(new p.a() { // from class: com.google.android.gms.drive.internal.q.2
                        final /* synthetic */ y OC;
                        final /* synthetic */ DriveId Oy;
                        final /* synthetic */ int Oz;

                        AnonymousClass2(DriveId driveId2, int i2, y yVarRemove2) {
                            driveId = driveId2;
                            i = i2;
                            yVar = yVarRemove2;
                        }

                        @Override // com.google.android.gms.common.api.BaseImplementation.a
                        public void a(q qVar) throws RemoteException {
                            qVar.hY().a(new RemoveEventListenerRequest(driveId, i), yVar, (String) null, new bb(this));
                        }
                    });
                }
            }
        }
        return pendingResultB;
    }

    @Override // com.google.android.gms.common.internal.d, com.google.android.gms.common.api.Api.a
    public void disconnect() {
        ab abVarGS = gS();
        if (abVarGS != null) {
            try {
                abVarGS.a(new DisconnectRequest());
            } catch (RemoteException e) {
            }
        }
        super.disconnect();
        this.Ox.clear();
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return "com.google.android.gms.drive.internal.IDriveService";
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return "com.google.android.gms.drive.ApiService.START";
    }

    public ab hY() {
        return gS();
    }

    public DriveId hZ() {
        return this.Ou;
    }

    public DriveId ia() {
        return this.Ov;
    }

    public boolean ib() {
        return this.Ot;
    }
}
