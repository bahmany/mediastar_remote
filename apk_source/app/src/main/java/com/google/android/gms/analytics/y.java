package com.google.android.gms.analytics;

/* loaded from: classes.dex */
class y implements ac {
    private final long AN;
    private final int AO;
    private double AP;
    private long AQ;
    private final Object AR;
    private final String AS;

    public y(int i, long j, String str) {
        this.AR = new Object();
        this.AO = i;
        this.AP = this.AO;
        this.AN = j;
        this.AS = str;
    }

    public y(String str) {
        this(60, 2000L, str);
    }

    @Override // com.google.android.gms.analytics.ac
    public boolean eK() {
        boolean z;
        synchronized (this.AR) {
            long jCurrentTimeMillis = System.currentTimeMillis();
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
                z.W("Excessive " + this.AS + " detected; call ignored.");
                z = false;
            }
        }
        return z;
    }
}
