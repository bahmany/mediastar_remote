package org.teleal.cling.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.LocalGENASubscription;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.resource.Resource;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.protocol.SendingAsync;

/* loaded from: classes.dex */
class LocalItems extends RegistryItems<LocalDevice, LocalGENASubscription> {
    private static Logger log = Logger.getLogger(Registry.class.getName());
    protected Random randomGenerator;

    LocalItems(RegistryImpl registry) {
        super(registry);
        this.randomGenerator = new Random();
    }

    @Override // org.teleal.cling.registry.RegistryItems
    public void add(LocalDevice localDevice) throws RegistrationException {
        if (this.registry.getDevice(localDevice.getIdentity().getUdn(), false) != null) {
            log.fine("Ignoring addition, device already registered: " + localDevice);
            return;
        }
        log.fine("Adding local device to registry: " + localDevice);
        for (Resource deviceResource : getResources(localDevice)) {
            if (this.registry.getResource(deviceResource.getPathQuery()) != null) {
                throw new RegistrationException("URI namespace conflict with already registered resource: " + deviceResource);
            }
            this.registry.addResource(deviceResource);
            log.fine("Registered resource: " + deviceResource);
        }
        log.fine("Adding item to registry with expiration in seconds: " + localDevice.getIdentity().getMaxAgeSeconds());
        RegistryItem<UDN, LocalDevice> localItem = new RegistryItem<>(localDevice.getIdentity().getUdn(), localDevice, localDevice.getIdentity().getMaxAgeSeconds().intValue());
        this.deviceItems.add(localItem);
        log.fine("Registered local device: " + localItem);
        advertiseAlive(localDevice);
        for (RegistryListener listener : this.registry.getListeners()) {
            listener.localDeviceAdded(this.registry, localDevice);
        }
    }

    @Override // org.teleal.cling.registry.RegistryItems
    Collection<LocalDevice> get() {
        Set<LocalDevice> c = new HashSet<>();
        Iterator it = this.deviceItems.iterator();
        while (it.hasNext()) {
            RegistryItem<UDN, LocalDevice> item = (RegistryItem) it.next();
            c.add(item.getItem());
        }
        return Collections.unmodifiableCollection(c);
    }

    @Override // org.teleal.cling.registry.RegistryItems
    public boolean remove(LocalDevice localDevice) throws RegistrationException {
        return remove(localDevice, false);
    }

    boolean remove(LocalDevice localDevice, boolean shuttingDown) throws RegistrationException {
        LocalDevice registeredDevice = get(localDevice.getIdentity().getUdn(), true);
        if (registeredDevice != null) {
            log.fine("Removing local device from registry: " + localDevice);
            this.deviceItems.remove(new RegistryItem(localDevice.getIdentity().getUdn()));
            for (Resource deviceResource : getResources(localDevice)) {
                if (this.registry.removeResource(deviceResource)) {
                    log.fine("Unregistered resource: " + deviceResource);
                }
            }
            Iterator<RegistryItem<String, LocalGENASubscription>> it = this.subscriptionItems.iterator();
            while (it.hasNext()) {
                RegistryItem<String, LocalGENASubscription> incomingSubscription = it.next();
                UDN subscriptionForUDN = incomingSubscription.getItem().getService().getDevice().getIdentity().getUdn();
                if (subscriptionForUDN.equals(registeredDevice.getIdentity().getUdn())) {
                    log.fine("Removing incoming subscription: " + incomingSubscription.getKey());
                    it.remove();
                    if (!shuttingDown) {
                        this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.registry.LocalItems.1
                            private final /* synthetic */ RegistryItem val$incomingSubscription;

                            AnonymousClass1(RegistryItem incomingSubscription2) {
                                registryItem = incomingSubscription2;
                            }

                            @Override // java.lang.Runnable
                            public void run() {
                                ((LocalGENASubscription) registryItem.getItem()).end(CancelReason.DEVICE_WAS_REMOVED);
                            }
                        });
                    }
                }
            }
            advertiseByebye(localDevice, !shuttingDown);
            if (!shuttingDown) {
                for (RegistryListener listener : this.registry.getListeners()) {
                    this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.registry.LocalItems.2
                        private final /* synthetic */ RegistryListener val$listener;
                        private final /* synthetic */ LocalDevice val$localDevice;

                        AnonymousClass2(RegistryListener listener2, LocalDevice localDevice2) {
                            registryListener = listener2;
                            localDevice = localDevice2;
                        }

                        @Override // java.lang.Runnable
                        public void run() {
                            registryListener.localDeviceRemoved(LocalItems.this.registry, localDevice);
                        }
                    });
                }
            }
            return true;
        }
        return false;
    }

    /* renamed from: org.teleal.cling.registry.LocalItems$1 */
    class AnonymousClass1 implements Runnable {
        private final /* synthetic */ RegistryItem val$incomingSubscription;

        AnonymousClass1(RegistryItem incomingSubscription2) {
            registryItem = incomingSubscription2;
        }

        @Override // java.lang.Runnable
        public void run() {
            ((LocalGENASubscription) registryItem.getItem()).end(CancelReason.DEVICE_WAS_REMOVED);
        }
    }

    /* renamed from: org.teleal.cling.registry.LocalItems$2 */
    class AnonymousClass2 implements Runnable {
        private final /* synthetic */ RegistryListener val$listener;
        private final /* synthetic */ LocalDevice val$localDevice;

        AnonymousClass2(RegistryListener listener2, LocalDevice localDevice2) {
            registryListener = listener2;
            localDevice = localDevice2;
        }

        @Override // java.lang.Runnable
        public void run() {
            registryListener.localDeviceRemoved(LocalItems.this.registry, localDevice);
        }
    }

    @Override // org.teleal.cling.registry.RegistryItems
    void removeAll() throws RegistrationException {
        removeAll(false);
    }

    void removeAll(boolean shuttingDown) throws RegistrationException {
        LocalDevice[] allDevices = (LocalDevice[]) get().toArray(new LocalDevice[get().size()]);
        for (LocalDevice device : allDevices) {
            remove(device, shuttingDown);
        }
    }

    @Override // org.teleal.cling.registry.RegistryItems
    void maintain() {
        if (!this.deviceItems.isEmpty()) {
            Set<RegistryItem<UDN, LocalDevice>> expiredLocalItems = new HashSet<>();
            Iterator it = this.deviceItems.iterator();
            while (it.hasNext()) {
                RegistryItem<UDN, LocalDevice> localItem = (RegistryItem) it.next();
                if (localItem.getExpirationDetails().hasExpired(true)) {
                    log.finer("Local item has expired: " + localItem);
                    expiredLocalItems.add(localItem);
                }
            }
            for (RegistryItem<UDN, LocalDevice> expiredLocalItem : expiredLocalItems) {
                log.fine("Refreshing local device advertisement: " + expiredLocalItem.getItem());
                advertiseAlive(expiredLocalItem.getItem());
                expiredLocalItem.getExpirationDetails().stampLastRefresh();
            }
            Set<RegistryItem<String, LocalGENASubscription>> expiredIncomingSubscriptions = new HashSet<>();
            Iterator it2 = this.subscriptionItems.iterator();
            while (it2.hasNext()) {
                RegistryItem<String, LocalGENASubscription> item = (RegistryItem) it2.next();
                if (item.getExpirationDetails().hasExpired(false)) {
                    expiredIncomingSubscriptions.add(item);
                }
            }
            for (RegistryItem<String, LocalGENASubscription> subscription : expiredIncomingSubscriptions) {
                log.fine("Removing expired: " + subscription);
                removeSubscription(subscription.getItem());
                subscription.getItem().end(CancelReason.EXPIRED);
            }
        }
    }

    @Override // org.teleal.cling.registry.RegistryItems
    void shutdown() throws RegistrationException {
        log.fine("Clearing all registered subscriptions to local devices during shutdown");
        this.subscriptionItems.clear();
        log.fine("Removing all local devices from registry during shutdown");
        removeAll(true);
    }

    protected void advertiseAlive(LocalDevice localDevice) {
        this.registry.executeAsyncProtocol(new Runnable() { // from class: org.teleal.cling.registry.LocalItems.3
            private final /* synthetic */ LocalDevice val$localDevice;

            AnonymousClass3(LocalDevice localDevice2) {
                localDevice = localDevice2;
            }

            @Override // java.lang.Runnable
            public void run() throws InterruptedException {
                try {
                    LocalItems.log.finer("Sleeping some milliseconds to avoid flooding the network with ALIVE msgs");
                    Thread.sleep(LocalItems.this.randomGenerator.nextInt(100));
                } catch (InterruptedException ex) {
                    LocalItems.log.severe("Background execution interrupted: " + ex.getMessage());
                }
                LocalItems.this.registry.getProtocolFactory().createSendingNotificationAlive(localDevice).run();
            }
        });
    }

    /* renamed from: org.teleal.cling.registry.LocalItems$3 */
    class AnonymousClass3 implements Runnable {
        private final /* synthetic */ LocalDevice val$localDevice;

        AnonymousClass3(LocalDevice localDevice2) {
            localDevice = localDevice2;
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException {
            try {
                LocalItems.log.finer("Sleeping some milliseconds to avoid flooding the network with ALIVE msgs");
                Thread.sleep(LocalItems.this.randomGenerator.nextInt(100));
            } catch (InterruptedException ex) {
                LocalItems.log.severe("Background execution interrupted: " + ex.getMessage());
            }
            LocalItems.this.registry.getProtocolFactory().createSendingNotificationAlive(localDevice).run();
        }
    }

    protected void advertiseByebye(LocalDevice localDevice, boolean asynchronous) {
        SendingAsync prot = this.registry.getProtocolFactory().createSendingNotificationByebye(localDevice);
        if (asynchronous) {
            this.registry.executeAsyncProtocol(prot);
        } else {
            prot.run();
        }
    }
}
