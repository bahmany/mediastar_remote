package com.google.android.gms.tagmanager;

/* loaded from: classes.dex */
class cw implements cg {
    private final long AN;
    private final int AO;
    private double AP;
    private final Object AR;
    private long are;

    public cw() {
        this(60, 2000L);
    }

    public cw(int i, long j) {
        this.AR = new Object();
        this.AO = i;
        this.AP = this.AO;
        this.AN = j;
    }

    @Override // com.google.android.gms.tagmanager.cg
    public boolean eK() {
        boolean z;
        synchronized (this.AR) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (this.AP < this.AO) {
                double d = (jCurrentTimeMillis - this.are) / this.AN;
                if (d > 0.0d) {
                    this.AP = Math.min(this.AO, d + this.AP);
                }
            }
            this.are = jCurrentTimeMillis;
            if (this.AP >= 1.0d) {
                this.AP -= 1.0d;
                z = true;
            } else {
                bh.W("No more tokens available.");
                z = false;
            }
        }
        return z;
    }
}
