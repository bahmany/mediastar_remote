package org.cybergarage.upnp.ssdp;

import org.cybergarage.net.HostInterface;

/* loaded from: classes.dex */
public class SSDPSearchRequest extends SSDPRequest {
    public SSDPSearchRequest(String serachTarget, int mx) {
        setMethod("M-SEARCH");
        setURI("*");
        setHeader("ST", serachTarget);
        setHeader("MX", Integer.toString(mx));
        setHeader("MAN", "\"ssdp:discover\"");
    }

    public SSDPSearchRequest(String serachTarget) {
        this(serachTarget, 3);
    }

    public SSDPSearchRequest() {
        this("upnp:rootdevice");
    }

    public void setLocalAddress(String bindAddr) {
        String ssdpAddr = "239.255.255.250";
        if (HostInterface.isIPv6Address(bindAddr)) {
            ssdpAddr = SSDP.getIPv6Address();
        }
        setHost(ssdpAddr, 1900);
    }
}
