package com.google.android.gms.internal;

import android.content.Intent;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.panorama.PanoramaApi;

/* loaded from: classes.dex */
public class my extends nd implements PanoramaApi.a {
    private final int akm;

    public my(Status status, Intent intent, int i) {
        super(status, intent);
        this.akm = i;
    }

    @Override // com.google.android.gms.internal.nd, com.google.android.gms.common.api.Result
    public /* bridge */ /* synthetic */ Status getStatus() {
        return super.getStatus();
    }

    @Override // com.google.android.gms.internal.nd, com.google.android.gms.panorama.PanoramaApi.PanoramaResult
    public /* bridge */ /* synthetic */ Intent getViewerIntent() {
        return super.getViewerIntent();
    }
}
