package com.google.android.gms.tagmanager;

import android.text.TextUtils;

/* loaded from: classes.dex */
class ap {
    private final long AF;
    private final long AG;
    private final long apb;
    private String apc;

    ap(long j, long j2, long j3) {
        this.AF = j;
        this.AG = j2;
        this.apb = j3;
    }

    void ak(String str) {
        if (str == null || TextUtils.isEmpty(str.trim())) {
            return;
        }
        this.apc = str;
    }

    long eH() {
        return this.AF;
    }

    long or() {
        return this.apb;
    }

    String os() {
        return this.apc;
    }
}
