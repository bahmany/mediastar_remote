package com.google.android.gms.tagmanager;

import android.content.Context;
import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class b extends aj {
    private static final String ID = com.google.android.gms.internal.a.ADVERTISER_ID.toString();
    private final a anH;

    public b(Context context) {
        this(a.V(context));
    }

    b(a aVar) {
        super(ID, new String[0]);
        this.anH = aVar;
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        String strNH = this.anH.nH();
        return strNH == null ? di.pI() : di.u(strNH);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return false;
    }
}
