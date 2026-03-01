package com.google.android.gms.internal;

import android.support.v4.media.TransportMediator;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/* loaded from: classes.dex */
public final class pf {
    private final int awx;
    private final byte[] buffer;
    private int position;

    public static class a extends IOException {
        a(int i, int i2) {
            super("CodedOutputStream was writing to a flat byte array and ran out of space (pos " + i + " limit " + i2 + ").");
        }
    }

    private pf(byte[] bArr, int i, int i2) {
        this.buffer = bArr;
        this.position = i;
        this.awx = i + i2;
    }

    public static int D(long j) {
        return G(j);
    }

    public static int E(long j) {
        return G(I(j));
    }

    public static int G(long j) {
        if (((-128) & j) == 0) {
            return 1;
        }
        if (((-16384) & j) == 0) {
            return 2;
        }
        if (((-2097152) & j) == 0) {
            return 3;
        }
        if (((-268435456) & j) == 0) {
            return 4;
        }
        if (((-34359738368L) & j) == 0) {
            return 5;
        }
        if (((-4398046511104L) & j) == 0) {
            return 6;
        }
        if (((-562949953421312L) & j) == 0) {
            return 7;
        }
        if (((-72057594037927936L) & j) == 0) {
            return 8;
        }
        return (Long.MIN_VALUE & j) == 0 ? 9 : 10;
    }

    public static long I(long j) {
        return (j << 1) ^ (j >> 63);
    }

    public static int V(boolean z) {
        return 1;
    }

    public static int b(int i, double d) {
        return gy(i) + f(d);
    }

    public static int b(int i, pm pmVar) {
        return (gy(i) * 2) + d(pmVar);
    }

    public static int b(int i, byte[] bArr) {
        return gy(i) + s(bArr);
    }

    public static pf b(byte[] bArr, int i, int i2) {
        return new pf(bArr, i, i2);
    }

    public static int c(int i, float f) {
        return gy(i) + e(f);
    }

    public static int c(int i, pm pmVar) {
        return gy(i) + e(pmVar);
    }

    public static int c(int i, boolean z) {
        return gy(i) + V(z);
    }

    public static int d(int i, long j) {
        return gy(i) + D(j);
    }

    public static int d(pm pmVar) {
        return pmVar.qG();
    }

    public static int df(String str) throws UnsupportedEncodingException {
        try {
            byte[] bytes = str.getBytes("UTF-8");
            return bytes.length + gA(bytes.length);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported.");
        }
    }

    public static int e(float f) {
        return 4;
    }

    public static int e(int i, long j) {
        return gy(i) + E(j);
    }

    public static int e(pm pmVar) {
        int iQG = pmVar.qG();
        return iQG + gA(iQG);
    }

    public static int f(double d) {
        return 8;
    }

    public static int gA(int i) {
        if ((i & (-128)) == 0) {
            return 1;
        }
        if ((i & (-16384)) == 0) {
            return 2;
        }
        if (((-2097152) & i) == 0) {
            return 3;
        }
        return ((-268435456) & i) == 0 ? 4 : 5;
    }

    public static int gC(int i) {
        return (i << 1) ^ (i >> 31);
    }

    public static int gv(int i) {
        if (i >= 0) {
            return gA(i);
        }
        return 10;
    }

    public static int gw(int i) {
        return gA(gC(i));
    }

    public static int gy(int i) {
        return gA(pp.x(i, 0));
    }

    public static int j(int i, String str) {
        return gy(i) + df(str);
    }

    public static pf q(byte[] bArr) {
        return b(bArr, 0, bArr.length);
    }

    public static int s(byte[] bArr) {
        return gA(bArr.length) + bArr.length;
    }

    public static int u(int i, int i2) {
        return gy(i) + gv(i2);
    }

    public static int v(int i, int i2) {
        return gy(i) + gw(i2);
    }

    public void B(long j) throws IOException {
        F(j);
    }

    public void C(long j) throws IOException {
        F(I(j));
    }

    public void F(long j) throws IOException {
        while (((-128) & j) != 0) {
            gx((((int) j) & TransportMediator.KEYCODE_MEDIA_PAUSE) | 128);
            j >>>= 7;
        }
        gx((int) j);
    }

    public void H(long j) throws IOException {
        gx(((int) j) & 255);
        gx(((int) (j >> 8)) & 255);
        gx(((int) (j >> 16)) & 255);
        gx(((int) (j >> 24)) & 255);
        gx(((int) (j >> 32)) & 255);
        gx(((int) (j >> 40)) & 255);
        gx(((int) (j >> 48)) & 255);
        gx(((int) (j >> 56)) & 255);
    }

    public void U(boolean z) throws IOException {
        gx(z ? 1 : 0);
    }

    public void a(int i, double d) throws IOException {
        w(i, 1);
        e(d);
    }

    public void a(int i, pm pmVar) throws IOException {
        w(i, 2);
        c(pmVar);
    }

    public void a(int i, byte[] bArr) throws IOException {
        w(i, 2);
        r(bArr);
    }

    public void b(byte b) throws IOException {
        if (this.position == this.awx) {
            throw new a(this.position, this.awx);
        }
        byte[] bArr = this.buffer;
        int i = this.position;
        this.position = i + 1;
        bArr[i] = b;
    }

    public void b(int i, float f) throws IOException {
        w(i, 5);
        d(f);
    }

    public void b(int i, long j) throws IOException {
        w(i, 0);
        B(j);
    }

    public void b(int i, String str) throws IOException {
        w(i, 2);
        de(str);
    }

    public void b(int i, boolean z) throws IOException {
        w(i, 0);
        U(z);
    }

    public void b(pm pmVar) throws IOException {
        pmVar.a(this);
    }

    public void c(int i, long j) throws IOException {
        w(i, 0);
        C(j);
    }

    public void c(pm pmVar) throws IOException {
        gz(pmVar.qF());
        pmVar.a(this);
    }

    public void c(byte[] bArr, int i, int i2) throws IOException {
        if (this.awx - this.position < i2) {
            throw new a(this.position, this.awx);
        }
        System.arraycopy(bArr, i, this.buffer, this.position, i2);
        this.position += i2;
    }

    public void d(float f) throws IOException {
        gB(Float.floatToIntBits(f));
    }

    public void de(String str) throws IOException {
        byte[] bytes = str.getBytes("UTF-8");
        gz(bytes.length);
        t(bytes);
    }

    public void e(double d) throws IOException {
        H(Double.doubleToLongBits(d));
    }

    public void gB(int i) throws IOException {
        gx(i & 255);
        gx((i >> 8) & 255);
        gx((i >> 16) & 255);
        gx((i >> 24) & 255);
    }

    public void gt(int i) throws IOException {
        if (i >= 0) {
            gz(i);
        } else {
            F(i);
        }
    }

    public void gu(int i) throws IOException {
        gz(gC(i));
    }

    public void gx(int i) throws IOException {
        b((byte) i);
    }

    public void gz(int i) throws IOException {
        while ((i & (-128)) != 0) {
            gx((i & TransportMediator.KEYCODE_MEDIA_PAUSE) | 128);
            i >>>= 7;
        }
        gx(i);
    }

    public int qv() {
        return this.awx - this.position;
    }

    public void qw() {
        if (qv() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    public void r(byte[] bArr) throws IOException {
        gz(bArr.length);
        t(bArr);
    }

    public void s(int i, int i2) throws IOException {
        w(i, 0);
        gt(i2);
    }

    public void t(int i, int i2) throws IOException {
        w(i, 0);
        gu(i2);
    }

    public void t(byte[] bArr) throws IOException {
        c(bArr, 0, bArr.length);
    }

    public void w(int i, int i2) throws IOException {
        gz(pp.x(i, i2));
    }
}
