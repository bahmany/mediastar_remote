package org.teleal.cling.transport;

import com.google.android.gms.games.GamesStatusCodes;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.model.NetworkAddress;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.OutgoingDatagramMessage;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.NetworkAddressFactory;
import org.teleal.cling.transport.spi.UpnpStream;
import org.teleal.common.util.Exceptions;

/* loaded from: classes.dex */
public class SwitchableRouterImpl implements SwitchableRouter {
    private static final Logger log = Logger.getLogger(Router.class.getName());
    private final UpnpServiceConfiguration configuration;
    private final ProtocolFactory protocolFactory;
    private Router router;
    protected ReentrantReadWriteLock routerLock = new ReentrantReadWriteLock(true);
    protected Lock readLock = this.routerLock.readLock();
    protected Lock writeLock = this.routerLock.writeLock();

    public SwitchableRouterImpl(UpnpServiceConfiguration configuration, ProtocolFactory protocolFactory) {
        this.configuration = configuration;
        this.protocolFactory = protocolFactory;
    }

    @Override // org.teleal.cling.transport.Router
    public UpnpServiceConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.transport.Router
    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }

    @Override // org.teleal.cling.transport.SwitchableRouter
    public boolean isEnabled() throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            return this.router != null;
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.SwitchableRouter
    public boolean enable() throws RouterLockAcquisitionException {
        lock(this.writeLock);
        try {
            if (this.router == null) {
                try {
                    log.fine("Enabling network transport router");
                    this.router = new RouterImpl(getConfiguration(), getProtocolFactory());
                    unlock(this.writeLock);
                    return true;
                } catch (InitializationException ex) {
                    handleStartFailure(ex);
                }
            }
            unlock(this.writeLock);
            return false;
        } catch (Throwable th) {
            unlock(this.writeLock);
            throw th;
        }
    }

    @Override // org.teleal.cling.transport.SwitchableRouter
    public void handleStartFailure(InitializationException ex) {
        log.severe("Unable to initialize network router: " + ex);
        log.severe("Cause: " + Exceptions.unwrap(ex));
    }

    @Override // org.teleal.cling.transport.SwitchableRouter
    public boolean disable() throws RouterLockAcquisitionException {
        lock(this.writeLock);
        try {
            if (this.router != null) {
                log.fine("Disabling network transport router");
                this.router.shutdown();
                this.router = null;
                unlock(this.writeLock);
                return true;
            }
            unlock(this.writeLock);
            return false;
        } catch (Throwable th) {
            unlock(this.writeLock);
            throw th;
        }
    }

    @Override // org.teleal.cling.transport.Router
    public NetworkAddressFactory getNetworkAddressFactory() throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            return this.router != null ? this.router.getNetworkAddressFactory() : new DisabledNetworkAddressFactory();
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public List<NetworkAddress> getActiveStreamServers(InetAddress preferredAddress) throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            return this.router != null ? this.router.getActiveStreamServers(preferredAddress) : Collections.EMPTY_LIST;
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public void shutdown() throws RouterLockAcquisitionException {
        disable();
    }

    @Override // org.teleal.cling.transport.Router
    public void received(IncomingDatagramMessage msg) throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            if (this.router != null) {
                this.router.received(msg);
            }
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public void received(UpnpStream stream) throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            if (this.router != null) {
                this.router.received(stream);
            }
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public void send(OutgoingDatagramMessage msg) throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            if (this.router != null) {
                this.router.send(msg);
            }
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public StreamResponseMessage send(StreamRequestMessage msg) throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            return this.router != null ? this.router.send(msg) : null;
        } finally {
            unlock(this.readLock);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public void broadcast(byte[] bytes) throws RouterLockAcquisitionException {
        lock(this.readLock);
        try {
            if (this.router != null) {
                this.router.broadcast(bytes);
            }
        } finally {
            unlock(this.readLock);
        }
    }

    protected void lock(Lock lock, int timeoutMilliseconds) throws RouterLockAcquisitionException {
        try {
            log.finest("Trying to obtain lock with timeout milliseconds '" + timeoutMilliseconds + "': " + lock.getClass().getSimpleName());
            if (lock.tryLock(timeoutMilliseconds, TimeUnit.MILLISECONDS)) {
                log.finest("Acquired router lock: " + lock.getClass().getSimpleName());
                return;
            }
            throw new RouterLockAcquisitionException("Failed to acquire router lock: " + lock.getClass().getSimpleName());
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to acquire router lock: " + lock.getClass().getSimpleName(), e);
        }
    }

    protected void lock(Lock lock) throws RouterLockAcquisitionException {
        lock(lock, getLockTimeoutMillis());
    }

    protected void unlock(Lock lock) {
        log.finest("Releasing router lock: " + lock.getClass().getSimpleName());
        lock.unlock();
    }

    protected int getLockTimeoutMillis() {
        return GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_CREATION_NOT_ALLOWED;
    }

    class DisabledNetworkAddressFactory implements NetworkAddressFactory {
        DisabledNetworkAddressFactory() {
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public InetAddress getMulticastGroup() {
            return null;
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public int getMulticastPort() {
            return 0;
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public int getStreamListenPort() {
            return 0;
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public NetworkInterface[] getNetworkInterfaces() {
            return new NetworkInterface[0];
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public InetAddress[] getBindAddresses() {
            return new InetAddress[0];
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public byte[] getHardwareAddress(InetAddress inetAddress) {
            return new byte[0];
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public InetAddress getBroadcastAddress(InetAddress inetAddress) {
            return null;
        }

        @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
        public InetAddress getLocalAddress(NetworkInterface networkInterface, boolean isIPv6, InetAddress remoteAddress) throws IllegalStateException {
            return null;
        }
    }

    public static class RouterLockAcquisitionException extends RuntimeException {
        public RouterLockAcquisitionException() {
        }

        public RouterLockAcquisitionException(String s) {
            super(s);
        }

        public RouterLockAcquisitionException(String s, Throwable throwable) {
            super(s, throwable);
        }

        public RouterLockAcquisitionException(Throwable throwable) {
            super(throwable);
        }
    }
}
