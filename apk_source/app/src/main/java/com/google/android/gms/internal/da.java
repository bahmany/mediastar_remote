package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.ads.AdRequest;
import com.google.ads.mediation.MediationBannerAdapter;
import com.google.ads.mediation.MediationBannerListener;
import com.google.ads.mediation.MediationInterstitialAdapter;
import com.google.ads.mediation.MediationInterstitialListener;
import com.google.ads.mediation.MediationServerParameters;
import com.google.ads.mediation.NetworkExtras;

@ez
/* loaded from: classes.dex */
public final class da<NETWORK_EXTRAS extends NetworkExtras, SERVER_PARAMETERS extends MediationServerParameters> implements MediationBannerListener, MediationInterstitialListener {
    private final cv qF;

    public da(cv cvVar) {
        this.qF = cvVar;
    }

    @Override // com.google.ads.mediation.MediationBannerListener
    public void onClick(MediationBannerAdapter<?, ?> adapter) {
        gs.S("Adapter called onClick.");
        if (!gr.dt()) {
            gs.W("onClick must be called on the main UI thread.");
            gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.da.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        da.this.qF.onAdClicked();
                    } catch (RemoteException e) {
                        gs.d("Could not call onAdClicked.", e);
                    }
                }
            });
        } else {
            try {
                this.qF.onAdClicked();
            } catch (RemoteException e) {
                gs.d("Could not call onAdClicked.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationBannerListener
    public void onDismissScreen(MediationBannerAdapter<?, ?> adapter) {
        gs.S("Adapter called onDismissScreen.");
        if (!gr.dt()) {
            gs.W("onDismissScreen must be called on the main UI thread.");
            gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.da.4
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        da.this.qF.onAdClosed();
                    } catch (RemoteException e) {
                        gs.d("Could not call onAdClosed.", e);
                    }
                }
            });
        } else {
            try {
                this.qF.onAdClosed();
            } catch (RemoteException e) {
                gs.d("Could not call onAdClosed.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationInterstitialListener
    public void onDismissScreen(MediationInterstitialAdapter<?, ?> adapter) {
        gs.S("Adapter called onDismissScreen.");
        if (!gr.dt()) {
            gs.W("onDismissScreen must be called on the main UI thread.");
            gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.da.9
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        da.this.qF.onAdClosed();
                    } catch (RemoteException e) {
                        gs.d("Could not call onAdClosed.", e);
                    }
                }
            });
        } else {
            try {
                this.qF.onAdClosed();
            } catch (RemoteException e) {
                gs.d("Could not call onAdClosed.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationBannerListener
    public void onFailedToReceiveAd(MediationBannerAdapter<?, ?> adapter, final AdRequest.ErrorCode errorCode) {
        gs.S("Adapter called onFailedToReceiveAd with error. " + errorCode);
        if (!gr.dt()) {
            gs.W("onFailedToReceiveAd must be called on the main UI thread.");
            gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.da.5
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        da.this.qF.onAdFailedToLoad(db.a(errorCode));
                    } catch (RemoteException e) {
                        gs.d("Could not call onAdFailedToLoad.", e);
                    }
                }
            });
        } else {
            try {
                this.qF.onAdFailedToLoad(db.a(errorCode));
            } catch (RemoteException e) {
                gs.d("Could not call onAdFailedToLoad.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationInterstitialListener
    public void onFailedToReceiveAd(MediationInterstitialAdapter<?, ?> adapter, final AdRequest.ErrorCode errorCode) {
        gs.S("Adapter called onFailedToReceiveAd with error " + errorCode + ".");
        if (!gr.dt()) {
            gs.W("onFailedToReceiveAd must be called on the main UI thread.");
            gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.da.10
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        da.this.qF.onAdFailedToLoad(db.a(errorCode));
                    } catch (RemoteException e) {
                        gs.d("Could not call onAdFailedToLoad.", e);
                    }
                }
            });
        } else {
            try {
                this.qF.onAdFailedToLoad(db.a(errorCode));
            } catch (RemoteException e) {
                gs.d("Could not call onAdFailedToLoad.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationBannerListener
    public void onLeaveApplication(MediationBannerAdapter<?, ?> adapter) {
        gs.S("Adapter called onLeaveApplication.");
        if (!gr.dt()) {
            gs.W("onLeaveApplication must be called on the main UI thread.");
            gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.da.6
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        da.this.qF.onAdLeftApplication();
                    } catch (RemoteException e) {
                        gs.d("Could not call onAdLeftApplication.", e);
                    }
                }
            });
        } else {
            try {
                this.qF.onAdLeftApplication();
            } catch (RemoteException e) {
                gs.d("Could not call onAdLeftApplication.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationInterstitialListener
    public void onLeaveApplication(MediationInterstitialAdapter<?, ?> adapter) {
        gs.S("Adapter called onLeaveApplication.");
        if (!gr.dt()) {
            gs.W("onLeaveApplication must be called on the main UI thread.");
            gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.da.11
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        da.this.qF.onAdLeftApplication();
                    } catch (RemoteException e) {
                        gs.d("Could not call onAdLeftApplication.", e);
                    }
                }
            });
        } else {
            try {
                this.qF.onAdLeftApplication();
            } catch (RemoteException e) {
                gs.d("Could not call onAdLeftApplication.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationBannerListener
    public void onPresentScreen(MediationBannerAdapter<?, ?> adapter) {
        gs.S("Adapter called onPresentScreen.");
        if (!gr.dt()) {
            gs.W("onPresentScreen must be called on the main UI thread.");
            gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.da.7
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        da.this.qF.onAdOpened();
                    } catch (RemoteException e) {
                        gs.d("Could not call onAdOpened.", e);
                    }
                }
            });
        } else {
            try {
                this.qF.onAdOpened();
            } catch (RemoteException e) {
                gs.d("Could not call onAdOpened.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationInterstitialListener
    public void onPresentScreen(MediationInterstitialAdapter<?, ?> adapter) {
        gs.S("Adapter called onPresentScreen.");
        if (!gr.dt()) {
            gs.W("onPresentScreen must be called on the main UI thread.");
            gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.da.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        da.this.qF.onAdOpened();
                    } catch (RemoteException e) {
                        gs.d("Could not call onAdOpened.", e);
                    }
                }
            });
        } else {
            try {
                this.qF.onAdOpened();
            } catch (RemoteException e) {
                gs.d("Could not call onAdOpened.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationBannerListener
    public void onReceivedAd(MediationBannerAdapter<?, ?> adapter) {
        gs.S("Adapter called onReceivedAd.");
        if (!gr.dt()) {
            gs.W("onReceivedAd must be called on the main UI thread.");
            gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.da.8
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        da.this.qF.onAdLoaded();
                    } catch (RemoteException e) {
                        gs.d("Could not call onAdLoaded.", e);
                    }
                }
            });
        } else {
            try {
                this.qF.onAdLoaded();
            } catch (RemoteException e) {
                gs.d("Could not call onAdLoaded.", e);
            }
        }
    }

    @Override // com.google.ads.mediation.MediationInterstitialListener
    public void onReceivedAd(MediationInterstitialAdapter<?, ?> adapter) {
        gs.S("Adapter called onReceivedAd.");
        if (!gr.dt()) {
            gs.W("onReceivedAd must be called on the main UI thread.");
            gr.wC.post(new Runnable() { // from class: com.google.android.gms.internal.da.3
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        da.this.qF.onAdLoaded();
                    } catch (RemoteException e) {
                        gs.d("Could not call onAdLoaded.", e);
                    }
                }
            });
        } else {
            try {
                this.qF.onAdLoaded();
            } catch (RemoteException e) {
                gs.d("Could not call onAdLoaded.", e);
            }
        }
    }
}
