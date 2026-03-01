package com.hisilicon.multiscreen.protocol.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class DefaultResponseMessage extends PushMessage {
    public int mCode = 0;

    public void setCode(int code) {
        this.mCode = code;
    }

    public int getCode() {
        return this.mCode;
    }

    @Override // com.hisilicon.multiscreen.protocol.message.PushMessage
    public void sendBody(DataOutputStream out) throws IOException {
        out.writeInt(this.mCode);
        out.flush();
    }

    @Override // com.hisilicon.multiscreen.protocol.message.PushMessage
    public void readBody(DataInputStream in) throws IOException {
        this.mCode = in.readInt();
    }
}
