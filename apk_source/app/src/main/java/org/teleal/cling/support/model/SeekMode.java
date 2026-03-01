package org.teleal.cling.support.model;

/* loaded from: classes.dex */
public enum SeekMode {
    TRACK_NR("TRACK_NR"),
    ABS_TIME("ABS_TIME"),
    REL_TIME("REL_TIME"),
    ABS_COUNT("ABS_COUNT"),
    REL_COUNT("REL_COUNT"),
    CHANNEL_FREQ("CHANNEL_FREQ"),
    TAPE_INDEX("TAPE-INDEX"),
    FRAME("FRAME");

    private String protocolString;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static SeekMode[] valuesCustom() {
        SeekMode[] seekModeArrValuesCustom = values();
        int length = seekModeArrValuesCustom.length;
        SeekMode[] seekModeArr = new SeekMode[length];
        System.arraycopy(seekModeArrValuesCustom, 0, seekModeArr, 0, length);
        return seekModeArr;
    }

    SeekMode(String protocolString) {
        this.protocolString = protocolString;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.protocolString;
    }

    public static SeekMode valueOrExceptionOf(String s) throws IllegalArgumentException {
        for (SeekMode seekMode : valuesCustom()) {
            if (seekMode.protocolString.equals(s)) {
                return seekMode;
            }
        }
        throw new IllegalArgumentException("Invalid seek mode string: " + s);
    }
}
