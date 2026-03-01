package com.google.android.gms.tagmanager;

import android.util.Base64;
import com.google.android.gms.internal.d;
import java.util.Map;

/* loaded from: classes.dex */
class ac extends aj {
    private static final String ID = com.google.android.gms.internal.a.ENCODE.toString();
    private static final String aoU = com.google.android.gms.internal.b.ARG0.toString();
    private static final String aoV = com.google.android.gms.internal.b.NO_PADDING.toString();
    private static final String aoW = com.google.android.gms.internal.b.INPUT_FORMAT.toString();
    private static final String aoX = com.google.android.gms.internal.b.OUTPUT_FORMAT.toString();

    public ac() {
        super(ID, aoU);
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        byte[] bArrDecode;
        String strEncodeToString;
        d.a aVar = map.get(aoU);
        if (aVar == null || aVar == di.pI()) {
            return di.pI();
        }
        String strJ = di.j(aVar);
        d.a aVar2 = map.get(aoW);
        String strJ2 = aVar2 == null ? "text" : di.j(aVar2);
        d.a aVar3 = map.get(aoX);
        String strJ3 = aVar3 == null ? "base16" : di.j(aVar3);
        d.a aVar4 = map.get(aoV);
        int i = (aVar4 == null || !di.n(aVar4).booleanValue()) ? 2 : 3;
        try {
            if ("text".equals(strJ2)) {
                bArrDecode = strJ.getBytes();
            } else if ("base16".equals(strJ2)) {
                bArrDecode = j.cj(strJ);
            } else if ("base64".equals(strJ2)) {
                bArrDecode = Base64.decode(strJ, i);
            } else {
                if (!"base64url".equals(strJ2)) {
                    bh.T("Encode: unknown input format: " + strJ2);
                    return di.pI();
                }
                bArrDecode = Base64.decode(strJ, i | 8);
            }
            if ("base16".equals(strJ3)) {
                strEncodeToString = j.d(bArrDecode);
            } else if ("base64".equals(strJ3)) {
                strEncodeToString = Base64.encodeToString(bArrDecode, i);
            } else {
                if (!"base64url".equals(strJ3)) {
                    bh.T("Encode: unknown output format: " + strJ3);
                    return di.pI();
                }
                strEncodeToString = Base64.encodeToString(bArrDecode, i | 8);
            }
            return di.u(strEncodeToString);
        } catch (IllegalArgumentException e) {
            bh.T("Encode: invalid input:");
            return di.pI();
        }
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
