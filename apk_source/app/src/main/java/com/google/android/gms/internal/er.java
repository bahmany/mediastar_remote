package com.google.android.gms.internal;

import android.view.View;
import com.google.android.gms.internal.es;

@ez
/* loaded from: classes.dex */
public final class er extends es.a {
    private final aa sM;
    private final String sN;
    private final String sO;

    public er(aa aaVar, String str, String str2) {
        this.sM = aaVar;
        this.sN = str;
        this.sO = str2;
    }

    @Override // com.google.android.gms.internal.es
    public void ar() {
        this.sM.ar();
    }

    @Override // com.google.android.gms.internal.es
    public void as() {
        this.sM.as();
    }

    @Override // com.google.android.gms.internal.es
    public void c(com.google.android.gms.dynamic.d dVar) {
        if (dVar == null) {
            return;
        }
        this.sM.b((View) com.google.android.gms.dynamic.e.f(dVar));
    }

    @Override // com.google.android.gms.internal.es
    public String cv() {
        return this.sN;
    }

    @Override // com.google.android.gms.internal.es
    public String cw() {
        return this.sO;
    }
}
