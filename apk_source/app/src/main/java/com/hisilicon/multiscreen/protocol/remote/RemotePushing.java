package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.protocol.HiDeviceInfo;
import com.hisilicon.multiscreen.protocol.message.AppListResponseMessage;
import com.hisilicon.multiscreen.protocol.message.DefaultResponseMessage;
import com.hisilicon.multiscreen.protocol.message.MessageDef;
import com.hisilicon.multiscreen.protocol.message.PushMessage;
import com.hisilicon.multiscreen.protocol.message.PushMessageHead;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/* loaded from: classes.dex */
public class RemotePushing {
    private static final int SOCKET_CONNECT_TIME_OUT = 500;
    private HiDeviceInfo mDevice = null;

    public RemotePushing(HiDeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            LogTool.e("device info is null in remote pushing.");
        } else {
            resetDevice(deviceInfo);
        }
    }

    protected void resetDevice(HiDeviceInfo deviceInfo) {
        this.mDevice = deviceInfo;
    }

    public PushMessage pushing(PushMessage msg) throws IOException {
        PushMessage response = null;
        String servIp = this.mDevice.getDeviceIP();
        Socket socket = new Socket();
        SocketAddress remoteAddr = new InetSocketAddress(servIp, this.mDevice.getService(MessageDef.PUSHSERVER_SERVICE_NAME).getServicePort());
        try {
            socket.connect(remoteAddr, 500);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            msg.sendOutputMsg(out);
            PushMessageHead head = new PushMessageHead();
            head.readInputMsg(in);
            int type = head.getType();
            switch (type) {
                case PushMessageHead.GET_APPS_RESPONSE /* 771 */:
                    response = new AppListResponseMessage();
                    response.setHead(head);
                    response.readBody(in);
                    break;
                default:
                    response = new DefaultResponseMessage();
                    response.readBody(in);
                    break;
            }
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            socket.close();
            LogTool.e("IO Exception");
        } catch (IllegalArgumentException e2) {
            socket.close();
            LogTool.e("Illegal Argument Exception.");
        }
        return response;
    }
}
