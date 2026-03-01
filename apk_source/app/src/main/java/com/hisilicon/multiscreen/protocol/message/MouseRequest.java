package com.hisilicon.multiscreen.protocol.message;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class MouseRequest extends Request {
    private static final short MOUSE_MESSAGE_LENGTH = 22;
    private short mClickType;
    private float mDx;
    private float mDy;
    private ByteBuffer mBuf = null;
    private byte[] mMsg = null;

    public MouseRequest(short clickType, float dx, float dy) {
        setData(clickType, dx, dy);
        initHead();
        initBuf();
    }

    private void initHead() {
        this.head.setMsgType((short) 3);
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

    public void setData(short clickType, float dx, float dy) {
        setMouseClickType(clickType);
        setDx(dx);
        setDy(dy);
    }

    public void setMouseClickType(short type) {
        this.mClickType = type;
    }

    public short getMouseClickType() {
        return this.mClickType;
    }

    public void setDxDy(float dx, float dy) {
        this.mDx = dx;
        this.mDy = dy;
    }

    public void setDx(float dx) {
        this.mDx = dx;
    }

    public void setDy(float dy) {
        this.mDy = dy;
    }

    public float getDx() {
        return this.mDx;
    }

    public float getDy() {
        return this.mDy;
    }

    @Override // com.hisilicon.multiscreen.protocol.message.Request
    public byte[] getBytes() {
        if (this.mBuf != null && this.mMsg != null) {
            this.mBuf.position(12);
            this.mBuf.putShort(Short.reverseBytes(getMouseClickType()));
            this.mBuf.putInt(Integer.reverseBytes((int) getDx()));
            this.mBuf.putInt(Integer.reverseBytes((int) getDy()));
            this.mBuf.rewind();
            this.mBuf.get(this.mMsg);
        }
        return this.mMsg;
    }
}
