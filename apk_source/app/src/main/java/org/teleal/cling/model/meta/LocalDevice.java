package org.teleal.cling.model.meta;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.teleal.cling.model.Namespace;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.profile.ControlPointInfo;
import org.teleal.cling.model.profile.DeviceDetailsProvider;
import org.teleal.cling.model.resource.DeviceDescriptorResource;
import org.teleal.cling.model.resource.IconResource;
import org.teleal.cling.model.resource.Resource;
import org.teleal.cling.model.resource.ServiceControlResource;
import org.teleal.cling.model.resource.ServiceDescriptorResource;
import org.teleal.cling.model.resource.ServiceEventSubscriptionResource;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;

/* loaded from: classes.dex */
public class LocalDevice extends Device<DeviceIdentity, LocalDevice, LocalService> {
    private final DeviceDetailsProvider deviceDetailsProvider;

    @Override // org.teleal.cling.model.meta.Device
    public /* bridge */ /* synthetic */ Device newInstance(UDN udn, UDAVersion uDAVersion, DeviceType deviceType, DeviceDetails deviceDetails, Icon[] iconArr, Service[] serviceArr, List list) throws ValidationException {
        return newInstance(udn, uDAVersion, deviceType, deviceDetails, iconArr, (LocalService[]) serviceArr, (List<LocalDevice>) list);
    }

    @Override // org.teleal.cling.model.meta.Device
    public /* bridge */ /* synthetic */ Service newInstance(ServiceType serviceType, ServiceId serviceId, URI uri, URI uri2, URI uri3, Action[] actionArr, StateVariable[] stateVariableArr) throws ValidationException {
        return newInstance(serviceType, serviceId, uri, uri2, uri3, (Action<LocalService>[]) actionArr, (StateVariable<LocalService>[]) stateVariableArr);
    }

    @Override // org.teleal.cling.model.meta.Device
    public /* bridge */ /* synthetic */ Device[] toDeviceArray(Collection collection) {
        return toDeviceArray((Collection<LocalDevice>) collection);
    }

    @Override // org.teleal.cling.model.meta.Device
    public /* bridge */ /* synthetic */ Service[] toServiceArray(Collection collection) {
        return toServiceArray((Collection<LocalService>) collection);
    }

    public LocalDevice(DeviceIdentity identity) throws ValidationException {
        super(identity);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details, LocalService service) throws ValidationException {
        super(identity, type, details, null, new LocalService[]{service});
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetailsProvider deviceDetailsProvider, LocalService service) throws ValidationException {
        super(identity, type, null, null, new LocalService[]{service});
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetailsProvider deviceDetailsProvider, LocalService service, LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, null, null, new LocalService[]{service}, new LocalDevice[]{embeddedDevice});
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details, LocalService service, LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, null, new LocalService[]{service}, new LocalDevice[]{embeddedDevice});
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details, LocalService[] services) throws ValidationException {
        super(identity, type, details, null, services);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details, LocalService[] services, LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, null, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details, Icon icon, LocalService service) throws ValidationException {
        super(identity, type, details, new Icon[]{icon}, new LocalService[]{service});
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details, Icon icon, LocalService service, LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, new Icon[]{icon}, new LocalService[]{service}, new LocalDevice[]{embeddedDevice});
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details, Icon icon, LocalService[] services) throws ValidationException {
        super(identity, type, details, new Icon[]{icon}, services);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetailsProvider deviceDetailsProvider, Icon icon, LocalService[] services) throws ValidationException {
        super(identity, type, null, new Icon[]{icon}, services);
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details, Icon icon, LocalService[] services, LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, new Icon[]{icon}, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details, Icon[] icons, LocalService service) throws ValidationException {
        super(identity, type, details, icons, new LocalService[]{service});
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details, Icon[] icons, LocalService service, LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, icons, new LocalService[]{service}, new LocalDevice[]{embeddedDevice});
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetailsProvider deviceDetailsProvider, Icon[] icons, LocalService service, LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, null, icons, new LocalService[]{service}, new LocalDevice[]{embeddedDevice});
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details, Icon[] icons, LocalService[] services) throws ValidationException {
        super(identity, type, details, icons, services);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details, Icon[] icons, LocalService[] services, LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, icons, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, UDAVersion version, DeviceType type, DeviceDetails details, Icon[] icons, LocalService[] services, LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, version, type, details, icons, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, UDAVersion version, DeviceType type, DeviceDetailsProvider deviceDetailsProvider, Icon[] icons, LocalService[] services, LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, version, type, null, icons, services, embeddedDevices);
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    @Override // org.teleal.cling.model.meta.Device
    public DeviceDetails getDetails(ControlPointInfo info) {
        return this.deviceDetailsProvider != null ? this.deviceDetailsProvider.provide(info) : getDetails();
    }

    @Override // org.teleal.cling.model.meta.Device
    public LocalService[] getServices() {
        return this.services != 0 ? (LocalService[]) this.services : new LocalService[0];
    }

    @Override // org.teleal.cling.model.meta.Device
    public LocalDevice[] getEmbeddedDevices() {
        return this.embeddedDevices != 0 ? (LocalDevice[]) this.embeddedDevices : new LocalDevice[0];
    }

    public LocalDevice newInstance(UDN udn, UDAVersion version, DeviceType type, DeviceDetails details, Icon[] icons, LocalService[] services, List<LocalDevice> embeddedDevices) throws ValidationException {
        return new LocalDevice(new DeviceIdentity(udn, getIdentity().getMaxAgeSeconds()), version, type, details, icons, services, embeddedDevices.size() > 0 ? (LocalDevice[]) embeddedDevices.toArray(new LocalDevice[embeddedDevices.size()]) : null);
    }

    @Override // org.teleal.cling.model.meta.Device
    public LocalService newInstance(ServiceType serviceType, ServiceId serviceId, URI descriptorURI, URI controlURI, URI eventSubscriptionURI, Action<LocalService>[] actionArr, StateVariable<LocalService>[] stateVariableArr) throws ValidationException {
        return new LocalService(serviceType, serviceId, actionArr, stateVariableArr);
    }

    @Override // org.teleal.cling.model.meta.Device
    public LocalDevice[] toDeviceArray(Collection<LocalDevice> col) {
        return (LocalDevice[]) col.toArray(new LocalDevice[col.size()]);
    }

    @Override // org.teleal.cling.model.meta.Device
    public LocalService[] newServiceArray(int size) {
        return new LocalService[size];
    }

    @Override // org.teleal.cling.model.meta.Device
    public LocalService[] toServiceArray(Collection<LocalService> col) {
        return (LocalService[]) col.toArray(new LocalService[col.size()]);
    }

    @Override // org.teleal.cling.model.meta.Device, org.teleal.cling.model.Validatable
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        errors.addAll(super.validate());
        if (hasIcons()) {
            for (Icon icon : getIcons()) {
                if (icon.getUri().isAbsolute()) {
                    errors.add(new ValidationError(getClass(), "icons", "Local icon URI can not be absolute: " + icon.getUri()));
                }
                if (icon.getUri().toString().contains("../")) {
                    errors.add(new ValidationError(getClass(), "icons", "Local icon URI must not contain '../': " + icon.getUri()));
                }
                if (icon.getUri().toString().startsWith(ServiceReference.DELIMITER)) {
                    errors.add(new ValidationError(getClass(), "icons", "Local icon URI must not start with '/': " + icon.getUri()));
                }
            }
        }
        return errors;
    }

    @Override // org.teleal.cling.model.meta.Device
    public Resource[] discoverResources(Namespace namespace) {
        List<Resource> discovered = new ArrayList<>();
        if (isRoot()) {
            discovered.add(new DeviceDescriptorResource(namespace.getDescriptorPath(this), this));
        }
        for (LocalService service : getServices()) {
            discovered.add(new ServiceDescriptorResource(namespace.getDescriptorPath(service), service));
            discovered.add(new ServiceControlResource(namespace.getControlPath(service), service));
            discovered.add(new ServiceEventSubscriptionResource(namespace.getEventSubscriptionPath(service), service));
        }
        for (Icon icon : getIcons()) {
            discovered.add(new IconResource(namespace.prefixIfRelative(this, icon.getUri()), icon));
        }
        if (hasEmbeddedDevices()) {
            for (Device embeddedDevice : getEmbeddedDevices()) {
                discovered.addAll(Arrays.asList(embeddedDevice.discoverResources(namespace)));
            }
        }
        return (Resource[]) discovered.toArray(new Resource[discovered.size()]);
    }

    @Override // org.teleal.cling.model.meta.Device
    public LocalDevice getRoot() {
        if (!isRoot()) {
            LocalDevice current = this;
            while (current.getParentDevice() != null) {
                current = current.getParentDevice();
            }
            return current;
        }
        return this;
    }

    @Override // org.teleal.cling.model.meta.Device
    public LocalDevice findDevice(UDN udn) {
        return find(udn, (UDN) this);
    }
}
