package com.jcraft.jzlib;

import java.io.ByteArrayOutputStream;

/* loaded from: classes.dex */
final class Inflate {
    private static final int BAD = 13;
    private static final int BLOCKS = 7;
    private static final int CHECK1 = 11;
    private static final int CHECK2 = 10;
    private static final int CHECK3 = 9;
    private static final int CHECK4 = 8;
    private static final int COMMENT = 21;
    private static final int DICT0 = 6;
    private static final int DICT1 = 5;
    private static final int DICT2 = 4;
    private static final int DICT3 = 3;
    private static final int DICT4 = 2;
    private static final int DONE = 12;
    private static final int EXLEN = 18;
    private static final int EXTRA = 19;
    private static final int FLAG = 1;
    private static final int FLAGS = 23;
    private static final int HCRC = 22;
    private static final int HEAD = 14;
    static final int INFLATE_ANY = 1073741824;
    private static final int LENGTH = 15;
    private static final int MAX_WBITS = 15;
    private static final int METHOD = 0;
    private static final int NAME = 20;
    private static final int OS = 17;
    private static final int PRESET_DICT = 32;
    private static final int TIME = 16;
    private static final int Z_BUF_ERROR = -5;
    private static final int Z_DATA_ERROR = -3;
    private static final int Z_DEFLATED = 8;
    private static final int Z_ERRNO = -1;
    static final int Z_FINISH = 4;
    static final int Z_FULL_FLUSH = 3;
    private static final int Z_MEM_ERROR = -4;
    private static final int Z_NEED_DICT = 2;
    static final int Z_NO_FLUSH = 0;
    private static final int Z_OK = 0;
    static final int Z_PARTIAL_FLUSH = 1;
    private static final int Z_STREAM_END = 1;
    private static final int Z_STREAM_ERROR = -2;
    static final int Z_SYNC_FLUSH = 2;
    private static final int Z_VERSION_ERROR = -6;
    private static byte[] mark = {0, 0, -1, -1};
    InfBlocks blocks;
    private int flags;
    int marker;
    int method;
    int mode;
    long need;
    int wbits;
    int wrap;
    private final ZStream z;
    long was = -1;
    private int need_bytes = -1;
    private byte[] crcbuf = new byte[4];
    GZIPHeader gheader = null;
    private ByteArrayOutputStream tmp_string = null;

    int inflateReset() {
        if (this.z == null) {
            return -2;
        }
        ZStream zStream = this.z;
        this.z.total_out = 0L;
        zStream.total_in = 0L;
        this.z.msg = null;
        this.mode = 14;
        this.need_bytes = -1;
        this.blocks.reset();
        return 0;
    }

    int inflateEnd() {
        if (this.blocks != null) {
            this.blocks.free();
            return 0;
        }
        return 0;
    }

    Inflate(ZStream z) {
        this.z = z;
    }

    int inflateInit(int w) {
        this.z.msg = null;
        this.blocks = null;
        this.wrap = 0;
        if (w < 0) {
            w = -w;
        } else if ((1073741824 & w) != 0) {
            this.wrap = 4;
            w &= -1073741825;
            if (w < 48) {
                w &= 15;
            }
        } else if ((w & (-32)) != 0) {
            this.wrap = 4;
            w &= 15;
        } else {
            this.wrap = (w >> 4) + 1;
            if (w < 48) {
                w &= 15;
            }
        }
        if (w < 8 || w > 15) {
            inflateEnd();
            return -2;
        }
        if (this.blocks != null && this.wbits != w) {
            this.blocks.free();
            this.blocks = null;
        }
        this.wbits = w;
        this.blocks = new InfBlocks(this.z, 1 << w);
        inflateReset();
        return 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:444:0x0129, code lost:
    
        if (r12.z.avail_in == 0) goto L730;
     */
    /* JADX WARN: Code restructure failed: missing block: B:445:0x012b, code lost:
    
        r4 = r13;
        r5 = r12.z;
        r5.avail_in--;
        r12.z.total_in++;
        r5 = r12.z.next_in;
        r6 = r12.z;
        r6.next_in_index = r6.next_in_index + 1;
        r12.need = ((r5[r7] & 255) << 24) & 4278190080L;
        r12.mode = 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:447:0x015f, code lost:
    
        if (r12.z.avail_in == 0) goto L731;
     */
    /* JADX WARN: Code restructure failed: missing block: B:448:0x0161, code lost:
    
        r4 = r13;
        r5 = r12.z;
        r5.avail_in--;
        r12.z.total_in++;
        r6 = r12.need;
        r5 = r12.z.next_in;
        r8 = r12.z;
        r8.next_in_index = r8.next_in_index + 1;
        r12.need = r6 + (((r5[r9] & 255) << 16) & 16711680);
        r12.mode = 4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:450:0x0196, code lost:
    
        if (r12.z.avail_in == 0) goto L732;
     */
    /* JADX WARN: Code restructure failed: missing block: B:451:0x0198, code lost:
    
        r4 = r13;
        r5 = r12.z;
        r5.avail_in--;
        r12.z.total_in++;
        r6 = r12.need;
        r5 = r12.z.next_in;
        r8 = r12.z;
        r8.next_in_index = r8.next_in_index + 1;
        r12.need = r6 + (((r5[r9] & 255) << 8) & 65280);
        r12.mode = 5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:453:0x01cd, code lost:
    
        if (r12.z.avail_in == 0) goto L733;
     */
    /* JADX WARN: Code restructure failed: missing block: B:454:0x01cf, code lost:
    
        r5 = r12.z;
        r5.avail_in--;
        r12.z.total_in++;
        r6 = r12.need;
        r5 = r12.z.next_in;
        r8 = r12.z;
        r8.next_in_index = r8.next_in_index + 1;
        r12.need = r6 + (r5[r9] & 255);
        r12.z.adler.reset(r12.need);
        r12.mode = 6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:730:?, code lost:
    
        return r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:731:?, code lost:
    
        return r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:732:?, code lost:
    
        return r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:733:?, code lost:
    
        return r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:734:?, code lost:
    
        return 2;
     */
    /* JADX WARN: Removed duplicated region for block: B:469:0x0253  */
    /* JADX WARN: Removed duplicated region for block: B:472:0x028a  */
    /* JADX WARN: Removed duplicated region for block: B:475:0x02c2  */
    /* JADX WARN: Removed duplicated region for block: B:478:0x02fa  */
    /* JADX WARN: Removed duplicated region for block: B:533:0x043e  */
    /* JADX WARN: Removed duplicated region for block: B:536:0x044a  */
    /* JADX WARN: Removed duplicated region for block: B:542:0x045d  */
    /* JADX WARN: Removed duplicated region for block: B:545:0x0477  */
    /* JADX WARN: Removed duplicated region for block: B:549:0x0487  */
    /* JADX WARN: Removed duplicated region for block: B:582:0x051e  */
    /* JADX WARN: Removed duplicated region for block: B:596:0x0562  */
    /* JADX WARN: Removed duplicated region for block: B:608:0x058e  */
    /* JADX WARN: Removed duplicated region for block: B:613:0x059e  */
    /* JADX WARN: Removed duplicated region for block: B:621:0x04ff A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:623:0x04e0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:625:0x04b3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:684:0x0015 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:685:0x0015 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:686:0x0015 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:687:0x0015 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:702:0x05ae A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:705:0x057e A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    int inflate(int r13) {
        /*
            Method dump skipped, instructions count: 1516
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jcraft.jzlib.Inflate.inflate(int):int");
    }

    int inflateSetDictionary(byte[] dictionary, int dictLength) {
        if (this.z == null || (this.mode != 6 && this.wrap != 0)) {
            return -2;
        }
        int index = 0;
        int length = dictLength;
        if (this.mode == 6) {
            long adler_need = this.z.adler.getValue();
            this.z.adler.reset();
            this.z.adler.update(dictionary, 0, dictLength);
            if (this.z.adler.getValue() != adler_need) {
                return -3;
            }
        }
        this.z.adler.reset();
        if (length >= (1 << this.wbits)) {
            length = (1 << this.wbits) - 1;
            index = dictLength - length;
        }
        this.blocks.set_dictionary(dictionary, index, length);
        this.mode = 7;
        return 0;
    }

    int inflateSync() {
        if (this.z == null) {
            return -2;
        }
        if (this.mode != 13) {
            this.mode = 13;
            this.marker = 0;
        }
        int n = this.z.avail_in;
        if (n == 0) {
            return -5;
        }
        int p = this.z.next_in_index;
        int m = this.marker;
        while (n != 0 && m < 4) {
            if (this.z.next_in[p] == mark[m]) {
                m++;
            } else if (this.z.next_in[p] != 0) {
                m = 0;
            } else {
                m = 4 - m;
            }
            p++;
            n--;
        }
        this.z.total_in += p - this.z.next_in_index;
        this.z.next_in_index = p;
        this.z.avail_in = n;
        this.marker = m;
        if (m != 4) {
            return -3;
        }
        long r = this.z.total_in;
        long w = this.z.total_out;
        inflateReset();
        this.z.total_in = r;
        this.z.total_out = w;
        this.mode = 7;
        return 0;
    }

    int inflateSyncPoint() {
        if (this.z == null || this.blocks == null) {
            return -2;
        }
        return this.blocks.sync_point();
    }

    private int readBytes(int n, int r, int f) throws Return {
        if (this.need_bytes == -1) {
            this.need_bytes = n;
            this.need = 0L;
        }
        while (this.need_bytes > 0) {
            if (this.z.avail_in == 0) {
                throw new Return(r);
            }
            r = f;
            ZStream zStream = this.z;
            zStream.avail_in--;
            this.z.total_in++;
            long j = this.need;
            byte[] bArr = this.z.next_in;
            ZStream zStream2 = this.z;
            zStream2.next_in_index = zStream2.next_in_index + 1;
            this.need = j | ((bArr[r4] & 255) << ((n - this.need_bytes) * 8));
            this.need_bytes--;
        }
        if (n == 2) {
            this.need &= 65535;
        } else if (n == 4) {
            this.need &= 4294967295L;
        }
        this.need_bytes = -1;
        return r;
    }

    class Return extends Exception {
        int r;

        Return(int r) {
            this.r = r;
        }
    }

    private int readString(int r, int f) throws Return {
        if (this.tmp_string == null) {
            this.tmp_string = new ByteArrayOutputStream();
        }
        while (this.z.avail_in != 0) {
            r = f;
            ZStream zStream = this.z;
            zStream.avail_in--;
            this.z.total_in++;
            int b = this.z.next_in[this.z.next_in_index];
            if (b != 0) {
                this.tmp_string.write(this.z.next_in, this.z.next_in_index, 1);
            }
            this.z.adler.update(this.z.next_in, this.z.next_in_index, 1);
            this.z.next_in_index++;
            if (b == 0) {
                return r;
            }
        }
        throw new Return(r);
    }

    private int readBytes(int r, int f) throws Return {
        if (this.tmp_string == null) {
            this.tmp_string = new ByteArrayOutputStream();
        }
        while (this.need > 0) {
            if (this.z.avail_in == 0) {
                throw new Return(r);
            }
            r = f;
            ZStream zStream = this.z;
            zStream.avail_in--;
            this.z.total_in++;
            int b = this.z.next_in[this.z.next_in_index];
            this.tmp_string.write(this.z.next_in, this.z.next_in_index, 1);
            this.z.adler.update(this.z.next_in, this.z.next_in_index, 1);
            this.z.next_in_index++;
            this.need--;
        }
        return r;
    }

    private void checksum(int n, long v) {
        for (int i = 0; i < n; i++) {
            this.crcbuf[i] = (byte) (255 & v);
            v >>= 8;
        }
        this.z.adler.update(this.crcbuf, 0, n);
    }

    public GZIPHeader getGZIPHeader() {
        return this.gheader;
    }

    boolean inParsingHeader() {
        switch (this.mode) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 14:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                return true;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 15:
            default:
                return false;
        }
    }
}
