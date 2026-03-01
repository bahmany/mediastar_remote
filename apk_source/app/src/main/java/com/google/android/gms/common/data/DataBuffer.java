package com.google.android.gms.common.data;

import android.os.Bundle;
import com.google.android.gms.common.api.Releasable;
import java.util.Iterator;

/* loaded from: classes.dex */
public abstract class DataBuffer<T> implements Releasable, Iterable<T> {
    protected final DataHolder IC;

    protected DataBuffer(DataHolder dataHolder) {
        this.IC = dataHolder;
        if (this.IC != null) {
            this.IC.e(this);
        }
    }

    @Deprecated
    public final void close() {
        release();
    }

    public int describeContents() {
        return 0;
    }

    public abstract T get(int i);

    public int getCount() {
        if (this.IC == null) {
            return 0;
        }
        return this.IC.getCount();
    }

    public Bundle gz() {
        return this.IC.gz();
    }

    @Deprecated
    public boolean isClosed() {
        if (this.IC == null) {
            return true;
        }
        return this.IC.isClosed();
    }

    @Override // java.lang.Iterable
    public Iterator<T> iterator() {
        return new c(this);
    }

    @Override // com.google.android.gms.common.api.Releasable
    public void release() {
        if (this.IC != null) {
            this.IC.close();
        }
    }

    public Iterator<T> singleRefIterator() {
        return new h(this);
    }
}
