package com.google.android.gms.internal;

import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.internal.eu;

@ez
/* loaded from: classes.dex */
public final class ex extends eu.a {
    private final PublisherInterstitialAd oF;
    private final com.google.android.gms.ads.doubleclick.c oG;

    public ex(com.google.android.gms.ads.doubleclick.c cVar, PublisherInterstitialAd publisherInterstitialAd) {
        this.oG = cVar;
        this.oF = publisherInterstitialAd;
    }

    @Override // com.google.android.gms.internal.eu
    public void a(es esVar) {
        this.oG.a(this.oF, new ev(esVar));
    }

    @Override // com.google.android.gms.internal.eu
    public boolean e(String str, String str2) {
        return this.oG.a(this.oF, str, str2);
    }
}
