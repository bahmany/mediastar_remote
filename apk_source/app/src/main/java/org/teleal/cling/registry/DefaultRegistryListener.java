package org.teleal.cling.registry;

import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;

/* loaded from: classes.dex */
public class DefaultRegistryListener implements RegistryListener {
    @Override // org.teleal.cling.registry.RegistryListener
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        deviceAdded(registry, device);
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        deviceRemoved(registry, device);
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void localDeviceAdded(Registry registry, LocalDevice device) {
        deviceAdded(registry, device);
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void localDeviceRemoved(Registry registry, LocalDevice device) {
        deviceRemoved(registry, device);
    }

    public void deviceAdded(Registry registry, Device device) {
    }

    public void deviceRemoved(Registry registry, Device device) {
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void beforeShutdown(Registry registry) {
    }

    @Override // org.teleal.cling.registry.RegistryListener
    public void afterShutdown() {
    }
}
