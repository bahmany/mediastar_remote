package mktvsmart.screen.gchat.client;

/* loaded from: classes.dex */
public class TransmissionPackage {
    public static final short GCHAT_MSG_RESPOND_BLACK_LIST = 16;
    public static final short GCHAT_MSG_RESPOND_LOGIN = 4;
    public static final short GCHAT_MSG_RESPOND_MSG_RECEIVE = 11;
    public static final short GCHAT_MSG_RESPOND_MSG_SEND_STATE = 10;
    public static final short GCHAT_MSG_RESPOND_REPEAT_LOGIN = 21;
    public static final short GCHAT_MSG_RESPOND_ROOM_INFO = 6;
    public static final short GCHAT_MSG_RESPOND_SERVER_INFO = 2;
    public static final short GCHAT_MSG_RESPOND_SET_BLACK_LIST_STATE = 14;
    public static final short GCHAT_MSG_RESPOND_SET_USER_NAME_STATE = 18;
    public static final short GCHAT_MSG_RESPOND_USER_CHANGE = 19;
    public static final short GCHAT_MSG_RESPOND_USER_LIST = 8;
    public static final short GCHAT_MSG_SEND_ENTER_ROOM = 5;
    public static final short GCHAT_MSG_SEND_EXIT_CHAT = 22;
    public static final short GCHAT_MSG_SEND_HEARTBEAT = 0;
    public static final short GCHAT_MSG_SEND_LOGIN = 3;
    public static final short GCHAT_MSG_SEND_MSG_RECEIVE_STATE = 12;
    public static final short GCHAT_MSG_SEND_MSG_SEND = 9;
    public static final short GCHAT_MSG_SEND_REQUEST_BLACK_LIST = 15;
    public static final short GCHAT_MSG_SEND_REQUEST_SERVER = 1;
    public static final short GCHAT_MSG_SEND_REQUEST_USER_LIST = 7;
    public static final short GCHAT_MSG_SEND_SET_BLACK_LIST = 13;
    public static final short GCHAT_MSG_SEND_SET_USER_NAME = 17;
    public static final short GCHAT_MSG_SEND_USER_NUMBER_RECEIVED = 20;
    public static final short TRANSMISSION_HEADER_LENGTH = 8;
    public static final short TRANSMISSION_PACKAGE_HEADER = 0;
    private byte[] mBody;
    private short mType;
    private int mLength = 8;
    private short mHeader = 0;

    public int getLength() {
        return this.mLength;
    }

    public void setLength(int length) {
        this.mLength = length;
    }

    public short getHeader() {
        return this.mHeader;
    }

    public void setHeader(short header) {
        this.mHeader = header;
    }

    public short getType() {
        return this.mType;
    }

    public void setType(short type) {
        this.mType = type;
    }

    public byte[] getBody() {
        return this.mBody;
    }

    public void setBody(byte[] body) {
        this.mBody = body;
    }
}
