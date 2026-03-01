package com.hisilicon.multiscreen.protocol.utils;

import com.hisilicon.multiscreen.protocol.message.Request;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/* loaded from: classes.dex */
public class UDPClient {
    public static void send(DatagramSocket s, String remoteHostIp, int ServicePort, Request req) throws IOException {
        if (s != null) {
            try {
                InetAddress local = InetAddress.getByName(remoteHostIp);
                DatagramPacket p = new DatagramPacket(req.getBytes(), req.getHead().getMsgLen(), local, ServicePort);
                s.send(p);
                return;
            } catch (UnknownHostException e) {
                LogTool.e(e.getMessage());
                return;
            } catch (IOException e2) {
                LogTool.e(e2.getMessage());
                return;
            }
        }
        LogTool.e("Socket is null!");
    }
}
