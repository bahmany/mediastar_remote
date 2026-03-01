package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
class w extends dg {
    private static final String ID = com.google.android.gms.internal.a.DATA_LAYER_WRITE.toString();
    private static final String VALUE = com.google.android.gms.internal.b.VALUE.toString();
    private static final String aoP = com.google.android.gms.internal.b.CLEAR_PERSISTENT_DATA_LAYER_PREFIX.toString();
    private final DataLayer anS;

    public w(DataLayer dataLayer) {
        super(ID, VALUE);
        this.anS = dataLayer;
    }

    private void a(d.a aVar) throws InterruptedException {
        String strJ;
        if (aVar == null || aVar == di.pC() || (strJ = di.j(aVar)) == di.pH()) {
            return;
        }
        this.anS.cs(strJ);
    }

    private void b(d.a aVar) throws InterruptedException {
        if (aVar == null || aVar == di.pC()) {
            return;
        }
        Object objO = di.o(aVar);
        if (objO instanceof List) {
            for (Object obj : (List) objO) {
                if (obj instanceof Map) {
                    this.anS.push((Map) obj);
                }
            }
        }
    }

    @Override // com.google.android.gms.tagmanager.dg
    public void E(Map<String, d.a> map) throws InterruptedException {
        b(map.get(VALUE));
        a(map.get(aoP));
    }
}
