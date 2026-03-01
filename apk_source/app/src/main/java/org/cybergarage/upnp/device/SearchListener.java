package org.cybergarage.upnp.device;

import org.cybergarage.upnp.ssdp.SSDPPacket;

/* loaded from: classes.dex */
public interface SearchListener {
    void deviceSearchReceived(SSDPPacket sSDPPacket);
}
