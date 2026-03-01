package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.protocol.HiDeviceInfo;
import com.hisilicon.multiscreen.protocol.ServiceInfo;
import com.hisilicon.multiscreen.protocol.message.TouchRequest;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.protocol.utils.UDPClient;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

/* loaded from: classes.dex */
public class RemoteTouch {
    private HiDeviceInfo mDevice = null;
    private DatagramSocket mSocket = null;

    public RemoteTouch(HiDeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            LogTool.e("device info is null in remote touch.");
        } else {
            resetDevice(deviceInfo);
        }
    }

    public void destroy() {
        if (this.mSocket != null) {
            this.mSocket.close();
            this.mSocket = null;
        }
    }

    protected void resetDevice(HiDeviceInfo deviceInfo) {
        this.mDevice = deviceInfo;
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

    public void sendMultiTouchEvent(TouchRequest mtInfo) throws IOException {
        String ip = this.mDevice.getDeviceIP();
        ServiceInfo service = this.mDevice.getService("HI_UPNP_VAR_VinpuServerURI");
        if (ip != null && service != null) {
            int port = service.getServicePort();
            UDPClient.send(this.mSocket, ip, port, mtInfo);
        }
    }
}
