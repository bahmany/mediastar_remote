package com.google.android.gms.internal;

import java.util.Map;

@ez
/* loaded from: classes.dex */
public class ca implements by {
    private final cb pJ;

    public ca(cb cbVar) {
        this.pJ = cbVar;
    }

    @Override // com.google.android.gms.internal.by
    public void a(gv gvVar, Map<String, String> map) {
        this.pJ.b("1".equals(map.get("transparentBackground")));
    }
}
