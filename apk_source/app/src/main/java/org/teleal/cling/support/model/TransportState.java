package org.teleal.cling.support.model;

/* loaded from: classes.dex */
public enum TransportState {
    STOPPED,
    PLAYING,
    TRANSITIONING,
    PAUSED_PLAYBACK,
    PAUSED_RECORDING,
    RECORDING,
    NO_MEDIA_PRESENT,
    CUSTOM;

    String value = name();

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static TransportState[] valuesCustom() {
        TransportState[] transportStateArrValuesCustom = values();
        int length = transportStateArrValuesCustom.length;
        TransportState[] transportStateArr = new TransportState[length];
        System.arraycopy(transportStateArrValuesCustom, 0, transportStateArr, 0, length);
        return transportStateArr;
    }

    TransportState() {
    }

    public String getValue() {
        return this.value;
    }

    public TransportState setValue(String value) {
        this.value = value;
        return this;
    }

    public static TransportState valueOrCustomOf(String s) {
        try {
            return valueOf(s);
        } catch (IllegalArgumentException e) {
            return CUSTOM.setValue(s);
        }
    }
}
