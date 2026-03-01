package org.teleal.cling.protocol.async;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.Location;
import org.teleal.cling.model.NetworkAddress;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.discovery.IncomingSearchRequest;
import org.teleal.cling.model.message.discovery.OutgoingSearchResponse;
import org.teleal.cling.model.message.discovery.OutgoingSearchResponseDeviceType;
import org.teleal.cling.model.message.discovery.OutgoingSearchResponseRootDevice;
import org.teleal.cling.model.message.discovery.OutgoingSearchResponseRootDeviceUDN;
import org.teleal.cling.model.message.discovery.OutgoingSearchResponseServiceType;
import org.teleal.cling.model.message.discovery.OutgoingSearchResponseUDN;
import org.teleal.cling.model.message.header.DeviceTypeHeader;
import org.teleal.cling.model.message.header.MXHeader;
import org.teleal.cling.model.message.header.RootDeviceHeader;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.message.header.ServiceTypeHeader;
import org.teleal.cling.model.message.header.UDNHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.protocol.ReceivingAsync;

/* loaded from: classes.dex */
public class ReceivingSearch extends ReceivingAsync<IncomingSearchRequest> {
    private static final Logger log = Logger.getLogger(ReceivingSearch.class.getName());
    protected final Random randomGenerator;

    public ReceivingSearch(UpnpService upnpService, IncomingDatagramMessage<UpnpRequest> inputMessage) {
        super(upnpService, new IncomingSearchRequest(inputMessage));
        this.randomGenerator = new Random();
    }

    @Override // org.teleal.cling.protocol.ReceivingAsync
    protected void execute() {
        if (getUpnpService().getRouter() == null) {
            log.fine("Router hasn't completed initialization, ignoring received search message");
            return;
        }
        if (!getInputMessage().isMANSSDPDiscover()) {
            log.fine("Invalid search request, no or invalid MAN ssdp:discover header: " + getInputMessage());
            return;
        }
        UpnpHeader searchTarget = getInputMessage().getSearchTarget();
        if (searchTarget == null) {
            log.fine("Invalid search request, did not contain ST header: " + getInputMessage());
            return;
        }
        List<NetworkAddress> activeStreamServers = getUpnpService().getRouter().getActiveStreamServers(getInputMessage().getLocalAddress());
        if (activeStreamServers.size() == 0) {
            log.fine("Aborting search response, no active stream servers found (network disabled?)");
            return;
        }
        for (NetworkAddress activeStreamServer : activeStreamServers) {
            sendResponses(searchTarget, activeStreamServer);
        }
    }

    @Override // org.teleal.cling.protocol.ReceivingAsync
    protected boolean waitBeforeExecution() throws InterruptedException {
        Integer mx = getInputMessage().getMX();
        if (mx == null) {
            log.fine("Invalid search request, did not contain MX header: " + getInputMessage());
            return false;
        }
        if (mx.intValue() > 120 || mx.intValue() <= 0) {
            mx = MXHeader.DEFAULT_VALUE;
        }
        if (getUpnpService().getRegistry().getLocalDevices().size() > 0) {
            int sleepTime = this.randomGenerator.nextInt(mx.intValue() * 1000);
            log.fine("Sleeping " + sleepTime + " milliseconds to avoid flooding with search responses");
            Thread.sleep(sleepTime);
        }
        return true;
    }

    protected void sendResponses(UpnpHeader searchTarget, NetworkAddress activeStreamServer) {
        if (searchTarget instanceof STAllHeader) {
            sendSearchResponseAll(activeStreamServer);
            return;
        }
        if (searchTarget instanceof RootDeviceHeader) {
            sendSearchResponseRootDevices(activeStreamServer);
            return;
        }
        if (searchTarget instanceof UDNHeader) {
            sendSearchResponseUDN((UDN) searchTarget.getValue(), activeStreamServer);
            return;
        }
        if (searchTarget instanceof DeviceTypeHeader) {
            sendSearchResponseDeviceType((DeviceType) searchTarget.getValue(), activeStreamServer);
        } else if (searchTarget instanceof ServiceTypeHeader) {
            sendSearchResponseServiceType((ServiceType) searchTarget.getValue(), activeStreamServer);
        } else {
            log.warning("Non-implemented search request target: " + searchTarget.getClass());
        }
    }

    protected void sendSearchResponseAll(NetworkAddress activeStreamServer) {
        log.fine("Responding to 'all' search with advertisement messages for all local devices");
        for (LocalDevice localDevice : getUpnpService().getRegistry().getLocalDevices()) {
            log.finer("Sending root device messages: " + localDevice);
            List<OutgoingSearchResponse> rootDeviceMsgs = createDeviceMessages(localDevice, activeStreamServer);
            for (OutgoingSearchResponse upnpMessage : rootDeviceMsgs) {
                getUpnpService().getRouter().send(upnpMessage);
            }
            if (localDevice.hasEmbeddedDevices()) {
                for (LocalDevice embeddedDevice : localDevice.findEmbeddedDevices()) {
                    log.finer("Sending embedded device messages: " + embeddedDevice);
                    List<OutgoingSearchResponse> embeddedDeviceMsgs = createDeviceMessages(embeddedDevice, activeStreamServer);
                    for (OutgoingSearchResponse upnpMessage2 : embeddedDeviceMsgs) {
                        getUpnpService().getRouter().send(upnpMessage2);
                    }
                }
            }
            List<OutgoingSearchResponse> serviceTypeMsgs = createServiceTypeMessages(localDevice, activeStreamServer);
            if (serviceTypeMsgs.size() > 0) {
                log.finer("Sending service type messages");
                for (OutgoingSearchResponse upnpMessage3 : serviceTypeMsgs) {
                    getUpnpService().getRouter().send(upnpMessage3);
                }
            }
        }
    }

    protected List<OutgoingSearchResponse> createDeviceMessages(LocalDevice device, NetworkAddress activeStreamServer) {
        List<OutgoingSearchResponse> msgs = new ArrayList<>();
        if (device.isRoot()) {
            msgs.add(new OutgoingSearchResponseRootDevice(getInputMessage(), getDescriptorLocation(activeStreamServer, device), device));
        }
        msgs.add(new OutgoingSearchResponseUDN(getInputMessage(), getDescriptorLocation(activeStreamServer, device), device));
        msgs.add(new OutgoingSearchResponseDeviceType(getInputMessage(), getDescriptorLocation(activeStreamServer, device), device));
        return msgs;
    }

    protected List<OutgoingSearchResponse> createServiceTypeMessages(LocalDevice device, NetworkAddress activeStreamServer) {
        List<OutgoingSearchResponse> msgs = new ArrayList<>();
        for (ServiceType serviceType : device.findServiceTypes()) {
            msgs.add(new OutgoingSearchResponseServiceType(getInputMessage(), getDescriptorLocation(activeStreamServer, device), device, serviceType));
        }
        return msgs;
    }

    protected void sendSearchResponseRootDevices(NetworkAddress activeStreamServer) {
        log.fine("Responding to root device search with advertisement messages for all local root devices");
        for (LocalDevice device : getUpnpService().getRegistry().getLocalDevices()) {
            getUpnpService().getRouter().send(new OutgoingSearchResponseRootDeviceUDN(getInputMessage(), getDescriptorLocation(activeStreamServer, device), device));
        }
    }

    protected void sendSearchResponseUDN(UDN udn, NetworkAddress activeStreamServer) {
        Device device = getUpnpService().getRegistry().getDevice(udn, false);
        if (device != null && (device instanceof LocalDevice)) {
            log.fine("Responding to UDN device search: " + udn);
            getUpnpService().getRouter().send(new OutgoingSearchResponseUDN(getInputMessage(), getDescriptorLocation(activeStreamServer, (LocalDevice) device), (LocalDevice) device));
        }
    }

    protected void sendSearchResponseDeviceType(DeviceType deviceType, NetworkAddress activeStreamServer) {
        log.fine("Responding to device type search: " + deviceType);
        Collection<Device> devices = getUpnpService().getRegistry().getDevices(deviceType);
        for (Device device : devices) {
            if (device instanceof LocalDevice) {
                log.finer("Sending matching device type search result for: " + device);
                getUpnpService().getRouter().send(new OutgoingSearchResponseDeviceType(getInputMessage(), getDescriptorLocation(activeStreamServer, (LocalDevice) device), (LocalDevice) device));
            }
        }
    }

    protected void sendSearchResponseServiceType(ServiceType serviceType, NetworkAddress activeStreamServer) {
        log.fine("Responding to service type search: " + serviceType);
        Collection<Device> devices = getUpnpService().getRegistry().getDevices(serviceType);
        for (Device device : devices) {
            if (device instanceof LocalDevice) {
                log.finer("Sending matching service type search result: " + device);
                getUpnpService().getRouter().send(new OutgoingSearchResponseServiceType(getInputMessage(), getDescriptorLocation(activeStreamServer, (LocalDevice) device), (LocalDevice) device, serviceType));
            }
        }
    }

    protected Location getDescriptorLocation(NetworkAddress activeStreamServer, LocalDevice device) {
        return new Location(activeStreamServer, getUpnpService().getConfiguration().getNamespace().getDescriptorPath(device));
    }
}
