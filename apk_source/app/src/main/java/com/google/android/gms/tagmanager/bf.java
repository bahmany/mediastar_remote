package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.ju;

/* loaded from: classes.dex */
class bf implements cg {
    private final long AN;
    private final int AO;
    private double AP;
    private long AQ;
    private final Object AR = new Object();
    private final String AS;
    private final long apA;
    private final ju yD;

    public bf(int i, long j, long j2, String str, ju juVar) {
        this.AO = i;
        this.AP = this.AO;
        this.AN = j;
        this.apA = j2;
        this.AS = str;
        this.yD = juVar;
    }

    @Override // com.google.android.gms.tagmanager.cg
    public boolean eK() {
        boolean z = false;
        synchronized (this.AR) {
            long jCurrentTimeMillis = this.yD.currentTimeMillis();
            if (jCurrentTimeMillis - this.AQ < this.apA) {
                bh.W("Excessive " + this.AS + " detected; call ignored.");
            } else {
                if (this.AP < this.AO) {
                    double d = (jCurrentTimeMillis - this.AQ) / this.AN;
                    if (d > 0.0d) {
                        this.AP = Math.min(this.AO, d + this.AP);
                    }
                }
                this.AQ = jCurrentTimeMillis;
                if (this.AP >= 1.0d) {
                    this.AP -= 1.0d;
                    z = true;
                } else {
                    bh.W("Excessive " + this.AS + " detected; call ignored.");
                }
            }
        }
        return z;
    }
}
