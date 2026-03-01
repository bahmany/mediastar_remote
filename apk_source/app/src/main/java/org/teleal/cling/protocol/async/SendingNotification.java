package org.teleal.cling.protocol.async;

import com.alibaba.fastjson.asm.Opcodes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.Location;
import org.teleal.cling.model.NetworkAddress;
import org.teleal.cling.model.message.discovery.OutgoingNotificationRequest;
import org.teleal.cling.model.message.discovery.OutgoingNotificationRequestDeviceType;
import org.teleal.cling.model.message.discovery.OutgoingNotificationRequestRootDevice;
import org.teleal.cling.model.message.discovery.OutgoingNotificationRequestServiceType;
import org.teleal.cling.model.message.discovery.OutgoingNotificationRequestUDN;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.types.NotificationSubtype;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.protocol.SendingAsync;

/* loaded from: classes.dex */
public abstract class SendingNotification extends SendingAsync {
    private static final Logger log = Logger.getLogger(SendingNotification.class.getName());
    private LocalDevice device;

    protected abstract NotificationSubtype getNotificationSubtype();

    public SendingNotification(UpnpService upnpService, LocalDevice device) {
        super(upnpService);
        this.device = device;
    }

    public LocalDevice getDevice() {
        return this.device;
    }

    @Override // org.teleal.cling.protocol.SendingAsync
    protected void execute() throws InterruptedException {
        List<NetworkAddress> activeStreamServers = getUpnpService().getRouter().getActiveStreamServers(null);
        if (activeStreamServers.size() == 0) {
            log.fine("Aborting notifications, no active stream servers found (network disabled?)");
            return;
        }
        List<Location> descriptorLocations = new ArrayList<>();
        for (NetworkAddress activeStreamServer : activeStreamServers) {
            descriptorLocations.add(new Location(activeStreamServer, getUpnpService().getConfiguration().getNamespace().getDescriptorPath(getDevice())));
        }
        for (int i = 0; i < getBulkRepeat(); i++) {
            try {
                for (Location descriptorLocation : descriptorLocations) {
                    sendMessages(descriptorLocation);
                }
                log.finer("Sleeping " + getBulkIntervalMilliseconds() + " milliseconds");
                Thread.sleep(getBulkIntervalMilliseconds());
            } catch (InterruptedException ex) {
                log.warning("Advertisement thread was interrupted: " + ex);
            }
        }
    }

    protected int getBulkRepeat() {
        return 3;
    }

    protected int getBulkIntervalMilliseconds() {
        return Opcodes.FCMPG;
    }

    public void sendMessages(Location descriptorLocation) {
        log.finer("Sending root device messages: " + getDevice());
        List<OutgoingNotificationRequest> rootDeviceMsgs = createDeviceMessages(getDevice(), descriptorLocation);
        for (OutgoingNotificationRequest upnpMessage : rootDeviceMsgs) {
            getUpnpService().getRouter().send(upnpMessage);
        }
        if (getDevice().hasEmbeddedDevices()) {
            for (LocalDevice embeddedDevice : getDevice().findEmbeddedDevices()) {
                log.finer("Sending embedded device messages: " + embeddedDevice);
                List<OutgoingNotificationRequest> embeddedDeviceMsgs = createDeviceMessages(embeddedDevice, descriptorLocation);
                for (OutgoingNotificationRequest upnpMessage2 : embeddedDeviceMsgs) {
                    getUpnpService().getRouter().send(upnpMessage2);
                }
            }
        }
        List<OutgoingNotificationRequest> serviceTypeMsgs = createServiceTypeMessages(getDevice(), descriptorLocation);
        if (serviceTypeMsgs.size() > 0) {
            log.finer("Sending service type messages");
            for (OutgoingNotificationRequest upnpMessage3 : serviceTypeMsgs) {
                getUpnpService().getRouter().send(upnpMessage3);
            }
        }
    }

    protected List<OutgoingNotificationRequest> createDeviceMessages(LocalDevice device, Location descriptorLocation) {
        List<OutgoingNotificationRequest> msgs = new ArrayList<>();
        if (device.isRoot()) {
            msgs.add(new OutgoingNotificationRequestRootDevice(descriptorLocation, device, getNotificationSubtype()));
        }
        msgs.add(new OutgoingNotificationRequestUDN(descriptorLocation, device, getNotificationSubtype()));
        msgs.add(new OutgoingNotificationRequestDeviceType(descriptorLocation, device, getNotificationSubtype()));
        return msgs;
    }

    protected List<OutgoingNotificationRequest> createServiceTypeMessages(LocalDevice device, Location descriptorLocation) {
        List<OutgoingNotificationRequest> msgs = new ArrayList<>();
        for (ServiceType serviceType : device.findServiceTypes()) {
            msgs.add(new OutgoingNotificationRequestServiceType(descriptorLocation, device, getNotificationSubtype(), serviceType));
        }
        return msgs;
    }
}
