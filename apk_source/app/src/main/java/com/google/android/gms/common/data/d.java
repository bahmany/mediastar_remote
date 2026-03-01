package com.google.android.gms.common.data;

import android.database.CharArrayBuffer;
import android.net.Uri;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.n;

/* loaded from: classes.dex */
public abstract class d {
    protected final DataHolder IC;
    protected int JQ;
    private int JR;

    public d(DataHolder dataHolder, int i) {
        this.IC = (DataHolder) n.i(dataHolder);
        ap(i);
    }

    protected void a(String str, CharArrayBuffer charArrayBuffer) {
        this.IC.a(str, this.JQ, this.JR, charArrayBuffer);
    }

    public boolean aQ(String str) {
        return this.IC.aQ(str);
    }

    protected Uri aR(String str) {
        return this.IC.g(str, this.JQ, this.JR);
    }

    protected boolean aS(String str) {
        return this.IC.h(str, this.JQ, this.JR);
    }

    protected void ap(int i) {
        n.I(i >= 0 && i < this.IC.getCount());
        this.JQ = i;
        this.JR = this.IC.ar(this.JQ);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof d)) {
            return false;
        }
        d dVar = (d) obj;
        return m.equal(Integer.valueOf(dVar.JQ), Integer.valueOf(this.JQ)) && m.equal(Integer.valueOf(dVar.JR), Integer.valueOf(this.JR)) && dVar.IC == this.IC;
    }

    protected int gA() {
        return this.JQ;
    }

    protected boolean getBoolean(String column) {
        return this.IC.d(column, this.JQ, this.JR);
    }

    protected byte[] getByteArray(String column) {
        return this.IC.f(column, this.JQ, this.JR);
    }

    protected float getFloat(String column) {
        return this.IC.e(column, this.JQ, this.JR);
    }

    protected int getInteger(String column) {
        return this.IC.b(column, this.JQ, this.JR);
    }

    protected long getLong(String column) {
        return this.IC.a(column, this.JQ, this.JR);
    }

    protected String getString(String column) {
        return this.IC.c(column, this.JQ, this.JR);
    }

    public int hashCode() {
        return m.hashCode(Integer.valueOf(this.JQ), Integer.valueOf(this.JR), this.IC);
    }

    public boolean isDataValid() {
        return !this.IC.isClosed();
    }
}
