package org.cybergarage.multiscreennet;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import org.cybergarage.multiscreenutil.Debug;

/* loaded from: classes.dex */
public class HostInterface {
    public static boolean USE_LOOPBACK_ADDR = false;
    public static boolean USE_ONLY_IPV4_ADDR = true;
    public static boolean USE_ONLY_IPV6_ADDR = false;
    private static String ifAddress = "";

    public static final void setInterface(String ifaddr) {
        ifAddress = ifaddr;
    }

    public static final String getInterface() {
        return ifAddress;
    }

    private static final boolean hasAssignedInterface() {
        return ifAddress.length() > 0;
    }

    private static final boolean isUseAddress(InetAddress addr) {
        if (!USE_LOOPBACK_ADDR && addr.isLoopbackAddress()) {
            return false;
        }
        if (USE_ONLY_IPV4_ADDR && (addr instanceof Inet6Address)) {
            return false;
        }
        return (USE_ONLY_IPV6_ADDR && (addr instanceof Inet4Address)) ? false : true;
    }

    public static final int getNHostAddresses() throws SocketException {
        if (hasAssignedInterface()) {
            return 1;
        }
        int nHostAddrs = 0;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                Enumeration addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (isUseAddress(addr)) {
                        nHostAddrs++;
                    }
                }
            }
            return nHostAddrs;
        } catch (Exception e) {
            return nHostAddrs;
        }
    }

    public static final String getHostAddress(int n) throws SocketException {
        if (hasAssignedInterface()) {
            return getInterface();
        }
        int hostAddrCnt = 0;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                Enumeration addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (isUseAddress(addr)) {
                        if (hostAddrCnt < n) {
                            hostAddrCnt++;
                        } else {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    public static final boolean isIPv6Address(String host) throws UnknownHostException {
        try {
            InetAddress addr = InetAddress.getByName(host);
            return addr instanceof Inet6Address;
        } catch (Exception e) {
            return false;
        }
    }

    public static final boolean isIPv4Address(String host) throws UnknownHostException {
        try {
            InetAddress addr = InetAddress.getByName(host);
            return addr instanceof Inet4Address;
        } catch (Exception e) {
            return false;
        }
    }

    public static final boolean hasIPv4Addresses() throws SocketException {
        int addrCnt = getNHostAddresses();
        for (int n = 0; n < addrCnt; n++) {
            String addr = getHostAddress(n);
            if (isIPv4Address(addr)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean hasIPv6Addresses() throws SocketException {
        int addrCnt = getNHostAddresses();
        for (int n = 0; n < addrCnt; n++) {
            String addr = getHostAddress(n);
            if (isIPv6Address(addr)) {
                return true;
            }
        }
        return false;
    }

    public static final String getIPv4Address() throws SocketException {
        int addrCnt = getNHostAddresses();
        for (int n = 0; n < addrCnt; n++) {
            String addr = getHostAddress(n);
            Debug.message("ip[" + n + "]=" + addr);
            if (isIPv4Address(addr)) {
                return addr;
            }
        }
        return "";
    }

    public static final String getIPv6Address() throws SocketException {
        int addrCnt = getNHostAddresses();
        for (int n = 0; n < addrCnt; n++) {
            String addr = getHostAddress(n);
            if (isIPv6Address(addr)) {
                return addr;
            }
        }
        return "";
    }

    public static final String getHostURL(String host, int port, String uri) {
        String hostAddr = host;
        if (isIPv6Address(host)) {
            hostAddr = "[" + host + "]";
        }
        return "http://" + hostAddr + ":" + Integer.toString(port) + uri;
    }
}
