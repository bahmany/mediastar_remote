package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class bj extends aj {
    private static final String ID = com.google.android.gms.internal.a.LOWERCASE_STRING.toString();
    private static final String aoU = com.google.android.gms.internal.b.ARG0.toString();

    public bj() {
        super(ID, aoU);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        return di.u(di.j(map.get(aoU)).toLowerCase());
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
