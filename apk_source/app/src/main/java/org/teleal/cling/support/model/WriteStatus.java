package org.teleal.cling.support.model;

/* loaded from: classes.dex */
public enum WriteStatus {
    WRITABLE,
    NOT_WRITABLE,
    UNKNOWN,
    MIXED;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static WriteStatus[] valuesCustom() {
        WriteStatus[] writeStatusArrValuesCustom = values();
        int length = writeStatusArrValuesCustom.length;
        WriteStatus[] writeStatusArr = new WriteStatus[length];
        System.arraycopy(writeStatusArrValuesCustom, 0, writeStatusArr, 0, length);
        return writeStatusArr;
    }
}
