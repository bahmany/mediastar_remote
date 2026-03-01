package org.teleal.cling.support.igd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.support.igd.callback.PortMappingAdd;
import org.teleal.cling.support.igd.callback.PortMappingDelete;
import org.teleal.cling.support.model.PortMapping;

/* loaded from: classes.dex */
public class PortMappingListener extends DefaultRegistryListener {
    protected Map<Service, List<PortMapping>> activePortMappings;
    protected PortMapping[] portMappings;
    private static final Logger log = Logger.getLogger(PortMappingListener.class.getName());
    public static final DeviceType IGD_DEVICE_TYPE = new UDADeviceType("InternetGatewayDevice", 1);
    public static final DeviceType CONNECTION_DEVICE_TYPE = new UDADeviceType("WANConnectionDevice", 1);
    public static final ServiceType IP_SERVICE_TYPE = new UDAServiceType("WANIPConnection", 1);
    public static final ServiceType PPP_SERVICE_TYPE = new UDAServiceType("WANPPPConnection", 1);

    public PortMappingListener(PortMapping portMapping) {
        this(new PortMapping[]{portMapping});
    }

    public PortMappingListener(PortMapping[] portMappings) {
        this.activePortMappings = new HashMap();
        this.portMappings = portMappings;
    }

    @Override // org.teleal.cling.registry.DefaultRegistryListener
    public synchronized void deviceAdded(Registry registry, Device device) {
        Service connectionService = discoverConnectionService(device);
        if (connectionService != null) {
            log.fine("Activating port mappings on: " + connectionService);
            final List<PortMapping> activeForService = new ArrayList<>();
            for (final PortMapping pm : this.portMappings) {
                new PortMappingAdd(connectionService, registry.getUpnpService().getControlPoint(), pm) { // from class: org.teleal.cling.support.igd.PortMappingListener.1
                    @Override // org.teleal.cling.controlpoint.ActionCallback
                    public void success(ActionInvocation invocation) {
                        PortMappingListener.log.fine("Port mapping added: " + pm);
                        activeForService.add(pm);
                    }

                    @Override // org.teleal.cling.controlpoint.ActionCallback
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        PortMappingListener.this.handleFailureMessage("Failed to add port mapping: " + pm);
                        PortMappingListener.this.handleFailureMessage("Reason: " + defaultMsg);
                    }
                }.run();
            }
            this.activePortMappings.put(connectionService, activeForService);
        }
    }

    @Override // org.teleal.cling.registry.DefaultRegistryListener
    public synchronized void deviceRemoved(Registry registry, Device device) {
        for (Service service : device.findServices()) {
            Iterator<Map.Entry<Service, List<PortMapping>>> it = this.activePortMappings.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Service, List<PortMapping>> activeEntry = it.next();
                if (activeEntry.getKey().equals(service)) {
                    if (activeEntry.getValue().size() > 0) {
                        handleFailureMessage("Device disappeared, couldn't delete port mappings: " + activeEntry.getValue().size());
                    }
                    it.remove();
                }
            }
        }
    }

    @Override // org.teleal.cling.registry.DefaultRegistryListener, org.teleal.cling.registry.RegistryListener
    public synchronized void beforeShutdown(Registry registry) {
        for (Map.Entry<Service, List<PortMapping>> activeEntry : this.activePortMappings.entrySet()) {
            final Iterator<PortMapping> it = activeEntry.getValue().iterator();
            while (it.hasNext()) {
                final PortMapping pm = it.next();
                log.fine("Trying to delete port mapping on IGD: " + pm);
                new PortMappingDelete(activeEntry.getKey(), registry.getUpnpService().getControlPoint(), pm) { // from class: org.teleal.cling.support.igd.PortMappingListener.2
                    @Override // org.teleal.cling.controlpoint.ActionCallback
                    public void success(ActionInvocation invocation) {
                        PortMappingListener.log.fine("Port mapping deleted: " + pm);
                        it.remove();
                    }

                    @Override // org.teleal.cling.controlpoint.ActionCallback
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        PortMappingListener.this.handleFailureMessage("Failed to delete port mapping: " + pm);
                        PortMappingListener.this.handleFailureMessage("Reason: " + defaultMsg);
                    }
                }.run();
            }
        }
    }

    protected Service discoverConnectionService(Device device) {
        if (!device.getType().equals(IGD_DEVICE_TYPE)) {
            return null;
        }
        Device[] connectionDevices = device.findDevices(CONNECTION_DEVICE_TYPE);
        if (connectionDevices.length == 0) {
            log.fine("IGD doesn't support '" + CONNECTION_DEVICE_TYPE + "': " + device);
            return null;
        }
        Device connectionDevice = connectionDevices[0];
        log.fine("Using first discovered WAN connection device: " + connectionDevice);
        Service ipConnectionService = connectionDevice.findService(IP_SERVICE_TYPE);
        Service pppConnectionService = connectionDevice.findService(PPP_SERVICE_TYPE);
        if (ipConnectionService == null && pppConnectionService == null) {
            log.fine("IGD doesn't support IP or PPP WAN connection service: " + device);
        }
        return ipConnectionService == null ? pppConnectionService : ipConnectionService;
    }

    protected void handleFailureMessage(String s) {
        log.warning(s);
    }
}
