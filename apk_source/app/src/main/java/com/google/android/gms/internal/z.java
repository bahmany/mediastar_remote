package com.google.android.gms.internal;

import java.util.HashMap;

@ez
/* loaded from: classes.dex */
class z implements ac {
    private gv mi;

    public z(gv gvVar) {
        this.mi = gvVar;
    }

    @Override // com.google.android.gms.internal.ac
    public void a(af afVar, boolean z) {
        HashMap map = new HashMap();
        map.put("isVisible", z ? "1" : "0");
        this.mi.a("onAdVisibilityChanged", map);
    }
}
