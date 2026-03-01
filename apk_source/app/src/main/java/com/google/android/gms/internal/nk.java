package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.internal.nf;

/* loaded from: classes.dex */
public class nk implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private final nf.a akE;
    private nn aku = null;
    private boolean akF = true;

    public nk(nf.a aVar) {
        this.akE = aVar;
    }

    public void R(boolean z) {
        this.akF = z;
    }

    public void a(nn nnVar) {
        this.aku = nnVar;
    }

    @Override // com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        this.aku.S(false);
        if (this.akF && this.akE != null) {
            this.akE.mS();
        }
        this.akF = false;
    }

    @Override // com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult result) {
        this.aku.S(true);
        if (this.akF && this.akE != null) {
            if (result.hasResolution()) {
                this.akE.b(result.getResolution());
            } else {
                this.akE.mT();
            }
        }
        this.akF = false;
    }

    @Override // com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks
    public void onDisconnected() {
        this.aku.S(true);
    }
}
