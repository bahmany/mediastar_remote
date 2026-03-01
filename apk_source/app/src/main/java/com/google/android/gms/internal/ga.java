package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

@ez
/* loaded from: classes.dex */
public class ga {
    private final Object mw;
    private boolean uC;
    private final String vA;
    private long vB;
    private long vC;
    private long vD;
    private long vE;
    private long vF;
    private long vG;
    private final gb vx;
    private final LinkedList<a> vy;
    private final String vz;

    @ez
    private static final class a {
        private long vH = -1;
        private long vI = -1;

        public long cS() {
            return this.vI;
        }

        public void cT() {
            this.vI = SystemClock.elapsedRealtime();
        }

        public void cU() {
            this.vH = SystemClock.elapsedRealtime();
        }

        public Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putLong("topen", this.vH);
            bundle.putLong("tclose", this.vI);
            return bundle;
        }
    }

    public ga(gb gbVar, String str, String str2) {
        this.mw = new Object();
        this.vB = -1L;
        this.vC = -1L;
        this.uC = false;
        this.vD = -1L;
        this.vE = 0L;
        this.vF = -1L;
        this.vG = -1L;
        this.vx = gbVar;
        this.vz = str;
        this.vA = str2;
        this.vy = new LinkedList<>();
    }

    public ga(String str, String str2) {
        this(gb.cV(), str, str2);
    }

    public void cP() {
        synchronized (this.mw) {
            if (this.vG != -1 && this.vC == -1) {
                this.vC = SystemClock.elapsedRealtime();
                this.vx.a(this);
            }
            gb gbVar = this.vx;
            gb.cZ().cP();
        }
    }

    public void cQ() {
        synchronized (this.mw) {
            if (this.vG != -1) {
                a aVar = new a();
                aVar.cU();
                this.vy.add(aVar);
                this.vE++;
                gb gbVar = this.vx;
                gb.cZ().cQ();
                this.vx.a(this);
            }
        }
    }

    public void cR() {
        synchronized (this.mw) {
            if (this.vG != -1 && !this.vy.isEmpty()) {
                a last = this.vy.getLast();
                if (last.cS() == -1) {
                    last.cT();
                    this.vx.a(this);
                }
            }
        }
    }

    public void e(av avVar) {
        synchronized (this.mw) {
            this.vF = SystemClock.elapsedRealtime();
            gb gbVar = this.vx;
            gb.cZ().b(avVar, this.vF);
        }
    }

    public void j(long j) {
        synchronized (this.mw) {
            this.vG = j;
            if (this.vG != -1) {
                this.vx.a(this);
            }
        }
    }

    public void k(long j) {
        synchronized (this.mw) {
            if (this.vG != -1) {
                this.vB = j;
                this.vx.a(this);
            }
        }
    }

    public void t(boolean z) {
        synchronized (this.mw) {
            if (this.vG != -1) {
                this.vD = SystemClock.elapsedRealtime();
                if (!z) {
                    this.vC = this.vD;
                    this.vx.a(this);
                }
            }
        }
    }

    public Bundle toBundle() {
        Bundle bundle;
        synchronized (this.mw) {
            bundle = new Bundle();
            bundle.putString("seq_num", this.vz);
            bundle.putString("slotid", this.vA);
            bundle.putBoolean("ismediation", this.uC);
            bundle.putLong("treq", this.vF);
            bundle.putLong("tresponse", this.vG);
            bundle.putLong("timp", this.vC);
            bundle.putLong("tload", this.vD);
            bundle.putLong("pcc", this.vE);
            bundle.putLong("tfetch", this.vB);
            ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
            Iterator<a> it = this.vy.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().toBundle());
            }
            bundle.putParcelableArrayList("tclick", arrayList);
        }
        return bundle;
    }

    public void u(boolean z) {
        synchronized (this.mw) {
            if (this.vG != -1) {
                this.uC = z;
                this.vx.a(this);
            }
        }
    }
}
