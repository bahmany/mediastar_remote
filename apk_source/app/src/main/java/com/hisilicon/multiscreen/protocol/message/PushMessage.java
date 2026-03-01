package com.hisilicon.multiscreen.protocol.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class PushMessage {
    protected PushMessageHead mHead;

    public abstract void readBody(DataInputStream dataInputStream) throws IOException;

    public abstract void sendBody(DataOutputStream dataOutputStream) throws IOException;

    public void setHead(PushMessageHead h) {
        this.mHead = h;
    }

    public PushMessageHead getHead() {
        return this.mHead;
    }

    public void sendOutputMsg(DataOutputStream out) throws IOException {
        if (this.mHead != null) {
            this.mHead.sendOutputMsg(out);
            sendBody(out);
        }
    }
}
