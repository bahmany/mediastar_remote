package com.google.android.gms.common.data;

import java.util.NoSuchElementException;

/* loaded from: classes.dex */
public class h<T> extends c<T> {
    private T Kk;

    public h(DataBuffer<T> dataBuffer) {
        super(dataBuffer);
    }

    @Override // com.google.android.gms.common.data.c, java.util.Iterator
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Cannot advance the iterator beyond " + this.JP);
        }
        this.JP++;
        if (this.JP == 0) {
            this.Kk = this.JO.get(0);
            if (!(this.Kk instanceof d)) {
                throw new IllegalStateException("DataBuffer reference of type " + this.Kk.getClass() + " is not movable");
            }
        } else {
            ((d) this.Kk).ap(this.JP);
        }
        return this.Kk;
    }
}
