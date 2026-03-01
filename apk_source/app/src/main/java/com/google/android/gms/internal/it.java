package com.google.android.gms.internal;

import android.os.SystemClock;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class it {
    private static final ip Gr = new ip("RequestTracker");
    public static final Object Hz = new Object();
    private long Hv;
    private long Hw = -1;
    private long Hx = 0;
    private is Hy;

    public it(long j) {
        this.Hv = j;
    }

    private void fV() {
        this.Hw = -1L;
        this.Hy = null;
        this.Hx = 0L;
    }

    public void a(long j, is isVar) {
        is isVar2;
        long j2;
        synchronized (Hz) {
            isVar2 = this.Hy;
            j2 = this.Hw;
            this.Hw = j;
            this.Hy = isVar;
            this.Hx = SystemClock.elapsedRealtime();
        }
        if (isVar2 != null) {
            isVar2.n(j2);
        }
    }

    public boolean b(long j, int i, JSONObject jSONObject) {
        boolean z = true;
        is isVar = null;
        synchronized (Hz) {
            if (this.Hw == -1 || this.Hw != j) {
                z = false;
            } else {
                Gr.b("request %d completed", Long.valueOf(this.Hw));
                isVar = this.Hy;
                fV();
            }
        }
        if (isVar != null) {
            isVar.a(j, i, jSONObject);
        }
        return z;
    }

    public void clear() {
        synchronized (Hz) {
            if (this.Hw != -1) {
                fV();
            }
        }
    }

    public boolean d(long j, int i) {
        return b(j, i, null);
    }

    public boolean e(long j, int i) {
        is isVar;
        boolean z = true;
        long j2 = 0;
        synchronized (Hz) {
            if (this.Hw == -1 || j - this.Hx < this.Hv) {
                z = false;
                isVar = null;
            } else {
                Gr.b("request %d timed out", Long.valueOf(this.Hw));
                j2 = this.Hw;
                isVar = this.Hy;
                fV();
            }
        }
        if (isVar != null) {
            isVar.a(j2, i, null);
        }
        return z;
    }

    public boolean fW() {
        boolean z;
        synchronized (Hz) {
            z = this.Hw != -1;
        }
        return z;
    }

    public boolean p(long j) {
        boolean z;
        synchronized (Hz) {
            z = this.Hw != -1 && this.Hw == j;
        }
        return z;
    }
}
