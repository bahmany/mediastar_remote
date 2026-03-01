package com.voicetechnology.rtspclient.headers;

import com.iflytek.speech.VoiceWakeuperAidl;
import com.voicetechnology.rtspclient.concepts.Header;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class TransportHeader extends Header {
    public static final String NAME = "Transport";
    private List<String> parameters;
    private LowerTransport transport;

    public enum LowerTransport {
        TCP,
        UDP,
        DEFAULT;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static LowerTransport[] valuesCustom() {
            LowerTransport[] lowerTransportArrValuesCustom = values();
            int length = lowerTransportArrValuesCustom.length;
            LowerTransport[] lowerTransportArr = new LowerTransport[length];
            System.arraycopy(lowerTransportArrValuesCustom, 0, lowerTransportArr, 0, length);
            return lowerTransportArr;
        }
    }

    public TransportHeader(String header) {
        super(header);
        String value = getRawValue();
        if (!value.startsWith("RTP/AVP")) {
            throw new IllegalArgumentException("Missing RTP/AVP");
        }
        int index = 7;
        if (value.charAt(7) == '/') {
            int index2 = 7 + 1;
            switch (value.charAt(index2)) {
                case 'T':
                    this.transport = LowerTransport.TCP;
                    break;
                case 'U':
                    this.transport = LowerTransport.UDP;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid Transport: " + value.substring(7));
            }
            index = index2 + 2;
        } else {
            this.transport = LowerTransport.DEFAULT;
        }
        int index3 = index + 1;
        if (value.charAt(index3) != ';' || index3 != value.length()) {
            throw new IllegalArgumentException("Parameter block expected");
        }
        addParameters(value.substring(index3 + 1).split(VoiceWakeuperAidl.PARAMS_SEPARATE));
    }

    public TransportHeader(LowerTransport transport, String... parameters) {
        super(NAME);
        this.transport = transport;
        addParameters(parameters);
    }

    void addParameters(String[] parameterList) {
        if (this.parameters == null) {
            this.parameters = Arrays.asList(parameterList);
        } else {
            this.parameters.addAll(Arrays.asList(parameterList));
        }
    }

    LowerTransport getTransport() {
        return this.transport;
    }

    String getParameter(String part) {
        for (String parameter : this.parameters) {
            if (parameter.startsWith(part)) {
                return parameter;
            }
        }
        throw new IllegalArgumentException("No such parameter named " + part);
    }

    @Override // com.voicetechnology.rtspclient.concepts.Header
    public String toString() {
        StringBuilder buffer = new StringBuilder(NAME).append(": ").append("RTP/AVP");
        if (this.transport != LowerTransport.DEFAULT) {
            buffer.append('/').append(this.transport);
        }
        for (String parameter : this.parameters) {
            buffer.append(';').append(parameter);
        }
        return buffer.toString();
    }
}
