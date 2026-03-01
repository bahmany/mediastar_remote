package com.google.android.gms.fitness.service;

import android.app.AppOpsManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.internal.kc;
import com.google.android.gms.internal.km;
import com.google.android.gms.internal.ks;
import com.google.android.gms.internal.lf;
import com.google.android.gms.internal.lh;
import com.google.android.gms.internal.lj;
import java.util.List;

/* loaded from: classes.dex */
public abstract class FitnessSensorService extends Service {
    public static final String SERVICE_ACTION = "com.google.android.gms.fitness.service.FitnessSensorService";
    private a UP;

    private static class a extends lj.a {
        private final FitnessSensorService UQ;

        private a(FitnessSensorService fitnessSensorService) {
            this.UQ = fitnessSensorService;
        }

        private void jK() throws SecurityException {
            int callingUid = Binder.getCallingUid();
            if (kc.hI()) {
                ((AppOpsManager) this.UQ.getSystemService("appops")).checkPackage(callingUid, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE);
                return;
            }
            String[] packagesForUid = this.UQ.getPackageManager().getPackagesForUid(callingUid);
            if (packagesForUid != null) {
                for (String str : packagesForUid) {
                    if (str.equals(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE)) {
                        return;
                    }
                }
            }
            throw new SecurityException("Unauthorized caller");
        }

        @Override // com.google.android.gms.internal.lj
        public void a(FitnessSensorServiceRequest fitnessSensorServiceRequest, ks ksVar) throws SecurityException, RemoteException {
            jK();
            if (this.UQ.onRegister(fitnessSensorServiceRequest)) {
                ksVar.k(Status.Jo);
            } else {
                ksVar.k(new Status(13));
            }
        }

        @Override // com.google.android.gms.internal.lj
        public void a(lf lfVar, km kmVar) throws SecurityException, RemoteException {
            jK();
            kmVar.a(new DataSourcesResult(this.UQ.onFindDataSources(lfVar.getDataTypes()), Status.Jo));
        }

        @Override // com.google.android.gms.internal.lj
        public void a(lh lhVar, ks ksVar) throws SecurityException, RemoteException {
            jK();
            if (this.UQ.onUnregister(lhVar.getDataSource())) {
                ksVar.k(Status.Jo);
            } else {
                ksVar.k(new Status(13));
            }
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (!SERVICE_ACTION.equals(intent.getAction())) {
            return null;
        }
        if (Log.isLoggable("FitnessSensorService", 3)) {
            Log.d("FitnessSensorService", "Intent " + intent + " received by " + getClass().getName());
        }
        return this.UP.asBinder();
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.UP = new a();
    }

    public abstract List<DataSource> onFindDataSources(List<DataType> list);

    public abstract boolean onRegister(FitnessSensorServiceRequest fitnessSensorServiceRequest);

    public abstract boolean onUnregister(DataSource dataSource);
}
