package org.teleal.cling.registry;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.gena.LocalGENASubscription;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteDeviceIdentity;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.resource.Resource;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.protocol.ProtocolFactory;

/* loaded from: classes.dex */
public class RegistryImpl implements Registry {
    private static Logger log = Logger.getLogger(Registry.class.getName());
    protected RegistryMaintainer registryMaintainer;
    protected final UpnpService upnpService;
    protected ReentrantLock remoteSubscriptionsLock = new ReentrantLock(true);
    protected final Set<RegistryListener> registryListeners = new HashSet();
    protected final Set<RegistryItem<URI, Resource>> resourceItems = new HashSet();
    protected final List<Runnable> pendingExecutions = new ArrayList();
    protected final RemoteItems remoteItems = new RemoteItems(this);
    protected final LocalItems localItems = new LocalItems(this);

    public RegistryImpl(UpnpService upnpService) {
        log.fine("Creating Registry: " + getClass().getName());
        this.upnpService = upnpService;
        log.fine("Starting registry background maintenance...");
        this.registryMaintainer = createRegistryMaintainer();
        if (this.registryMaintainer != null) {
            getConfiguration().getRegistryMaintainerExecutor().execute(this.registryMaintainer);
        }
    }

    @Override // org.teleal.cling.registry.Registry
    public UpnpService getUpnpService() {
        return this.upnpService;
    }

    @Override // org.teleal.cling.registry.Registry
    public UpnpServiceConfiguration getConfiguration() {
        return getUpnpService().getConfiguration();
    }

    @Override // org.teleal.cling.registry.Registry
    public ProtocolFactory getProtocolFactory() {
        return getUpnpService().getProtocolFactory();
    }

    protected RegistryMaintainer createRegistryMaintainer() {
        return new RegistryMaintainer(this, getConfiguration().getRegistryMaintenanceIntervalMillis());
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addListener(RegistryListener listener) {
        this.registryListeners.add(listener);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void removeListener(RegistryListener listener) {
        this.registryListeners.remove(listener);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<RegistryListener> getListeners() {
        return Collections.unmodifiableCollection(this.registryListeners);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean notifyDiscoveryStart(final RemoteDevice device) {
        boolean z;
        if (getUpnpService().getRegistry().getRemoteDevice(device.getIdentity().getUdn(), true) != null) {
            log.finer("Not notifying listeners, already registered: " + device);
            z = false;
        } else {
            for (final RegistryListener listener : getListeners()) {
                getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.registry.RegistryImpl.1
                    @Override // java.lang.Runnable
                    public void run() {
                        listener.remoteDeviceDiscoveryStarted(RegistryImpl.this, device);
                    }
                });
            }
            z = true;
        }
        return z;
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void notifyDiscoveryFailure(final RemoteDevice device, final Exception ex) {
        for (final RegistryListener listener : getListeners()) {
            getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.registry.RegistryImpl.2
                @Override // java.lang.Runnable
                public void run() {
                    listener.remoteDeviceDiscoveryFailed(RegistryImpl.this, device, ex);
                }
            });
        }
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addDevice(LocalDevice localDevice) {
        this.localItems.add(localDevice);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addDevice(RemoteDevice remoteDevice) {
        this.remoteItems.add(remoteDevice);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean update(RemoteDeviceIdentity rdIdentity) {
        return this.remoteItems.update(rdIdentity);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean removeDevice(LocalDevice localDevice) {
        return this.localItems.remove(localDevice);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean removeDevice(RemoteDevice remoteDevice) {
        return this.remoteItems.remove(remoteDevice);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void removeAllLocalDevices() {
        this.localItems.removeAll();
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void removeAllRemoteDevices() {
        this.remoteItems.removeAll();
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean removeDevice(UDN udn) {
        boolean zRemoveDevice;
        Device device = getDevice(udn, true);
        if (device != null && (device instanceof LocalDevice)) {
            zRemoveDevice = removeDevice((LocalDevice) device);
        } else if (device != null && (device instanceof RemoteDevice)) {
            zRemoveDevice = removeDevice((RemoteDevice) device);
        } else {
            zRemoveDevice = false;
        }
        return zRemoveDevice;
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Device getDevice(UDN udn, boolean rootOnly) {
        Device device;
        Device device2 = this.localItems.get(udn, rootOnly);
        if (device2 != null) {
            device = device2;
        } else {
            Device device3 = this.remoteItems.get(udn, rootOnly);
            device = device3 != null ? device3 : null;
        }
        return device;
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized LocalDevice getLocalDevice(UDN udn, boolean rootOnly) {
        return this.localItems.get(udn, rootOnly);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized RemoteDevice getRemoteDevice(UDN udn, boolean rootOnly) {
        return this.remoteItems.get(udn, rootOnly);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<LocalDevice> getLocalDevices() {
        return Collections.unmodifiableCollection(this.localItems.get());
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<RemoteDevice> getRemoteDevices() {
        return Collections.unmodifiableCollection(this.remoteItems.get());
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<Device> getDevices() {
        Set all;
        all = new HashSet();
        all.addAll(this.localItems.get());
        all.addAll(this.remoteItems.get());
        return Collections.unmodifiableCollection(all);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<Device> getDevices(DeviceType deviceType) {
        Collection<Device> devices;
        devices = new HashSet<>();
        devices.addAll(this.localItems.get(deviceType));
        devices.addAll(this.remoteItems.get(deviceType));
        return Collections.unmodifiableCollection(devices);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<Device> getDevices(ServiceType serviceType) {
        Collection<Device> devices;
        devices = new HashSet<>();
        devices.addAll(this.localItems.get(serviceType));
        devices.addAll(this.remoteItems.get(serviceType));
        return Collections.unmodifiableCollection(devices);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Service getService(ServiceReference serviceReference) {
        Device device;
        device = getDevice(serviceReference.getUdn(), false);
        return device != null ? device.findService(serviceReference.getServiceId()) : null;
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Resource getResource(URI pathQuery) throws IllegalArgumentException {
        Resource resource;
        if (pathQuery.isAbsolute()) {
            throw new IllegalArgumentException("Resource URI can not be absolute, only path and query:" + pathQuery);
        }
        Iterator<RegistryItem<URI, Resource>> it = this.resourceItems.iterator();
        while (true) {
            if (it.hasNext()) {
                RegistryItem<URI, Resource> resourceItem = it.next();
                resource = resourceItem.getItem();
                if (resource.matches(pathQuery)) {
                    break;
                }
            } else if (pathQuery.getPath().endsWith(ServiceReference.DELIMITER)) {
                URI pathQueryWithoutSlash = URI.create(pathQuery.toString().substring(0, pathQuery.toString().length() - 1));
                for (RegistryItem<URI, Resource> resourceItem2 : this.resourceItems) {
                    resource = resourceItem2.getItem();
                    if (resource.matches(pathQueryWithoutSlash)) {
                        break;
                    }
                }
                resource = null;
            } else {
                resource = null;
            }
        }
        return resource;
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x0013  */
    @Override // org.teleal.cling.registry.Registry
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized <T extends org.teleal.cling.model.resource.Resource> T getResource(java.lang.Class<T> r3, java.net.URI r4) throws java.lang.IllegalArgumentException {
        /*
            r2 = this;
            monitor-enter(r2)
            org.teleal.cling.model.resource.Resource r0 = r2.getResource(r4)     // Catch: java.lang.Throwable -> L15
            if (r0 == 0) goto L13
            java.lang.Class r1 = r0.getClass()     // Catch: java.lang.Throwable -> L15
            boolean r1 = r3.isAssignableFrom(r1)     // Catch: java.lang.Throwable -> L15
            if (r1 == 0) goto L13
        L11:
            monitor-exit(r2)
            return r0
        L13:
            r0 = 0
            goto L11
        L15:
            r1 = move-exception
            monitor-exit(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.teleal.cling.registry.RegistryImpl.getResource(java.lang.Class, java.net.URI):org.teleal.cling.model.resource.Resource");
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized Collection<Resource> getResources() {
        Collection<Resource> s;
        s = new HashSet<>();
        for (RegistryItem<URI, Resource> resourceItem : this.resourceItems) {
            s.add(resourceItem.getItem());
        }
        return s;
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized <T extends Resource> Collection<T> getResources(Class<T> resourceType) {
        HashSet hashSet;
        hashSet = new HashSet();
        for (RegistryItem<URI, Resource> resourceItem : this.resourceItems) {
            if (resourceType.isAssignableFrom(resourceItem.getItem().getClass())) {
                hashSet.add(resourceItem.getItem());
            }
        }
        return hashSet;
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addResource(Resource resource) {
        addResource(resource, 0);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addResource(Resource resource, int maxAgeSeconds) {
        RegistryItem resourceItem = new RegistryItem(resource.getPathQuery(), resource, maxAgeSeconds);
        this.resourceItems.remove(resourceItem);
        this.resourceItems.add(resourceItem);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean removeResource(Resource resource) {
        return this.resourceItems.remove(new RegistryItem(resource.getPathQuery()));
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addLocalSubscription(LocalGENASubscription subscription) {
        this.localItems.addSubscription(subscription);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized LocalGENASubscription getLocalSubscription(String subscriptionId) {
        return this.localItems.getSubscription(subscriptionId);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean updateLocalSubscription(LocalGENASubscription subscription) {
        return this.localItems.updateSubscription(subscription);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean removeLocalSubscription(LocalGENASubscription subscription) {
        return this.localItems.removeSubscription(subscription);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void addRemoteSubscription(RemoteGENASubscription subscription) {
        this.remoteItems.addSubscription(subscription);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized RemoteGENASubscription getRemoteSubscription(String subscriptionId) {
        return this.remoteItems.getSubscription(subscriptionId);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void updateRemoteSubscription(RemoteGENASubscription subscription) {
        this.remoteItems.updateSubscription(subscription);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void removeRemoteSubscription(RemoteGENASubscription subscription) {
        this.remoteItems.removeSubscription(subscription);
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void shutdown() {
        log.fine("Shutting down registry...");
        if (this.registryMaintainer != null) {
            this.registryMaintainer.stop();
        }
        log.finest("Executing final pending operations on shutdown: " + this.pendingExecutions.size());
        runPendingExecutions(false);
        for (RegistryListener listener : this.registryListeners) {
            listener.beforeShutdown(this);
        }
        RegistryItem<URI, Resource>[] resources = (RegistryItem[]) this.resourceItems.toArray(new RegistryItem[this.resourceItems.size()]);
        for (RegistryItem<URI, Resource> resourceItem : resources) {
            resourceItem.getItem().shutdown();
        }
        this.remoteItems.shutdown();
        this.localItems.shutdown();
        for (RegistryListener listener2 : this.registryListeners) {
            listener2.afterShutdown();
        }
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void pause() {
        if (this.registryMaintainer != null) {
            log.fine("Pausing registry maintenance");
            runPendingExecutions(true);
            this.registryMaintainer.stop();
            this.registryMaintainer = null;
        }
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized void resume() {
        if (this.registryMaintainer == null) {
            log.fine("Resuming registry maintenance");
            this.remoteItems.resume();
            this.registryMaintainer = createRegistryMaintainer();
            if (this.registryMaintainer != null) {
                getConfiguration().getRegistryMaintainerExecutor().execute(this.registryMaintainer);
            }
        }
    }

    @Override // org.teleal.cling.registry.Registry
    public synchronized boolean isPaused() {
        return this.registryMaintainer == null;
    }

    synchronized void maintain() {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Maintaining registry...");
        }
        Iterator<RegistryItem<URI, Resource>> it = this.resourceItems.iterator();
        while (it.hasNext()) {
            RegistryItem<URI, Resource> item = it.next();
            if (item.getExpirationDetails().hasExpired()) {
                if (log.isLoggable(Level.FINER)) {
                    log.finer("Removing expired resource: " + item);
                }
                it.remove();
            }
        }
        for (RegistryItem<URI, Resource> resourceItem : this.resourceItems) {
            resourceItem.getItem().maintain(this.pendingExecutions, resourceItem.getExpirationDetails());
        }
        this.remoteItems.maintain();
        this.localItems.maintain();
        runPendingExecutions(true);
    }

    synchronized void executeAsyncProtocol(Runnable runnable) {
        this.pendingExecutions.add(runnable);
    }

    synchronized void runPendingExecutions(boolean async) {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Executing pending operations: " + this.pendingExecutions.size());
        }
        for (Runnable pendingExecution : this.pendingExecutions) {
            if (async) {
                getConfiguration().getAsyncProtocolExecutor().execute(pendingExecution);
            } else {
                pendingExecution.run();
            }
        }
        if (this.pendingExecutions.size() > 0) {
            this.pendingExecutions.clear();
        }
    }

    public void printDebugLog() {
        if (log.isLoggable(Level.FINE)) {
            log.fine("====================================    REMOTE   ================================================");
            for (RemoteDevice remoteDevice : this.remoteItems.get()) {
                log.fine(remoteDevice.toString());
            }
            log.fine("====================================    LOCAL    ================================================");
            for (LocalDevice localDevice : this.localItems.get()) {
                log.fine(localDevice.toString());
            }
            log.fine("====================================  RESOURCES  ================================================");
            for (RegistryItem<URI, Resource> resourceItem : this.resourceItems) {
                log.fine(resourceItem.toString());
            }
            log.fine("=================================================================================================");
        }
    }

    @Override // org.teleal.cling.registry.Registry
    public void lockRemoteSubscriptions() {
        this.remoteSubscriptionsLock.lock();
    }

    @Override // org.teleal.cling.registry.Registry
    public void unlockRemoteSubscriptions() {
        this.remoteSubscriptionsLock.unlock();
    }
}
