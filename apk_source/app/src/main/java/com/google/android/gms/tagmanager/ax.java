package com.google.android.gms.tagmanager;

import android.content.Context;
import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class ax extends aj {
    private static final String ID = com.google.android.gms.internal.a.INSTALL_REFERRER.toString();
    private static final String anI = com.google.android.gms.internal.b.COMPONENT.toString();
    private final Context lB;

    public ax(Context context) {
        super(ID, new String[0]);
        this.lB = context;
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        String strE = ay.e(this.lB, map.get(anI) != null ? di.j(map.get(anI)) : null);
        return strE != null ? di.u(strE) : di.pI();
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
