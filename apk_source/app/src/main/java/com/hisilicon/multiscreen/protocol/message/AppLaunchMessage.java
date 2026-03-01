package com.hisilicon.multiscreen.protocol.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class AppLaunchMessage extends PushMessage {
    private String mAppName;

    public void setApplicationName(String name) {
        this.mAppName = name;
    }

    public String getApplicationName() {
        return this.mAppName;
    }

    @Override // com.hisilicon.multiscreen.protocol.message.PushMessage
    public void sendBody(DataOutputStream out) throws IOException {
        if (this.mAppName != null) {
            byte[] data = this.mAppName.getBytes("UTF-8");
            out.writeInt(data.length);
            out.write(data);
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
            this.mAppName = new String(data, "UTF-8");
            return;
        }
        this.mAppName = null;
    }
}
