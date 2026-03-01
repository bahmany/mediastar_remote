package com.google.android.gms.internal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@ez
/* loaded from: classes.dex */
public class al {
    private int np;
    private final Object mw = new Object();
    private List<ak> nq = new LinkedList();

    public boolean a(ak akVar) {
        boolean z;
        synchronized (this.mw) {
            z = this.nq.contains(akVar);
        }
        return z;
    }

    public ak aU() {
        int i;
        ak akVar;
        ak akVar2 = null;
        synchronized (this.mw) {
            if (this.nq.size() == 0) {
                gs.S("Queue empty");
                return null;
            }
            if (this.nq.size() < 2) {
                ak akVar3 = this.nq.get(0);
                akVar3.aP();
                return akVar3;
            }
            int i2 = Integer.MIN_VALUE;
            for (ak akVar4 : this.nq) {
                int score = akVar4.getScore();
                if (score > i2) {
                    akVar = akVar4;
                    i = score;
                } else {
                    i = i2;
                    akVar = akVar2;
                }
                i2 = i;
                akVar2 = akVar;
            }
            this.nq.remove(akVar2);
            return akVar2;
        }
    }

    public boolean b(ak akVar) {
        boolean z;
        synchronized (this.mw) {
            Iterator<ak> it = this.nq.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                }
                ak next = it.next();
                if (akVar != next && next.aO().equals(akVar.aO())) {
                    this.nq.remove(akVar);
                    z = true;
                    break;
                }
            }
        }
        return z;
    }

    public void c(ak akVar) {
        synchronized (this.mw) {
            if (this.nq.size() >= 10) {
                gs.S("Queue is full, current size = " + this.nq.size());
                this.nq.remove(0);
            }
            int i = this.np;
            this.np = i + 1;
            akVar.c(i);
            this.nq.add(akVar);
        }
    }
}
