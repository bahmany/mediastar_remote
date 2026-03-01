package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.d;
import com.google.android.gms.internal.ko;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public class kk extends com.google.android.gms.common.internal.d<ko> implements kj {
    private static final Set<String> Tm = Collections.unmodifiableSet(new HashSet<String>() { // from class: com.google.android.gms.internal.kk.1
        {
            add("https://www.googleapis.com/auth/fitness.activity.read");
            add("https://www.googleapis.com/auth/fitness.activity.write");
            add("https://www.googleapis.com/auth/fitness.body.read");
            add("https://www.googleapis.com/auth/fitness.body.write");
            add("https://www.googleapis.com/auth/fitness.location.read");
            add("https://www.googleapis.com/auth/fitness.location.write");
        }
    });
    private final String Dd;

    public kk(Context context, Looper looper, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener, String str, String[] strArr) {
        super(context, looper, connectionCallbacks, onConnectionFailedListener, strArr);
        this.Dd = str;
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(com.google.android.gms.common.internal.k kVar, d.e eVar) throws RemoteException {
        kVar.a(eVar, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, getContext().getPackageName(), this.Dd, gR(), new Bundle());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.d
    /* renamed from: ao, reason: merged with bridge method [inline-methods] */
    public ko j(IBinder iBinder) {
        return ko.a.as(iBinder);
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return "com.google.android.gms.fitness.internal.IGoogleFitnessService";
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return "com.google.android.gms.fitness.GoogleFitnessService.START";
    }

    @Override // com.google.android.gms.internal.kj
    public ko iT() {
        return gS();
    }
}
