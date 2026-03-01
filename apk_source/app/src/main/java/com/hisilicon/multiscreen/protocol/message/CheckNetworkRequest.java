package com.hisilicon.multiscreen.protocol.message;

import com.hisilicon.multiscreen.protocol.utils.LogTool;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class CheckNetworkRequest {
    private static final int BIT_NUMBER_IN_BYTE = 8;
    private static final int CHECK_NUMBER_LENGTH = 4;
    private static final int CHECK_TAG_LENGTH = 2;
    public static final int DATA_LENGTH = 6;
    private int mNumber;
    private short mTag;

    public CheckNetworkRequest() {
        this.mTag = (short) 0;
        this.mNumber = 0;
    }

    public CheckNetworkRequest(short tag, int number) {
        this.mTag = tag;
        this.mNumber = number;
    }

    public short getTag() {
        return this.mTag;
    }

    public void setTag(short tag) {
        this.mTag = tag;
    }

    public int getNumber() {
        return this.mNumber;
    }

    public void setNumber(int number) {
        this.mNumber = number;
    }

    public byte[] getData() {
        ByteBuffer buf = ByteBuffer.allocate(6);
        byte[] msg = new byte[6];
        buf.putShort(this.mTag);
        buf.putInt(this.mNumber);
        if (buf != null && msg != null) {
            buf.rewind();
            buf.get(msg, 0, msg.length);
            buf.clear();
        }
        return msg;
    }

    public CheckNetworkRequest getCheckNetworkRequestfromBytes(byte[] data) throws IOException {
        if (data != null) {
            byte[] tagData = new byte[2];
            byte[] numberData = new byte[4];
            try {
                System.arraycopy(data, 0, tagData, 0, 2);
                InputStream inputStreamTag = new ByteArrayInputStream(tagData);
                DataInputStream inTag = new DataInputStream(inputStreamTag);
                short tag = inTag.readShort();
                System.arraycopy(data, 2, numberData, 0, 4);
                InputStream inputStreamNumber = new ByteArrayInputStream(numberData);
                DataInputStream inNumber = new DataInputStream(inputStreamNumber);
                int number = inNumber.readInt();
                CheckNetworkRequest request = new CheckNetworkRequest(tag, number);
                inTag.close();
                inNumber.close();
                return request;
            } catch (IOException e) {
                LogTool.e("IO Exception");
            }
        }
        return null;
    }
}
