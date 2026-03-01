package com.jcraft.jzlib;

/* loaded from: classes.dex */
public final class CRC32 implements Checksum {
    private static final int GF2_DIM = 32;
    private static int[] crc_table;
    private int v = 0;

    static {
        crc_table = null;
        crc_table = new int[256];
        for (int n = 0; n < 256; n++) {
            int c = n;
            int k = 8;
            while (true) {
                k--;
                if (k < 0) {
                    break;
                } else if ((c & 1) != 0) {
                    c = (-306674912) ^ (c >>> 1);
                } else {
                    c >>>= 1;
                }
            }
            crc_table[n] = c;
        }
    }

    @Override // com.jcraft.jzlib.Checksum
    public void update(byte[] buf, int index, int len) {
        int c = this.v ^ (-1);
        while (true) {
            int index2 = index;
            len--;
            if (len >= 0) {
                index = index2 + 1;
                c = crc_table[(buf[index2] ^ c) & 255] ^ (c >>> 8);
            } else {
                this.v = c ^ (-1);
                return;
            }
        }
    }

    @Override // com.jcraft.jzlib.Checksum
    public void reset() {
        this.v = 0;
    }

    @Override // com.jcraft.jzlib.Checksum
    public void reset(long vv) {
        this.v = (int) (4294967295L & vv);
    }

    @Override // com.jcraft.jzlib.Checksum
    public long getValue() {
        return this.v & 4294967295L;
    }

    static long combine(long crc1, long crc2, long len2) {
        long[] even = new long[32];
        long[] odd = new long[32];
        if (len2 > 0) {
            odd[0] = 3988292384L;
            long row = 1;
            for (int n = 1; n < 32; n++) {
                odd[n] = row;
                row <<= 1;
            }
            gf2_matrix_square(even, odd);
            gf2_matrix_square(odd, even);
            do {
                gf2_matrix_square(even, odd);
                if ((1 & len2) != 0) {
                    crc1 = gf2_matrix_times(even, crc1);
                }
                long len22 = len2 >> 1;
                if (len22 == 0) {
                    break;
                }
                gf2_matrix_square(odd, even);
                if ((1 & len22) != 0) {
                    crc1 = gf2_matrix_times(odd, crc1);
                }
                len2 = len22 >> 1;
            } while (len2 != 0);
            return crc1 ^ crc2;
        }
        return crc1;
    }

    private static long gf2_matrix_times(long[] mat, long vec) {
        long sum = 0;
        int index = 0;
        while (vec != 0) {
            if ((1 & vec) != 0) {
                sum ^= mat[index];
            }
            vec >>= 1;
            index++;
        }
        return sum;
    }

    static final void gf2_matrix_square(long[] square, long[] mat) {
        for (int n = 0; n < 32; n++) {
            square[n] = gf2_matrix_times(mat, mat[n]);
        }
    }

    @Override // com.jcraft.jzlib.Checksum
    public CRC32 copy() {
        CRC32 foo = new CRC32();
        foo.v = this.v;
        return foo;
    }

    public static int[] getCRC32Table() {
        int[] tmp = new int[crc_table.length];
        System.arraycopy(crc_table, 0, tmp, 0, tmp.length);
        return tmp;
    }
}
