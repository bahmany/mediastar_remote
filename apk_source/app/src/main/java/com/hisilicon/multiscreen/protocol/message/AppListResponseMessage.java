package com.hisilicon.multiscreen.protocol.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class AppListResponseMessage extends PushMessage {
    private byte[] mContent;

    public void setContent(byte[] content) {
        this.mContent = content;
    }

    public byte[] getContent() {
        return this.mContent;
    }

    @Override // com.hisilicon.multiscreen.protocol.message.PushMessage
    public void sendBody(DataOutputStream out) throws IOException {
        if (this.mContent != null) {
            out.writeInt(this.mContent.length);
            out.write(this.mContent);
            out.flush();
        }
    }

    @Override // com.hisilicon.multiscreen.protocol.message.PushMessage
    public void readBody(DataInputStream in) throws IOException {
        int len = in.readInt();
        if (len > 0) {
            byte[] data = new byte[len];
            byte[] buf = new byte[1024];
            int p = 0;
            while (p < len) {
                int c = in.read(buf, 0, buf.length);
                System.arraycopy(buf, 0, data, p, c);
                p += c;
            }
            this.mContent = data;
            return;
        }
        this.mContent = null;
    }
}
