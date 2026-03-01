package com.google.android.gms.ads;

import android.content.Context;
import com.google.android.gms.ads.purchase.InAppPurchaseListener;
import com.google.android.gms.ads.purchase.PlayStorePurchaseListener;
import com.google.android.gms.internal.bi;

/* loaded from: classes.dex */
public final class InterstitialAd {
    private final bi lj;

    public InterstitialAd(Context context) {
        this.lj = new bi(context);
    }

    public AdListener getAdListener() {
        return this.lj.getAdListener();
    }

    public String getAdUnitId() {
        return this.lj.getAdUnitId();
    }

    public InAppPurchaseListener getInAppPurchaseListener() {
        return this.lj.getInAppPurchaseListener();
    }

    public String getMediationAdapterClassName() {
        return this.lj.getMediationAdapterClassName();
    }

    public boolean isLoaded() {
        return this.lj.isLoaded();
    }

    public void loadAd(AdRequest adRequest) {
        this.lj.a(adRequest.V());
    }

    public void setAdListener(AdListener adListener) {
        this.lj.setAdListener(adListener);
    }

    public void setAdUnitId(String adUnitId) {
        this.lj.setAdUnitId(adUnitId);
    }

    public void setInAppPurchaseListener(InAppPurchaseListener inAppPurchaseListener) {
        this.lj.setInAppPurchaseListener(inAppPurchaseListener);
    }

    public void setPlayStorePurchaseParams(PlayStorePurchaseListener playStorePurchaseListener, String publicKey) {
        this.lj.setPlayStorePurchaseParams(playStorePurchaseListener, publicKey);
    }

    public void show() {
        this.lj.show();
    }
}
