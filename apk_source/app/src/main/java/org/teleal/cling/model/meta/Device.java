package org.teleal.cling.model.meta;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.model.Namespace;
import org.teleal.cling.model.Validatable;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.profile.ControlPointInfo;
import org.teleal.cling.model.resource.Resource;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;

/* loaded from: classes.dex */
public abstract class Device<DI extends DeviceIdentity, D extends Device, S extends Service> implements Validatable {
    private static final Logger log = Logger.getLogger(Device.class.getName());
    private final DeviceDetails details;
    protected final D[] embeddedDevices;
    private final Icon[] icons;
    private final DI identity;
    private D parentDevice;
    protected final S[] services;
    private final DeviceType type;
    private final UDAVersion version;

    public abstract Resource[] discoverResources(Namespace namespace);

    public abstract D findDevice(UDN udn);

    public abstract D[] getEmbeddedDevices();

    public abstract D getRoot();

    public abstract S[] getServices();

    public abstract D newInstance(UDN udn, UDAVersion uDAVersion, DeviceType deviceType, DeviceDetails deviceDetails, Icon[] iconArr, S[] sArr, List<D> list) throws ValidationException;

    public abstract S newInstance(ServiceType serviceType, ServiceId serviceId, URI uri, URI uri2, URI uri3, Action<S>[] actionArr, StateVariable<S>[] stateVariableArr) throws ValidationException;

    public abstract S[] newServiceArray(int i);

    public abstract D[] toDeviceArray(Collection<D> collection);

    public abstract S[] toServiceArray(Collection<S> collection);

    public Device(DI identity) throws ValidationException {
        this(identity, null, null, null, null, null);
    }

    public Device(DI identity, DeviceType type, DeviceDetails details, Icon[] icons, S[] sArr) throws ValidationException {
        this(identity, null, type, details, icons, sArr, null);
    }

    public Device(DI identity, DeviceType type, DeviceDetails details, Icon[] icons, S[] sArr, D[] dArr) throws ValidationException {
        this(identity, null, type, details, icons, sArr, dArr);
    }

    public Device(DI identity, UDAVersion version, DeviceType type, DeviceDetails details, Icon[] icons, S[] sArr, D[] dArr) throws ValidationException {
        this.identity = identity;
        this.version = version == null ? new UDAVersion() : version;
        this.type = type;
        this.details = details;
        boolean allNullIcons = true;
        if (icons != null) {
            for (Icon icon : icons) {
                if (icon != null) {
                    allNullIcons = false;
                    icon.setDevice(this);
                }
            }
        }
        this.icons = (icons == null || allNullIcons) ? new Icon[0] : icons;
        boolean allNullServices = true;
        if (sArr != null) {
            for (S service : sArr) {
                if (service != null) {
                    allNullServices = false;
                    service.setDevice(this);
                }
            }
        }
        this.services = (sArr == null || allNullServices) ? null : sArr;
        boolean allNullEmbedded = true;
        if (dArr != null) {
            for (D embeddedDevice : dArr) {
                if (embeddedDevice != null) {
                    allNullEmbedded = false;
                    embeddedDevice.setParentDevice(this);
                }
            }
        }
        this.embeddedDevices = (dArr == null || allNullEmbedded) ? null : dArr;
        List<ValidationError> errors = validate();
        if (errors.size() > 0) {
            if (log.isLoggable(Level.FINEST)) {
                for (ValidationError error : errors) {
                    log.finest(error.toString());
                }
            }
            throw new ValidationException("Validation of device graph failed, call getErrors() on exception", errors);
        }
    }

    public DI getIdentity() {
        return this.identity;
    }

    public UDAVersion getVersion() {
        return this.version;
    }

    public DeviceType getType() {
        return this.type;
    }

    public DeviceDetails getDetails() {
        return this.details;
    }

    public DeviceDetails getDetails(ControlPointInfo info) {
        return getDetails();
    }

    public Icon[] getIcons() {
        return this.icons;
    }

    public boolean hasIcons() {
        return getIcons() != null && getIcons().length > 0;
    }

    public boolean hasServices() {
        return getServices() != null && getServices().length > 0;
    }

    public boolean hasEmbeddedDevices() {
        return getEmbeddedDevices() != null && getEmbeddedDevices().length > 0;
    }

    public D getParentDevice() {
        return this.parentDevice;
    }

    void setParentDevice(D parentDevice) {
        if (this.parentDevice != null) {
            throw new IllegalStateException("Final value has been set already, model is immutable");
        }
        this.parentDevice = parentDevice;
    }

    public boolean isRoot() {
        return getParentDevice() == null;
    }

    public D[] findEmbeddedDevices() {
        return (D[]) toDeviceArray(findEmbeddedDevices(this));
    }

    public D[] findDevices(DeviceType deviceType) {
        return (D[]) toDeviceArray(find(deviceType, (DeviceType) this));
    }

    public D[] findDevices(ServiceType serviceType) {
        return (D[]) toDeviceArray(find(serviceType, (ServiceType) this));
    }

    public Icon[] findIcons() {
        List<Icon> icons = new ArrayList<>();
        if (hasIcons()) {
            icons.addAll(Arrays.asList(getIcons()));
        }
        Device[] embeddedDevices = findEmbeddedDevices();
        for (Device device : embeddedDevices) {
            if (device.hasIcons()) {
                icons.addAll(Arrays.asList(device.getIcons()));
            }
        }
        return (Icon[]) icons.toArray(new Icon[icons.size()]);
    }

    public S[] findServices() {
        return (S[]) toServiceArray(findServices(null, null, this));
    }

    public S[] findServices(ServiceType serviceType) {
        return (S[]) toServiceArray(findServices(serviceType, null, this));
    }

    protected D find(UDN udn, D d) {
        if (!d.getIdentity().getUdn().equals(udn)) {
            if (d.hasEmbeddedDevices()) {
                for (Device device : d.getEmbeddedDevices()) {
                    D d2 = (D) find(udn, (UDN) device);
                    if (d2 != null) {
                        return d2;
                    }
                }
            }
            return null;
        }
        return d;
    }

    protected Collection<D> findEmbeddedDevices(D current) {
        Collection<D> devices = new HashSet<>();
        if (!current.isRoot()) {
            devices.add(current);
        }
        if (current.hasEmbeddedDevices()) {
            for (Device device : current.getEmbeddedDevices()) {
                devices.addAll(findEmbeddedDevices(device));
            }
        }
        return devices;
    }

    protected Collection<D> find(DeviceType deviceType, D current) {
        Collection<D> devices = new HashSet<>();
        if (current.getType() != null && current.getType().implementsVersion(deviceType)) {
            devices.add(current);
        }
        if (current.hasEmbeddedDevices()) {
            for (Device device : current.getEmbeddedDevices()) {
                devices.addAll(find(deviceType, (DeviceType) device));
            }
        }
        return devices;
    }

    protected Collection<D> find(ServiceType serviceType, D current) {
        Collection<S> services = findServices(serviceType, null, current);
        HashSet hashSet = new HashSet();
        for (Service service : services) {
            hashSet.add(service.getDevice());
        }
        return hashSet;
    }

    protected Collection<S> findServices(ServiceType serviceType, ServiceId serviceId, D current) {
        Collection services = new HashSet();
        if (current.hasServices()) {
            for (Service service : current.getServices()) {
                if (isMatch(service, serviceType, serviceId)) {
                    services.add(service);
                }
            }
        }
        Collection<D> embeddedDevices = findEmbeddedDevices(current);
        if (embeddedDevices != null) {
            for (D embeddedDevice : embeddedDevices) {
                if (embeddedDevice.hasServices()) {
                    for (Service service2 : embeddedDevice.getServices()) {
                        if (isMatch(service2, serviceType, serviceId)) {
                            services.add(service2);
                        }
                    }
                }
            }
        }
        return services;
    }

    public S findService(ServiceId serviceId) {
        Collection<S> services = findServices(null, serviceId, this);
        if (services.size() == 1) {
            return services.iterator().next();
        }
        return null;
    }

    public S findService(ServiceType serviceType) {
        Collection<S> services = findServices(serviceType, null, this);
        if (services.size() > 0) {
            return services.iterator().next();
        }
        return null;
    }

    public ServiceType[] findServiceTypes() {
        Collection<S> services = findServices(null, null, this);
        Collection<ServiceType> col = new HashSet<>();
        for (S service : services) {
            col.add(service.getServiceType());
        }
        return (ServiceType[]) col.toArray(new ServiceType[col.size()]);
    }

    private boolean isMatch(Service s, ServiceType serviceType, ServiceId serviceId) {
        boolean matchesType = serviceType == null || s.getServiceType().implementsVersion(serviceType);
        boolean matchesId = serviceId == null || s.getServiceId().equals(serviceId);
        return matchesType && matchesId;
    }

    public boolean isFullyHydrated() {
        Service[] services = findServices();
        for (Service service : services) {
            if (service.hasStateVariables()) {
                return true;
            }
        }
        return false;
    }

    public String getDisplayString() {
        String cleanModelName = null;
        String cleanModelNumber = null;
        if (getDetails() != null && getDetails().getModelDetails() != null) {
            ModelDetails modelDetails = getDetails().getModelDetails();
            if (modelDetails.getModelName() != null) {
                if (modelDetails.getModelNumber() != null && modelDetails.getModelName().endsWith(modelDetails.getModelNumber())) {
                    cleanModelName = modelDetails.getModelName().substring(0, modelDetails.getModelName().length() - modelDetails.getModelNumber().length());
                } else {
                    cleanModelName = modelDetails.getModelName();
                }
            }
            cleanModelNumber = cleanModelName != null ? (modelDetails.getModelNumber() == null || cleanModelName.startsWith(modelDetails.getModelNumber())) ? "" : modelDetails.getModelNumber() : modelDetails.getModelNumber();
        }
        StringBuilder sb = new StringBuilder();
        if (getDetails() != null && getDetails().getManufacturerDetails() != null) {
            if (cleanModelName != null && getDetails().getManufacturerDetails().getManufacturer() != null) {
                if (cleanModelName.startsWith(getDetails().getManufacturerDetails().getManufacturer())) {
                    cleanModelName = cleanModelName.substring(getDetails().getManufacturerDetails().getManufacturer().length()).trim();
                } else {
                    cleanModelName = cleanModelName.trim();
                }
            }
            if (getDetails().getManufacturerDetails().getManufacturer() != null) {
                sb.append(getDetails().getManufacturerDetails().getManufacturer());
            }
        }
        sb.append((cleanModelName == null || cleanModelName.length() <= 0) ? "" : " " + cleanModelName);
        sb.append((cleanModelNumber == null || cleanModelNumber.length() <= 0) ? "" : " " + cleanModelNumber.trim());
        return sb.toString();
    }

    @Override // org.teleal.cling.model.Validatable
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        if (getType() != null) {
            errors.addAll(getVersion().validate());
            if (getDetails() != null) {
                errors.addAll(getDetails().validate());
            }
            if (hasIcons()) {
                for (Icon icon : getIcons()) {
                    if (icon != null) {
                        errors.addAll(icon.validate());
                    }
                }
            }
            if (hasServices()) {
                for (Service service : getServices()) {
                    if (service != null) {
                        errors.addAll(service.validate());
                    }
                }
            }
            if (hasEmbeddedDevices()) {
                for (Device embeddedDevice : getEmbeddedDevices()) {
                    if (embeddedDevice != null) {
                        errors.addAll(embeddedDevice.validate());
                    }
                }
            }
        }
        return errors;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Device device = (Device) o;
        return this.identity.equals(device.identity);
    }

    public int hashCode() {
        return this.identity.hashCode();
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ") Identity: " + getIdentity().toString() + ", Root: " + isRoot();
    }
}
