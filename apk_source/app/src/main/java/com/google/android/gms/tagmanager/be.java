package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class be extends by {
    private static final String ID = com.google.android.gms.internal.a.LESS_THAN.toString();

    public be() {
        super(ID);
    }

    @Override // com.google.android.gms.tagmanager.by
    protected boolean a(dh dhVar, dh dhVar2, Map<String, d.a> map) {
        return dhVar.compareTo(dhVar2) < 0;
    }
}
