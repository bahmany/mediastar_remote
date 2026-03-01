package com.google.android.gms.internal;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.internal.bc;

@ez
/* loaded from: classes.dex */
public final class at extends bc.a {
    private final AdListener nR;

    public at(AdListener adListener) {
        this.nR = adListener;
    }

    @Override // com.google.android.gms.internal.bc
    public void onAdClosed() {
        this.nR.onAdClosed();
    }

    @Override // com.google.android.gms.internal.bc
    public void onAdFailedToLoad(int errorCode) {
        this.nR.onAdFailedToLoad(errorCode);
    }

    @Override // com.google.android.gms.internal.bc
    public void onAdLeftApplication() {
        this.nR.onAdLeftApplication();
    }

    @Override // com.google.android.gms.internal.bc
    public void onAdLoaded() {
        this.nR.onAdLoaded();
    }

    @Override // com.google.android.gms.internal.bc
    public void onAdOpened() {
        this.nR.onAdOpened();
    }
}
