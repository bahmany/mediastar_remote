package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.appstate.AppStateBuffer;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.d;
import com.google.android.gms.internal.id;

/* loaded from: classes.dex */
public final class ib extends com.google.android.gms.common.internal.d<id> {
    private final String Dd;

    private static final class a extends ia {
        private final BaseImplementation.b<AppStateManager.StateDeletedResult> De;

        public a(BaseImplementation.b<AppStateManager.StateDeletedResult> bVar) {
            this.De = (BaseImplementation.b) com.google.android.gms.common.internal.n.b(bVar, "Result holder must not be null");
        }

        @Override // com.google.android.gms.internal.ia, com.google.android.gms.internal.ic
        public void e(int i, int i2) {
            this.De.b(new b(new Status(i), i2));
        }
    }

    private static final class b implements AppStateManager.StateDeletedResult {
        private final Status CM;
        private final int Df;

        public b(Status status, int i) {
            this.CM = status;
            this.Df = i;
        }

        @Override // com.google.android.gms.appstate.AppStateManager.StateDeletedResult
        public int getStateKey() {
            return this.Df;
        }

        @Override // com.google.android.gms.common.api.Result
        public Status getStatus() {
            return this.CM;
        }
    }

    private static final class c extends ia {
        private final BaseImplementation.b<AppStateManager.StateListResult> De;

        public c(BaseImplementation.b<AppStateManager.StateListResult> bVar) {
            this.De = (BaseImplementation.b) com.google.android.gms.common.internal.n.b(bVar, "Result holder must not be null");
        }

        @Override // com.google.android.gms.internal.ia, com.google.android.gms.internal.ic
        public void a(DataHolder dataHolder) {
            this.De.b(new d(dataHolder));
        }
    }

    private static final class d extends com.google.android.gms.common.api.a implements AppStateManager.StateListResult {
        private final AppStateBuffer Dg;

        public d(DataHolder dataHolder) {
            super(dataHolder);
            this.Dg = new AppStateBuffer(dataHolder);
        }

        @Override // com.google.android.gms.appstate.AppStateManager.StateListResult
        public AppStateBuffer getStateBuffer() {
            return this.Dg;
        }
    }

    private static final class e extends ia {
        private final BaseImplementation.b<AppStateManager.StateResult> De;

        public e(BaseImplementation.b<AppStateManager.StateResult> bVar) {
            this.De = (BaseImplementation.b) com.google.android.gms.common.internal.n.b(bVar, "Result holder must not be null");
        }

        @Override // com.google.android.gms.internal.ia, com.google.android.gms.internal.ic
        public void a(int i, DataHolder dataHolder) {
            this.De.b(new f(i, dataHolder));
        }
    }

    private static final class f extends com.google.android.gms.common.api.a implements AppStateManager.StateConflictResult, AppStateManager.StateLoadedResult, AppStateManager.StateResult {
        private final int Df;
        private final AppStateBuffer Dg;

        public f(int i, DataHolder dataHolder) {
            super(dataHolder);
            this.Df = i;
            this.Dg = new AppStateBuffer(dataHolder);
        }

        private boolean ft() {
            return this.CM.getStatusCode() == 2000;
        }

        @Override // com.google.android.gms.appstate.AppStateManager.StateResult
        public AppStateManager.StateConflictResult getConflictResult() {
            if (ft()) {
                return this;
            }
            return null;
        }

        @Override // com.google.android.gms.appstate.AppStateManager.StateResult
        public AppStateManager.StateLoadedResult getLoadedResult() {
            if (ft()) {
                return null;
            }
            return this;
        }

        @Override // com.google.android.gms.appstate.AppStateManager.StateConflictResult, com.google.android.gms.appstate.AppStateManager.StateLoadedResult
        public byte[] getLocalData() {
            if (this.Dg.getCount() == 0) {
                return null;
            }
            return this.Dg.get(0).getLocalData();
        }

        @Override // com.google.android.gms.appstate.AppStateManager.StateConflictResult
        public String getResolvedVersion() {
            if (this.Dg.getCount() == 0) {
                return null;
            }
            return this.Dg.get(0).getConflictVersion();
        }

        @Override // com.google.android.gms.appstate.AppStateManager.StateConflictResult
        public byte[] getServerData() {
            if (this.Dg.getCount() == 0) {
                return null;
            }
            return this.Dg.get(0).getConflictData();
        }

        @Override // com.google.android.gms.appstate.AppStateManager.StateConflictResult, com.google.android.gms.appstate.AppStateManager.StateLoadedResult
        public int getStateKey() {
            return this.Df;
        }

        @Override // com.google.android.gms.common.api.a, com.google.android.gms.common.api.Releasable
        public void release() {
            this.Dg.close();
        }
    }

    private static final class g extends ia {
        private final BaseImplementation.b<Status> De;

        public g(BaseImplementation.b<Status> bVar) {
            this.De = (BaseImplementation.b) com.google.android.gms.common.internal.n.b(bVar, "Holder must not be null");
        }

        @Override // com.google.android.gms.internal.ia, com.google.android.gms.internal.ic
        public void fq() {
            this.De.b(new Status(0));
        }
    }

    public ib(Context context, Looper looper, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener, String str, String[] strArr) {
        super(context, looper, connectionCallbacks, onConnectionFailedListener, strArr);
        this.Dd = (String) com.google.android.gms.common.internal.n.i(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.d
    /* renamed from: I, reason: merged with bridge method [inline-methods] */
    public id j(IBinder iBinder) {
        return id.a.K(iBinder);
    }

    public void a(BaseImplementation.b<AppStateManager.StateListResult> bVar) {
        try {
            gS().a(new c(bVar));
        } catch (RemoteException e2) {
            Log.w("AppStateClient", "service died");
        }
    }

    public void a(BaseImplementation.b<AppStateManager.StateDeletedResult> bVar, int i) {
        try {
            gS().b(new a(bVar), i);
        } catch (RemoteException e2) {
            Log.w("AppStateClient", "service died");
        }
    }

    public void a(BaseImplementation.b<AppStateManager.StateResult> bVar, int i, String str, byte[] bArr) {
        try {
            gS().a(new e(bVar), i, str, bArr);
        } catch (RemoteException e2) {
            Log.w("AppStateClient", "service died");
        }
    }

    public void a(BaseImplementation.b<AppStateManager.StateResult> bVar, int i, byte[] bArr) {
        e eVar;
        if (bVar == null) {
            eVar = null;
        } else {
            try {
                eVar = new e(bVar);
            } catch (RemoteException e2) {
                Log.w("AppStateClient", "service died");
                return;
            }
        }
        gS().a(eVar, i, bArr);
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(com.google.android.gms.common.internal.k kVar, d.e eVar) throws RemoteException {
        kVar.a(eVar, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, getContext().getPackageName(), this.Dd, gR());
    }

    public void b(BaseImplementation.b<Status> bVar) {
        try {
            gS().b(new g(bVar));
        } catch (RemoteException e2) {
            Log.w("AppStateClient", "service died");
        }
    }

    public void b(BaseImplementation.b<AppStateManager.StateResult> bVar, int i) {
        try {
            gS().a(new e(bVar), i);
        } catch (RemoteException e2) {
            Log.w("AppStateClient", "service died");
        }
    }

    @Override // com.google.android.gms.common.internal.d
    protected void c(String... strArr) {
        boolean z = false;
        for (String str : strArr) {
            if (str.equals(Scopes.APP_STATE)) {
                z = true;
            }
        }
        com.google.android.gms.common.internal.n.a(z, String.format("App State APIs requires %s to function.", Scopes.APP_STATE));
    }

    public int fr() {
        try {
            return gS().fr();
        } catch (RemoteException e2) {
            Log.w("AppStateClient", "service died");
            return 2;
        }
    }

    public int fs() {
        try {
            return gS().fs();
        } catch (RemoteException e2) {
            Log.w("AppStateClient", "service died");
            return 2;
        }
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return "com.google.android.gms.appstate.internal.IAppStateService";
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return "com.google.android.gms.appstate.service.START";
    }
}
