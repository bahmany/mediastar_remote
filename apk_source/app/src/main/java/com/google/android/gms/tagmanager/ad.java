package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class ad extends dd {
    private static final String ID = com.google.android.gms.internal.a.ENDS_WITH.toString();

    public ad() {
        super(ID);
    }

    @Override // com.google.android.gms.tagmanager.dd
    protected boolean a(String str, String str2, Map<String, d.a> map) {
        return str.endsWith(str2);
    }
}
