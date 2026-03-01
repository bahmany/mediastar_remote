package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class m extends aj {
    private static final String ID = com.google.android.gms.internal.a.CONSTANT.toString();
    private static final String VALUE = com.google.android.gms.internal.b.VALUE.toString();

    public m() {
        super(ID, VALUE);
    }

    public static String nO() {
        return ID;
    }

    public static String nP() {
        return VALUE;
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        return map.get(VALUE);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
