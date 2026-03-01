package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class am extends by {
    private static final String ID = com.google.android.gms.internal.a.GREATER_THAN.toString();

    public am() {
        super(ID);
    }

    @Override // com.google.android.gms.tagmanager.by
    protected boolean a(dh dhVar, dh dhVar2, Map<String, d.a> map) {
        return dhVar.compareTo(dhVar2) > 0;
    }
}
