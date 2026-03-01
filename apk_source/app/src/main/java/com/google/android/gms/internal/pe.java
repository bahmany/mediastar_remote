package com.google.android.gms.internal;

import android.support.v7.internal.widget.ActivityChooserView;
import java.io.IOException;

/* loaded from: classes.dex */
public final class pe {
    private int awo;
    private int awp;
    private int awq;
    private int awr;
    private int aws;
    private int awu;
    private final byte[] buffer;
    private int awt = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
    private int awv = 64;
    private int aww = 67108864;

    private pe(byte[] bArr, int i, int i2) {
        this.buffer = bArr;
        this.awo = i;
        this.awp = i + i2;
        this.awr = i;
    }

    public static long A(long j) {
        return (j >>> 1) ^ (-(1 & j));
    }

    public static pe a(byte[] bArr, int i, int i2) {
        return new pe(bArr, i, i2);
    }

    public static int gn(int i) {
        return (i >>> 1) ^ (-(i & 1));
    }

    public static pe p(byte[] bArr) {
        return a(bArr, 0, bArr.length);
    }

    private void qr() {
        this.awp += this.awq;
        int i = this.awp;
        if (i <= this.awt) {
            this.awq = 0;
        } else {
            this.awq = i - this.awt;
            this.awp -= this.awq;
        }
    }

    public void a(pm pmVar) throws IOException {
        int iQn = qn();
        if (this.awu >= this.awv) {
            throw pl.qE();
        }
        int iGo = go(iQn);
        this.awu++;
        pmVar.b(this);
        gl(0);
        this.awu--;
        gp(iGo);
    }

    public void a(pm pmVar, int i) throws IOException {
        if (this.awu >= this.awv) {
            throw pl.qE();
        }
        this.awu++;
        pmVar.b(this);
        gl(pp.x(i, 4));
        this.awu--;
    }

    public int getPosition() {
        return this.awr - this.awo;
    }

    public void gl(int i) throws pl {
        if (this.aws != i) {
            throw pl.qC();
        }
    }

    public boolean gm(int i) throws IOException {
        switch (pp.gG(i)) {
            case 0:
                qj();
                return true;
            case 1:
                qq();
                return true;
            case 2:
                gs(qn());
                return true;
            case 3:
                qh();
                gl(pp.x(pp.gH(i), 4));
                return true;
            case 4:
                return false;
            case 5:
                qp();
                return true;
            default:
                throw pl.qD();
        }
    }

    public int go(int i) throws pl {
        if (i < 0) {
            throw pl.qz();
        }
        int i2 = this.awr + i;
        int i3 = this.awt;
        if (i2 > i3) {
            throw pl.qy();
        }
        this.awt = i2;
        qr();
        return i3;
    }

    public void gp(int i) {
        this.awt = i;
        qr();
    }

    public void gq(int i) {
        if (i > this.awr - this.awo) {
            throw new IllegalArgumentException("Position " + i + " is beyond current " + (this.awr - this.awo));
        }
        if (i < 0) {
            throw new IllegalArgumentException("Bad position " + i);
        }
        this.awr = this.awo + i;
    }

    public byte[] gr(int i) throws IOException {
        if (i < 0) {
            throw pl.qz();
        }
        if (this.awr + i > this.awt) {
            gs(this.awt - this.awr);
            throw pl.qy();
        }
        if (i > this.awp - this.awr) {
            throw pl.qy();
        }
        byte[] bArr = new byte[i];
        System.arraycopy(this.buffer, this.awr, bArr, 0, i);
        this.awr += i;
        return bArr;
    }

    public void gs(int i) throws IOException {
        if (i < 0) {
            throw pl.qz();
        }
        if (this.awr + i > this.awt) {
            gs(this.awt - this.awr);
            throw pl.qy();
        }
        if (i > this.awp - this.awr) {
            throw pl.qy();
        }
        this.awr += i;
    }

    public int qg() throws IOException {
        if (qt()) {
            this.aws = 0;
            return 0;
        }
        this.aws = qn();
        if (this.aws == 0) {
            throw pl.qB();
        }
        return this.aws;
    }

    public void qh() throws IOException {
        int iQg;
        do {
            iQg = qg();
            if (iQg == 0) {
                return;
            }
        } while (gm(iQg));
    }

    public long qi() throws IOException {
        return qo();
    }

    public int qj() throws IOException {
        return qn();
    }

    public boolean qk() throws IOException {
        return qn() != 0;
    }

    public int ql() throws IOException {
        return gn(qn());
    }

    public long qm() throws IOException {
        return A(qo());
    }

    public int qn() throws IOException {
        byte bQu = qu();
        if (bQu >= 0) {
            return bQu;
        }
        int i = bQu & Byte.MAX_VALUE;
        byte bQu2 = qu();
        if (bQu2 >= 0) {
            return i | (bQu2 << 7);
        }
        int i2 = i | ((bQu2 & Byte.MAX_VALUE) << 7);
        byte bQu3 = qu();
        if (bQu3 >= 0) {
            return i2 | (bQu3 << 14);
        }
        int i3 = i2 | ((bQu3 & Byte.MAX_VALUE) << 14);
        byte bQu4 = qu();
        if (bQu4 >= 0) {
            return i3 | (bQu4 << 21);
        }
        int i4 = i3 | ((bQu4 & Byte.MAX_VALUE) << 21);
        byte bQu5 = qu();
        int i5 = i4 | (bQu5 << 28);
        if (bQu5 >= 0) {
            return i5;
        }
        for (int i6 = 0; i6 < 5; i6++) {
            if (qu() >= 0) {
                return i5;
            }
        }
        throw pl.qA();
    }

    public long qo() throws IOException {
        long j = 0;
        for (int i = 0; i < 64; i += 7) {
            j |= (r3 & Byte.MAX_VALUE) << i;
            if ((qu() & 128) == 0) {
                return j;
            }
        }
        throw pl.qA();
    }

    public int qp() throws IOException {
        return (qu() & 255) | ((qu() & 255) << 8) | ((qu() & 255) << 16) | ((qu() & 255) << 24);
    }

    public long qq() throws IOException {
        return ((qu() & 255) << 8) | (qu() & 255) | ((qu() & 255) << 16) | ((qu() & 255) << 24) | ((qu() & 255) << 32) | ((qu() & 255) << 40) | ((qu() & 255) << 48) | ((qu() & 255) << 56);
    }

    public int qs() {
        if (this.awt == Integer.MAX_VALUE) {
            return -1;
        }
        return this.awt - this.awr;
    }

    public boolean qt() {
        return this.awr == this.awp;
    }

    public byte qu() throws IOException {
        if (this.awr == this.awp) {
            throw pl.qy();
        }
        byte[] bArr = this.buffer;
        int i = this.awr;
        this.awr = i + 1;
        return bArr[i];
    }

    public byte[] r(int i, int i2) {
        if (i2 == 0) {
            return pp.awS;
        }
        byte[] bArr = new byte[i2];
        System.arraycopy(this.buffer, this.awo + i, bArr, 0, i2);
        return bArr;
    }

    public byte[] readBytes() throws IOException {
        int iQn = qn();
        if (iQn > this.awp - this.awr || iQn <= 0) {
            return gr(iQn);
        }
        byte[] bArr = new byte[iQn];
        System.arraycopy(this.buffer, this.awr, bArr, 0, iQn);
        this.awr = iQn + this.awr;
        return bArr;
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(qq());
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(qp());
    }

    public String readString() throws IOException {
        int iQn = qn();
        if (iQn > this.awp - this.awr || iQn <= 0) {
            return new String(gr(iQn), "UTF-8");
        }
        String str = new String(this.buffer, this.awr, iQn, "UTF-8");
        this.awr = iQn + this.awr;
        return str;
    }
}
