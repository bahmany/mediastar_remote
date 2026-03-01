package com.google.android.gms.analytics;

import android.text.TextUtils;

/* loaded from: classes.dex */
class w {
    private String AE;
    private final long AF;
    private final long AG;
    private String AH = "https:";

    w(String str, long j, long j2) {
        this.AE = str;
        this.AF = j;
        this.AG = j2;
    }

    void aj(String str) {
        this.AE = str;
    }

    void ak(String str) {
        if (str == null || TextUtils.isEmpty(str.trim()) || !str.toLowerCase().startsWith("http:")) {
            return;
        }
        this.AH = "http:";
    }

    String eG() {
        return this.AE;
    }

    long eH() {
        return this.AF;
    }

    long eI() {
        return this.AG;
    }

    String eJ() {
        return this.AH;
    }
}
