package com.google.android.gms.internal;

import com.google.android.gms.ads.purchase.InAppPurchaseListener;
import com.google.android.gms.internal.eh;

@ez
/* loaded from: classes.dex */
public final class em extends eh.a {
    private final InAppPurchaseListener oC;

    public em(InAppPurchaseListener inAppPurchaseListener) {
        this.oC = inAppPurchaseListener;
    }

    @Override // com.google.android.gms.internal.eh
    public void a(eg egVar) {
        this.oC.onInAppPurchaseRequested(new ep(egVar));
    }
}
