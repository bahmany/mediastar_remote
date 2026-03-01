package org.teleal.cling.mock;

import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServerUtil;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import org.teleal.cling.DefaultUpnpServiceConfiguration;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.controlpoint.ControlPointImpl;
import org.teleal.cling.model.NetworkAddress;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.OutgoingDatagramMessage;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.protocol.ProtocolFactoryImpl;
import org.teleal.cling.protocol.async.SendingNotificationAlive;
import org.teleal.cling.protocol.async.SendingSearch;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryImpl;
import org.teleal.cling.registry.RegistryMaintainer;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.impl.NetworkAddressFactoryImpl;
import org.teleal.cling.transport.spi.NetworkAddressFactory;
import org.teleal.cling.transport.spi.StreamClient;
import org.teleal.cling.transport.spi.UpnpStream;

/* loaded from: classes.dex */
public class MockUpnpService implements UpnpService {
    private List<byte[]> broadcastedBytes;
    protected final UpnpServiceConfiguration configuration;
    protected final ControlPoint controlPoint;
    private List<IncomingDatagramMessage> incomingDatagramMessages;
    protected final NetworkAddressFactory networkAddressFactory;
    private List<OutgoingDatagramMessage> outgoingDatagramMessages;
    protected final ProtocolFactory protocolFactory;
    private List<UpnpStream> receivedUpnpStreams;
    protected final Registry registry;
    protected final Router router;
    private List<StreamRequestMessage> sentStreamRequestMessages;

    public MockUpnpService() {
        this(false, false, false);
    }

    public MockUpnpService(boolean sendsAlive, boolean maintainsRegistry) {
        this(sendsAlive, maintainsRegistry, false);
    }

    public MockUpnpService(boolean sendsAlive, final boolean maintainsRegistry, final boolean multiThreaded) {
        this.incomingDatagramMessages = new ArrayList();
        this.outgoingDatagramMessages = new ArrayList();
        this.receivedUpnpStreams = new ArrayList();
        this.sentStreamRequestMessages = new ArrayList();
        this.broadcastedBytes = new ArrayList();
        this.configuration = new DefaultUpnpServiceConfiguration(false) { // from class: org.teleal.cling.mock.MockUpnpService.1
            @Override // org.teleal.cling.DefaultUpnpServiceConfiguration
            protected NetworkAddressFactory createNetworkAddressFactory(int streamListenPort) {
                return new NetworkAddressFactoryImpl(streamListenPort) { // from class: org.teleal.cling.mock.MockUpnpService.1.1
                    @Override // org.teleal.cling.transport.impl.NetworkAddressFactoryImpl
                    protected boolean isUsableNetworkInterface(NetworkInterface iface) throws Exception {
                        return iface.isLoopback();
                    }

                    @Override // org.teleal.cling.transport.impl.NetworkAddressFactoryImpl
                    protected boolean isUsableAddress(NetworkInterface networkInterface, InetAddress address) {
                        return address.isLoopbackAddress() && (address instanceof Inet4Address);
                    }
                };
            }

            @Override // org.teleal.cling.DefaultUpnpServiceConfiguration, org.teleal.cling.UpnpServiceConfiguration
            public Executor getRegistryMaintainerExecutor() {
                return maintainsRegistry ? new Executor() { // from class: org.teleal.cling.mock.MockUpnpService.1.2
                    @Override // java.util.concurrent.Executor
                    public void execute(Runnable runnable) {
                        new Thread(runnable).start();
                    }
                } : createDefaultExecutor();
            }

            @Override // org.teleal.cling.DefaultUpnpServiceConfiguration
            protected Executor createDefaultExecutor() {
                if (multiThreaded) {
                    return super.createDefaultExecutor();
                }
                return new Executor() { // from class: org.teleal.cling.mock.MockUpnpService.1.3
                    @Override // java.util.concurrent.Executor
                    public void execute(Runnable runnable) {
                        runnable.run();
                    }
                };
            }
        };
        this.protocolFactory = createProtocolFactory(this, sendsAlive);
        this.registry = new RegistryImpl(this) { // from class: org.teleal.cling.mock.MockUpnpService.2
            @Override // org.teleal.cling.registry.RegistryImpl
            protected RegistryMaintainer createRegistryMaintainer() {
                if (maintainsRegistry) {
                    return super.createRegistryMaintainer();
                }
                return null;
            }
        };
        this.networkAddressFactory = this.configuration.createNetworkAddressFactory();
        this.router = createRouter();
        this.controlPoint = new ControlPointImpl(this.configuration, this.protocolFactory, this.registry);
    }

    protected ProtocolFactory createProtocolFactory(UpnpService service, boolean sendsAlive) {
        return new MockProtocolFactory(service, sendsAlive);
    }

    protected Router createRouter() {
        return new MockRouter();
    }

    public static class MockProtocolFactory extends ProtocolFactoryImpl {
        private boolean sendsAlive;

        public MockProtocolFactory(UpnpService upnpService, boolean sendsAlive) {
            super(upnpService);
            this.sendsAlive = sendsAlive;
        }

        @Override // org.teleal.cling.protocol.ProtocolFactoryImpl, org.teleal.cling.protocol.ProtocolFactory
        public SendingNotificationAlive createSendingNotificationAlive(LocalDevice localDevice) {
            return new SendingNotificationAlive(getUpnpService(), localDevice) { // from class: org.teleal.cling.mock.MockUpnpService.MockProtocolFactory.1
                @Override // org.teleal.cling.protocol.async.SendingNotificationAlive, org.teleal.cling.protocol.async.SendingNotification, org.teleal.cling.protocol.SendingAsync
                protected void execute() throws InterruptedException {
                    if (MockProtocolFactory.this.sendsAlive) {
                        super.execute();
                    }
                }
            };
        }

        @Override // org.teleal.cling.protocol.ProtocolFactoryImpl, org.teleal.cling.protocol.ProtocolFactory
        public SendingSearch createSendingSearch(UpnpHeader searchTarget, int mxSeconds) {
            return new SendingSearch(getUpnpService(), searchTarget, mxSeconds) { // from class: org.teleal.cling.mock.MockUpnpService.MockProtocolFactory.2
                @Override // org.teleal.cling.protocol.async.SendingSearch
                public int getBulkIntervalMilliseconds() {
                    return 0;
                }
            };
        }
    }

    public class MockRouter implements Router {
        int counter = -1;

        public MockRouter() {
        }

        @Override // org.teleal.cling.transport.Router
        public UpnpServiceConfiguration getConfiguration() {
            return MockUpnpService.this.configuration;
        }

        @Override // org.teleal.cling.transport.Router
        public ProtocolFactory getProtocolFactory() {
            return MockUpnpService.this.protocolFactory;
        }

        public StreamClient getStreamClient() {
            return null;
        }

        @Override // org.teleal.cling.transport.Router
        public NetworkAddressFactory getNetworkAddressFactory() {
            return MockUpnpService.this.networkAddressFactory;
        }

        @Override // org.teleal.cling.transport.Router
        public List<NetworkAddress> getActiveStreamServers(InetAddress preferredAddress) {
            try {
                return Arrays.asList(new NetworkAddress(InetAddress.getByName(HttpServerUtil.LOOP), 0));
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override // org.teleal.cling.transport.Router
        public void shutdown() {
        }

        @Override // org.teleal.cling.transport.Router
        public void received(IncomingDatagramMessage msg) {
            MockUpnpService.this.incomingDatagramMessages.add(msg);
        }

        @Override // org.teleal.cling.transport.Router
        public void received(UpnpStream stream) {
            MockUpnpService.this.receivedUpnpStreams.add(stream);
        }

        @Override // org.teleal.cling.transport.Router
        public void send(OutgoingDatagramMessage msg) {
            MockUpnpService.this.outgoingDatagramMessages.add(msg);
        }

        @Override // org.teleal.cling.transport.Router
        public StreamResponseMessage send(StreamRequestMessage msg) {
            MockUpnpService.this.sentStreamRequestMessages.add(msg);
            this.counter++;
            if (MockUpnpService.this.getStreamResponseMessages() != null) {
                return MockUpnpService.this.getStreamResponseMessages()[this.counter];
            }
            return MockUpnpService.this.getStreamResponseMessage(msg);
        }

        @Override // org.teleal.cling.transport.Router
        public void broadcast(byte[] bytes) {
            MockUpnpService.this.broadcastedBytes.add(bytes);
        }
    }

    @Override // org.teleal.cling.UpnpService
    public UpnpServiceConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.UpnpService
    public ControlPoint getControlPoint() {
        return this.controlPoint;
    }

    @Override // org.teleal.cling.UpnpService
    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }

    @Override // org.teleal.cling.UpnpService
    public Registry getRegistry() {
        return this.registry;
    }

    @Override // org.teleal.cling.UpnpService
    public Router getRouter() {
        return this.router;
    }

    @Override // org.teleal.cling.UpnpService
    public void shutdown() {
        getRouter().shutdown();
        getRegistry().shutdown();
        getConfiguration().shutdown();
    }

    public List<IncomingDatagramMessage> getIncomingDatagramMessages() {
        return this.incomingDatagramMessages;
    }

    public List<OutgoingDatagramMessage> getOutgoingDatagramMessages() {
        return this.outgoingDatagramMessages;
    }

    public List<UpnpStream> getReceivedUpnpStreams() {
        return this.receivedUpnpStreams;
    }

    public List<StreamRequestMessage> getSentStreamRequestMessages() {
        return this.sentStreamRequestMessages;
    }

    public List<byte[]> getBroadcastedBytes() {
        return this.broadcastedBytes;
    }

    public StreamResponseMessage[] getStreamResponseMessages() {
        return null;
    }

    public StreamResponseMessage getStreamResponseMessage(StreamRequestMessage request) {
        return null;
    }
}
