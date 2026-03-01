package com.google.android.gms.tagmanager;

import android.os.Build;
import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class ca extends aj {
    private static final String ID = com.google.android.gms.internal.a.OS_VERSION.toString();

    public ca() {
        super(ID, new String[0]);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        return di.u(Build.VERSION.RELEASE);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
