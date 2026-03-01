package org.teleal.cling.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteDeviceIdentity;
import org.teleal.cling.model.resource.Resource;
import org.teleal.cling.model.types.UDN;

/* loaded from: classes.dex */
class RemoteItems extends RegistryItems<RemoteDevice, RemoteGENASubscription> {
    private static Logger log = Logger.getLogger(Registry.class.getName());

    RemoteItems(RegistryImpl registry) {
        super(registry);
    }

    @Override // org.teleal.cling.registry.RegistryItems
    public void add(RemoteDevice device) {
        if (update(device.getIdentity())) {
            log.fine("Ignoring addition, device already registered: " + device);
            return;
        }
        Resource[] resources = getResources(device);
        for (Resource deviceResource : resources) {
            log.fine("Validating remote device resource; " + deviceResource);
            if (this.registry.getResource(deviceResource.getPathQuery()) != null) {
                throw new RegistrationException("URI namespace conflict with already registered resource: " + deviceResource);
            }
        }
        for (Resource validatedResource : resources) {
            this.registry.addResource(validatedResource);
            log.fine("Added remote device resource: " + validatedResource);
        }
        RegistryItem item = new RegistryItem(device.getIdentity().getUdn(), device, device.getIdentity().getMaxAgeSeconds().intValue());
        log.fine("Adding hydrated remote device to registry with " + item.getExpirationDetails().getMaxAgeSeconds() + " seconds expiration: " + device);
        this.deviceItems.add(item);
        if (log.isLoggable(Level.FINEST)) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append("-------------------------- START Registry Namespace -----------------------------------\n");
            for (Resource resource : this.registry.getResources()) {
                sb.append(resource).append("\n");
            }
            sb.append("-------------------------- END Registry Namespace -----------------------------------");
            log.finest(sb.toString());
        }
        log.fine("Completely hydrated remote device graph available, calling listeners: " + device);
        for (RegistryListener listener : this.registry.getListeners()) {
            this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.registry.RemoteItems.1
                private final /* synthetic */ RemoteDevice val$device;
                private final /* synthetic */ RegistryListener val$listener;

                AnonymousClass1(RegistryListener listener2, RemoteDevice device2) {
                    registryListener = listener2;
                    remoteDevice = device2;
                }

                @Override // java.lang.Runnable
                public void run() {
                    registryListener.remoteDeviceAdded(RemoteItems.this.registry, remoteDevice);
                }
            });
        }
    }

    /* renamed from: org.teleal.cling.registry.RemoteItems$1 */
    class AnonymousClass1 implements Runnable {
        private final /* synthetic */ RemoteDevice val$device;
        private final /* synthetic */ RegistryListener val$listener;

        AnonymousClass1(RegistryListener listener2, RemoteDevice device2) {
            registryListener = listener2;
            remoteDevice = device2;
        }

        @Override // java.lang.Runnable
        public void run() {
            registryListener.remoteDeviceAdded(RemoteItems.this.registry, remoteDevice);
        }
    }

    boolean update(RemoteDeviceIdentity rdIdentity) {
        for (LocalDevice localDevice : this.registry.getLocalDevices()) {
            if (localDevice.findDevice(rdIdentity.getUdn()) != null) {
                log.fine("Ignoring update, a local device graph contains UDN");
                return true;
            }
        }
        RemoteDevice registeredRemoteDevice = get(rdIdentity.getUdn(), false);
        if (registeredRemoteDevice == null) {
            return false;
        }
        if (!registeredRemoteDevice.isRoot()) {
            log.fine("Updating root device of embedded: " + registeredRemoteDevice);
            registeredRemoteDevice = registeredRemoteDevice.getRoot();
        }
        RegistryItem<UDN, RemoteDevice> item = new RegistryItem<>(registeredRemoteDevice.getIdentity().getUdn(), registeredRemoteDevice, rdIdentity.getMaxAgeSeconds().intValue());
        log.fine("Updating expiration of: " + registeredRemoteDevice);
        this.deviceItems.remove(item);
        this.deviceItems.add(item);
        log.fine("Remote device updated, calling listeners: " + registeredRemoteDevice);
        for (RegistryListener listener : this.registry.getListeners()) {
            this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.registry.RemoteItems.2
                private final /* synthetic */ RegistryItem val$item;
                private final /* synthetic */ RegistryListener val$listener;

                AnonymousClass2(RegistryListener listener2, RegistryItem item2) {
                    registryListener = listener2;
                    registryItem = item2;
                }

                @Override // java.lang.Runnable
                public void run() {
                    registryListener.remoteDeviceUpdated(RemoteItems.this.registry, (RemoteDevice) registryItem.getItem());
                }
            });
        }
        return true;
    }

    /* renamed from: org.teleal.cling.registry.RemoteItems$2 */
    class AnonymousClass2 implements Runnable {
        private final /* synthetic */ RegistryItem val$item;
        private final /* synthetic */ RegistryListener val$listener;

        AnonymousClass2(RegistryListener listener2, RegistryItem item2) {
            registryListener = listener2;
            registryItem = item2;
        }

        @Override // java.lang.Runnable
        public void run() {
            registryListener.remoteDeviceUpdated(RemoteItems.this.registry, (RemoteDevice) registryItem.getItem());
        }
    }

    @Override // org.teleal.cling.registry.RegistryItems
    public boolean remove(RemoteDevice remoteDevice) {
        return remove(remoteDevice, false);
    }

    boolean remove(RemoteDevice remoteDevice, boolean shuttingDown) throws RegistrationException {
        RemoteDevice registeredDevice = (RemoteDevice) get(remoteDevice.getIdentity().getUdn(), true);
        if (registeredDevice == null) {
            return false;
        }
        log.fine("Removing remote device from registry: " + remoteDevice);
        for (Resource deviceResource : getResources(registeredDevice)) {
            if (this.registry.removeResource(deviceResource)) {
                log.fine("Unregistered resource: " + deviceResource);
            }
        }
        Iterator<RegistryItem<String, RemoteGENASubscription>> it = this.subscriptionItems.iterator();
        while (it.hasNext()) {
            RegistryItem<String, RemoteGENASubscription> outgoingSubscription = it.next();
            UDN subscriptionForUDN = outgoingSubscription.getItem().getService().getDevice().getIdentity().getUdn();
            if (subscriptionForUDN.equals(registeredDevice.getIdentity().getUdn())) {
                log.fine("Removing outgoing subscription: " + outgoingSubscription.getKey());
                it.remove();
                if (!shuttingDown) {
                    this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.registry.RemoteItems.3
                        private final /* synthetic */ RegistryItem val$outgoingSubscription;

                        AnonymousClass3(RegistryItem outgoingSubscription2) {
                            registryItem = outgoingSubscription2;
                        }

                        @Override // java.lang.Runnable
                        public void run() {
                            ((RemoteGENASubscription) registryItem.getItem()).end(CancelReason.DEVICE_WAS_REMOVED, null);
                        }
                    });
                }
            }
        }
        if (!shuttingDown) {
            for (RegistryListener listener : this.registry.getListeners()) {
                this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.registry.RemoteItems.4
                    private final /* synthetic */ RegistryListener val$listener;
                    private final /* synthetic */ RemoteDevice val$registeredDevice;

                    AnonymousClass4(RegistryListener listener2, RemoteDevice registeredDevice2) {
                        registryListener = listener2;
                        remoteDevice = registeredDevice2;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        registryListener.remoteDeviceRemoved(RemoteItems.this.registry, remoteDevice);
                    }
                });
            }
        }
        this.deviceItems.remove(new RegistryItem(registeredDevice2.getIdentity().getUdn()));
        return true;
    }

    /* renamed from: org.teleal.cling.registry.RemoteItems$3 */
    class AnonymousClass3 implements Runnable {
        private final /* synthetic */ RegistryItem val$outgoingSubscription;

        AnonymousClass3(RegistryItem outgoingSubscription2) {
            registryItem = outgoingSubscription2;
        }

        @Override // java.lang.Runnable
        public void run() {
            ((RemoteGENASubscription) registryItem.getItem()).end(CancelReason.DEVICE_WAS_REMOVED, null);
        }
    }

    /* renamed from: org.teleal.cling.registry.RemoteItems$4 */
    class AnonymousClass4 implements Runnable {
        private final /* synthetic */ RegistryListener val$listener;
        private final /* synthetic */ RemoteDevice val$registeredDevice;

        AnonymousClass4(RegistryListener listener2, RemoteDevice registeredDevice2) {
            registryListener = listener2;
            remoteDevice = registeredDevice2;
        }

        @Override // java.lang.Runnable
        public void run() {
            registryListener.remoteDeviceRemoved(RemoteItems.this.registry, remoteDevice);
        }
    }

    @Override // org.teleal.cling.registry.RegistryItems
    void removeAll() throws RegistrationException {
        removeAll(false);
    }

    void removeAll(boolean shuttingDown) throws RegistrationException {
        RemoteDevice[] allDevices = (RemoteDevice[]) get().toArray(new RemoteDevice[get().size()]);
        for (RemoteDevice device : allDevices) {
            remove(device, shuttingDown);
        }
    }

    void start() {
    }

    @Override // org.teleal.cling.registry.RegistryItems
    void maintain() {
        if (!this.deviceItems.isEmpty()) {
            Map<UDN, RemoteDevice> expiredRemoteDevices = new HashMap<>();
            Iterator it = this.deviceItems.iterator();
            while (it.hasNext()) {
                RegistryItem<UDN, RemoteDevice> remoteItem = (RegistryItem) it.next();
                if (log.isLoggable(Level.FINEST)) {
                    log.finest("Device '" + remoteItem.getItem() + "' expires in seconds: " + remoteItem.getExpirationDetails().getSecondsUntilExpiration());
                }
                if (remoteItem.getExpirationDetails().hasExpired(false)) {
                    expiredRemoteDevices.put(remoteItem.getKey(), remoteItem.getItem());
                }
            }
            for (RemoteDevice remoteDevice : expiredRemoteDevices.values()) {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("Removing expired: " + remoteDevice);
                }
                remove(remoteDevice);
            }
            Set<RemoteGENASubscription> expiredOutgoingSubscriptions = new HashSet<>();
            Iterator it2 = this.subscriptionItems.iterator();
            while (it2.hasNext()) {
                RegistryItem<String, RemoteGENASubscription> item = (RegistryItem) it2.next();
                if (item.getExpirationDetails().hasExpired(true)) {
                    expiredOutgoingSubscriptions.add(item.getItem());
                }
            }
            for (RemoteGENASubscription subscription : expiredOutgoingSubscriptions) {
                if (log.isLoggable(Level.FINEST)) {
                    log.fine("Renewing outgoing subscription: " + subscription);
                }
                renewOutgoingSubscription(subscription);
            }
        }
    }

    public void resume() {
        log.fine("Updating remote device expiration timestamps on resume");
        List<RemoteDeviceIdentity> toUpdate = new ArrayList<>();
        Iterator it = this.deviceItems.iterator();
        while (it.hasNext()) {
            RegistryItem<UDN, RemoteDevice> remoteItem = (RegistryItem) it.next();
            toUpdate.add(remoteItem.getItem().getIdentity());
        }
        for (RemoteDeviceIdentity identity : toUpdate) {
            update(identity);
        }
    }

    @Override // org.teleal.cling.registry.RegistryItems
    void shutdown() throws RegistrationException {
        log.fine("Cancelling all outgoing subscriptions to remote devices during shutdown");
        List<RemoteGENASubscription> remoteSubscriptions = new ArrayList<>();
        Iterator it = this.subscriptionItems.iterator();
        while (it.hasNext()) {
            RegistryItem<String, RemoteGENASubscription> item = (RegistryItem) it.next();
            remoteSubscriptions.add(item.getItem());
        }
        for (RemoteGENASubscription remoteSubscription : remoteSubscriptions) {
            this.registry.getProtocolFactory().createSendingUnsubscribe(remoteSubscription).run();
        }
        log.fine("Removing all remote devices from registry during shutdown");
        removeAll(true);
    }

    protected void renewOutgoingSubscription(RemoteGENASubscription subscription) {
        this.registry.executeAsyncProtocol(this.registry.getProtocolFactory().createSendingRenewal(subscription));
    }
}
