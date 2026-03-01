package com.google.android.gms.common.data;

import java.util.ArrayList;

/* loaded from: classes.dex */
public abstract class g<T> extends DataBuffer<T> {
    private boolean Ki;
    private ArrayList<Integer> Kj;

    protected g(DataHolder dataHolder) {
        super(dataHolder);
        this.Ki = false;
    }

    private void gF() {
        synchronized (this) {
            if (!this.Ki) {
                int count = this.IC.getCount();
                this.Kj = new ArrayList<>();
                if (count > 0) {
                    this.Kj.add(0);
                    String strGE = gE();
                    String strC = this.IC.c(strGE, 0, this.IC.ar(0));
                    int i = 1;
                    while (i < count) {
                        String strC2 = this.IC.c(strGE, i, this.IC.ar(i));
                        if (strC2.equals(strC)) {
                            strC2 = strC;
                        } else {
                            this.Kj.add(Integer.valueOf(i));
                        }
                        i++;
                        strC = strC2;
                    }
                }
                this.Ki = true;
            }
        }
    }

    int au(int i) {
        if (i < 0 || i >= this.Kj.size()) {
            throw new IllegalArgumentException("Position " + i + " is out of bounds for this buffer");
        }
        return this.Kj.get(i).intValue();
    }

    protected int av(int i) {
        if (i < 0 || i == this.Kj.size()) {
            return 0;
        }
        int count = i == this.Kj.size() + (-1) ? this.IC.getCount() - this.Kj.get(i).intValue() : this.Kj.get(i + 1).intValue() - this.Kj.get(i).intValue();
        if (count != 1) {
            return count;
        }
        int iAu = au(i);
        int iAr = this.IC.ar(iAu);
        String strGG = gG();
        if (strGG == null || this.IC.c(strGG, iAu, iAr) != null) {
            return count;
        }
        return 0;
    }

    protected abstract T f(int i, int i2);

    protected abstract String gE();

    protected String gG() {
        return null;
    }

    @Override // com.google.android.gms.common.data.DataBuffer
    public final T get(int position) {
        gF();
        return f(au(position), av(position));
    }

    @Override // com.google.android.gms.common.data.DataBuffer
    public int getCount() {
        gF();
        return this.Kj.size();
    }
}
