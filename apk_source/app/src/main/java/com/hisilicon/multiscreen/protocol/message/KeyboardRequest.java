package com.hisilicon.multiscreen.protocol.message;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class KeyboardRequest extends Request {
    private static final short KEYBOARD_EVEN_TYPE = 263;
    private static final short KEYBOARD_MESSAGE_LENGTH = 22;
    private int mKeyValue = 0;
    private int mUpdownState = 0;
    private ByteBuffer mBuf = null;
    private byte[] mMsg = null;

    public KeyboardRequest(int key, int state) {
        setData(key, state);
        initHead();
        initBuf();
    }

    private void initHead() {
        this.head.setMsgType((short) 2);
        this.head.setSendModlueName((short) 1);
        this.head.setRcvModlueName((short) 2);
        this.head.setMsgLen((short) 22);
        this.head.setRsv((short) 0);
        this.head.setRsvTwo((short) 0);
    }

    private void initBuf() {
        this.mBuf = ByteBuffer.allocate(22);
        this.mBuf.clear();
        this.mBuf.put(this.head.getBytes());
        this.mMsg = new byte[22];
    }

    public void setData(int key, int state) {
        setKey(key);
        setState(state);
    }

    public void setKey(int key) {
        this.mKeyValue = key;
    }

    public void setState(int type) {
        this.mUpdownState = type;
    }

    public int getKey() {
        return this.mKeyValue;
    }

    public int getState() {
        return this.mUpdownState;
    }

    @Override // com.hisilicon.multiscreen.protocol.message.Request
    public byte[] getBytes() {
        if (this.mBuf != null && this.mMsg != null) {
            this.mBuf.position(12);
            this.mBuf.putShort(Short.reverseBytes(KEYBOARD_EVEN_TYPE));
            this.mBuf.putInt(Integer.reverseBytes(getKey()));
            this.mBuf.putInt(Integer.reverseBytes(getState()));
            this.mBuf.rewind();
            this.mBuf.get(this.mMsg, 0, this.mMsg.length);
        }
        return this.mMsg;
    }
}
