package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
abstract class dd extends cd {
    public dd(String str) {
        super(str);
    }

    @Override // com.google.android.gms.tagmanager.cd
    protected boolean a(d.a aVar, d.a aVar2, Map<String, d.a> map) {
        String strJ = di.j(aVar);
        String strJ2 = di.j(aVar2);
        if (strJ == di.pH() || strJ2 == di.pH()) {
            return false;
        }
        return a(strJ, strJ2, map);
    }

    protected abstract boolean a(String str, String str2, Map<String, d.a> map);
}
