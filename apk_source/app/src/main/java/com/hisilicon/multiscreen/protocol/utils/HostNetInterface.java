package com.hisilicon.multiscreen.protocol.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class HostNetInterface {
    public static final String SEPARATOR_BETWEEN_HEAD_AND_IP = "://";
    public static final String SEPARATOR_BETWEEN_IP_AND_PORT = ":";
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
            Enumeration<?> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<?> addrs = ni.getInetAddresses();
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
            Enumeration<?> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<?> addrs = ni.getInetAddresses();
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

    public static final ArrayList<String> getIPv4Address() throws SocketException {
        int addrCnt = getNHostAddresses();
        ArrayList<String> ipList = new ArrayList<>();
        for (int n = 0; n < addrCnt; n++) {
            String addr = getHostAddress(n);
            if (isIPv4Address(addr)) {
                ipList.add(addr);
            }
        }
        return ipList;
    }

    public static final String getSameSegmentIp(String ip) throws SocketException {
        ArrayList<String> list = getIPv4Address();
        if (list.size() <= 0 || ip == null) {
            return null;
        }
        String[] ipSeg = ip.split("\\.");
        StringBuffer prefixBuf = new StringBuffer();
        prefixBuf.append(ipSeg[0]);
        prefixBuf.append(".");
        prefixBuf.append(ipSeg[1]);
        for (int index = 0; index < list.size(); index++) {
            System.out.println("[" + index + "]=" + list.get(index));
            if (list.get(index).contains(prefixBuf.toString())) {
                return list.get(index);
            }
        }
        return null;
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

    public static final String uri2Ip(String uri) {
        if (uri == null || uri.equals("")) {
            LogTool.e("source location is null");
            return null;
        }
        if (uri.split(ServiceReference.DELIMITER)[0].length() == 0) {
            String ipAddress = uri.replace(ServiceReference.DELIMITER, "").split(":")[0];
            return ipAddress;
        }
        String ipAddress2 = uri.replaceAll(ServiceReference.DELIMITER, "").split(":")[1];
        return ipAddress2;
    }

    public static final int uri2port(String uri) throws NumberFormatException {
        if (uri == null || uri.equals("")) {
            LogTool.e("source uri is null");
            return -1;
        }
        if (uri.split(ServiceReference.DELIMITER)[0].length() == 0) {
            int port = Integer.parseInt(uri.split(":")[1].replaceAll(ServiceReference.DELIMITER, ""));
            return port;
        }
        int port2 = Integer.parseInt(uri.split(":")[2].replaceAll(ServiceReference.DELIMITER, ""));
        return port2;
    }

    public static final String ipAndPort2Uri(String head, String ip, int port) {
        return head + SEPARATOR_BETWEEN_HEAD_AND_IP + ip + ":" + String.valueOf(port);
    }

    public static final String int2Ip(int ip) {
        StringBuffer ipAddressBuff = new StringBuffer();
        ipAddressBuff.append(ip & 255);
        ipAddressBuff.append(".");
        ipAddressBuff.append((ip >> 8) & 255);
        ipAddressBuff.append(".");
        ipAddressBuff.append((ip >> 16) & 255);
        ipAddressBuff.append(".");
        ipAddressBuff.append((ip >> 24) & 255);
        return ipAddressBuff.toString();
    }

    public static boolean isValidIp(String ip) {
        String ip2 = ip.trim();
        if (!ip2.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            return false;
        }
        String[] s = ip2.split("\\.");
        if (Integer.parseInt(s[0]) >= 255 || Integer.parseInt(s[1]) > 255 || Integer.parseInt(s[2]) > 255 || Integer.parseInt(s[3]) > 255) {
            return false;
        }
        return true;
    }
}
