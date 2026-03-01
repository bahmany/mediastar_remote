package com.google.android.gms.internal;

import android.content.Intent;
import android.os.RemoteException;
import com.google.android.gms.ads.purchase.InAppPurchaseResult;

@ez
/* loaded from: classes.dex */
public class eo implements InAppPurchaseResult {
    private final ek sL;

    public eo(ek ekVar) {
        this.sL = ekVar;
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchaseResult
    public void finishPurchase() {
        try {
            this.sL.finishPurchase();
        } catch (RemoteException e) {
            gs.d("Could not forward finishPurchase to InAppPurchaseResult", e);
        }
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchaseResult
    public String getProductId() {
        try {
            return this.sL.getProductId();
        } catch (RemoteException e) {
            gs.d("Could not forward getProductId to InAppPurchaseResult", e);
            return null;
        }
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchaseResult
    public Intent getPurchaseData() {
        try {
            return this.sL.getPurchaseData();
        } catch (RemoteException e) {
            gs.d("Could not forward getPurchaseData to InAppPurchaseResult", e);
            return null;
        }
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchaseResult
    public int getResultCode() {
        try {
            return this.sL.getResultCode();
        } catch (RemoteException e) {
            gs.d("Could not forward getPurchaseData to InAppPurchaseResult", e);
            return 0;
        }
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchaseResult
    public boolean isVerified() {
        try {
            return this.sL.isVerified();
        } catch (RemoteException e) {
            gs.d("Could not forward isVerified to InAppPurchaseResult", e);
            return false;
        }
    }
}
