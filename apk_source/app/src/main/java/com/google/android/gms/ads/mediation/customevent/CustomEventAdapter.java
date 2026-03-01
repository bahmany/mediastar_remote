package com.google.android.gms.ads.mediation.customevent;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.MediationBannerAdapter;
import com.google.android.gms.ads.mediation.MediationBannerListener;
import com.google.android.gms.ads.mediation.MediationInterstitialAdapter;
import com.google.android.gms.ads.mediation.MediationInterstitialListener;
import com.google.android.gms.internal.gs;

/* loaded from: classes.dex */
public final class CustomEventAdapter implements MediationBannerAdapter, MediationInterstitialAdapter {
    private View n;
    private CustomEventBanner xf;
    private CustomEventInterstitial xg;

    private static final class a implements CustomEventBannerListener {
        private final MediationBannerListener l;
        private final CustomEventAdapter xh;

        public a(CustomEventAdapter customEventAdapter, MediationBannerListener mediationBannerListener) {
            this.xh = customEventAdapter;
            this.l = mediationBannerListener;
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public void onAdClicked() {
            gs.S("Custom event adapter called onAdClicked.");
            this.l.onAdClicked(this.xh);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public void onAdClosed() {
            gs.S("Custom event adapter called onAdClosed.");
            this.l.onAdClosed(this.xh);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public void onAdFailedToLoad(int errorCode) {
            gs.S("Custom event adapter called onAdFailedToLoad.");
            this.l.onAdFailedToLoad(this.xh, errorCode);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public void onAdLeftApplication() {
            gs.S("Custom event adapter called onAdLeftApplication.");
            this.l.onAdLeftApplication(this.xh);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener
        public void onAdLoaded(View view) {
            gs.S("Custom event adapter called onAdLoaded.");
            this.xh.a(view);
            this.l.onAdLoaded(this.xh);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public void onAdOpened() {
            gs.S("Custom event adapter called onAdOpened.");
            this.l.onAdOpened(this.xh);
        }
    }

    private class b implements CustomEventInterstitialListener {
        private final MediationInterstitialListener m;
        private final CustomEventAdapter xh;

        public b(CustomEventAdapter customEventAdapter, MediationInterstitialListener mediationInterstitialListener) {
            this.xh = customEventAdapter;
            this.m = mediationInterstitialListener;
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public void onAdClicked() {
            gs.S("Custom event adapter called onAdClicked.");
            this.m.onAdClicked(this.xh);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public void onAdClosed() {
            gs.S("Custom event adapter called onAdClosed.");
            this.m.onAdClosed(this.xh);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public void onAdFailedToLoad(int errorCode) {
            gs.S("Custom event adapter called onFailedToReceiveAd.");
            this.m.onAdFailedToLoad(this.xh, errorCode);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public void onAdLeftApplication() {
            gs.S("Custom event adapter called onAdLeftApplication.");
            this.m.onAdLeftApplication(this.xh);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener
        public void onAdLoaded() {
            gs.S("Custom event adapter called onReceivedAd.");
            this.m.onAdLoaded(CustomEventAdapter.this);
        }

        @Override // com.google.android.gms.ads.mediation.customevent.CustomEventListener
        public void onAdOpened() {
            gs.S("Custom event adapter called onAdOpened.");
            this.m.onAdOpened(this.xh);
        }
    }

    private static <T> T a(String str) {
        try {
            return (T) Class.forName(str).newInstance();
        } catch (Throwable th) {
            gs.W("Could not instantiate custom event adapter: " + str + ". " + th.getMessage());
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(View view) {
        this.n = view;
    }

    @Override // com.google.android.gms.ads.mediation.MediationBannerAdapter
    public View getBannerView() {
        return this.n;
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdapter
    public void onDestroy() {
        if (this.xf != null) {
            this.xf.onDestroy();
        }
        if (this.xg != null) {
            this.xg.onDestroy();
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdapter
    public void onPause() {
        if (this.xf != null) {
            this.xf.onPause();
        }
        if (this.xg != null) {
            this.xg.onPause();
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationAdapter
    public void onResume() {
        if (this.xf != null) {
            this.xf.onResume();
        }
        if (this.xg != null) {
            this.xg.onResume();
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationBannerAdapter
    public void requestBannerAd(Context context, MediationBannerListener listener, Bundle serverParameters, AdSize adSize, MediationAdRequest mediationAdRequest, Bundle customEventExtras) {
        this.xf = (CustomEventBanner) a(serverParameters.getString("class_name"));
        if (this.xf == null) {
            listener.onAdFailedToLoad(this, 0);
        } else {
            this.xf.requestBannerAd(context, new a(this, listener), serverParameters.getString("parameter"), adSize, mediationAdRequest, customEventExtras == null ? null : customEventExtras.getBundle(serverParameters.getString("class_name")));
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialAdapter
    public void requestInterstitialAd(Context context, MediationInterstitialListener listener, Bundle serverParameters, MediationAdRequest mediationAdRequest, Bundle customEventExtras) {
        this.xg = (CustomEventInterstitial) a(serverParameters.getString("class_name"));
        if (this.xg == null) {
            listener.onAdFailedToLoad(this, 0);
        } else {
            this.xg.requestInterstitialAd(context, new b(this, listener), serverParameters.getString("parameter"), mediationAdRequest, customEventExtras == null ? null : customEventExtras.getBundle(serverParameters.getString("class_name")));
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialAdapter
    public void showInterstitial() {
        this.xg.showInterstitial();
    }
}
