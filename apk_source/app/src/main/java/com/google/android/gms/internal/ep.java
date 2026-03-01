package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.ads.purchase.InAppPurchase;

@ez
/* loaded from: classes.dex */
public class ep implements InAppPurchase {
    private final eg sx;

    public ep(eg egVar) {
        this.sx = egVar;
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchase
    public String getProductId() {
        try {
            return this.sx.getProductId();
        } catch (RemoteException e) {
            gs.d("Could not forward getProductId to InAppPurchase", e);
            return null;
        }
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchase
    public void recordPlayBillingResolution(int billingResponseCode) {
        try {
            this.sx.recordPlayBillingResolution(billingResponseCode);
        } catch (RemoteException e) {
            gs.d("Could not forward recordPlayBillingResolution to InAppPurchase", e);
        }
    }

    @Override // com.google.android.gms.ads.purchase.InAppPurchase
    public void recordResolution(int resolution) {
        try {
            this.sx.recordResolution(resolution);
        } catch (RemoteException e) {
            gs.d("Could not forward recordResolution to InAppPurchase", e);
        }
    }
}
