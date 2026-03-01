package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/* loaded from: classes.dex */
class dm {
    private static bz<d.a> a(bz<d.a> bzVar) {
        try {
            return new bz<>(di.u(db(di.j(bzVar.getObject()))), bzVar.oE());
        } catch (UnsupportedEncodingException e) {
            bh.b("Escape URI: unsupported encoding", e);
            return bzVar;
        }
    }

    private static bz<d.a> a(bz<d.a> bzVar, int i) {
        if (!q(bzVar.getObject())) {
            bh.T("Escaping can only be applied to strings.");
            return bzVar;
        }
        switch (i) {
            case 12:
                break;
            default:
                bh.T("Unsupported Value Escaping: " + i);
                break;
        }
        return bzVar;
    }

    static bz<d.a> a(bz<d.a> bzVar, int... iArr) {
        for (int i : iArr) {
            bzVar = a(bzVar, i);
        }
        return bzVar;
    }

    static String db(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, "UTF-8").replaceAll("\\+", "%20");
    }

    private static boolean q(d.a aVar) {
        return di.o(aVar) instanceof String;
    }
}
