package org.teleal.cling.model.meta;

import java.net.InetAddress;
import java.net.URL;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.model.message.discovery.IncomingNotificationRequest;
import org.teleal.cling.model.message.discovery.IncomingSearchResponse;
import org.teleal.cling.model.types.UDN;

/* loaded from: classes.dex */
public class RemoteDeviceIdentity extends DeviceIdentity {
    private final URL descriptorURL;
    private final InetAddress discoveredOnLocalAddress;
    private final byte[] interfaceMacAddress;

    public RemoteDeviceIdentity(UDN udn, RemoteDeviceIdentity template) {
        this(udn, template.getMaxAgeSeconds(), template.getDescriptorURL(), template.getInterfaceMacAddress(), template.getDiscoveredOnLocalAddress());
    }

    public RemoteDeviceIdentity(UDN udn, Integer maxAgeSeconds, URL descriptorURL, byte[] interfaceMacAddress, InetAddress discoveredOnLocalAddress) {
        super(udn, maxAgeSeconds);
        this.descriptorURL = descriptorURL;
        this.interfaceMacAddress = interfaceMacAddress;
        this.discoveredOnLocalAddress = discoveredOnLocalAddress;
    }

    public RemoteDeviceIdentity(IncomingNotificationRequest notificationRequest) {
        this(notificationRequest.getUDN(), notificationRequest.getMaxAge(), notificationRequest.getLocationURL(), notificationRequest.getInterfaceMacHeader(), notificationRequest.getLocalAddress());
    }

    public RemoteDeviceIdentity(IncomingSearchResponse searchResponse) {
        this(searchResponse.getRootDeviceUDN(), searchResponse.getMaxAge(), searchResponse.getLocationURL(), searchResponse.getInterfaceMacHeader(), searchResponse.getLocalAddress());
    }

    public URL getDescriptorURL() {
        return this.descriptorURL;
    }

    public byte[] getInterfaceMacAddress() {
        return this.interfaceMacAddress;
    }

    public InetAddress getDiscoveredOnLocalAddress() {
        return this.discoveredOnLocalAddress;
    }

    public byte[] getWakeOnLANBytes() {
        if (getInterfaceMacAddress() == null) {
            return null;
        }
        byte[] bytes = new byte[(getInterfaceMacAddress().length * 16) + 6];
        for (int i = 0; i < 6; i++) {
            bytes[i] = -1;
        }
        int i2 = 6;
        while (i2 < bytes.length) {
            System.arraycopy(getInterfaceMacAddress(), 0, bytes, i2, getInterfaceMacAddress().length);
            i2 += getInterfaceMacAddress().length;
        }
        return bytes;
    }

    @Override // org.teleal.cling.model.meta.DeviceIdentity
    public String toString() {
        return ModelUtil.ANDROID_RUNTIME ? "(RemoteDeviceIdentity) UDN: " + getUdn() + ", Descriptor: " + getDescriptorURL() : "(" + getClass().getSimpleName() + ") UDN: " + getUdn() + ", Descriptor: " + getDescriptorURL();
    }
}
