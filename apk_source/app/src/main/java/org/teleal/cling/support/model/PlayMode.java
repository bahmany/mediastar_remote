package org.teleal.cling.support.model;

/* loaded from: classes.dex */
public enum PlayMode {
    NORMAL,
    SHUFFLE,
    REPEAT_ONE,
    REPEAT_ALL,
    RANDOM,
    DIRECT_1,
    INTRO;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static PlayMode[] valuesCustom() {
        PlayMode[] playModeArrValuesCustom = values();
        int length = playModeArrValuesCustom.length;
        PlayMode[] playModeArr = new PlayMode[length];
        System.arraycopy(playModeArrValuesCustom, 0, playModeArr, 0, length);
        return playModeArr;
    }
}
