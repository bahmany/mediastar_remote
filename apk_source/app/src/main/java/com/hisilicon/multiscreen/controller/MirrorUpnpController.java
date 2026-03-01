package com.hisilicon.multiscreen.controller;

import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.upnputils.MultiScreenUpnpControlPoint;
import com.hisilicon.multiscreen.upnputils.UpnpMultiScreenDeviceInfo;
import org.cybergarage.upnp.Action;

/* loaded from: classes.dex */
public class MirrorUpnpController {
    private MultiScreenUpnpControlPoint mControlPoint;

    public MirrorUpnpController() {
        this.mControlPoint = null;
        this.mControlPoint = MultiScreenControlService.getInstance().getControlPoint();
    }

    public void reset() {
        this.mControlPoint = MultiScreenControlService.getInstance().getControlPoint();
    }

    public boolean setMirrorParameter(String MirrorParameter, int times) {
        boolean isOK;
        if (times <= 0) {
            times = 1;
        }
        do {
            times--;
            isOK = setMirrorParameter(MirrorParameter);
            if (isOK) {
                break;
            }
        } while (times > 0);
        return isOK;
    }

    public boolean startMirror(int times) {
        boolean isOK;
        if (times <= 0) {
            times = 1;
        }
        do {
            times--;
            isOK = startMirror();
            if (isOK) {
                break;
            }
        } while (times > 0);
        return isOK;
    }

    public boolean stopMirror(int times) {
        boolean isOK;
        if (times <= 0) {
            times = 1;
        }
        do {
            times--;
            isOK = stopMirror();
            if (isOK) {
                break;
            }
        } while (times > 0);
        return isOK;
    }

    private boolean setMirrorParameter(String MirrorParameter) {
        Action setMirrorParameterAct = this.mControlPoint.getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_MIRROR_TYPE, UpnpMultiScreenDeviceInfo.ACTION_MIRROR_SET_PARAMETER);
        if (setMirrorParameterAct == null) {
            LogTool.e("setMirrorParameterAct not found");
            return false;
        }
        setMirrorParameterAct.setArgumentValue(UpnpMultiScreenDeviceInfo.ARG_REMOTE_ID, this.mControlPoint.getRemoteId());
        setMirrorParameterAct.setArgumentValue(UpnpMultiScreenDeviceInfo.ARG_MIRROR_PARAMETER, MirrorParameter);
        return setMirrorParameterAct.postControlAction();
    }

    private boolean startMirror() {
        Action startMirrorAct = this.mControlPoint.getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_MIRROR_TYPE, UpnpMultiScreenDeviceInfo.ACTION_MIRROR_START);
        if (startMirrorAct != null) {
            return this.mControlPoint.postAction(startMirrorAct);
        }
        LogTool.e("StartMirrorAct not found");
        return false;
    }

    private boolean stopMirror() {
        Action stopMirrorAct = this.mControlPoint.getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_MIRROR_TYPE, UpnpMultiScreenDeviceInfo.ACTION_MIRROR_STOP);
        if (stopMirrorAct != null) {
            return this.mControlPoint.postAction(stopMirrorAct);
        }
        LogTool.e("StopMirrorAct not found");
        return false;
    }
}
