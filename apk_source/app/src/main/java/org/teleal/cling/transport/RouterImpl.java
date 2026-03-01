package org.teleal.cling.transport;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.model.NetworkAddress;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.OutgoingDatagramMessage;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.protocol.ProtocolCreationException;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.protocol.ReceivingAsync;
import org.teleal.cling.transport.spi.DatagramIO;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.MulticastReceiver;
import org.teleal.cling.transport.spi.NetworkAddressFactory;
import org.teleal.cling.transport.spi.StreamClient;
import org.teleal.cling.transport.spi.StreamServer;
import org.teleal.cling.transport.spi.UpnpStream;
import org.teleal.common.util.Exceptions;

/* loaded from: classes.dex */
public class RouterImpl implements Router {
    private static Logger log = Logger.getLogger(Router.class.getName());
    protected final UpnpServiceConfiguration configuration;
    protected final NetworkAddressFactory networkAddressFactory;
    protected final ProtocolFactory protocolFactory;
    protected final StreamClient streamClient;
    protected final Map<NetworkInterface, MulticastReceiver> multicastReceivers = new HashMap();
    protected final Map<InetAddress, DatagramIO> datagramIOs = new HashMap();
    protected final Map<InetAddress, StreamServer> streamServers = new HashMap();

    public RouterImpl(UpnpServiceConfiguration configuration, ProtocolFactory protocolFactory) throws InitializationException {
        log.info("Creating Router: " + getClass().getName());
        this.configuration = configuration;
        this.protocolFactory = protocolFactory;
        log.fine("Starting networking services...");
        this.networkAddressFactory = getConfiguration().createNetworkAddressFactory();
        this.streamClient = getConfiguration().createStreamClient();
        for (NetworkInterface networkInterface : this.networkAddressFactory.getNetworkInterfaces()) {
            MulticastReceiver multicastReceiver = getConfiguration().createMulticastReceiver(this.networkAddressFactory);
            if (multicastReceiver != null) {
                this.multicastReceivers.put(networkInterface, multicastReceiver);
            }
        }
        for (InetAddress inetAddress : this.networkAddressFactory.getBindAddresses()) {
            DatagramIO datagramIO = getConfiguration().createDatagramIO(this.networkAddressFactory);
            if (datagramIO != null) {
                this.datagramIOs.put(inetAddress, datagramIO);
            }
            StreamServer streamServer = getConfiguration().createStreamServer(this.networkAddressFactory);
            if (streamServer != null) {
                this.streamServers.put(inetAddress, streamServer);
            }
        }
        for (Map.Entry<InetAddress, StreamServer> entry : this.streamServers.entrySet()) {
            log.fine("Starting stream server on address: " + entry.getKey());
            entry.getValue().init(entry.getKey(), this);
            getConfiguration().getStreamServerExecutor().execute(entry.getValue());
        }
        for (Map.Entry<NetworkInterface, MulticastReceiver> entry2 : this.multicastReceivers.entrySet()) {
            log.fine("Starting multicast receiver on interface: " + entry2.getKey().getDisplayName());
            entry2.getValue().init(entry2.getKey(), this, getConfiguration().getDatagramProcessor());
            getConfiguration().getMulticastReceiverExecutor().execute(entry2.getValue());
        }
        for (Map.Entry<InetAddress, DatagramIO> entry3 : this.datagramIOs.entrySet()) {
            log.fine("Starting datagram I/O on address: " + entry3.getKey());
            entry3.getValue().init(entry3.getKey(), this, getConfiguration().getDatagramProcessor());
            getConfiguration().getDatagramIOExecutor().execute(entry3.getValue());
        }
    }

    @Override // org.teleal.cling.transport.Router
    public UpnpServiceConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.transport.Router
    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }

    @Override // org.teleal.cling.transport.Router
    public NetworkAddressFactory getNetworkAddressFactory() {
        return this.networkAddressFactory;
    }

    protected Map<NetworkInterface, MulticastReceiver> getMulticastReceivers() {
        return this.multicastReceivers;
    }

    protected Map<InetAddress, DatagramIO> getDatagramIOs() {
        return this.datagramIOs;
    }

    protected StreamClient getStreamClient() {
        return this.streamClient;
    }

    protected Map<InetAddress, StreamServer> getStreamServers() {
        return this.streamServers;
    }

    @Override // org.teleal.cling.transport.Router
    public synchronized List<NetworkAddress> getActiveStreamServers(InetAddress preferredAddress) {
        List<NetworkAddress> streamServerAddresses;
        StreamServer preferredServer;
        if (getStreamServers().size() == 0) {
            streamServerAddresses = Collections.EMPTY_LIST;
        } else {
            streamServerAddresses = new ArrayList<>();
            if (preferredAddress != null && (preferredServer = getStreamServers().get(preferredAddress)) != null) {
                streamServerAddresses.add(new NetworkAddress(preferredAddress, preferredServer.getPort(), getNetworkAddressFactory().getHardwareAddress(preferredAddress)));
            } else {
                for (Map.Entry<InetAddress, StreamServer> entry : getStreamServers().entrySet()) {
                    byte[] hardwareAddress = getNetworkAddressFactory().getHardwareAddress(entry.getKey());
                    streamServerAddresses.add(new NetworkAddress(entry.getKey(), entry.getValue().getPort(), hardwareAddress));
                }
            }
        }
        return streamServerAddresses;
    }

    @Override // org.teleal.cling.transport.Router
    public synchronized void shutdown() {
        log.fine("Shutting down network services");
        if (this.streamClient != null) {
            log.fine("Stopping stream client connection management/pool");
            this.streamClient.stop();
        }
        for (Map.Entry<InetAddress, StreamServer> entry : this.streamServers.entrySet()) {
            log.fine("Stopping stream server on address: " + entry.getKey());
            entry.getValue().stop();
        }
        this.streamServers.clear();
        for (Map.Entry<NetworkInterface, MulticastReceiver> entry2 : this.multicastReceivers.entrySet()) {
            log.fine("Stopping multicast receiver on interface: " + entry2.getKey().getDisplayName());
            entry2.getValue().stop();
        }
        this.multicastReceivers.clear();
        for (Map.Entry<InetAddress, DatagramIO> entry3 : this.datagramIOs.entrySet()) {
            log.fine("Stopping datagram I/O on address: " + entry3.getKey());
            entry3.getValue().stop();
        }
        this.datagramIOs.clear();
    }

    @Override // org.teleal.cling.transport.Router
    public void received(IncomingDatagramMessage msg) {
        try {
            ReceivingAsync protocol = getProtocolFactory().createReceivingAsync(msg);
            if (protocol == null) {
                if (log.isLoggable(Level.FINEST)) {
                    log.finest("No protocol, ignoring received message: " + msg);
                }
            } else {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("Received asynchronous message: " + msg);
                }
                getConfiguration().getAsyncProtocolExecutor().execute(protocol);
            }
        } catch (ProtocolCreationException ex) {
            log.warning("Handling received datagram failed - " + Exceptions.unwrap(ex).toString());
        }
    }

    @Override // org.teleal.cling.transport.Router
    public void received(UpnpStream stream) {
        log.fine("Received synchronous stream: " + stream);
        getConfiguration().getSyncProtocolExecutor().execute(stream);
    }

    @Override // org.teleal.cling.transport.Router
    public void send(OutgoingDatagramMessage msg) {
        for (DatagramIO datagramIO : getDatagramIOs().values()) {
            datagramIO.send(msg);
        }
    }

    @Override // org.teleal.cling.transport.Router
    public StreamResponseMessage send(StreamRequestMessage msg) {
        if (getStreamClient() == null) {
            log.fine("No StreamClient available, ignoring: " + msg);
            return null;
        }
        log.fine("Sending via TCP unicast stream: " + msg);
        return getStreamClient().sendRequest(msg);
    }

    @Override // org.teleal.cling.transport.Router
    public void broadcast(byte[] bytes) {
        for (Map.Entry<InetAddress, DatagramIO> entry : getDatagramIOs().entrySet()) {
            InetAddress broadcast = getNetworkAddressFactory().getBroadcastAddress(entry.getKey());
            if (broadcast != null) {
                log.fine("Sending UDP datagram to broadcast address: " + broadcast.getHostAddress());
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, broadcast, 9);
                entry.getValue().send(packet);
            }
        }
    }
}
