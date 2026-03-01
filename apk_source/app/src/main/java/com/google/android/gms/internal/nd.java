package com.google.android.gms.internal;

import android.content.Intent;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.panorama.PanoramaApi;

/* loaded from: classes.dex */
class nd implements PanoramaApi.PanoramaResult {
    private final Status CM;
    private final Intent akr;

    public nd(Status status, Intent intent) {
        this.CM = (Status) com.google.android.gms.common.internal.n.i(status);
        this.akr = intent;
    }

    @Override // com.google.android.gms.common.api.Result
    public Status getStatus() {
        return this.CM;
    }

    @Override // com.google.android.gms.panorama.PanoramaApi.PanoramaResult
    public Intent getViewerIntent() {
        return this.akr;
    }
}
