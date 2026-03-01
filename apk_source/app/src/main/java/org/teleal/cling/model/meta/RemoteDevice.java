package org.teleal.cling.model.meta;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.teleal.cling.model.Namespace;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.resource.Resource;
import org.teleal.cling.model.resource.ServiceEventCallbackResource;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.common.util.URIUtil;

/* loaded from: classes.dex */
public class RemoteDevice extends Device<RemoteDeviceIdentity, RemoteDevice, RemoteService> {
    @Override // org.teleal.cling.model.meta.Device
    public /* bridge */ /* synthetic */ Device newInstance(UDN udn, UDAVersion uDAVersion, DeviceType deviceType, DeviceDetails deviceDetails, Icon[] iconArr, Service[] serviceArr, List list) throws ValidationException {
        return newInstance(udn, uDAVersion, deviceType, deviceDetails, iconArr, (RemoteService[]) serviceArr, (List<RemoteDevice>) list);
    }

    @Override // org.teleal.cling.model.meta.Device
    public /* bridge */ /* synthetic */ Service newInstance(ServiceType serviceType, ServiceId serviceId, URI uri, URI uri2, URI uri3, Action[] actionArr, StateVariable[] stateVariableArr) throws ValidationException {
        return newInstance(serviceType, serviceId, uri, uri2, uri3, (Action<RemoteService>[]) actionArr, (StateVariable<RemoteService>[]) stateVariableArr);
    }

    @Override // org.teleal.cling.model.meta.Device
    public /* bridge */ /* synthetic */ Device[] toDeviceArray(Collection collection) {
        return toDeviceArray((Collection<RemoteDevice>) collection);
    }

    @Override // org.teleal.cling.model.meta.Device
    public /* bridge */ /* synthetic */ Service[] toServiceArray(Collection collection) {
        return toServiceArray((Collection<RemoteService>) collection);
    }

    public RemoteDevice(RemoteDeviceIdentity identity) throws ValidationException {
        super(identity);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details, RemoteService service) throws ValidationException {
        super(identity, type, details, null, new RemoteService[]{service});
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details, RemoteService service, RemoteDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, null, new RemoteService[]{service}, new RemoteDevice[]{embeddedDevice});
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details, RemoteService[] services) throws ValidationException {
        super(identity, type, details, null, services);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details, RemoteService[] services, RemoteDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, null, services, embeddedDevices);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details, Icon icon, RemoteService service) throws ValidationException {
        super(identity, type, details, new Icon[]{icon}, new RemoteService[]{service});
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details, Icon icon, RemoteService service, RemoteDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, new Icon[]{icon}, new RemoteService[]{service}, new RemoteDevice[]{embeddedDevice});
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details, Icon icon, RemoteService[] services) throws ValidationException {
        super(identity, type, details, new Icon[]{icon}, services);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details, Icon icon, RemoteService[] services, RemoteDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, new Icon[]{icon}, services, embeddedDevices);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details, Icon[] icons, RemoteService service) throws ValidationException {
        super(identity, type, details, icons, new RemoteService[]{service});
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details, Icon[] icons, RemoteService service, RemoteDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, icons, new RemoteService[]{service}, new RemoteDevice[]{embeddedDevice});
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details, Icon[] icons, RemoteService[] services) throws ValidationException {
        super(identity, type, details, icons, services);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details, Icon[] icons, RemoteService[] services, RemoteDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, icons, services, embeddedDevices);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, UDAVersion version, DeviceType type, DeviceDetails details, Icon[] icons, RemoteService[] services, RemoteDevice[] embeddedDevices) throws ValidationException {
        super(identity, version, type, details, icons, services, embeddedDevices);
    }

    @Override // org.teleal.cling.model.meta.Device
    public RemoteService[] getServices() {
        return this.services != 0 ? (RemoteService[]) this.services : new RemoteService[0];
    }

    @Override // org.teleal.cling.model.meta.Device
    public RemoteDevice[] getEmbeddedDevices() {
        return this.embeddedDevices != 0 ? (RemoteDevice[]) this.embeddedDevices : new RemoteDevice[0];
    }

    public URL normalizeURI(URI relativeOrAbsoluteURI) {
        return (getDetails() == null || getDetails().getBaseURL() == null) ? URIUtil.createAbsoluteURL(getIdentity().getDescriptorURL(), relativeOrAbsoluteURI) : URIUtil.createAbsoluteURL(getDetails().getBaseURL(), relativeOrAbsoluteURI);
    }

    public RemoteDevice newInstance(UDN udn, UDAVersion version, DeviceType type, DeviceDetails details, Icon[] icons, RemoteService[] services, List<RemoteDevice> embeddedDevices) throws ValidationException {
        return new RemoteDevice(new RemoteDeviceIdentity(udn, getIdentity()), version, type, details, icons, services, embeddedDevices.size() > 0 ? (RemoteDevice[]) embeddedDevices.toArray(new RemoteDevice[embeddedDevices.size()]) : null);
    }

    @Override // org.teleal.cling.model.meta.Device
    public RemoteService newInstance(ServiceType serviceType, ServiceId serviceId, URI descriptorURI, URI controlURI, URI eventSubscriptionURI, Action<RemoteService>[] actionArr, StateVariable<RemoteService>[] stateVariableArr) throws ValidationException {
        return new RemoteService(serviceType, serviceId, descriptorURI, controlURI, eventSubscriptionURI, actionArr, stateVariableArr);
    }

    @Override // org.teleal.cling.model.meta.Device
    public RemoteDevice[] toDeviceArray(Collection<RemoteDevice> col) {
        return (RemoteDevice[]) col.toArray(new RemoteDevice[col.size()]);
    }

    @Override // org.teleal.cling.model.meta.Device
    public RemoteService[] newServiceArray(int size) {
        return new RemoteService[size];
    }

    @Override // org.teleal.cling.model.meta.Device
    public RemoteService[] toServiceArray(Collection<RemoteService> col) {
        return (RemoteService[]) col.toArray(new RemoteService[col.size()]);
    }

    @Override // org.teleal.cling.model.meta.Device
    public Resource[] discoverResources(Namespace namespace) {
        List<Resource> discovered = new ArrayList<>();
        for (RemoteService service : getServices()) {
            if (service != null) {
                discovered.add(new ServiceEventCallbackResource(namespace.getEventCallbackPath(service), service));
            }
        }
        if (hasEmbeddedDevices()) {
            for (Device embeddedDevice : getEmbeddedDevices()) {
                if (embeddedDevice != null) {
                    discovered.addAll(Arrays.asList(embeddedDevice.discoverResources(namespace)));
                }
            }
        }
        return (Resource[]) discovered.toArray(new Resource[discovered.size()]);
    }

    @Override // org.teleal.cling.model.meta.Device
    public RemoteDevice getRoot() {
        if (!isRoot()) {
            RemoteDevice current = this;
            while (current.getParentDevice() != null) {
                current = current.getParentDevice();
            }
            return current;
        }
        return this;
    }

    @Override // org.teleal.cling.model.meta.Device
    public RemoteDevice findDevice(UDN udn) {
        return find(udn, (UDN) this);
    }
}
