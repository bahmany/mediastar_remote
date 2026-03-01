package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.ads.purchase.InAppPurchaseListener;
import com.google.android.gms.ads.purchase.PlayStorePurchaseListener;

/* loaded from: classes.dex */
public class bi {
    private final Context mContext;
    private AdListener nR;
    private String oA;
    private InAppPurchaseListener oC;
    private PlayStorePurchaseListener oD;
    private PublisherInterstitialAd oF;
    private com.google.android.gms.ads.doubleclick.c oG;
    private AppEventListener oi;
    private String ok;
    private final cs ox;
    private final ax oy;
    private bd oz;

    public bi(Context context) {
        this(context, ax.bb(), null);
    }

    public bi(Context context, PublisherInterstitialAd publisherInterstitialAd) {
        this(context, ax.bb(), publisherInterstitialAd);
    }

    public bi(Context context, ax axVar, PublisherInterstitialAd publisherInterstitialAd) {
        this.ox = new cs();
        this.mContext = context;
        this.oy = axVar;
        this.oF = publisherInterstitialAd;
    }

    private void v(String str) throws RemoteException {
        if (this.ok == null) {
            w(str);
        }
        this.oz = au.a(this.mContext, new ay(), this.ok, this.ox);
        if (this.nR != null) {
            this.oz.a(new at(this.nR));
        }
        if (this.oi != null) {
            this.oz.a(new ba(this.oi));
        }
        if (this.oC != null) {
            this.oz.a(new em(this.oC));
        }
        if (this.oD != null) {
            this.oz.a(new eq(this.oD), this.oA);
        }
        if (this.oG != null) {
            this.oz.a(new ex(this.oG, this.oF));
        }
    }

    private void w(String str) {
        if (this.oz == null) {
            throw new IllegalStateException("The ad unit ID must be set on InterstitialAd before " + str + " is called.");
        }
    }

    public void a(bg bgVar) {
        try {
            if (this.oz == null) {
                v("loadAd");
            }
            if (this.oz.a(this.oy.a(this.mContext, bgVar))) {
                this.ox.d(bgVar.be());
            }
        } catch (RemoteException e) {
            gs.d("Failed to load ad.", e);
        }
    }

    public AdListener getAdListener() {
        return this.nR;
    }

    public String getAdUnitId() {
        return this.ok;
    }

    public AppEventListener getAppEventListener() {
        return this.oi;
    }

    public InAppPurchaseListener getInAppPurchaseListener() {
        return this.oC;
    }

    public String getMediationAdapterClassName() {
        try {
            if (this.oz != null) {
                return this.oz.getMediationAdapterClassName();
            }
        } catch (RemoteException e) {
            gs.d("Failed to get the mediation adapter class name.", e);
        }
        return null;
    }

    public boolean isLoaded() {
        try {
            if (this.oz == null) {
                return false;
            }
            return this.oz.isReady();
        } catch (RemoteException e) {
            gs.d("Failed to check if ad is ready.", e);
            return false;
        }
    }

    public void setAdListener(AdListener adListener) {
        try {
            this.nR = adListener;
            if (this.oz != null) {
                this.oz.a(adListener != null ? new at(adListener) : null);
            }
        } catch (RemoteException e) {
            gs.d("Failed to set the AdListener.", e);
        }
    }

    public void setAdUnitId(String adUnitId) {
        if (this.ok != null) {
            throw new IllegalStateException("The ad unit ID can only be set once on InterstitialAd.");
        }
        this.ok = adUnitId;
    }

    public void setAppEventListener(AppEventListener appEventListener) {
        try {
            this.oi = appEventListener;
            if (this.oz != null) {
                this.oz.a(appEventListener != null ? new ba(appEventListener) : null);
            }
        } catch (RemoteException e) {
            gs.d("Failed to set the AppEventListener.", e);
        }
    }

    public void setInAppPurchaseListener(InAppPurchaseListener inAppPurchaseListener) {
        if (this.oD != null) {
            throw new IllegalStateException("Play store purchase parameter has already been set.");
        }
        try {
            this.oC = inAppPurchaseListener;
            if (this.oz != null) {
                this.oz.a(inAppPurchaseListener != null ? new em(inAppPurchaseListener) : null);
            }
        } catch (RemoteException e) {
            gs.d("Failed to set the InAppPurchaseListener.", e);
        }
    }

    public void setPlayStorePurchaseParams(PlayStorePurchaseListener playStorePurchaseListener, String publicKey) {
        try {
            this.oD = playStorePurchaseListener;
            if (this.oz != null) {
                this.oz.a(playStorePurchaseListener != null ? new eq(playStorePurchaseListener) : null, publicKey);
            }
        } catch (RemoteException e) {
            gs.d("Failed to set the play store purchase parameter.", e);
        }
    }

    public void show() {
        try {
            w("show");
            this.oz.showInterstitial();
        } catch (RemoteException e) {
            gs.d("Failed to show interstitial.", e);
        }
    }
}
