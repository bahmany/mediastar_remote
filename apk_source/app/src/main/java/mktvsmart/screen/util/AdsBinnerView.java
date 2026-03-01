package mktvsmart.screen.util;

import android.content.Context;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/* loaded from: classes.dex */
public class AdsBinnerView {
    private Context mContext;

    public AdsBinnerView(Context mContext) {
        this.mContext = mContext;
    }

    public AdView getAdView() {
        AdView adView = new AdView(this.mContext);
        AdSize adSize = new AdSize(-1, -2);
        adView.setAdUnitId("ca-app-pub-4741798363812571/2199540641");
        adView.setAdSize(adSize);
        adView.setAdListener(new GsAdListenner("AD of ADSProgressDialog"));
        adView.loadAd(new AdRequest.Builder().build());
        return adView;
    }
}
