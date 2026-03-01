package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.protocol.HiDeviceInfo;
import com.hisilicon.multiscreen.protocol.message.MouseRequest;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.protocol.utils.UDPClient;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

/* loaded from: classes.dex */
public class RemoteMouse {
    public static final short MOUSE_ACTION_MOVE = 256;
    public static final short MOUSE_LEFT_DOUBLE_CLICK = 770;
    public static final short MOUSE_LEFT_DOWN = 771;
    public static final short MOUSE_LEFT_DOWN_MOVE = 773;
    public static final short MOUSE_LEFT_SINGLE_CLICK = 769;
    public static final short MOUSE_LEFT_UP = 772;
    public static final short MOUSE_RGIHT_DOWN_MOVE = 517;
    public static final short MOUSE_RIGHT_DOUBLE_CLICK = 514;
    public static final short MOUSE_RIGHT_DOWN = 515;
    public static final short MOUSE_RIGHT_SINGLE_CLICK = 513;
    public static final short MOUSE_RIGHT_UP = 516;
    public static final short MOUSE_WHEEL = 774;
    public static final int MOUSE_WHEEL_DOWN = 0;
    private static final float MOUSE_WHEEL_DOWN_VALUE = -10.0f;
    public static final int MOUSE_WHEEL_UP = 2;
    private static final float MOUSE_WHEEL_UP_VALUE = 10.0f;
    private HiDeviceInfo mDevice;
    private MouseRequest mRequest = null;
    private DatagramSocket mSocket = null;

    public RemoteMouse(HiDeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            LogTool.e("device info is null in remote mouse.");
        } else {
            resetDevice(deviceInfo);
        }
    }

    protected void destroy() {
        if (this.mSocket != null) {
            this.mSocket.close();
            this.mSocket = null;
        }
    }

    protected void resetDevice(HiDeviceInfo deviceInfo) {
        this.mDevice = deviceInfo;
        if (this.mRequest == null) {
            this.mRequest = new MouseRequest((short) 256, 0.0f, 0.0f);
        }
        if (this.mSocket == null) {
            try {
                this.mSocket = new DatagramSocket();
                return;
            } catch (SocketException e) {
                LogTool.e(e.getMessage());
                return;
            }
        }
        if (this.mSocket.isClosed()) {
            try {
                this.mSocket = new DatagramSocket();
            } catch (SocketException e2) {
                LogTool.e(e2.getMessage());
            }
        }
    }

    public void sendMouseMoveEvent(int event_type, float sendX, float sendY) throws IOException {
        if (this.mDevice == null) {
            LogTool.e("mDevice is null");
        } else {
            this.mRequest.setData((short) event_type, sendX, sendY);
            UDPClient.send(this.mSocket, this.mDevice.getDeviceIP(), this.mDevice.getService("HI_UPNP_VAR_VinpuServerURI").getServicePort(), this.mRequest);
        }
    }

    public void sendMouseClickEvent(int event_type) throws IOException {
        if (this.mDevice == null) {
            LogTool.e("mDevice is null");
        } else if (event_type != 513) {
            this.mRequest.setData((short) event_type, 0.0f, 0.0f);
            UDPClient.send(this.mSocket, this.mDevice.getDeviceIP(), this.mDevice.getService("HI_UPNP_VAR_VinpuServerURI").getServicePort(), this.mRequest);
        } else {
            MultiScreenControlService.getInstance().getRemoteControlCenter().getRemoteKeyboard().sendDownAndUpKeyCode(158);
        }
    }

    public void sendMouseWheelEvent(int WheelEvent) throws IOException {
        if (this.mDevice == null) {
            LogTool.e("mDevice is null");
            return;
        }
        this.mRequest.setData(MOUSE_WHEEL, 0.0f, 0.0f);
        if (WheelEvent == 0) {
            this.mRequest.setDx(MOUSE_WHEEL_DOWN_VALUE);
        } else if (WheelEvent == 2) {
            this.mRequest.setDx(MOUSE_WHEEL_UP_VALUE);
        }
        UDPClient.send(this.mSocket, this.mDevice.getDeviceIP(), this.mDevice.getService("HI_UPNP_VAR_VinpuServerURI").getServicePort(), this.mRequest);
    }
}
