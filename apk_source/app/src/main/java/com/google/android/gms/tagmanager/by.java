package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
abstract class by extends cd {
    public by(String str) {
        super(str);
    }

    @Override // com.google.android.gms.tagmanager.cd
    protected boolean a(d.a aVar, d.a aVar2, Map<String, d.a> map) {
        dh dhVarK = di.k(aVar);
        dh dhVarK2 = di.k(aVar2);
        if (dhVarK == di.pG() || dhVarK2 == di.pG()) {
            return false;
        }
        return a(dhVarK, dhVarK2, map);
    }

    protected abstract boolean a(dh dhVar, dh dhVar2, Map<String, d.a> map);
}
