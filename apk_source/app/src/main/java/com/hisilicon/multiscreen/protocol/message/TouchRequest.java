package com.hisilicon.multiscreen.protocol.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class TouchRequest extends Request {
    private static final int FINGER_UP_VALUE = 0;
    public static final int MAX_FINGER_NUM = 5;
    private static final int PRESSED_VALUE = 1;
    private static final short TOUCH_MESSAGE_LENGTH = 76;
    private int mFingerNum = 0;
    private ArrayList<FingerInfo> mFingers = new ArrayList<>();
    private ByteBuffer mBuf = null;
    private byte[] mMsg = null;

    public TouchRequest() {
        initHead();
        initBuf();
    }

    private void initHead() {
        this.head.setMsgType((short) 4);
        this.head.setSendModlueName((short) 1);
        this.head.setRcvModlueName((short) 2);
        this.head.setMsgLen(TOUCH_MESSAGE_LENGTH);
        this.head.setRsv((short) 0);
        this.head.setRsvTwo((short) 0);
    }

    private void initBuf() {
        this.mBuf = ByteBuffer.allocate(76);
        this.mBuf.clear();
        this.mBuf.put(this.head.getBytes());
        this.mMsg = new byte[this.head.getMsgLen()];
        for (int i = 0; i < 5; i++) {
            this.mFingers.add(new FingerInfo());
        }
        setFingerNum(2);
    }

    public void setFingerNum(int num) {
        this.mFingerNum = num;
    }

    public int getFingerNum() {
        return this.mFingerNum;
    }

    public void setFingers(ArrayList<FingerInfo> fingers) {
        this.mFingers = fingers;
    }

    public ArrayList<FingerInfo> getFingers() {
        return this.mFingers;
    }

    public void setFingerInfo(int index, int x, int y, boolean press) {
        if (index < 5) {
            FingerInfo info = this.mFingers.get(index);
            info.setX(x);
            info.setY(y);
            info.setPress(press);
        }
    }

    public FingerInfo getFingerInfo(int index) {
        return this.mFingers.get(index);
    }

    @Override // com.hisilicon.multiscreen.protocol.message.Request
    public byte[] getBytes() {
        if (this.mBuf != null && this.mMsg != null) {
            this.mBuf.position(12);
            this.mBuf.putInt(Integer.reverseBytes(getFingerNum()));
            for (int i = 0; i < 5; i++) {
                if (i < getFingerNum()) {
                    FingerInfo fin = getFingerInfo(i);
                    this.mBuf.putInt(Integer.reverseBytes(fin.getX()));
                    this.mBuf.putInt(Integer.reverseBytes(fin.getY()));
                    this.mBuf.putInt(Integer.reverseBytes(fin.getPress() ? 1 : 0));
                } else {
                    this.mBuf.putInt(0);
                    this.mBuf.putInt(0);
                    this.mBuf.putInt(0);
                }
            }
            this.mBuf.rewind();
            this.mBuf.get(this.mMsg, 0, this.mMsg.length);
        }
        return this.mMsg;
    }
}
