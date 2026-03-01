package org.teleal.cling.registry;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.resource.Resource;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;

/* loaded from: classes.dex */
abstract class RegistryItems<D extends Device, S extends GENASubscription> {
    protected final RegistryImpl registry;
    protected final Set<RegistryItem<UDN, D>> deviceItems = new HashSet();
    protected final Set<RegistryItem<String, S>> subscriptionItems = new HashSet();

    abstract void add(D d);

    abstract void maintain();

    abstract boolean remove(D d);

    abstract void removeAll();

    abstract void shutdown();

    RegistryItems(RegistryImpl registry) {
        this.registry = registry;
    }

    Set<RegistryItem<UDN, D>> getDeviceItems() {
        return this.deviceItems;
    }

    Set<RegistryItem<String, S>> getSubscriptionItems() {
        return this.subscriptionItems;
    }

    D get(UDN udn, boolean z) {
        D d;
        for (RegistryItem<UDN, D> registryItem : this.deviceItems) {
            D item = registryItem.getItem();
            if (item.getIdentity().getUdn().equals(udn)) {
                return item;
            }
            if (!z && (d = (D) registryItem.getItem().findDevice(udn)) != null) {
                return d;
            }
        }
        return null;
    }

    Collection<D> get(DeviceType deviceType) {
        Collection<D> devices = new HashSet<>();
        for (RegistryItem<UDN, D> item : this.deviceItems) {
            Device[] d = item.getItem().findDevices(deviceType);
            if (d != null) {
                devices.addAll(Arrays.asList(d));
            }
        }
        return devices;
    }

    Collection<D> get(ServiceType serviceType) {
        Collection<D> devices = new HashSet<>();
        for (RegistryItem<UDN, D> item : this.deviceItems) {
            Device[] d = item.getItem().findDevices(serviceType);
            if (d != null) {
                devices.addAll(Arrays.asList(d));
            }
        }
        return devices;
    }

    Collection<D> get() {
        Collection<D> devices = new HashSet<>();
        for (RegistryItem<UDN, D> item : this.deviceItems) {
            devices.add(item.getItem());
        }
        return devices;
    }

    boolean contains(D device) {
        return contains(device.getIdentity().getUdn());
    }

    boolean contains(UDN udn) {
        return this.deviceItems.contains(new RegistryItem(udn));
    }

    void addSubscription(S subscription) {
        RegistryItem<String, S> subscriptionItem = new RegistryItem<>(subscription.getSubscriptionId(), subscription, subscription.getActualDurationSeconds());
        this.subscriptionItems.add(subscriptionItem);
    }

    boolean updateSubscription(S subscription) {
        if (!removeSubscription(subscription)) {
            return false;
        }
        addSubscription(subscription);
        return true;
    }

    boolean removeSubscription(S subscription) {
        return this.subscriptionItems.remove(new RegistryItem(subscription.getSubscriptionId()));
    }

    S getSubscription(String subscriptionId) {
        for (RegistryItem<String, S> registryItem : this.subscriptionItems) {
            if (registryItem.getKey().equals(subscriptionId)) {
                return registryItem.getItem();
            }
        }
        return null;
    }

    Resource[] getResources(Device device) throws RegistrationException {
        try {
            return this.registry.getConfiguration().getNamespace().getResources(device);
        } catch (ValidationException ex) {
            throw new RegistrationException("Resource discover error: " + ex.toString(), ex);
        }
    }
}
