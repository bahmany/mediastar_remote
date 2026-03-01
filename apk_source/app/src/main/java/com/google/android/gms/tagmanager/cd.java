package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
abstract class cd extends aj {
    private static final String aoU = com.google.android.gms.internal.b.ARG0.toString();
    private static final String apQ = com.google.android.gms.internal.b.ARG1.toString();

    public cd(String str) {
        super(str, aoU, apQ);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        Iterator<d.a> it = map.values().iterator();
        while (it.hasNext()) {
            if (it.next() == di.pI()) {
                return di.u(false);
            }
        }
        d.a aVar = map.get(aoU);
        d.a aVar2 = map.get(apQ);
        return di.u(Boolean.valueOf((aVar == null || aVar2 == null) ? false : a(aVar, aVar2, map)));
    }

    protected abstract boolean a(d.a aVar, d.a aVar2, Map<String, d.a> map);

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
