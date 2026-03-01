package com.voicetechnology.rtspclient.concepts;

/* loaded from: classes.dex */
public class MessageBuffer {
    private byte[] data;
    private int length;
    private Message message;
    private int offset;
    private int used;

    public void addData(byte[] newData, int newLength) {
        if (this.data == null) {
            this.data = newData;
            this.length = newLength;
            this.offset = 0;
            return;
        }
        if ((this.data.length - this.offset) - this.length < newLength) {
            if (this.offset >= this.length && this.data.length - this.length >= newLength) {
                System.arraycopy(this.data, this.offset, this.data, 0, this.length);
                this.offset = 0;
            } else {
                byte[] temp = new byte[this.data.length + newLength];
                System.arraycopy(this.data, this.offset, temp, 0, this.length);
                this.offset = 0;
                this.data = temp;
            }
        }
        System.arraycopy(newData, 0, this.data, this.offset + this.length, newLength);
        this.length += newLength;
    }

    public void discardData() {
        this.offset += this.used;
        this.length -= this.used;
    }

    public byte[] getData() {
        return this.data;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLength() {
        return this.length;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }

    public void setused(int used) {
        this.used = used;
    }
}
