package com.google.android.gms.internal;

import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.internal.et;

@ez
/* loaded from: classes.dex */
public final class ew extends et.a {
    private final com.google.android.gms.ads.doubleclick.b oE;
    private final PublisherAdView sQ;

    public ew(com.google.android.gms.ads.doubleclick.b bVar, PublisherAdView publisherAdView) {
        this.oE = bVar;
        this.sQ = publisherAdView;
    }

    @Override // com.google.android.gms.internal.et
    public void a(es esVar) {
        this.oE.a(this.sQ, new ev(esVar));
    }

    @Override // com.google.android.gms.internal.et
    public boolean e(String str, String str2) {
        return this.oE.a(this.sQ, str, str2);
    }
}
