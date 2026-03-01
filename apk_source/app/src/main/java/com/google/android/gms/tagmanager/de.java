package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class de extends aj {
    private static final String ID = com.google.android.gms.internal.a.TIME.toString();

    public de() {
        super(ID, new String[0]);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        return di.u(Long.valueOf(System.currentTimeMillis()));
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return false;
    }
}
