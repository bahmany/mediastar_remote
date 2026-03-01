package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.purchase.InAppPurchaseListener;
import com.google.android.gms.ads.purchase.PlayStorePurchaseListener;

/* loaded from: classes.dex */
public final class bh {
    private AdListener nR;
    private String oA;
    private ViewGroup oB;
    private InAppPurchaseListener oC;
    private PlayStorePurchaseListener oD;
    private com.google.android.gms.ads.doubleclick.b oE;
    private AppEventListener oi;
    private AdSize[] oj;
    private String ok;
    private final cs ox;
    private final ax oy;
    private bd oz;

    public bh(ViewGroup viewGroup) {
        this(viewGroup, null, false, ax.bb());
    }

    public bh(ViewGroup viewGroup, AttributeSet attributeSet, boolean z) {
        this(viewGroup, attributeSet, z, ax.bb());
    }

    bh(ViewGroup viewGroup, AttributeSet attributeSet, boolean z, ax axVar) {
        this(viewGroup, attributeSet, z, axVar, null);
    }

    bh(ViewGroup viewGroup, AttributeSet attributeSet, boolean z, ax axVar, bd bdVar) {
        this.ox = new cs();
        this.oB = viewGroup;
        this.oy = axVar;
        if (attributeSet != null) {
            Context context = viewGroup.getContext();
            try {
                bb bbVar = new bb(context, attributeSet);
                this.oj = bbVar.f(z);
                this.ok = bbVar.getAdUnitId();
                if (viewGroup.isInEditMode()) {
                    gr.a(viewGroup, new ay(context, this.oj[0]), "Ads by Google");
                    return;
                }
            } catch (IllegalArgumentException e) {
                gr.a(viewGroup, new ay(context, AdSize.BANNER), e.getMessage(), e.getMessage());
                return;
            }
        }
        this.oz = bdVar;
    }

    private void bh() {
        try {
            com.google.android.gms.dynamic.d dVarX = this.oz.X();
            if (dVarX == null) {
                return;
            }
            this.oB.addView((View) com.google.android.gms.dynamic.e.f(dVarX));
        } catch (RemoteException e) {
            gs.d("Failed to get an ad frame.", e);
        }
    }

    private void bi() throws RemoteException {
        if ((this.oj == null || this.ok == null) && this.oz == null) {
            throw new IllegalStateException("The ad size and ad unit ID must be set before loadAd is called.");
        }
        Context context = this.oB.getContext();
        this.oz = au.a(context, new ay(context, this.oj), this.ok, this.ox);
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
        if (this.oE != null) {
            this.oz.a(new ew(this.oE, (PublisherAdView) this.oB));
        }
        bh();
    }

    public void a(bg bgVar) {
        try {
            if (this.oz == null) {
                bi();
            }
            if (this.oz.a(this.oy.a(this.oB.getContext(), bgVar))) {
                this.ox.d(bgVar.be());
            }
        } catch (RemoteException e) {
            gs.d("Failed to load ad.", e);
        }
    }

    public void a(AdSize... adSizeArr) {
        this.oj = adSizeArr;
        try {
            if (this.oz != null) {
                this.oz.a(new ay(this.oB.getContext(), this.oj));
            }
        } catch (RemoteException e) {
            gs.d("Failed to set the ad size.", e);
        }
        this.oB.requestLayout();
    }

    public void destroy() {
        try {
            if (this.oz != null) {
                this.oz.destroy();
            }
        } catch (RemoteException e) {
            gs.d("Failed to destroy AdView.", e);
        }
    }

    public AdListener getAdListener() {
        return this.nR;
    }

    public AdSize getAdSize() {
        try {
            if (this.oz != null) {
                return this.oz.Y().bc();
            }
        } catch (RemoteException e) {
            gs.d("Failed to get the current AdSize.", e);
        }
        if (this.oj != null) {
            return this.oj[0];
        }
        return null;
    }

    public AdSize[] getAdSizes() {
        return this.oj;
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

    public void pause() {
        try {
            if (this.oz != null) {
                this.oz.pause();
            }
        } catch (RemoteException e) {
            gs.d("Failed to call pause.", e);
        }
    }

    public void recordManualImpression() {
        try {
            if (this.oz != null) {
                this.oz.aj();
            }
        } catch (RemoteException e) {
            gs.d("Failed to record impression.", e);
        }
    }

    public void resume() {
        try {
            if (this.oz != null) {
                this.oz.resume();
            }
        } catch (RemoteException e) {
            gs.d("Failed to call resume.", e);
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

    public void setAdSizes(AdSize... adSizes) {
        if (this.oj != null) {
            throw new IllegalStateException("The ad size can only be set once on AdView.");
        }
        a(adSizes);
    }

    public void setAdUnitId(String adUnitId) {
        if (this.ok != null) {
            throw new IllegalStateException("The ad unit ID can only be set once on AdView.");
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
        if (this.oC != null) {
            throw new IllegalStateException("InAppPurchaseListener has already been set.");
        }
        try {
            this.oD = playStorePurchaseListener;
            this.oA = publicKey;
            if (this.oz != null) {
                this.oz.a(playStorePurchaseListener != null ? new eq(playStorePurchaseListener) : null, publicKey);
            }
        } catch (RemoteException e) {
            gs.d("Failed to set the play store purchase parameter.", e);
        }
    }
}
