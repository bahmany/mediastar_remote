package org.cybergarage.upnp.ssdp;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.Vector;
import org.cybergarage.net.HostInterface;
import org.cybergarage.upnp.ControlPoint;

/* loaded from: classes.dex */
public class SSDPSearchResponseSocketList extends Vector {
    private InetAddress[] binds;

    public SSDPSearchResponseSocketList() {
        this.binds = null;
    }

    public SSDPSearchResponseSocketList(InetAddress[] binds) {
        this.binds = null;
        this.binds = binds;
    }

    public void setControlPoint(ControlPoint ctrlPoint) {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            sock.setControlPoint(ctrlPoint);
        }
    }

    public SSDPSearchResponseSocket getSSDPSearchResponseSocket(int n) {
        return (SSDPSearchResponseSocket) get(n);
    }

    public boolean open(int port) throws SocketException {
        String[] bindAddresses;
        InetAddress[] binds = this.binds;
        if (binds != null) {
            bindAddresses = new String[binds.length];
            for (int i = 0; i < binds.length; i++) {
                bindAddresses[i] = binds[i].getHostAddress();
            }
        } else {
            int nHostAddrs = HostInterface.getNHostAddresses();
            bindAddresses = new String[nHostAddrs];
            for (int n = 0; n < nHostAddrs; n++) {
                bindAddresses[n] = HostInterface.getHostAddress(n);
            }
        }
        for (String str : bindAddresses) {
            try {
                SSDPSearchResponseSocket socket = new SSDPSearchResponseSocket(str, port);
                add(socket);
            } catch (Exception e) {
                stop();
                close();
                clear();
                return false;
            }
        }
        return true;
    }

    public boolean open() {
        return open(1900);
    }

    public void close() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            sock.close();
        }
        clear();
    }

    public void start() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            sock.start();
        }
    }

    public void stop() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            sock.stop();
        }
    }

    public boolean post(SSDPSearchRequest req) {
        boolean ret = true;
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            String bindAddr = sock.getLocalAddress();
            req.setLocalAddress(bindAddr);
            String ssdpAddr = "239.255.255.250";
            if (HostInterface.isIPv6Address(bindAddr)) {
                ssdpAddr = SSDP.getIPv6Address();
            }
            if (!sock.post(ssdpAddr, 1900, req)) {
                ret = false;
            }
            sock.post(SSDP.getBroadCastAddress(), 1900, req);
        }
        return ret;
    }

    public boolean post(String ipAddress, SSDPSearchRequest req) {
        boolean ret = true;
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            String bindAddr = sock.getLocalAddress();
            req.setLocalAddress(bindAddr);
            String ssdpAddr = ipAddress;
            if (HostInterface.isIPv6Address(bindAddr)) {
                ssdpAddr = SSDP.getIPv6Address();
            }
            if (!sock.post(ssdpAddr, 1900, req)) {
                ret = false;
            }
            sock.post(SSDP.getBroadCastAddress(), 1900, req);
        }
        return ret;
    }
}
