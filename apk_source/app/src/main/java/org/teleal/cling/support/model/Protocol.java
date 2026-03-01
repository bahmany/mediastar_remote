package org.teleal.cling.support.model;

/* loaded from: classes.dex */
public enum Protocol {
    ALL("*"),
    HTTP_GET("http-get"),
    RTSP_RTP_UDP("rtsp-rtp-udp"),
    INTERNAL("internal"),
    IEC61883("iec61883");

    private String protocolString;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static Protocol[] valuesCustom() {
        Protocol[] protocolArrValuesCustom = values();
        int length = protocolArrValuesCustom.length;
        Protocol[] protocolArr = new Protocol[length];
        System.arraycopy(protocolArrValuesCustom, 0, protocolArr, 0, length);
        return protocolArr;
    }

    Protocol(String protocolString) {
        this.protocolString = protocolString;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.protocolString;
    }

    public static Protocol valueOrNullOf(String s) {
        for (Protocol protocol : valuesCustom()) {
            if (protocol.toString().equals(s)) {
                return protocol;
            }
        }
        return null;
    }
}
