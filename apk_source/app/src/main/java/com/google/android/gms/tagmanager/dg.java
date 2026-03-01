package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
abstract class dg extends aj {
    public dg(String str, String... strArr) {
        super(str, strArr);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        E(map);
        return di.pI();
    }

    public abstract void E(Map<String, d.a> map);

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return false;
    }
}
