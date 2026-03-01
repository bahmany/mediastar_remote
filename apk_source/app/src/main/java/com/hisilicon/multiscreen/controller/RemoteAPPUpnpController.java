package com.hisilicon.multiscreen.controller;

import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.upnputils.MultiScreenUpnpControlPoint;
import com.hisilicon.multiscreen.upnputils.UpnpMultiScreenDeviceInfo;
import org.cybergarage.upnp.Action;

/* loaded from: classes.dex */
public class RemoteAPPUpnpController {
    private MultiScreenUpnpControlPoint mControlPoint;

    public RemoteAPPUpnpController() {
        this.mControlPoint = null;
        this.mControlPoint = MultiScreenControlService.getInstance().getControlPoint();
    }

    public void reset() {
        this.mControlPoint = MultiScreenControlService.getInstance().getControlPoint();
    }

    public boolean startRemoteApp() {
        Action startRemoteAppAct = this.mControlPoint.getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_REMOTE_APP_TYPE, UpnpMultiScreenDeviceInfo.ACTION_REMOTE_APP_START);
        if (startRemoteAppAct == null) {
            LogTool.e("startRemoteAppAct is null");
            return false;
        }
        boolean result = this.mControlPoint.postAction(startRemoteAppAct);
        return result;
    }

    public boolean stopRemoteApp() {
        Action stopRemoteAppAct = this.mControlPoint.getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_VIME_TYPE, UpnpMultiScreenDeviceInfo.ACTION_REMOTE_APP_STOP);
        if (stopRemoteAppAct == null) {
            LogTool.e("stopRemoteAppAct is null");
            return false;
        }
        boolean result = this.mControlPoint.postAction(stopRemoteAppAct);
        return result;
    }
}
