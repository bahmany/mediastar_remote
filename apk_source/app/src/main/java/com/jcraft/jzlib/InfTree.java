package com.jcraft.jzlib;

import android.support.v4.media.TransportMediator;
import com.alibaba.fastjson.asm.Opcodes;
import com.google.android.gms.fitness.FitnessActivities;
import com.hisilicon.multiscreen.protocol.message.Action;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import com.hisilicon.multiscreen.protocol.message.MessageDef;
import com.hisilicon.multiscreen.protocol.message.PushMessageHead;
import com.iflytek.speech.VoiceWakeuperAidl;
import org.cybergarage.multiscreenhttp.HTTPStatus;

/* loaded from: classes.dex */
final class InfTree {
    static final int BMAX = 15;
    private static final int MANY = 1440;
    private static final int Z_BUF_ERROR = -5;
    private static final int Z_DATA_ERROR = -3;
    private static final int Z_ERRNO = -1;
    private static final int Z_MEM_ERROR = -4;
    private static final int Z_NEED_DICT = 2;
    private static final int Z_OK = 0;
    private static final int Z_STREAM_END = 1;
    private static final int Z_STREAM_ERROR = -2;
    private static final int Z_VERSION_ERROR = -6;
    static final int fixed_bd = 5;
    static final int fixed_bl = 9;
    static final int[] fixed_tl = {96, 7, 256, 0, 8, 80, 0, 8, 16, 84, 8, KeyInfo.KEYCODE_VOLUME_UP, 82, 7, 31, 0, 8, 112, 0, 8, 48, 0, 9, Opcodes.CHECKCAST, 80, 7, 10, 0, 8, 96, 0, 8, 32, 0, 9, Opcodes.IF_ICMPNE, 0, 8, 0, 0, 8, 128, 0, 8, 64, 0, 9, KeyInfo.KEYCODE_O, 80, 7, 6, 0, 8, 88, 0, 8, 24, 0, 9, 144, 83, 7, 59, 0, 8, KeyInfo.KEYCODE_ASK, 0, 8, 56, 0, 9, 208, 81, 7, 17, 0, 8, 104, 0, 8, 40, 0, 9, Opcodes.ARETURN, 0, 8, 8, 0, 8, 136, 0, 8, 72, 0, 9, 240, 80, 7, 4, 0, 8, 84, 0, 8, 20, 85, 8, 227, 83, 7, 43, 0, 8, 116, 0, 8, 52, 0, 9, 200, 81, 7, 13, 0, 8, 100, 0, 8, 36, 0, 9, 168, 0, 8, 4, 0, 8, Opcodes.IINC, 0, 8, 68, 0, 9, KeyInfo.KEYCODE_D, 80, 7, 8, 0, 8, 92, 0, 8, 28, 0, 9, Opcodes.DCMPG, 84, 7, 83, 0, 8, 124, 0, 8, 60, 0, 9, KeyInfo.KEYCODE_Q, 82, 7, 23, 0, 8, 108, 0, 8, 44, 0, 9, Opcodes.INVOKESTATIC, 0, 8, 12, 0, 8, 140, 0, 8, 76, 0, 9, KeyInfo.KEYCODE_B, 80, 7, 3, 0, 8, 82, 0, 8, 18, 85, 8, Opcodes.IF_ICMPGT, 83, 7, 35, 0, 8, KeyInfo.KEYCODE_VOLUME_DOWN, 0, 8, 50, 0, 9, 196, 81, 7, 11, 0, 8, 98, 0, 8, 34, 0, 9, 164, 0, 8, 2, 0, 8, TransportMediator.KEYCODE_MEDIA_RECORD, 0, 8, 66, 0, 9, 228, 80, 7, 7, 0, 8, 90, 0, 8, 26, 0, 9, Opcodes.LCMP, 84, 7, 67, 0, 8, 122, 0, 8, 58, 0, 9, 212, 82, 7, 19, 0, 8, 106, 0, 8, 42, 0, 9, Opcodes.GETFIELD, 0, 8, 10, 0, 8, 138, 0, 8, 74, 0, 9, KeyInfo.KEYCODE_Z, 80, 7, 5, 0, 8, 86, 0, 8, 22, Opcodes.CHECKCAST, 8, 0, 83, 7, 51, 0, 8, 118, 0, 8, 54, 0, 9, HTTPStatus.NO_CONTENT, 81, 7, 15, 0, 8, 102, 0, 8, 38, 0, 9, Opcodes.IRETURN, 0, 8, 6, 0, 8, 134, 0, 8, 70, 0, 9, KeyInfo.KEYCODE_J, 80, 7, 9, 0, 8, 94, 0, 8, 30, 0, 9, Opcodes.IFGE, 84, 7, 99, 0, 8, 126, 0, 8, 62, 0, 9, KeyInfo.KEYCODE_T, 82, 7, 27, 0, 8, 110, 0, 8, 46, 0, 9, 188, 0, 8, 14, 0, 8, 142, 0, 8, 78, 0, 9, 252, 96, 7, 256, 0, 8, 81, 0, 8, 17, 85, 8, 131, 82, 7, 31, 0, 8, KeyInfo.KEYCODE_MUTE, 0, 8, 49, 0, 9, 194, 80, 7, 10, 0, 8, 97, 0, 8, 33, 0, 9, Opcodes.IF_ICMPGE, 0, 8, 1, 0, 8, 129, 0, 8, 65, 0, 9, 226, 80, 7, 6, 0, 8, 89, 0, 8, 25, 0, 9, Opcodes.I2C, 83, 7, 59, 0, 8, 121, 0, 8, 57, 0, 9, 210, 81, 7, 17, 0, 8, 105, 0, 8, 41, 0, 9, Opcodes.GETSTATIC, 0, 8, 9, 0, 8, 137, 0, 8, 73, 0, 9, 242, 80, 7, 4, 0, 8, 85, 0, 8, 21, 80, 8, VoiceWakeuperAidl.RES_SPECIFIED, 83, 7, 43, 0, 8, 117, 0, 8, 53, 0, 9, HTTPStatus.ACCEPTED, 81, 7, 13, 0, 8, 101, 0, 8, 37, 0, 9, 170, 0, 8, 5, 0, 8, 133, 0, 8, 69, 0, 9, KeyInfo.KEYCODE_G, 80, 7, 8, 0, 8, 93, 0, 8, 29, 0, 9, Opcodes.IFNE, 84, 7, 83, 0, 8, 125, 0, 8, 61, 0, 9, KeyInfo.KEYCODE_E, 82, 7, 23, 0, 8, KeyInfo.KEYCODE_NEXT, 0, 8, 45, 0, 9, 186, 0, 8, 13, 0, 8, 141, 0, 8, 77, 0, 9, KeyInfo.KEYCODE_M, 80, 7, 3, 0, 8, 83, 0, 8, 19, 85, 8, 195, 83, 7, 35, 0, 8, KeyInfo.KEYCODE_VOLUME_UP, 0, 8, 51, 0, 9, Opcodes.IFNULL, 81, 7, 11, 0, 8, 99, 0, 8, 35, 0, 9, Opcodes.IF_ACMPNE, 0, 8, 3, 0, 8, 131, 0, 8, 67, 0, 9, KeyInfo.KEYCODE_A, 80, 7, 7, 0, 8, 91, 0, 8, 27, 0, 9, Opcodes.FCMPG, 84, 7, 67, 0, 8, 123, 0, 8, 59, 0, 9, 214, 82, 7, 19, 0, 8, FitnessActivities.KICK_SCOOTER, 0, 8, 43, 0, 9, Opcodes.INVOKEVIRTUAL, 0, 8, 11, 0, 8, KeyInfo.KEYCODE_MENU, 0, 8, 75, 0, 9, KeyInfo.KEYCODE_C, 80, 7, 5, 0, 8, 87, 0, 8, 23, Opcodes.CHECKCAST, 8, 0, 83, 7, 51, 0, 8, 119, 0, 8, 55, 0, 9, 206, 81, 7, 15, 0, 8, 103, 0, 8, 39, 0, 9, 174, 0, 8, 7, 0, 8, 135, 0, 8, 71, 0, 9, KeyInfo.KEYCODE_L, 80, 7, 9, 0, 8, 95, 0, 8, 31, 0, 9, 158, 84, 7, 99, 0, 8, TransportMediator.KEYCODE_MEDIA_PAUSE, 0, 8, 63, 0, 9, KeyInfo.KEYCODE_U, 82, 7, 27, 0, 8, 111, 0, 8, 47, 0, 9, 190, 0, 8, 15, 0, 8, 143, 0, 8, 79, 0, 9, 254, 96, 7, 256, 0, 8, 80, 0, 8, 16, 84, 8, KeyInfo.KEYCODE_VOLUME_UP, 82, 7, 31, 0, 8, 112, 0, 8, 48, 0, 9, Opcodes.INSTANCEOF, 80, 7, 10, 0, 8, 96, 0, 8, 32, 0, 9, Opcodes.IF_ICMPLT, 0, 8, 0, 0, 8, 128, 0, 8, 64, 0, 9, KeyInfo.KEYCODE_P, 80, 7, 6, 0, 8, 88, 0, 8, 24, 0, 9, Opcodes.I2B, 83, 7, 59, 0, 8, KeyInfo.KEYCODE_ASK, 0, 8, 56, 0, 9, 209, 81, 7, 17, 0, 8, 104, 0, 8, 40, 0, 9, Opcodes.RETURN, 0, 8, 8, 0, 8, 136, 0, 8, 72, 0, 9, 241, 80, 7, 4, 0, 8, 84, 0, 8, 20, 85, 8, 227, 83, 7, 43, 0, 8, 116, 0, 8, 52, 0, 9, HTTPStatus.CREATED, 81, 7, 13, 0, 8, 100, 0, 8, 36, 0, 9, Opcodes.RET, 0, 8, 4, 0, 8, Opcodes.IINC, 0, 8, 68, 0, 9, KeyInfo.KEYCODE_F, 80, 7, 8, 0, 8, 92, 0, 8, 28, 0, 9, Opcodes.IFEQ, 84, 7, 83, 0, 8, 124, 0, 8, 60, 0, 9, KeyInfo.KEYCODE_SEARCH, 82, 7, 23, 0, 8, 108, 0, 8, 44, 0, 9, Opcodes.INVOKEINTERFACE, 0, 8, 12, 0, 8, 140, 0, 8, 76, 0, 9, KeyInfo.KEYCODE_N, 80, 7, 3, 0, 8, 82, 0, 8, 18, 85, 8, Opcodes.IF_ICMPGT, 83, 7, 35, 0, 8, KeyInfo.KEYCODE_VOLUME_DOWN, 0, 8, 50, 0, 9, 197, 81, 7, 11, 0, 8, 98, 0, 8, 34, 0, 9, Opcodes.IF_ACMPEQ, 0, 8, 2, 0, 
    8, TransportMediator.KEYCODE_MEDIA_RECORD, 0, 8, 66, 0, 9, 229, 80, 7, 7, 0, 8, 90, 0, 8, 26, 0, 9, Opcodes.FCMPL, 84, 7, 67, 0, 8, 122, 0, 8, 58, 0, 9, 213, 82, 7, 19, 0, 8, 106, 0, 8, 42, 0, 9, Opcodes.PUTFIELD, 0, 8, 10, 0, 8, 138, 0, 8, 74, 0, 9, KeyInfo.KEYCODE_X, 80, 7, 5, 0, 8, 86, 0, 8, 22, Opcodes.CHECKCAST, 8, 0, 83, 7, 51, 0, 8, 118, 0, 8, 54, 0, 9, HTTPStatus.RESET_CONTENT, 81, 7, 15, 0, 8, 102, 0, 8, 38, 0, 9, 173, 0, 8, 6, 0, 8, 134, 0, 8, 70, 0, 9, KeyInfo.KEYCODE_K, 80, 7, 9, 0, 8, 94, 0, 8, 30, 0, 9, Opcodes.IFGT, 84, 7, 99, 0, 8, 126, 0, 8, 62, 0, 9, KeyInfo.KEYCODE_Y, 82, 7, 27, 0, 8, 110, 0, 8, 46, 0, 9, 189, 0, 8, 14, 0, 8, 142, 0, 8, 78, 0, 9, 253, 96, 7, 256, 0, 8, 81, 0, 8, 17, 85, 8, 131, 82, 7, 31, 0, 8, KeyInfo.KEYCODE_MUTE, 0, 8, 49, 0, 9, 195, 80, 7, 10, 0, 8, 97, 0, 8, 33, 0, 9, Opcodes.IF_ICMPGT, 0, 8, 1, 0, 8, 129, 0, 8, 65, 0, 9, 227, 80, 7, 6, 0, 8, 89, 0, 8, 25, 0, 9, Opcodes.I2S, 83, 7, 59, 0, 8, 121, 0, 8, 57, 0, 9, 211, 81, 7, 17, 0, 8, 105, 0, 8, 41, 0, 9, Opcodes.PUTSTATIC, 0, 8, 9, 0, 8, 137, 0, 8, 73, 0, 9, 243, 80, 7, 4, 0, 8, 85, 0, 8, 21, 80, 8, VoiceWakeuperAidl.RES_SPECIFIED, 83, 7, 43, 0, 8, 117, 0, 8, 53, 0, 9, HTTPStatus.NON_AUTHORITATIVE_INFORMATION, 81, 7, 13, 0, 8, 101, 0, 8, 37, 0, 9, 171, 0, 8, 5, 0, 8, 133, 0, 8, 69, 0, 9, KeyInfo.KEYCODE_H, 80, 7, 8, 0, 8, 93, 0, 8, 29, 0, 9, Opcodes.IFLT, 84, 7, 83, 0, 8, 125, 0, 8, 61, 0, 9, KeyInfo.KEYCODE_R, 82, 7, 23, 0, 8, KeyInfo.KEYCODE_NEXT, 0, 8, 45, 0, 9, Opcodes.NEW, 0, 8, 13, 0, 8, 141, 0, 8, 77, 0, 9, 251, 80, 7, 3, 0, 8, 83, 0, 8, 19, 85, 8, 195, 83, 7, 35, 0, 8, KeyInfo.KEYCODE_VOLUME_UP, 0, 8, 51, 0, 9, Opcodes.IFNONNULL, 81, 7, 11, 0, 8, 99, 0, 8, 35, 0, 9, Opcodes.GOTO, 0, 8, 3, 0, 8, 131, 0, 8, 67, 0, 9, KeyInfo.KEYCODE_S, 80, 7, 7, 0, 8, 91, 0, 8, 27, 0, 9, Opcodes.DCMPL, 84, 7, 67, 0, 8, 123, 0, 8, 59, 0, 9, 215, 82, 7, 19, 0, 8, FitnessActivities.KICK_SCOOTER, 0, 8, 43, 0, 9, Opcodes.INVOKESPECIAL, 0, 8, 11, 0, 8, KeyInfo.KEYCODE_MENU, 0, 8, 75, 0, 9, KeyInfo.KEYCODE_V, 80, 7, 5, 0, 8, 87, 0, 8, 23, Opcodes.CHECKCAST, 8, 0, 83, 7, 51, 0, 8, 119, 0, 8, 55, 0, 9, 207, 81, 7, 15, 0, 8, 103, 0, 8, 39, 0, 9, 175, 0, 8, 7, 0, 8, 135, 0, 8, 71, 0, 9, 239, 80, 7, 9, 0, 8, 95, 0, 8, 31, 0, 9, 159, 84, 7, 99, 0, 8, TransportMediator.KEYCODE_MEDIA_PAUSE, 0, 8, 63, 0, 9, KeyInfo.KEYCODE_I, 82, 7, 27, 0, 8, 111, 0, 8, 47, 0, 9, 191, 0, 8, 15, 0, 8, 143, 0, 8, 79, 0, 9, 255};
    static final int[] fixed_td = {80, 5, 1, 87, 5, 257, 83, 5, 17, 91, 5, 4097, 81, 5, 5, 89, 5, 1025, 85, 5, 65, 93, 5, Action.ACTION_ID_SPEECH_TEXT_SEND, 80, 5, 3, 88, 5, 513, 84, 5, 33, 92, 5, MessageDef.UPDATE_SENSOR_ICON, 82, 5, 9, 90, 5, 2049, 86, 5, 129, Opcodes.CHECKCAST, 5, 24577, 80, 5, 2, 87, 5, 385, 83, 5, 25, 91, 5, 6145, 81, 5, 7, 89, 5, 1537, 85, 5, 97, 93, 5, 24577, 80, 5, 4, 88, 5, PushMessageHead.DEFAULT_RESPONSE, 84, 5, 49, 92, 5, 12289, 82, 5, 13, 90, 5, 3073, 86, 5, Opcodes.INSTANCEOF, Opcodes.CHECKCAST, 5, 24577};
    static final int[] cplens = {3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31, 35, 43, 51, 59, 67, 83, 99, KeyInfo.KEYCODE_VOLUME_UP, 131, Opcodes.IF_ICMPGT, 195, 227, VoiceWakeuperAidl.RES_SPECIFIED, 0, 0};
    static final int[] cplext = {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0, 112, 112};
    static final int[] cpdist = {1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, Opcodes.INSTANCEOF, 257, 385, 513, PushMessageHead.DEFAULT_RESPONSE, 1025, 1537, 2049, 3073, 4097, 6145, MessageDef.UPDATE_SENSOR_ICON, 12289, Action.ACTION_ID_SPEECH_TEXT_SEND, 24577};
    static final int[] cpdext = {0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13};
    int[] hn = null;
    int[] v = null;
    int[] c = null;
    int[] r = null;
    int[] u = null;
    int[] x = null;

    InfTree() {
    }

    /* JADX WARN: Code restructure failed: missing block: B:54:0x012e, code lost:
    
        r12 = r12 + 1;
        r15 = r16;
     */
    /* JADX WARN: Incorrect condition in loop: B:86:0x025d */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int huft_build(int[] r27, int r28, int r29, int r30, int[] r31, int[] r32, int[] r33, int[] r34, int[] r35, int[] r36, int[] r37) {
        /*
            Method dump skipped, instructions count: 766
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jcraft.jzlib.InfTree.huft_build(int[], int, int, int, int[], int[], int[], int[], int[], int[], int[]):int");
    }

    int inflate_trees_bits(int[] c, int[] bb, int[] tb, int[] hp, ZStream z) {
        initWorkArea(19);
        this.hn[0] = 0;
        int result = huft_build(c, 0, 19, 19, null, null, tb, bb, hp, this.hn, this.v);
        if (result == -3) {
            z.msg = "oversubscribed dynamic bit lengths tree";
            return result;
        }
        if (result == -5 || bb[0] == 0) {
            z.msg = "incomplete dynamic bit lengths tree";
            return -3;
        }
        return result;
    }

    int inflate_trees_dynamic(int nl, int nd, int[] c, int[] bl, int[] bd, int[] tl, int[] td, int[] hp, ZStream z) {
        initWorkArea(288);
        this.hn[0] = 0;
        int result = huft_build(c, 0, nl, 257, cplens, cplext, tl, bl, hp, this.hn, this.v);
        if (result != 0 || bl[0] == 0) {
            if (result == -3) {
                z.msg = "oversubscribed literal/length tree";
            } else if (result != -4) {
                z.msg = "incomplete literal/length tree";
                result = -3;
            }
            return result;
        }
        initWorkArea(288);
        int result2 = huft_build(c, nl, nd, 0, cpdist, cpdext, td, bd, hp, this.hn, this.v);
        if (result2 != 0 || (bd[0] == 0 && nl > 257)) {
            if (result2 == -3) {
                z.msg = "oversubscribed distance tree";
            } else if (result2 == -5) {
                z.msg = "incomplete distance tree";
                result2 = -3;
            } else if (result2 != -4) {
                z.msg = "empty distance tree with lengths";
                result2 = -3;
            }
            return result2;
        }
        return 0;
    }

    static int inflate_trees_fixed(int[] bl, int[] bd, int[][] tl, int[][] td, ZStream z) {
        bl[0] = 9;
        bd[0] = 5;
        tl[0] = fixed_tl;
        td[0] = fixed_td;
        return 0;
    }

    private void initWorkArea(int vsize) {
        if (this.hn == null) {
            this.hn = new int[1];
            this.v = new int[vsize];
            this.c = new int[16];
            this.r = new int[3];
            this.u = new int[15];
            this.x = new int[16];
        }
        if (this.v.length < vsize) {
            this.v = new int[vsize];
        }
        for (int i = 0; i < vsize; i++) {
            this.v[i] = 0;
        }
        for (int i2 = 0; i2 < 16; i2++) {
            this.c[i2] = 0;
        }
        for (int i3 = 0; i3 < 3; i3++) {
            this.r[i3] = 0;
        }
        System.arraycopy(this.c, 0, this.u, 0, 15);
        System.arraycopy(this.c, 0, this.x, 0, 16);
    }
}
