package org.teleal.cling.support.model;

/* loaded from: classes.dex */
public enum TransportStatus {
    OK,
    ERROR_OCCURED,
    CUSTOM;

    String value = name();

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static TransportStatus[] valuesCustom() {
        TransportStatus[] transportStatusArrValuesCustom = values();
        int length = transportStatusArrValuesCustom.length;
        TransportStatus[] transportStatusArr = new TransportStatus[length];
        System.arraycopy(transportStatusArrValuesCustom, 0, transportStatusArr, 0, length);
        return transportStatusArr;
    }

    TransportStatus() {
    }

    public String getValue() {
        return this.value;
    }

    public TransportStatus setValue(String value) {
        this.value = value;
        return this;
    }

    public static TransportStatus valueOrCustomOf(String s) {
        try {
            return valueOf(s);
        } catch (IllegalArgumentException e) {
            return CUSTOM.setValue(s);
        }
    }
}
