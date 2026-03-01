package org.teleal.cling.support.model;

import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.common.util.MimeType;

/* loaded from: classes.dex */
public class ProtocolInfo {
    public static final String TRAILING_ZEROS = "000000000000000000000000";
    public static final String WILDCARD = "*";
    protected String additionalInfo;
    protected String contentFormat;
    protected String network;
    protected Protocol protocol;

    public static final class DLNAFlags {
        public static final int BACKGROUND_TRANSFERT_MODE = 4194304;
        public static final int BYTE_BASED_SEEK = 536870912;
        public static final int CONNECTION_STALL = 2097152;
        public static final int DLNA_V15 = 1048576;
        public static final int FLAG_PLAY_CONTAINER = 268435456;
        public static final int INTERACTIVE_TRANSFERT_MODE = 8388608;
        public static final int RTSP_PAUSE = 33554432;
        public static final int S0_INCREASE = 134217728;
        public static final int SENDER_PACED = Integer.MIN_VALUE;
        public static final int SN_INCREASE = 67108864;
        public static final int STREAMING_TRANSFER_MODE = 16777216;
        public static final int TIME_BASED_SEEK = 1073741824;
    }

    public ProtocolInfo(String s) throws InvalidValueException {
        this.protocol = Protocol.ALL;
        this.network = "*";
        this.contentFormat = "*";
        this.additionalInfo = "*";
        if (s == null) {
            throw new NullPointerException();
        }
        String s2 = s.trim();
        String[] split = s2.split(":");
        if (split.length != 4) {
            throw new InvalidValueException("Can't parse ProtocolInfo string: " + s2);
        }
        this.protocol = Protocol.valueOrNullOf(split[0]);
        this.network = split[1];
        this.contentFormat = split[2];
        this.additionalInfo = split[3];
    }

    public ProtocolInfo(MimeType contentFormatMimeType) {
        this.protocol = Protocol.ALL;
        this.network = "*";
        this.contentFormat = "*";
        this.additionalInfo = "*";
        this.protocol = Protocol.HTTP_GET;
        this.contentFormat = contentFormatMimeType.toString();
    }

    public ProtocolInfo(Protocol protocol, String network, String contentFormat, String additionalInfo) {
        this.protocol = Protocol.ALL;
        this.network = "*";
        this.contentFormat = "*";
        this.additionalInfo = "*";
        this.protocol = protocol;
        this.network = network;
        this.contentFormat = contentFormat;
        this.additionalInfo = additionalInfo;
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    public String getNetwork() {
        return this.network;
    }

    public String getContentFormat() {
        return this.contentFormat;
    }

    public MimeType getContentFormatMimeType() throws IllegalArgumentException {
        return MimeType.valueOf(this.contentFormat);
    }

    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProtocolInfo that = (ProtocolInfo) o;
        return this.additionalInfo.equals(that.additionalInfo) && this.contentFormat.equals(that.contentFormat) && this.network.equals(that.network) && this.protocol == that.protocol;
    }

    public int hashCode() {
        int result = this.protocol.hashCode();
        return (((((result * 31) + this.network.hashCode()) * 31) + this.contentFormat.hashCode()) * 31) + this.additionalInfo.hashCode();
    }

    public String toString() {
        return String.valueOf(this.protocol.toString()) + ":" + this.network + ":" + this.contentFormat + ":" + this.additionalInfo;
    }
}
