package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.ads.mediation.MediationBannerAdapter;
import com.google.android.gms.ads.mediation.MediationBannerListener;
import com.google.android.gms.ads.mediation.MediationInterstitialAdapter;
import com.google.android.gms.ads.mediation.MediationInterstitialListener;

@ez
/* loaded from: classes.dex */
public final class cy implements MediationBannerListener, MediationInterstitialListener {
    private final cv qF;

    public cy(cv cvVar) {
        this.qF = cvVar;
    }

    @Override // com.google.android.gms.ads.mediation.MediationBannerListener
    public void onAdClicked(MediationBannerAdapter adapter) {
        com.google.android.gms.common.internal.n.aT("onAdClicked must be called on the main UI thread.");
        gs.S("Adapter called onAdClicked.");
        try {
            this.qF.onAdClicked();
        } catch (RemoteException e) {
            gs.d("Could not call onAdClicked.", e);
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialListener
    public void onAdClicked(MediationInterstitialAdapter adapter) {
        com.google.android.gms.common.internal.n.aT("onAdClicked must be called on the main UI thread.");
        gs.S("Adapter called onAdClicked.");
        try {
            this.qF.onAdClicked();
        } catch (RemoteException e) {
            gs.d("Could not call onAdClicked.", e);
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationBannerListener
    public void onAdClosed(MediationBannerAdapter adapter) {
        com.google.android.gms.common.internal.n.aT("onAdClosed must be called on the main UI thread.");
        gs.S("Adapter called onAdClosed.");
        try {
            this.qF.onAdClosed();
        } catch (RemoteException e) {
            gs.d("Could not call onAdClosed.", e);
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialListener
    public void onAdClosed(MediationInterstitialAdapter adapter) {
        com.google.android.gms.common.internal.n.aT("onAdClosed must be called on the main UI thread.");
        gs.S("Adapter called onAdClosed.");
        try {
            this.qF.onAdClosed();
        } catch (RemoteException e) {
            gs.d("Could not call onAdClosed.", e);
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationBannerListener
    public void onAdFailedToLoad(MediationBannerAdapter adapter, int errorCode) {
        com.google.android.gms.common.internal.n.aT("onAdFailedToLoad must be called on the main UI thread.");
        gs.S("Adapter called onAdFailedToLoad with error. " + errorCode);
        try {
            this.qF.onAdFailedToLoad(errorCode);
        } catch (RemoteException e) {
            gs.d("Could not call onAdFailedToLoad.", e);
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialListener
    public void onAdFailedToLoad(MediationInterstitialAdapter adapter, int errorCode) {
        com.google.android.gms.common.internal.n.aT("onAdFailedToLoad must be called on the main UI thread.");
        gs.S("Adapter called onAdFailedToLoad with error " + errorCode + ".");
        try {
            this.qF.onAdFailedToLoad(errorCode);
        } catch (RemoteException e) {
            gs.d("Could not call onAdFailedToLoad.", e);
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationBannerListener
    public void onAdLeftApplication(MediationBannerAdapter adapter) {
        com.google.android.gms.common.internal.n.aT("onAdLeftApplication must be called on the main UI thread.");
        gs.S("Adapter called onAdLeftApplication.");
        try {
            this.qF.onAdLeftApplication();
        } catch (RemoteException e) {
            gs.d("Could not call onAdLeftApplication.", e);
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialListener
    public void onAdLeftApplication(MediationInterstitialAdapter adapter) {
        com.google.android.gms.common.internal.n.aT("onAdLeftApplication must be called on the main UI thread.");
        gs.S("Adapter called onAdLeftApplication.");
        try {
            this.qF.onAdLeftApplication();
        } catch (RemoteException e) {
            gs.d("Could not call onAdLeftApplication.", e);
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationBannerListener
    public void onAdLoaded(MediationBannerAdapter adapter) {
        com.google.android.gms.common.internal.n.aT("onAdLoaded must be called on the main UI thread.");
        gs.S("Adapter called onAdLoaded.");
        try {
            this.qF.onAdLoaded();
        } catch (RemoteException e) {
            gs.d("Could not call onAdLoaded.", e);
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialListener
    public void onAdLoaded(MediationInterstitialAdapter adapter) {
        com.google.android.gms.common.internal.n.aT("onAdLoaded must be called on the main UI thread.");
        gs.S("Adapter called onAdLoaded.");
        try {
            this.qF.onAdLoaded();
        } catch (RemoteException e) {
            gs.d("Could not call onAdLoaded.", e);
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationBannerListener
    public void onAdOpened(MediationBannerAdapter adapter) {
        com.google.android.gms.common.internal.n.aT("onAdOpened must be called on the main UI thread.");
        gs.S("Adapter called onAdOpened.");
        try {
            this.qF.onAdOpened();
        } catch (RemoteException e) {
            gs.d("Could not call onAdOpened.", e);
        }
    }

    @Override // com.google.android.gms.ads.mediation.MediationInterstitialListener
    public void onAdOpened(MediationInterstitialAdapter adapter) {
        com.google.android.gms.common.internal.n.aT("onAdOpened must be called on the main UI thread.");
        gs.S("Adapter called onAdOpened.");
        try {
            this.qF.onAdOpened();
        } catch (RemoteException e) {
            gs.d("Could not call onAdOpened.", e);
        }
    }
}
