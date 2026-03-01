package com.jcraft.jzlib;

import android.support.v4.media.TransportMediator;
import mktvsmart.screen.GlobalConstantValue;

/* loaded from: classes.dex */
final class InfCodes {
    private static final int BADCODE = 9;
    private static final int COPY = 5;
    private static final int DIST = 3;
    private static final int DISTEXT = 4;
    private static final int END = 8;
    private static final int LEN = 1;
    private static final int LENEXT = 2;
    private static final int LIT = 6;
    private static final int START = 0;
    private static final int WASH = 7;
    private static final int Z_BUF_ERROR = -5;
    private static final int Z_DATA_ERROR = -3;
    private static final int Z_ERRNO = -1;
    private static final int Z_MEM_ERROR = -4;
    private static final int Z_NEED_DICT = 2;
    private static final int Z_OK = 0;
    private static final int Z_STREAM_END = 1;
    private static final int Z_STREAM_ERROR = -2;
    private static final int Z_VERSION_ERROR = -6;
    private static final int[] inflate_mask = {0, 1, 3, 7, 15, 31, 63, TransportMediator.KEYCODE_MEDIA_PAUSE, 255, 511, GlobalConstantValue.GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE, 2047, 4095, 8191, 16383, 32767, 65535};
    byte dbits;
    int dist;
    int[] dtree;
    int dtree_index;
    int get;
    byte lbits;
    int len;
    int lit;
    int[] ltree;
    int ltree_index;
    int mode;
    int need;
    private final InfBlocks s;
    int[] tree;
    int tree_index = 0;
    private final ZStream z;

    InfCodes(ZStream z, InfBlocks s) {
        this.z = z;
        this.s = s;
    }

    void init(int bl, int bd, int[] tl, int tl_index, int[] td, int td_index) {
        this.mode = 0;
        this.lbits = (byte) bl;
        this.dbits = (byte) bd;
        this.ltree = tl;
        this.ltree_index = tl_index;
        this.dtree = td;
        this.dtree_index = td_index;
        this.tree = null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:346:0x07b7, code lost:
    
        r24.s.bitb = r11;
        r24.s.bitk = r16;
        r24.z.avail_in = r18;
        r24.z.total_in += r19 - r24.z.next_in_index;
        r24.z.next_in_index = r19;
        r24.s.write = r21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:408:?, code lost:
    
        return r24.s.inflate_flush(1);
     */
    /* JADX WARN: Removed duplicated region for block: B:236:0x01b5  */
    /* JADX WARN: Removed duplicated region for block: B:262:0x039b  */
    /* JADX WARN: Removed duplicated region for block: B:281:0x0504 A[LOOP:5: B:274:0x0495->B:281:0x0504, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:282:0x050c  */
    /* JADX WARN: Removed duplicated region for block: B:391:0x0179 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:393:0x02f6 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    int proc(int r25) {
        /*
            Method dump skipped, instructions count: 2142
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jcraft.jzlib.InfCodes.proc(int):int");
    }

    void free(ZStream z) {
    }

    int inflate_fast(int bl, int bd, int[] tl, int tl_index, int[] td, int td_index, InfBlocks s, ZStream z) {
        int r;
        int q;
        int q2;
        int q3;
        int p = z.next_in_index;
        int n = z.avail_in;
        int b = s.bitb;
        int k = s.bitk;
        int q4 = s.write;
        int m = q4 < s.read ? (s.read - q4) - 1 : s.end - q4;
        int ml = inflate_mask[bl];
        int md = inflate_mask[bd];
        int q5 = q4;
        while (true) {
            int p2 = p;
            if (k < 20) {
                n--;
                p = p2 + 1;
                b |= (z.next_in[p2] & 255) << k;
                k += 8;
            } else {
                int t = b & ml;
                int tp_index_t_3 = (tl_index + t) * 3;
                int e = tl[tp_index_t_3];
                if (e == 0) {
                    b >>= tl[tp_index_t_3 + 1];
                    k -= tl[tp_index_t_3 + 1];
                    q2 = q5 + 1;
                    s.window[q5] = (byte) tl[tp_index_t_3 + 2];
                    m--;
                    p = p2;
                } else {
                    while (true) {
                        b >>= tl[tp_index_t_3 + 1];
                        k -= tl[tp_index_t_3 + 1];
                        if ((e & 16) != 0) {
                            int e2 = e & 15;
                            int c = tl[tp_index_t_3 + 2] + (inflate_mask[e2] & b);
                            int b2 = b >> e2;
                            int k2 = k - e2;
                            while (k2 < 15) {
                                n--;
                                b2 |= (z.next_in[p2] & 255) << k2;
                                k2 += 8;
                                p2++;
                            }
                            int t2 = b2 & md;
                            int tp_index_t_32 = (td_index + t2) * 3;
                            int e3 = td[tp_index_t_32];
                            while (true) {
                                b2 >>= td[tp_index_t_32 + 1];
                                k2 -= td[tp_index_t_32 + 1];
                                if ((e3 & 16) != 0) {
                                    int e4 = e3 & 15;
                                    while (k2 < e4) {
                                        n--;
                                        b2 |= (z.next_in[p2] & 255) << k2;
                                        k2 += 8;
                                        p2++;
                                    }
                                    int d = td[tp_index_t_32 + 2] + (inflate_mask[e4] & b2);
                                    b = b2 >> e4;
                                    k = k2 - e4;
                                    m -= c;
                                    if (q5 >= d) {
                                        int r2 = q5 - d;
                                        if (q5 - r2 > 0 && 2 > q5 - r2) {
                                            int q6 = q5 + 1;
                                            int r3 = r2 + 1;
                                            s.window[q5] = s.window[r2];
                                            r = r3 + 1;
                                            s.window[q6] = s.window[r3];
                                            c -= 2;
                                            q = q6 + 1;
                                        } else {
                                            System.arraycopy(s.window, r2, s.window, q5, 2);
                                            q = q5 + 2;
                                            r = r2 + 2;
                                            c -= 2;
                                        }
                                    } else {
                                        r = q5 - d;
                                        do {
                                            r += s.end;
                                        } while (r < 0);
                                        int e5 = s.end - r;
                                        if (c > e5) {
                                            c -= e5;
                                            if (q5 - r <= 0 || e5 <= q5 - r) {
                                                System.arraycopy(s.window, r, s.window, q5, e5);
                                                q = q5 + e5;
                                                int i = r + e5;
                                            } else {
                                                while (true) {
                                                    int q7 = q5;
                                                    q5 = q7 + 1;
                                                    int r4 = r + 1;
                                                    s.window[q7] = s.window[r];
                                                    e5--;
                                                    if (e5 == 0) {
                                                        break;
                                                    }
                                                    r = r4;
                                                }
                                                q = q5;
                                            }
                                            r = 0;
                                        } else {
                                            q = q5;
                                        }
                                    }
                                    if (q - r <= 0 || c <= q - r) {
                                        System.arraycopy(s.window, r, s.window, q, c);
                                        q2 = q + c;
                                        int i2 = r + c;
                                        p = p2;
                                    } else {
                                        while (true) {
                                            q3 = q + 1;
                                            int r5 = r + 1;
                                            s.window[q] = s.window[r];
                                            c--;
                                            if (c == 0) {
                                                break;
                                            }
                                            r = r5;
                                            q = q3;
                                        }
                                        q2 = q3;
                                        p = p2;
                                    }
                                } else if ((e3 & 64) == 0) {
                                    t2 = t2 + td[tp_index_t_32 + 2] + (inflate_mask[e3] & b2);
                                    tp_index_t_32 = (td_index + t2) * 3;
                                    e3 = td[tp_index_t_32];
                                } else {
                                    z.msg = "invalid distance code";
                                    int c2 = z.avail_in - n;
                                    if ((k2 >> 3) < c2) {
                                        c2 = k2 >> 3;
                                    }
                                    s.bitb = b2;
                                    s.bitk = k2 - (c2 << 3);
                                    z.avail_in = n + c2;
                                    z.total_in += r15 - z.next_in_index;
                                    z.next_in_index = p2 - c2;
                                    s.write = q5;
                                    return -3;
                                }
                            }
                        } else if ((e & 64) == 0) {
                            t = t + tl[tp_index_t_3 + 2] + (inflate_mask[e] & b);
                            tp_index_t_3 = (tl_index + t) * 3;
                            e = tl[tp_index_t_3];
                            if (e == 0) {
                                b >>= tl[tp_index_t_3 + 1];
                                k -= tl[tp_index_t_3 + 1];
                                q2 = q5 + 1;
                                s.window[q5] = (byte) tl[tp_index_t_3 + 2];
                                m--;
                                p = p2;
                                break;
                            }
                        } else {
                            if ((e & 32) != 0) {
                                int c3 = z.avail_in - n;
                                if ((k >> 3) < c3) {
                                    c3 = k >> 3;
                                }
                                s.bitb = b;
                                s.bitk = k - (c3 << 3);
                                z.avail_in = n + c3;
                                z.total_in += r15 - z.next_in_index;
                                z.next_in_index = p2 - c3;
                                s.write = q5;
                                return 1;
                            }
                            z.msg = "invalid literal/length code";
                            int c4 = z.avail_in - n;
                            if ((k >> 3) < c4) {
                                c4 = k >> 3;
                            }
                            s.bitb = b;
                            s.bitk = k - (c4 << 3);
                            z.avail_in = n + c4;
                            z.total_in += r15 - z.next_in_index;
                            z.next_in_index = p2 - c4;
                            s.write = q5;
                            return -3;
                        }
                    }
                }
                if (m < 258 || n < 10) {
                    break;
                }
                q5 = q2;
            }
        }
        int c5 = z.avail_in - n;
        if ((k >> 3) < c5) {
            c5 = k >> 3;
        }
        s.bitb = b;
        s.bitk = k - (c5 << 3);
        z.avail_in = n + c5;
        z.total_in += r15 - z.next_in_index;
        z.next_in_index = p - c5;
        s.write = q2;
        return 0;
    }
}
