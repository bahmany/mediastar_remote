package org.teleal.cling.support.model;

/* loaded from: classes.dex */
public enum Channel {
    Master,
    LF,
    RF,
    CF,
    LFE,
    LS,
    RS,
    LFC,
    RFC,
    SD,
    SL,
    SR,
    T;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static Channel[] valuesCustom() {
        Channel[] channelArrValuesCustom = values();
        int length = channelArrValuesCustom.length;
        Channel[] channelArr = new Channel[length];
        System.arraycopy(channelArrValuesCustom, 0, channelArr, 0, length);
        return channelArr;
    }
}
