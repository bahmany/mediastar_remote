package com.hisilicon.multiscreen.protocol.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class PlayMediaMessage extends PushMessage {
    public static final int MEDIA_TYPE_AUDIO = 2;
    public static final int MEDIA_TYPE_IMAGE = 3;
    public static final int MEDIA_TYPE_VEDIO = 1;
    private int mMediaType;
    private String mUrl;

    public void setMediaType(int type) {
        this.mMediaType = type;
    }

    public int getMediaType() {
        return this.mMediaType;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getUrl() {
        return this.mUrl;
    }

    @Override // com.hisilicon.multiscreen.protocol.message.PushMessage
    public void sendBody(DataOutputStream out) throws IOException {
        out.writeInt(this.mMediaType);
        if (this.mUrl != null) {
            byte[] data = this.mUrl.getBytes("UTF-8");
            out.writeInt(data.length);
            out.write(data);
        } else {
            out.write(0);
        }
        out.flush();
    }

    @Override // com.hisilicon.multiscreen.protocol.message.PushMessage
    public void readBody(DataInputStream in) throws IOException {
        this.mMediaType = in.readInt();
        int len = in.readInt();
        if (len > 0) {
            byte[] data = new byte[len];
            byte[] buf = new byte[len];
            int p = 0;
            while (p < len) {
                int c = in.read(buf, 0, buf.length);
                System.arraycopy(buf, 0, data, p, c);
                p += c;
            }
            this.mUrl = new String(data, "UTF-8");
            return;
        }
        this.mUrl = null;
    }
}
