package com.google.android.gms.tagmanager;

import android.content.Context;
import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class e extends aj {
    private static final String ID = com.google.android.gms.internal.a.ADWORDS_CLICK_REFERRER.toString();
    private static final String anI = com.google.android.gms.internal.b.COMPONENT.toString();
    private static final String anJ = com.google.android.gms.internal.b.CONVERSION_ID.toString();
    private final Context lB;

    public e(Context context) {
        super(ID, anJ);
        this.lB = context;
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        d.a aVar = map.get(anJ);
        if (aVar == null) {
            return di.pI();
        }
        String strJ = di.j(aVar);
        d.a aVar2 = map.get(anI);
        String strF = ay.f(this.lB, strJ, aVar2 != null ? di.j(aVar2) : null);
        return strF != null ? di.u(strF) : di.pI();
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
