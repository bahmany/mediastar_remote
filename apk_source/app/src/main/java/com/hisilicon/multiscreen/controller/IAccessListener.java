package com.hisilicon.multiscreen.controller;

/* loaded from: classes.dex */
public interface IAccessListener {

    public enum Caller {
        AccessPing,
        ReAccess,
        KeepAlive,
        Others;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Caller[] valuesCustom() {
            Caller[] callerArrValuesCustom = values();
            int length = callerArrValuesCustom.length;
            Caller[] callerArr = new Caller[length];
            System.arraycopy(callerArrValuesCustom, 0, callerArr, 0, length);
            return callerArr;
        }
    }

    void dealNetWorkLostEvent(Caller caller);

    void dealNetWorkNotWellEvent();

    void dealReaveEvent(Caller caller);

    void dealSTBLeaveEvent(Caller caller);

    void dealSTBSuspendEvent(Caller caller);
}
