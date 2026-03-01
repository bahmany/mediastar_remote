package com.google.android.gms.internal;

import java.util.Map;
import java.util.concurrent.Future;

@ez
/* loaded from: classes.dex */
public final class ft {
    private gv md;
    private String uq;
    private final Object mw = new Object();
    private gk<fv> ur = new gk<>();
    public final by us = new by() { // from class: com.google.android.gms.internal.ft.1
        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            synchronized (ft.this.mw) {
                if (ft.this.ur.isDone()) {
                    return;
                }
                fv fvVar = new fv(1, map);
                gs.W("Invalid " + fvVar.getType() + " request error: " + fvVar.cM());
                ft.this.ur.a(fvVar);
            }
        }
    };
    public final by ut = new by() { // from class: com.google.android.gms.internal.ft.2
        @Override // com.google.android.gms.internal.by
        public void a(gv gvVar, Map<String, String> map) {
            synchronized (ft.this.mw) {
                if (ft.this.ur.isDone()) {
                    return;
                }
                fv fvVar = new fv(-2, map);
                String url = fvVar.getUrl();
                if (url == null) {
                    gs.W("URL missing in loadAdUrl GMSG.");
                    return;
                }
                if (url.contains("%40mediation_adapters%40")) {
                    String strReplaceAll = url.replaceAll("%40mediation_adapters%40", gf.a(gvVar.getContext(), map.get("check_adapters"), ft.this.uq));
                    fvVar.setUrl(strReplaceAll);
                    gs.V("Ad request URL modified to " + strReplaceAll);
                }
                ft.this.ur.a(fvVar);
            }
        }
    };

    public ft(String str) {
        this.uq = str;
    }

    public void b(gv gvVar) {
        this.md = gvVar;
    }

    public Future<fv> cL() {
        return this.ur;
    }
}
