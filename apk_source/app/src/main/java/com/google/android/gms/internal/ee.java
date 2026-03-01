package com.google.android.gms.internal;

import android.content.Intent;

@ez
/* loaded from: classes.dex */
public class ee {
    private final String oA;

    public ee(String str) {
        this.oA = str;
    }

    public boolean a(String str, int i, Intent intent) {
        if (str == null || intent == null) {
            return false;
        }
        String strE = ed.e(intent);
        String strF = ed.f(intent);
        if (strE == null || strF == null) {
            return false;
        }
        if (!str.equals(ed.D(strE))) {
            gs.W("Developer payload not match.");
            return false;
        }
        if (this.oA == null || ef.b(this.oA, strE, strF)) {
            return true;
        }
        gs.W("Fail to verify signature.");
        return false;
    }

    public String cu() {
        return gj.dp();
    }
}
