package com.google.android.gms.internal;

import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.internal.bf;

@ez
/* loaded from: classes.dex */
public final class ba extends bf.a {
    private final AppEventListener oi;

    public ba(AppEventListener appEventListener) {
        this.oi = appEventListener;
    }

    @Override // com.google.android.gms.internal.bf
    public void onAppEvent(String name, String info) {
        this.oi.onAppEvent(name, info);
    }
}
