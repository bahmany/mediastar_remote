package org.teleal.cling.support.model;

/* loaded from: classes.dex */
public enum RecordMediumWriteStatus {
    WRITABLE,
    PROTECTED,
    NOT_WRITABLE,
    UNKNOWN,
    NOT_IMPLEMENTED;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static RecordMediumWriteStatus[] valuesCustom() {
        RecordMediumWriteStatus[] recordMediumWriteStatusArrValuesCustom = values();
        int length = recordMediumWriteStatusArrValuesCustom.length;
        RecordMediumWriteStatus[] recordMediumWriteStatusArr = new RecordMediumWriteStatus[length];
        System.arraycopy(recordMediumWriteStatusArrValuesCustom, 0, recordMediumWriteStatusArr, 0, length);
        return recordMediumWriteStatusArr;
    }

    public static RecordMediumWriteStatus valueOrUnknownOf(String s) {
        try {
            return valueOf(s);
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
