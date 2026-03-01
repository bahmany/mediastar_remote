package com.google.android.gms.tagmanager;

import android.content.Context;
import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class c extends aj {
    private static final String ID = com.google.android.gms.internal.a.ADVERTISING_TRACKING_ENABLED.toString();
    private final a anH;

    public c(Context context) {
        this(a.V(context));
    }

    c(a aVar) {
        super(ID, new String[0]);
        this.anH = aVar;
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        return di.u(Boolean.valueOf(!this.anH.isLimitAdTrackingEnabled()));
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return false;
    }
}
