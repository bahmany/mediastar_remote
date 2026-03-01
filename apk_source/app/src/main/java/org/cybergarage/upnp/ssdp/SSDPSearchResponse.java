package org.cybergarage.upnp.ssdp;

import org.cybergarage.http.HTTP;
import org.cybergarage.upnp.UPnP;

/* loaded from: classes.dex */
public class SSDPSearchResponse extends SSDPResponse {
    public SSDPSearchResponse() {
        setStatusCode(200);
        setCacheControl(1800);
        setHeader(HTTP.SERVER, UPnP.getServerName());
        setHeader("EXT", "");
    }
}
