package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.internal.d;
import com.google.android.gms.internal.ch;

@ez
/* loaded from: classes.dex */
public class cg extends com.google.android.gms.common.internal.d<ch> {
    final int pP;

    public cg(Context context, GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener onConnectionFailedListener, int i) {
        super(context, connectionCallbacks, onConnectionFailedListener, new String[0]);
        this.pP = i;
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(com.google.android.gms.common.internal.k kVar, d.e eVar) throws RemoteException {
        kVar.g(eVar, this.pP, getContext().getPackageName(), new Bundle());
    }

    public ch bC() {
        return (ch) super.gS();
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return "com.google.android.gms.ads.internal.gservice.IGservicesValueService";
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return "com.google.android.gms.ads.gservice.START";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.d
    /* renamed from: i, reason: merged with bridge method [inline-methods] */
    public ch j(IBinder iBinder) {
        return ch.a.k(iBinder);
    }
}
