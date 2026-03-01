package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.protocol.HiDeviceInfo;
import com.hisilicon.multiscreen.protocol.message.PlayMediaMessage;
import com.hisilicon.multiscreen.protocol.message.PushMessageHead;
import com.hisilicon.multiscreen.protocol.utils.LogTool;

/* loaded from: classes.dex */
public class RemoteMedia {
    private HiDeviceInfo mDevice;
    private RemotePushing mRemotePushing;

    public RemoteMedia(HiDeviceInfo deviceInfo) {
        this.mDevice = null;
        this.mRemotePushing = null;
        this.mDevice = deviceInfo;
        this.mRemotePushing = new RemotePushing(this.mDevice);
    }

    protected void destroy() {
    }

    protected void resetDevice(HiDeviceInfo deviceInfo) {
        this.mDevice = deviceInfo;
        this.mRemotePushing.resetDevice(deviceInfo);
    }

    public void playMedia(int type, String url) {
        try {
            PushMessageHead head = new PushMessageHead();
            head.setType(PushMessageHead.PLAY_MEDIA);
            PlayMediaMessage msg = new PlayMediaMessage();
            msg.setHead(head);
            msg.setMediaType(type);
            msg.setUrl(url);
            this.mRemotePushing.pushing(msg);
        } catch (Exception e) {
            LogTool.e(e.getMessage());
        }
    }
}
