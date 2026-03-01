package com.google.android.gms.internal;

import java.util.Map;

@ez
/* loaded from: classes.dex */
public final class bv implements by {
    private final bw pz;

    public bv(bw bwVar) {
        this.pz = bwVar;
    }

    @Override // com.google.android.gms.internal.by
    public void a(gv gvVar, Map<String, String> map) {
        String str = map.get("name");
        if (str == null) {
            gs.W("App event with no name parameter.");
        } else {
            this.pz.onAppEvent(str, map.get("info"));
        }
    }
}
