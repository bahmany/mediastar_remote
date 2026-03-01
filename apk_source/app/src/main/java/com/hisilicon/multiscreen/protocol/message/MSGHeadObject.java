package com.hisilicon.multiscreen.protocol.message;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class MSGHeadObject {
    public static final int MESSAGE_HEAD_LENGTH = 12;
    private ByteBuffer mHeadBuf;
    private short s16msgType;
    private short s16rcvModuleName;
    private short s16sndModuleName;
    private short s16usMsglen;
    private short s16usRsv;
    private short s16usRsvTwo;

    public MSGHeadObject() {
        this.s16sndModuleName = (short) 0;
        this.s16rcvModuleName = (short) 0;
        this.s16msgType = (short) 0;
        this.s16usMsglen = (short) 0;
        this.s16usRsv = (short) 0;
        this.s16usRsvTwo = (short) 0;
        this.mHeadBuf = null;
        this.s16sndModuleName = (short) 0;
        this.s16rcvModuleName = (short) 0;
        this.s16msgType = (short) 0;
        this.s16usMsglen = (short) 0;
        this.s16usRsv = (short) 0;
        this.s16usRsvTwo = (short) 0;
        this.mHeadBuf = ByteBuffer.allocate(12);
    }

    public void setSendModlueName(short name) {
        this.s16sndModuleName = name;
    }

    public void setRcvModlueName(short name) {
        this.s16rcvModuleName = name;
    }

    public void setMsgType(short msgType) {
        this.s16msgType = msgType;
    }

    public void setMsgLen(short msgLen) {
        this.s16usMsglen = msgLen;
    }

    public void setRsv(short rsv) {
        this.s16usRsv = rsv;
    }

    public void setRsvTwo(short rsvTwo) {
        this.s16usRsvTwo = rsvTwo;
    }

    public short getSendModlueName() {
        return this.s16sndModuleName;
    }

    public short getRcvModlueName() {
        return this.s16rcvModuleName;
    }

    public short getMsgType() {
        return this.s16msgType;
    }

    public short getMsgLen() {
        return this.s16usMsglen;
    }

    public short getRsv() {
        return this.s16usRsv;
    }

    public short getRsvTwo() {
        return this.s16usRsvTwo;
    }

    public byte[] getBytes() {
        this.mHeadBuf.rewind();
        this.mHeadBuf.putShort(Short.reverseBytes(this.s16sndModuleName));
        this.mHeadBuf.putShort(Short.reverseBytes(this.s16rcvModuleName));
        this.mHeadBuf.putShort(Short.reverseBytes(this.s16msgType));
        this.mHeadBuf.putShort(Short.reverseBytes(this.s16usMsglen));
        this.mHeadBuf.putShort(Short.reverseBytes(this.s16usRsv));
        this.mHeadBuf.putShort(Short.reverseBytes(this.s16usRsvTwo));
        return this.mHeadBuf.array();
    }
}
