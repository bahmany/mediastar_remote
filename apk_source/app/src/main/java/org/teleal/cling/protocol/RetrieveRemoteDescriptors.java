package org.teleal.cling.protocol;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.binding.xml.DescriptorBindingException;
import org.teleal.cling.binding.xml.DeviceDescriptorBinder;
import org.teleal.cling.binding.xml.ServiceDescriptorBinder;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.meta.Icon;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteDeviceIdentity;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.registry.RegistrationException;
import org.teleal.common.util.Exceptions;

/* loaded from: classes.dex */
public class RetrieveRemoteDescriptors implements Runnable {
    private RemoteDevice rd;
    private final UpnpService upnpService;
    private static final Logger log = Logger.getLogger(RetrieveRemoteDescriptors.class.getName());
    private static final Set<URL> activeRetrievals = new CopyOnWriteArraySet();

    public RetrieveRemoteDescriptors(UpnpService upnpService, RemoteDevice rd) {
        this.upnpService = upnpService;
        this.rd = rd;
    }

    public UpnpService getUpnpService() {
        return this.upnpService;
    }

    @Override // java.lang.Runnable
    public void run() {
        URL deviceURL = this.rd.getIdentity().getDescriptorURL();
        if (activeRetrievals.contains(deviceURL)) {
            log.finer("Exiting early, active retrieval for URL already in progress: " + deviceURL);
            return;
        }
        if (getUpnpService().getRegistry().getRemoteDevice(this.rd.getIdentity().getUdn(), true) != null) {
            log.finer("Exiting early, already discovered: " + deviceURL);
            return;
        }
        try {
            activeRetrievals.add(deviceURL);
            describe();
        } finally {
            activeRetrievals.remove(deviceURL);
        }
    }

    protected void describe() {
        StreamRequestMessage deviceDescRetrievalMsg = new StreamRequestMessage(UpnpRequest.Method.GET, this.rd.getIdentity().getDescriptorURL());
        log.fine("Sending device descriptor retrieval message: " + deviceDescRetrievalMsg);
        StreamResponseMessage deviceDescMsg = getUpnpService().getRouter().send(deviceDescRetrievalMsg);
        if (deviceDescMsg == null) {
            log.warning("Device descriptor retrieval failed, no response: " + this.rd.getIdentity().getDescriptorURL());
            return;
        }
        if (deviceDescMsg.getOperation().isFailed()) {
            log.warning("Device descriptor retrieval failed: " + this.rd.getIdentity().getDescriptorURL() + ", " + deviceDescMsg.getOperation().getResponseDetails());
            return;
        }
        if (!deviceDescMsg.isContentTypeTextUDA()) {
            log.warning("Received device descriptor without or with invalid Content-Type: " + this.rd.getIdentity().getDescriptorURL());
        }
        log.fine("Received root device descriptor: " + deviceDescMsg);
        describe(deviceDescMsg.getBodyString());
    }

    protected void describe(String descriptorXML) {
        boolean notifiedStart = false;
        RemoteDevice describedDevice = null;
        try {
            DeviceDescriptorBinder deviceDescriptorBinder = getUpnpService().getConfiguration().getDeviceDescriptorBinderUDA10();
            describedDevice = (RemoteDevice) deviceDescriptorBinder.describe((DeviceDescriptorBinder) this.rd, descriptorXML);
            log.fine("Remote device described (without services) notifying listeners: " + describedDevice);
            notifiedStart = getUpnpService().getRegistry().notifyDiscoveryStart(describedDevice);
            log.fine("Hydrating described device's services: " + describedDevice);
            RemoteDevice hydratedDevice = describeServices(describedDevice);
            if (hydratedDevice == null) {
                log.warning("Device service description failed: " + this.rd);
                if (notifiedStart) {
                    getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, new DescriptorBindingException("Device service description failed: " + this.rd));
                }
            } else {
                log.fine("Adding fully hydrated remote device to registry: " + hydratedDevice);
                getUpnpService().getRegistry().addDevice(hydratedDevice);
            }
        } catch (DescriptorBindingException ex) {
            log.warning("Could not hydrate device or its services from descriptor: " + this.rd);
            log.warning("Cause was: " + Exceptions.unwrap(ex));
            if (describedDevice != null && notifiedStart) {
                getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, ex);
            }
        } catch (ValidationException ex2) {
            log.warning("Could not validate device model: " + this.rd);
            for (ValidationError validationError : ex2.getErrors()) {
                log.warning(validationError.toString());
            }
            if (describedDevice != null && notifiedStart) {
                getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, ex2);
            }
        } catch (RegistrationException ex3) {
            log.warning("Adding hydrated device to registry failed: " + this.rd);
            log.warning("Cause was: " + ex3.toString());
            if (describedDevice != null && notifiedStart) {
                getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, ex3);
            }
        }
    }

    protected RemoteDevice describeServices(RemoteDevice currentDevice) throws ValidationException, DescriptorBindingException {
        List<RemoteService> describedServices = new ArrayList<>();
        if (currentDevice.hasServices()) {
            List<RemoteService> filteredServices = filterExclusiveServices(currentDevice.getServices());
            for (RemoteService service : filteredServices) {
                RemoteService svc = describeService(service);
                if (svc == null) {
                    return null;
                }
                describedServices.add(svc);
            }
        }
        List<RemoteDevice> describedEmbeddedDevices = new ArrayList<>();
        if (currentDevice.hasEmbeddedDevices()) {
            for (RemoteDevice embeddedDevice : currentDevice.getEmbeddedDevices()) {
                if (embeddedDevice != null) {
                    RemoteDevice describedEmbeddedDevice = describeServices(embeddedDevice);
                    if (describedEmbeddedDevice == null) {
                        return null;
                    }
                    describedEmbeddedDevices.add(describedEmbeddedDevice);
                }
            }
        }
        Icon[] iconDupes = new Icon[currentDevice.getIcons().length];
        for (int i = 0; i < currentDevice.getIcons().length; i++) {
            Icon icon = currentDevice.getIcons()[i];
            iconDupes[i] = icon.deepCopy();
        }
        return currentDevice.newInstance(((RemoteDeviceIdentity) currentDevice.getIdentity()).getUdn(), currentDevice.getVersion(), currentDevice.getType(), currentDevice.getDetails(), iconDupes, currentDevice.toServiceArray((Collection<RemoteService>) describedServices), describedEmbeddedDevices);
    }

    protected RemoteService describeService(RemoteService service) throws ValidationException, DescriptorBindingException {
        URL descriptorURL = service.getDevice().normalizeURI(service.getDescriptorURI());
        StreamRequestMessage serviceDescRetrievalMsg = new StreamRequestMessage(UpnpRequest.Method.GET, descriptorURL);
        log.fine("Sending service descriptor retrieval message: " + serviceDescRetrievalMsg);
        StreamResponseMessage serviceDescMsg = getUpnpService().getRouter().send(serviceDescRetrievalMsg);
        if (serviceDescMsg == null) {
            log.warning("Could not retrieve service descriptor: " + service);
            return null;
        }
        if (serviceDescMsg.getOperation().isFailed()) {
            log.warning("Service descriptor retrieval failed: " + descriptorURL + ", " + serviceDescMsg.getOperation().getResponseDetails());
            return null;
        }
        if (!serviceDescMsg.isContentTypeTextUDA()) {
            log.warning("Received service descriptor without or with invalid Content-Type: " + descriptorURL);
        }
        String descriptorContent = serviceDescMsg.getBodyString();
        if (descriptorContent == null || descriptorContent.length() == 0) {
            log.warning("Received empty descriptor:" + descriptorURL);
            return null;
        }
        log.fine("Received service descriptor, hydrating service model: " + serviceDescMsg);
        ServiceDescriptorBinder serviceDescriptorBinder = getUpnpService().getConfiguration().getServiceDescriptorBinderUDA10();
        return (RemoteService) serviceDescriptorBinder.describe((ServiceDescriptorBinder) service, serviceDescMsg.getBodyString());
    }

    protected List<RemoteService> filterExclusiveServices(RemoteService[] services) {
        ServiceType[] exclusiveTypes = getUpnpService().getConfiguration().getExclusiveServiceTypes();
        if (exclusiveTypes == null || exclusiveTypes.length == 0) {
            return Arrays.asList(services);
        }
        List<RemoteService> exclusiveServices = new ArrayList<>();
        for (RemoteService discoveredService : services) {
            for (ServiceType exclusiveType : exclusiveTypes) {
                if (discoveredService.getServiceType().implementsVersion(exclusiveType)) {
                    log.fine("Including exlusive service: " + discoveredService);
                    exclusiveServices.add(discoveredService);
                } else {
                    log.fine("Excluding unwanted service: " + exclusiveType);
                }
            }
        }
        return exclusiveServices;
    }
}
