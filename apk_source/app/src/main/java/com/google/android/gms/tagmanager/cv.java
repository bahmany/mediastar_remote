package com.google.android.gms.tagmanager;

import android.os.Build;
import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class cv extends aj {
    private static final String ID = com.google.android.gms.internal.a.SDK_VERSION.toString();

    public cv() {
        super(ID, new String[0]);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        return di.u(Integer.valueOf(Build.VERSION.SDK_INT));
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
