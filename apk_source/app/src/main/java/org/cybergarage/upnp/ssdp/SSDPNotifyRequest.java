package org.cybergarage.upnp.ssdp;

/* loaded from: classes.dex */
public class SSDPNotifyRequest extends SSDPRequest {
    public SSDPNotifyRequest() {
        setMethod("NOTIFY");
        setURI("*");
    }
}
