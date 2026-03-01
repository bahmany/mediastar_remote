package com.hisilicon.multiscreen.controller;

import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.upnputils.MultiScreenUpnpControlPoint;
import com.hisilicon.multiscreen.upnputils.UpnpMultiScreenDeviceInfo;
import org.cybergarage.upnp.Action;

/* loaded from: classes.dex */
public class VinputUpnpController {
    private MultiScreenUpnpControlPoint mControlPoint;

    public VinputUpnpController() {
        this.mControlPoint = null;
        this.mControlPoint = MultiScreenControlService.getInstance().getControlPoint();
    }

    public void reset() {
        this.mControlPoint = MultiScreenControlService.getInstance().getControlPoint();
    }

    public boolean startVinput() {
        Action startVinputAct = this.mControlPoint.getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_VINPUT_TYPE, UpnpMultiScreenDeviceInfo.ACTION_VINPUT_START);
        if (startVinputAct != null) {
            return this.mControlPoint.postAction(startVinputAct);
        }
        LogTool.e("StartVinputAct is null");
        return false;
    }

    public boolean stopVinput() {
        Action stopVinputAct = this.mControlPoint.getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_VINPUT_TYPE, UpnpMultiScreenDeviceInfo.ACTION_VINPUT_STOP);
        if (stopVinputAct != null) {
            return this.mControlPoint.postAction(stopVinputAct);
        }
        LogTool.e("stopVinputAct is null");
        return false;
    }
}
