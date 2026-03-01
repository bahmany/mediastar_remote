package com.google.android.gms.common.data;

import com.google.android.gms.common.internal.n;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: classes.dex */
public class c<T> implements Iterator<T> {
    protected final DataBuffer<T> JO;
    protected int JP = -1;

    public c(DataBuffer<T> dataBuffer) {
        this.JO = (DataBuffer) n.i(dataBuffer);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.JP < this.JO.getCount() + (-1);
    }

    @Override // java.util.Iterator
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Cannot advance the iterator beyond " + this.JP);
        }
        DataBuffer<T> dataBuffer = this.JO;
        int i = this.JP + 1;
        this.JP = i;
        return dataBuffer.get(i);
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove elements from a DataBufferIterator");
    }
}
