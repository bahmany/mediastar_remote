package org.teleal.cling.model.gena;

/* loaded from: classes.dex */
public enum CancelReason {
    RENEWAL_FAILED,
    DEVICE_WAS_REMOVED,
    UNSUBSCRIBE_FAILED,
    EXPIRED;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static CancelReason[] valuesCustom() {
        CancelReason[] cancelReasonArrValuesCustom = values();
        int length = cancelReasonArrValuesCustom.length;
        CancelReason[] cancelReasonArr = new CancelReason[length];
        System.arraycopy(cancelReasonArrValuesCustom, 0, cancelReasonArr, 0, length);
        return cancelReasonArr;
    }
}
