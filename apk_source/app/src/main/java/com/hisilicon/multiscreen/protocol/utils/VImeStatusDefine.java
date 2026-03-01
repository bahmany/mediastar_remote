package com.hisilicon.multiscreen.protocol.utils;

/* loaded from: classes.dex */
public class VImeStatusDefine {
    private static final int DEINIT_STATUS_VIME_CLIENT = 5;
    private static final int INIT_STATUS_VIME_CLIENT = 1;
    private static final int INPUT_STATUS_VIME_CLIENT = 4;
    private static final int READY_STATUS_VIME_CLIENT = 3;
    private static final int SETUP_STATUS_VIME_CLIENT = 2;
    private static final int VIME_SERVER_STATUS_DEINIT = 83;
    private static final int VIME_SERVER_STATUS_INIT = 80;
    private static final int VIME_SERVER_STATUS_INPUT = 82;
    private static final int VIME_SERVER_STATUS_READY = 81;

    public enum VimeStatus {
        INIT_STATUS_VIME_CLIENT("client_init", 1),
        SETUP_STATUS_VIME_CLIENT("client_setup", 2),
        READY_STATUS_VIME_CLIENT("client_ready", 3),
        INPUT_STATUS_VIME_CLIENT("client_input", 4),
        DEINIT_STATUS_VIME_CLIENT("client_close", 5),
        VIME_SERVER_STATUS_INIT("server_init", 80),
        VIME_SERVER_STATUS_READY("server_ready", 81),
        VIME_SERVER_STATUS_INPUT("server_input", 82),
        VIME_SERVER_STATUS_DEINIT("server_deinit", 83);

        private int index;
        private String name;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static VimeStatus[] valuesCustom() {
            VimeStatus[] vimeStatusArrValuesCustom = values();
            int length = vimeStatusArrValuesCustom.length;
            VimeStatus[] vimeStatusArr = new VimeStatus[length];
            System.arraycopy(vimeStatusArrValuesCustom, 0, vimeStatusArr, 0, length);
            return vimeStatusArr;
        }

        public static VimeStatus getStatus(int index) {
            for (VimeStatus status : valuesCustom()) {
                if (status.getIndex() == index) {
                    return status;
                }
            }
            return null;
        }

        public static String getName(int index) {
            for (VimeStatus status : valuesCustom()) {
                if (status.getIndex() == index) {
                    return status.name;
                }
            }
            return null;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return this.index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        VimeStatus(String name, int index) {
            this.name = name;
            this.index = index;
        }
    }
}
