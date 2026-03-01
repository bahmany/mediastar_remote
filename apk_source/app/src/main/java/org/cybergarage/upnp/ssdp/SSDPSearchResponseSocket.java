package org.cybergarage.upnp.ssdp;

import java.net.DatagramSocket;
import java.net.InetAddress;
import org.cybergarage.upnp.ControlPoint;

/* loaded from: classes.dex */
public class SSDPSearchResponseSocket extends HTTPUSocket implements Runnable {
    private ControlPoint controlPoint;
    private Thread deviceSearchResponseThread;

    public SSDPSearchResponseSocket() {
        this.controlPoint = null;
        this.deviceSearchResponseThread = null;
        setControlPoint(null);
    }

    public SSDPSearchResponseSocket(String bindAddr, int port) {
        super(bindAddr, port);
        this.controlPoint = null;
        this.deviceSearchResponseThread = null;
        setControlPoint(null);
    }

    public void setControlPoint(ControlPoint ctrlp) {
        this.controlPoint = ctrlp;
    }

    public ControlPoint getControlPoint() {
        return this.controlPoint;
    }

    @Override // java.lang.Runnable
    public void run() {
        Thread thisThread = Thread.currentThread();
        ControlPoint ctrlPoint = getControlPoint();
        while (this.deviceSearchResponseThread == thisThread) {
            Thread.yield();
            SSDPPacket packet = receive();
            if (packet != null) {
                if (ctrlPoint != null) {
                    new SSDPSearchResponseHandleThread(ctrlPoint, packet).start();
                }
            } else {
                return;
            }
        }
    }

    public void start() {
        StringBuffer name = new StringBuffer("Cyber.SSDPSearchResponseSocket/");
        DatagramSocket s = getDatagramSocket();
        InetAddress localAddr = null;
        if (s != null) {
            localAddr = s.getLocalAddress();
        }
        if (localAddr != null) {
            name.append(s.getLocalAddress()).append(':');
            name.append(s.getLocalPort());
        }
        this.deviceSearchResponseThread = new Thread(this, name.toString());
        this.deviceSearchResponseThread.start();
    }

    public void stop() {
        close();
        this.deviceSearchResponseThread = null;
    }

    public boolean post(String addr, int port, SSDPSearchResponse res) {
        return post(addr, port, res.getHeader());
    }

    public boolean post(String addr, int port, SSDPSearchRequest req) {
        return post(addr, port, req.toString());
    }

    private class SSDPSearchResponseHandleThread extends Thread {
        ControlPoint mCtrlPoint;
        SSDPPacket mPacket;

        public SSDPSearchResponseHandleThread(ControlPoint point, SSDPPacket packet) {
            this.mCtrlPoint = null;
            this.mPacket = null;
            this.mCtrlPoint = point;
            this.mPacket = packet;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            this.mCtrlPoint.searchResponseReceived(this.mPacket);
        }
    }
}
