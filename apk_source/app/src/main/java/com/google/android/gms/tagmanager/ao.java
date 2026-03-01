package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/* loaded from: classes.dex */
class ao extends aj {
    private static final String ID = com.google.android.gms.internal.a.HASH.toString();
    private static final String aoU = com.google.android.gms.internal.b.ARG0.toString();
    private static final String apa = com.google.android.gms.internal.b.ALGORITHM.toString();
    private static final String aoW = com.google.android.gms.internal.b.INPUT_FORMAT.toString();

    public ao() {
        super(ID, aoU);
    }

    private byte[] d(String str, byte[] bArr) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(str);
        messageDigest.update(bArr);
        return messageDigest.digest();
    }

    @Override // com.google.android.gms.tagmanager.aj
    public d.a C(Map<String, d.a> map) {
        byte[] bArrCj;
        d.a aVar = map.get(aoU);
        if (aVar == null || aVar == di.pI()) {
            return di.pI();
        }
        String strJ = di.j(aVar);
        d.a aVar2 = map.get(apa);
        String strJ2 = aVar2 == null ? "MD5" : di.j(aVar2);
        d.a aVar3 = map.get(aoW);
        String strJ3 = aVar3 == null ? "text" : di.j(aVar3);
        if ("text".equals(strJ3)) {
            bArrCj = strJ.getBytes();
        } else {
            if (!"base16".equals(strJ3)) {
                bh.T("Hash: unknown input format: " + strJ3);
                return di.pI();
            }
            bArrCj = j.cj(strJ);
        }
        try {
            return di.u(j.d(d(strJ2, bArrCj)));
        } catch (NoSuchAlgorithmException e) {
            bh.T("Hash: unknown algorithm: " + strJ2);
            return di.pI();
        }
    }

    @Override // com.google.android.gms.tagmanager.aj
    public boolean nL() {
        return true;
    }
}
