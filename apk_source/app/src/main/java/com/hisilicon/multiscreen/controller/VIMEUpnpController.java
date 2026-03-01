package com.hisilicon.multiscreen.controller;

import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import com.hisilicon.multiscreen.protocol.utils.HostNetInterface;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.upnputils.MultiScreenUpnpControlPoint;
import com.hisilicon.multiscreen.upnputils.UpnpMultiScreenDeviceInfo;
import java.net.SocketException;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Device;

/* loaded from: classes.dex */
public class VIMEUpnpController {
    private MultiScreenUpnpControlPoint mControlPoint;

    public VIMEUpnpController() {
        this.mControlPoint = null;
        this.mControlPoint = MultiScreenControlService.getInstance().getControlPoint();
    }

    public void reset() {
        this.mControlPoint = MultiScreenControlService.getInstance().getControlPoint();
    }

    public void start() throws SocketException {
        setVIMEParameter(2016);
        startVIMEControlServer();
    }

    public void stop() {
        stopVIMEControlServer();
    }

    public boolean setVIMEParameter(int port) throws SocketException {
        Action setVIMEParameterAct = this.mControlPoint.getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_VIME_TYPE, UpnpMultiScreenDeviceInfo.ACTION_VIME_SET_PARAMETER);
        if (setVIMEParameterAct == null) {
            LogTool.e("setVIMEParameterAct is null");
            return false;
        }
        Device device = this.mControlPoint.getCurrentDevice();
        if (device == null) {
            LogTool.e("Current device is null, fail to set VIME parameter.");
            return false;
        }
        String clientIP = HostNetInterface.getSameSegmentIp(HostNetInterface.uri2Ip(device.getLocation()));
        String parameter = HostNetInterface.ipAndPort2Uri(ClientInfo.MODULE_VIME_HEAD, clientIP, port);
        setVIMEParameterAct.setArgumentValue(UpnpMultiScreenDeviceInfo.ARG_VIME_PARAMETER, parameter);
        boolean result = this.mControlPoint.postAction(setVIMEParameterAct);
        return result;
    }

    public boolean startVIMEControlServer() {
        Action startVIMEControlServerAct = this.mControlPoint.getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_VIME_TYPE, UpnpMultiScreenDeviceInfo.ACTION_VIME_START);
        if (startVIMEControlServerAct == null) {
            LogTool.e("startVIMEControlServerAct is null");
            return false;
        }
        boolean result = this.mControlPoint.postAction(startVIMEControlServerAct);
        return result;
    }

    public boolean stopVIMEControlServer() {
        Action stopVIMEControlServerAct = this.mControlPoint.getAction(UpnpMultiScreenDeviceInfo.MULTISCREEN_SERVICE_VIME_TYPE, UpnpMultiScreenDeviceInfo.ACTION_VIME_STOP);
        if (stopVIMEControlServerAct == null) {
            LogTool.e("stopVIMEControlServerAct is null");
            return false;
        }
        boolean result = this.mControlPoint.postAction(stopVIMEControlServerAct);
        return result;
    }
}
