package com.google.android.gms.auth.api;

import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import com.google.android.gms.auth.api.IGoogleAuthService;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.d;
import com.google.android.gms.common.internal.k;

/* loaded from: classes.dex */
public final class GoogleAuthApiClientImpl extends d<IGoogleAuthService> {
    public static final String ACTION_START_SERVICE = "com.google.android.gms.auth.service.START";
    public static final String SERVICE_DESCRIPTOR = "com.google.android.gms.auth.api.IGoogleAuthService";
    private final String Dd;
    private String[] Ds;

    public GoogleAuthApiClientImpl(Context context, Looper looper, ClientSettings settings, GoogleApiClient.ConnectionCallbacks connectedListener, GoogleApiClient.OnConnectionFailedListener connectionFailedListener, String accountName, String[] scopes) {
        super(context, looper, connectedListener, connectionFailedListener, scopes);
        this.Dd = accountName;
        this.Ds = scopes;
    }

    public GoogleAuthApiClientImpl(Context context, ClientSettings settings, GoogleApiClient.ConnectionCallbacks connectedListener, GoogleApiClient.OnConnectionFailedListener connectionFailedListener, String accountName, String[] scopes) {
        this(context, context.getMainLooper(), settings, connectedListener, connectionFailedListener, accountName, scopes);
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(k kVar, d.e eVar) throws RemoteException {
        kVar.b(eVar, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE, getContext().getPackageName(), this.Dd, gR());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.d
    /* renamed from: createServiceInterface, reason: merged with bridge method [inline-methods] */
    public IGoogleAuthService j(IBinder iBinder) {
        return IGoogleAuthService.Stub.asInterface(iBinder);
    }

    public String getAccountName() {
        return this.Dd;
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return SERVICE_DESCRIPTOR;
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return ACTION_START_SERVICE;
    }
}
