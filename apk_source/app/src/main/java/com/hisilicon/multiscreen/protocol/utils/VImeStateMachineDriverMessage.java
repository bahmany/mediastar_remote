package com.hisilicon.multiscreen.protocol.utils;

/* loaded from: classes.dex */
public enum VImeStateMachineDriverMessage {
    OPEN_VIME("client_open_vime", 1),
    CLOSE_VIME("client_close_vime", 2),
    CHECK_OK("client_check_ok", 3),
    NETWORK_EXCEPTION("client_network_exception", 4),
    CHECK_FAIL("client_check_fail", 5),
    CALL_INPUT("server_call_input", 6),
    END_INPUT("end_input", 7);

    private final int mIndex;
    private String mMessageName;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static VImeStateMachineDriverMessage[] valuesCustom() {
        VImeStateMachineDriverMessage[] vImeStateMachineDriverMessageArrValuesCustom = values();
        int length = vImeStateMachineDriverMessageArrValuesCustom.length;
        VImeStateMachineDriverMessage[] vImeStateMachineDriverMessageArr = new VImeStateMachineDriverMessage[length];
        System.arraycopy(vImeStateMachineDriverMessageArrValuesCustom, 0, vImeStateMachineDriverMessageArr, 0, length);
        return vImeStateMachineDriverMessageArr;
    }

    VImeStateMachineDriverMessage(String name, int index) {
        this.mMessageName = name;
        this.mIndex = index;
    }

    public static VImeStateMachineDriverMessage getState(int index) {
        for (VImeStateMachineDriverMessage status : valuesCustom()) {
            if (status.getIndex() == index) {
                return status;
            }
        }
        return null;
    }

    public static String getName(int index) {
        for (VImeStateMachineDriverMessage status : valuesCustom()) {
            if (status.getIndex() == index) {
                return status.mMessageName;
            }
        }
        return null;
    }

    public String getName() {
        return this.mMessageName;
    }

    public void setName(String name) {
        this.mMessageName = name;
    }

    public int getIndex() {
        return this.mIndex;
    }
}
