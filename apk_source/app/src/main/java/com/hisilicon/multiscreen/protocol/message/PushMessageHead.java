package com.hisilicon.multiscreen.protocol.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class PushMessageHead {
    public static final int DEFAULT_RESPONSE = 769;
    public static final int GET_APPS_REQUEST = 770;
    public static final int GET_APPS_RESPONSE = 771;
    public static final int LAUNCH_APP = 772;
    public static final int PLAY_MEDIA = 773;
    private static final int PUSH_MSG_BASE = 768;
    protected int mMsgType;

    public void setType(int type) {
        this.mMsgType = type;
    }

    public int getType() {
        return this.mMsgType;
    }

    public void sendOutputMsg(DataOutputStream out) throws IOException {
        if (out != null) {
            out.writeInt(this.mMsgType);
        }
    }

    public void readInputMsg(DataInputStream input) throws IOException {
        if (input != null) {
            this.mMsgType = input.readInt();
        }
    }
}
