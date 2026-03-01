package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class ah extends aj {
    private static final String ID = com.google.android.gms.internal.a.EVENT.toString();
    private final ct anT;

    public ah(ct ctVar) {
        super(ID, new String[0]);
        this.anT = ctVar;
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        String strPl = this.anT.pl();
        return strPl == null ? di.pI() : di.u(strPl);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return false;
    }
}
