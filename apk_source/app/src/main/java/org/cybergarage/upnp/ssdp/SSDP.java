package org.cybergarage.upnp.ssdp;

import org.cybergarage.net.AndoridNetInfoInterface;
import org.cybergarage.util.Debug;

/* loaded from: classes.dex */
public class SSDP {
    public static final String ADDRESS = "239.255.255.250";
    public static final int DEFAULT_MSEARCH_MX = 3;
    private static String IPV6_ADDRESS = null;
    public static final String IPV6_ADMINISTRATIVE_ADDRESS = "FF04::C";
    public static final String IPV6_GLOBAL_ADDRESS = "FF0E::C";
    public static final String IPV6_LINK_LOCAL_ADDRESS = "FF02::C";
    public static final String IPV6_SITE_LOCAL_ADDRESS = "FF05::C";
    public static final String IPV6_SUBNET_ADDRESS = "FF03::C";
    public static final int PORT = 1900;
    public static final int RECV_MESSAGE_BUFSIZE = 1024;
    private static AndoridNetInfoInterface netInterfaceUtil = null;

    static {
        setIPv6Address("FF02::C");
    }

    public static final void setIPv6Address(String addr) {
        IPV6_ADDRESS = addr;
    }

    public static final String getIPv6Address() {
        return IPV6_ADDRESS;
    }

    public static final int getLeaseTime(String cacheCont) throws NumberFormatException {
        int maxAgeIdx = cacheCont.indexOf("max-age");
        if (maxAgeIdx < 0) {
            return 0;
        }
        int endIdx = cacheCont.indexOf(44, maxAgeIdx);
        if (endIdx < 0) {
            endIdx = cacheCont.length();
        }
        try {
            String mxStr = cacheCont.substring(cacheCont.indexOf("=", maxAgeIdx) + 1, endIdx).trim();
            int mx = Integer.parseInt(mxStr);
            return mx;
        } catch (Exception e) {
            Debug.warning(e);
            return 0;
        }
    }

    public static final void setNetInterfaceUtil(AndoridNetInfoInterface netInfo) {
        netInterfaceUtil = netInfo;
    }

    public static final String getBroadCastAddress() {
        String brocastAddr;
        if (netInterfaceUtil == null || (brocastAddr = netInterfaceUtil.getBroadCastAddress()) == null) {
            return "239.255.255.250";
        }
        return brocastAddr;
    }
}
