package com.google.android.gms.internal;

import com.google.android.gms.ads.purchase.PlayStorePurchaseListener;
import com.google.android.gms.internal.el;

@ez
/* loaded from: classes.dex */
public final class eq extends el.a {
    private final PlayStorePurchaseListener oD;

    public eq(PlayStorePurchaseListener playStorePurchaseListener) {
        this.oD = playStorePurchaseListener;
    }

    @Override // com.google.android.gms.internal.el
    public void a(ek ekVar) {
        this.oD.onInAppPurchaseFinished(new eo(ekVar));
    }

    @Override // com.google.android.gms.internal.el
    public boolean isValidPurchase(String productId) {
        return this.oD.isValidPurchase(productId);
    }
}
