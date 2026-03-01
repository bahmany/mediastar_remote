package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.internal.d;
import com.google.android.gms.internal.fm;

@ez
/* loaded from: classes.dex */
public class fh extends com.google.android.gms.common.internal.d<fm> {
    final int pP;

    public fh(Context context, GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener onConnectionFailedListener, int i) {
        super(context, connectionCallbacks, onConnectionFailedListener, new String[0]);
        this.pP = i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.d
    /* renamed from: C, reason: merged with bridge method [inline-methods] */
    public fm j(IBinder iBinder) {
        return fm.a.D(iBinder);
    }

    @Override // com.google.android.gms.common.internal.d
    protected void a(com.google.android.gms.common.internal.k kVar, d.e eVar) throws RemoteException {
        kVar.g(eVar, this.pP, getContext().getPackageName(), new Bundle());
    }

    public fm cF() {
        return (fm) super.gS();
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getServiceDescriptor() {
        return "com.google.android.gms.ads.internal.request.IAdRequestService";
    }

    @Override // com.google.android.gms.common.internal.d
    protected String getStartServiceAction() {
        return "com.google.android.gms.ads.service.START";
    }
}
