package mktvsmart.screen.util;

import android.util.Log;
import com.google.android.gms.ads.AdListener;

/* loaded from: classes.dex */
public class GsAdListenner extends AdListener {
    private String TAG;

    public GsAdListenner(String TAG) {
        this.TAG = "GsAdsListenner";
        this.TAG = TAG;
    }

    @Override // com.google.android.gms.ads.AdListener
    public void onAdLoaded() {
        super.onAdLoaded();
        Log.d(this.TAG, "onAdLoaded");
    }

    @Override // com.google.android.gms.ads.AdListener
    public void onAdFailedToLoad(int errorCode) {
        super.onAdFailedToLoad(errorCode);
        Log.d(this.TAG, "onAdFailedToLoad");
    }

    @Override // com.google.android.gms.ads.AdListener
    public void onAdOpened() {
        super.onAdOpened();
        Log.d(this.TAG, "onAdOpened");
    }

    @Override // com.google.android.gms.ads.AdListener
    public void onAdClosed() {
        super.onAdClosed();
        Log.d(this.TAG, "onAdClosed");
    }

    @Override // com.google.android.gms.ads.AdListener
    public void onAdLeftApplication() {
        super.onAdLeftApplication();
        Log.d(this.TAG, "onAdLeftApplication");
    }
}
