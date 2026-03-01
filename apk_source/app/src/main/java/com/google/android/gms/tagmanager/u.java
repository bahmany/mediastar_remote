package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class u extends aj {
    private static final String ID = com.google.android.gms.internal.a.CUSTOM_VAR.toString();
    private static final String NAME = com.google.android.gms.internal.b.NAME.toString();
    private static final String aoE = com.google.android.gms.internal.b.DEFAULT_VALUE.toString();
    private final DataLayer anS;

    public u(DataLayer dataLayer) {
        super(ID, NAME);
        this.anS = dataLayer;
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        Object obj = this.anS.get(di.j(map.get(NAME)));
        if (obj != null) {
            return di.u(obj);
        }
        d.a aVar = map.get(aoE);
        return aVar != null ? aVar : di.pI();
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return false;
    }
}
