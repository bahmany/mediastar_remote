package com.google.android.gms.internal;

import android.os.Bundle;

@ez
/* loaded from: classes.dex */
public class ge {
    private final Object mw;
    private final String vA;
    private final gb vx;
    private int wc;
    private int wd;

    ge(gb gbVar, String str) {
        this.mw = new Object();
        this.vx = gbVar;
        this.vA = str;
    }

    public ge(String str) {
        this(gb.cV(), str);
    }

    public void d(int i, int i2) {
        synchronized (this.mw) {
            this.wc = i;
            this.wd = i2;
            this.vx.a(this.vA, this);
        }
    }

    public Bundle toBundle() {
        Bundle bundle;
        synchronized (this.mw) {
            bundle = new Bundle();
            bundle.putInt("pmnli", this.wc);
            bundle.putInt("pmnll", this.wd);
        }
        return bundle;
    }
}
