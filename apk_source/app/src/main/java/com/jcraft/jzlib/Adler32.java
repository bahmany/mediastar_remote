package com.jcraft.jzlib;

/* loaded from: classes.dex */
public final class Adler32 implements Checksum {
    private static final int BASE = 65521;
    private static final int NMAX = 5552;
    private long s1 = 1;
    private long s2 = 0;

    @Override // com.jcraft.jzlib.Checksum
    public void reset(long init) {
        this.s1 = init & 65535;
        this.s2 = (init >> 16) & 65535;
    }

    @Override // com.jcraft.jzlib.Checksum
    public void reset() {
        this.s1 = 1L;
        this.s2 = 0L;
    }

    @Override // com.jcraft.jzlib.Checksum
    public long getValue() {
        return (this.s2 << 16) | this.s1;
    }

    @Override // com.jcraft.jzlib.Checksum
    public void update(byte[] buf, int index, int len) {
        int index2;
        if (len == 1) {
            int i = index + 1;
            this.s1 += buf[index] & 255;
            this.s2 += this.s1;
            this.s1 %= 65521;
            this.s2 %= 65521;
            return;
        }
        int len1 = len / NMAX;
        int len2 = len % NMAX;
        int len12 = len1;
        while (true) {
            int len13 = len12 - 1;
            if (len12 <= 0) {
                break;
            }
            int k = NMAX;
            len -= NMAX;
            while (true) {
                int k2 = k;
                index2 = index;
                k = k2 - 1;
                if (k2 <= 0) {
                    break;
                }
                index = index2 + 1;
                this.s1 += buf[index2] & 255;
                this.s2 += this.s1;
            }
            this.s1 %= 65521;
            this.s2 %= 65521;
            len12 = len13;
            index = index2;
        }
        int k3 = len2;
        int i2 = len - k3;
        while (true) {
            int k4 = k3;
            int index3 = index;
            k3 = k4 - 1;
            if (k4 > 0) {
                index = index3 + 1;
                this.s1 += buf[index3] & 255;
                this.s2 += this.s1;
            } else {
                this.s1 %= 65521;
                this.s2 %= 65521;
                return;
            }
        }
    }

    @Override // com.jcraft.jzlib.Checksum
    public Adler32 copy() {
        Adler32 foo = new Adler32();
        foo.s1 = this.s1;
        foo.s2 = this.s2;
        return foo;
    }

    static long combine(long adler1, long adler2, long len2) {
        long rem = len2 % 65521;
        long sum1 = adler1 & 65535;
        long sum2 = rem * sum1;
        long sum12 = sum1 + (((65535 & adler2) + 65521) - 1);
        long sum22 = (sum2 % 65521) + (((((adler1 >> 16) & 65535) + ((adler2 >> 16) & 65535)) + 65521) - rem);
        if (sum12 >= 65521) {
            sum12 -= 65521;
        }
        if (sum12 >= 65521) {
            sum12 -= 65521;
        }
        if (sum22 >= (65521 << 1)) {
            sum22 -= 65521 << 1;
        }
        if (sum22 >= 65521) {
            sum22 -= 65521;
        }
        return (sum22 << 16) | sum12;
    }
}
