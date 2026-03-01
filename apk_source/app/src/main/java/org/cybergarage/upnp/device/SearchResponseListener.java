package org.cybergarage.upnp.device;

import org.cybergarage.upnp.ssdp.SSDPPacket;

/* loaded from: classes.dex */
public interface SearchResponseListener {
    void deviceSearchResponseReceived(SSDPPacket sSDPPacket);
}
