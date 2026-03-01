package com.google.android.gms.internal;

import com.google.android.gms.internal.pq;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ni {
    private int akA;
    private final ArrayList<a> akz;

    public static class a {
        public final nl akB;
        public final nh akC;
        public final pq.c akD;

        private a(nl nlVar, nh nhVar) {
            this.akB = (nl) com.google.android.gms.common.internal.n.i(nlVar);
            this.akC = (nh) com.google.android.gms.common.internal.n.i(nhVar);
            this.akD = null;
        }
    }

    public ni() {
        this(100);
    }

    public ni(int i) {
        this.akz = new ArrayList<>();
        this.akA = i;
    }

    private void mV() {
        while (getSize() > getCapacity()) {
            this.akz.remove(0);
        }
    }

    public void a(nl nlVar, nh nhVar) {
        this.akz.add(new a(nlVar, nhVar));
        mV();
    }

    public void clear() {
        this.akz.clear();
    }

    public int getCapacity() {
        return this.akA;
    }

    public int getSize() {
        return this.akz.size();
    }

    public boolean isEmpty() {
        return this.akz.isEmpty();
    }

    public ArrayList<a> mU() {
        return this.akz;
    }
}
