package com.github.mikephil.charting.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.media.TransportMediator;
import com.alibaba.fastjson.asm.Opcodes;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ColorTemplate {
    public static final int COLOR_NONE = -1;
    public static final int COLOR_SKIP = -2;
    public static final int[] LIBERTY_COLORS = {Color.rgb(207, KeyInfo.KEYCODE_B, KeyInfo.KEYCODE_C), Color.rgb(Opcodes.LCMP, 212, 212), Color.rgb(136, Opcodes.GETFIELD, Opcodes.NEW), Color.rgb(118, 174, 175), Color.rgb(42, KeyInfo.KEYCODE_NEXT, TransportMediator.KEYCODE_MEDIA_RECORD)};
    public static final int[] JOYFUL_COLORS = {Color.rgb(KeyInfo.KEYCODE_SEARCH, 80, 138), Color.rgb(254, Opcodes.FCMPL, 7), Color.rgb(254, KeyInfo.KEYCODE_V, KeyInfo.KEYCODE_ASK), Color.rgb(106, Opcodes.GOTO, 134), Color.rgb(53, 194, 209)};
    public static final int[] PASTEL_COLORS = {Color.rgb(64, 89, 128), Color.rgb(Opcodes.FCMPL, Opcodes.IF_ACMPEQ, 124), Color.rgb(KeyInfo.KEYCODE_SEARCH, Opcodes.INVOKESTATIC, Opcodes.IF_ICMPGE), Color.rgb(191, 134, 134), Color.rgb(Opcodes.PUTSTATIC, 48, 80)};
    public static final int[] COLORFUL_COLORS = {Color.rgb(Opcodes.INSTANCEOF, 37, 82), Color.rgb(255, 102, 0), Color.rgb(KeyInfo.KEYCODE_X, Opcodes.IFNONNULL, 0), Color.rgb(106, Opcodes.FCMPG, 31), Color.rgb(Opcodes.PUTSTATIC, 100, 53)};
    public static final int[] VORDIPLOM_COLORS = {Color.rgb(Opcodes.CHECKCAST, 255, 140), Color.rgb(255, KeyInfo.KEYCODE_V, 140), Color.rgb(255, 208, 140), Color.rgb(140, KeyInfo.KEYCODE_G, 255), Color.rgb(255, 140, Opcodes.IFGT)};

    public static int getHoloBlue() {
        return Color.rgb(51, Opcodes.PUTFIELD, 229);
    }

    public static List<Integer> createColors(Resources r, int[] colors) {
        List<Integer> result = new ArrayList<>();
        for (int i : colors) {
            result.add(Integer.valueOf(r.getColor(i)));
        }
        return result;
    }

    public static List<Integer> createColors(int[] colors) {
        List<Integer> result = new ArrayList<>();
        for (int i : colors) {
            result.add(Integer.valueOf(i));
        }
        return result;
    }
}
