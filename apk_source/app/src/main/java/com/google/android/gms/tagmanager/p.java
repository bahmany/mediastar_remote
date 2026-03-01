package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class p extends aj {
    private static final String ID = com.google.android.gms.internal.a.CONTAINER_VERSION.toString();
    private final String Sq;

    public p(String str) {
        super(ID, new String[0]);
        this.Sq = str;
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        return this.Sq == null ? di.pI() : di.u(this.Sq);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
