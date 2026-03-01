package com.jcraft.jzlib;

import android.support.v4.media.TransportMediator;
import mktvsmart.screen.GlobalConstantValue;

/* loaded from: classes.dex */
final class InfBlocks {
    private static final int BAD = 9;
    private static final int BTREE = 4;
    private static final int CODES = 6;
    private static final int DONE = 8;
    private static final int DRY = 7;
    private static final int DTREE = 5;
    private static final int LENS = 1;
    private static final int MANY = 1440;
    private static final int STORED = 2;
    private static final int TABLE = 3;
    private static final int TYPE = 0;
    private static final int Z_BUF_ERROR = -5;
    private static final int Z_DATA_ERROR = -3;
    private static final int Z_ERRNO = -1;
    private static final int Z_MEM_ERROR = -4;
    private static final int Z_NEED_DICT = 2;
    private static final int Z_OK = 0;
    private static final int Z_STREAM_END = 1;
    private static final int Z_STREAM_ERROR = -2;
    private static final int Z_VERSION_ERROR = -6;
    int bitb;
    int bitk;
    int[] blens;
    private boolean check;
    private final InfCodes codes;
    int end;
    int index;
    int last;
    int left;
    int mode;
    int read;
    int table;
    byte[] window;
    int write;
    private final ZStream z;
    private static final int[] inflate_mask = {0, 1, 3, 7, 15, 31, 63, TransportMediator.KEYCODE_MEDIA_PAUSE, 255, 511, GlobalConstantValue.GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE, 2047, 4095, 8191, 16383, 32767, 65535};
    static final int[] border = {16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15};
    int[] bb = new int[1];
    int[] tb = new int[1];
    int[] bl = new int[1];
    int[] bd = new int[1];
    int[][] tl = new int[1][];
    int[][] td = new int[1][];
    int[] tli = new int[1];
    int[] tdi = new int[1];
    private final InfTree inftree = new InfTree();
    int[] hufts = new int[4320];

    InfBlocks(ZStream z, int w) {
        this.z = z;
        this.codes = new InfCodes(this.z, this);
        this.window = new byte[w];
        this.end = w;
        this.check = z.istate.wrap != 0;
        this.mode = 0;
        reset();
    }

    void reset() {
        if (this.mode != 4) {
        }
        if (this.mode == 6) {
            this.codes.free(this.z);
        }
        this.mode = 0;
        this.bitk = 0;
        this.bitb = 0;
        this.write = 0;
        this.read = 0;
        if (this.check) {
            this.z.adler.reset();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:114:0x0553, code lost:
    
        r4 = r26.blens;
        r5 = com.jcraft.jzlib.InfBlocks.border;
        r6 = r26.index;
        r26.index = r6 + 1;
        r4[r5[r6]] = r14 & 7;
        r14 = r14 >>> 3;
        r19 = r19 - 3;
        r22 = r23;
     */
    /* JADX WARN: Code restructure failed: missing block: B:149:0x0771, code lost:
    
        r26.blens = null;
        r26.mode = 9;
        r26.z.msg = "invalid bit length repeat";
        r26.bitb = r14;
        r26.bitk = r19;
        r26.z.avail_in = r21;
        r26.z.total_in += r23 - r26.z.next_in_index;
        r26.z.next_in_index = r23;
        r26.write = r24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:256:?, code lost:
    
        return inflate_flush(-3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:262:?, code lost:
    
        return inflate_flush(-3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x03cd, code lost:
    
        r26.mode = 9;
        r26.z.msg = "too many length or distance symbols";
        r26.bitb = r14;
        r26.bitk = r19;
        r26.z.avail_in = r21;
        r26.z.total_in += r23 - r26.z.next_in_index;
        r26.z.next_in_index = r23;
        r26.write = r24;
     */
    /* JADX WARN: Removed duplicated region for block: B:126:0x067a  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x08b8  */
    /* JADX WARN: Removed duplicated region for block: B:204:0x08b2 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:235:0x05e1 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    int proc(int r27) {
        /*
            Method dump skipped, instructions count: 2590
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jcraft.jzlib.InfBlocks.proc(int):int");
    }

    void free() {
        reset();
        this.window = null;
        this.hufts = null;
    }

    void set_dictionary(byte[] d, int start, int n) {
        System.arraycopy(d, start, this.window, 0, n);
        this.write = n;
        this.read = n;
    }

    int sync_point() {
        return this.mode == 1 ? 1 : 0;
    }

    int inflate_flush(int r) {
        int p = this.z.next_out_index;
        int q = this.read;
        int n = (q <= this.write ? this.write : this.end) - q;
        if (n > this.z.avail_out) {
            n = this.z.avail_out;
        }
        if (n != 0 && r == -5) {
            r = 0;
        }
        this.z.avail_out -= n;
        this.z.total_out += n;
        if (this.check && n > 0) {
            this.z.adler.update(this.window, q, n);
        }
        System.arraycopy(this.window, q, this.z.next_out, p, n);
        int p2 = p + n;
        int q2 = q + n;
        if (q2 == this.end) {
            if (this.write == this.end) {
                this.write = 0;
            }
            int n2 = this.write - 0;
            if (n2 > this.z.avail_out) {
                n2 = this.z.avail_out;
            }
            if (n2 != 0 && r == -5) {
                r = 0;
            }
            this.z.avail_out -= n2;
            this.z.total_out += n2;
            if (this.check && n2 > 0) {
                this.z.adler.update(this.window, 0, n2);
            }
            System.arraycopy(this.window, 0, this.z.next_out, p2, n2);
            p2 += n2;
            q2 = 0 + n2;
        }
        this.z.next_out_index = p2;
        this.read = q2;
        return r;
    }
}
