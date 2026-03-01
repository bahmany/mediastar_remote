package org.teleal.cling;

import android.support.v7.internal.widget.ActivityChooserView;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import org.teleal.cling.binding.xml.DeviceDescriptorBinder;
import org.teleal.cling.binding.xml.ServiceDescriptorBinder;
import org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderImpl;
import org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderImpl;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.model.Namespace;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.transport.impl.DatagramIOConfigurationImpl;
import org.teleal.cling.transport.impl.DatagramIOImpl;
import org.teleal.cling.transport.impl.DatagramProcessorImpl;
import org.teleal.cling.transport.impl.GENAEventProcessorImpl;
import org.teleal.cling.transport.impl.MulticastReceiverConfigurationImpl;
import org.teleal.cling.transport.impl.MulticastReceiverImpl;
import org.teleal.cling.transport.impl.NetworkAddressFactoryImpl;
import org.teleal.cling.transport.impl.SOAPActionProcessorImpl;
import org.teleal.cling.transport.impl.StreamClientConfigurationImpl;
import org.teleal.cling.transport.impl.StreamClientImpl;
import org.teleal.cling.transport.impl.StreamServerConfigurationImpl;
import org.teleal.cling.transport.impl.StreamServerImpl;
import org.teleal.cling.transport.spi.DatagramIO;
import org.teleal.cling.transport.spi.DatagramProcessor;
import org.teleal.cling.transport.spi.GENAEventProcessor;
import org.teleal.cling.transport.spi.MulticastReceiver;
import org.teleal.cling.transport.spi.NetworkAddressFactory;
import org.teleal.cling.transport.spi.SOAPActionProcessor;
import org.teleal.cling.transport.spi.StreamClient;
import org.teleal.cling.transport.spi.StreamServer;
import org.teleal.common.util.Exceptions;

/* loaded from: classes.dex */
public class DefaultUpnpServiceConfiguration implements UpnpServiceConfiguration {
    private static Logger log = Logger.getLogger(DefaultUpnpServiceConfiguration.class.getName());
    private final DatagramProcessor datagramProcessor;
    private final Executor defaultExecutor;
    private final DeviceDescriptorBinder deviceDescriptorBinderUDA10;
    private final GENAEventProcessor genaEventProcessor;
    private final Namespace namespace;
    private final ServiceDescriptorBinder serviceDescriptorBinderUDA10;
    private final SOAPActionProcessor soapActionProcessor;
    private final int streamListenPort;

    public DefaultUpnpServiceConfiguration() {
        this(0);
    }

    public DefaultUpnpServiceConfiguration(int streamListenPort) {
        this(streamListenPort, true);
    }

    protected DefaultUpnpServiceConfiguration(boolean checkRuntime) {
        this(0, checkRuntime);
    }

    protected DefaultUpnpServiceConfiguration(int streamListenPort, boolean checkRuntime) {
        if (checkRuntime && ModelUtil.ANDROID_RUNTIME) {
            throw new Error("Unsupported runtime environment, use org.teleal.cling.android.AndroidUpnpServiceConfiguration");
        }
        this.streamListenPort = streamListenPort;
        this.defaultExecutor = createDefaultExecutor();
        this.datagramProcessor = createDatagramProcessor();
        this.soapActionProcessor = createSOAPActionProcessor();
        this.genaEventProcessor = createGENAEventProcessor();
        this.deviceDescriptorBinderUDA10 = createDeviceDescriptorBinderUDA10();
        this.serviceDescriptorBinderUDA10 = createServiceDescriptorBinderUDA10();
        this.namespace = createNamespace();
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public DatagramProcessor getDatagramProcessor() {
        return this.datagramProcessor;
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public SOAPActionProcessor getSoapActionProcessor() {
        return this.soapActionProcessor;
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public GENAEventProcessor getGenaEventProcessor() {
        return this.genaEventProcessor;
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public StreamClient createStreamClient() {
        return new StreamClientImpl(new StreamClientConfigurationImpl());
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public MulticastReceiver createMulticastReceiver(NetworkAddressFactory networkAddressFactory) {
        return new MulticastReceiverImpl(new MulticastReceiverConfigurationImpl(networkAddressFactory.getMulticastGroup(), networkAddressFactory.getMulticastPort()));
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public DatagramIO createDatagramIO(NetworkAddressFactory networkAddressFactory) {
        return new DatagramIOImpl(new DatagramIOConfigurationImpl());
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public StreamServer createStreamServer(NetworkAddressFactory networkAddressFactory) {
        return new StreamServerImpl(new StreamServerConfigurationImpl(networkAddressFactory.getStreamListenPort()));
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public Executor getMulticastReceiverExecutor() {
        return getDefaultExecutor();
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public Executor getDatagramIOExecutor() {
        return getDefaultExecutor();
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public Executor getStreamServerExecutor() {
        return getDefaultExecutor();
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
        return this.deviceDescriptorBinderUDA10;
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public ServiceDescriptorBinder getServiceDescriptorBinderUDA10() {
        return this.serviceDescriptorBinderUDA10;
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public ServiceType[] getExclusiveServiceTypes() {
        return new ServiceType[0];
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public int getRegistryMaintenanceIntervalMillis() {
        return 1000;
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public Executor getAsyncProtocolExecutor() {
        return getDefaultExecutor();
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public Executor getSyncProtocolExecutor() {
        return getDefaultExecutor();
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public Namespace getNamespace() {
        return this.namespace;
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public Executor getRegistryMaintainerExecutor() {
        return getDefaultExecutor();
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public Executor getRegistryListenerExecutor() {
        return getDefaultExecutor();
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public NetworkAddressFactory createNetworkAddressFactory() {
        return createNetworkAddressFactory(this.streamListenPort);
    }

    @Override // org.teleal.cling.UpnpServiceConfiguration
    public void shutdown() {
        if (getDefaultExecutor() instanceof ThreadPoolExecutor) {
            log.fine("Shutting down thread pool");
            ((ThreadPoolExecutor) getDefaultExecutor()).shutdown();
        }
    }

    protected NetworkAddressFactory createNetworkAddressFactory(int streamListenPort) {
        return new NetworkAddressFactoryImpl(streamListenPort);
    }

    protected DatagramProcessor createDatagramProcessor() {
        return new DatagramProcessorImpl();
    }

    protected SOAPActionProcessor createSOAPActionProcessor() {
        return new SOAPActionProcessorImpl();
    }

    protected GENAEventProcessor createGENAEventProcessor() {
        return new GENAEventProcessorImpl();
    }

    protected DeviceDescriptorBinder createDeviceDescriptorBinderUDA10() {
        return new UDA10DeviceDescriptorBinderImpl();
    }

    protected ServiceDescriptorBinder createServiceDescriptorBinderUDA10() {
        return new UDA10ServiceDescriptorBinderImpl();
    }

    protected Namespace createNamespace() {
        return new Namespace();
    }

    protected Executor getDefaultExecutor() {
        return this.defaultExecutor;
    }

    protected Executor createDefaultExecutor() {
        return new ClingExecutor();
    }

    public static class ClingExecutor extends ThreadPoolExecutor {

        /* renamed from: org.teleal.cling.DefaultUpnpServiceConfiguration$ClingExecutor$1 */
        class AnonymousClass1 extends ThreadPoolExecutor.DiscardPolicy {
            AnonymousClass1() {
            }

            @Override // java.util.concurrent.ThreadPoolExecutor.DiscardPolicy, java.util.concurrent.RejectedExecutionHandler
            public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                DefaultUpnpServiceConfiguration.log.info("Thread pool rejected execution of " + runnable.getClass());
                super.rejectedExecution(runnable, threadPoolExecutor);
            }
        }

        public ClingExecutor() {
            this(new ClingThreadFactory(), new ThreadPoolExecutor.DiscardPolicy() { // from class: org.teleal.cling.DefaultUpnpServiceConfiguration.ClingExecutor.1
                AnonymousClass1() {
                }

                @Override // java.util.concurrent.ThreadPoolExecutor.DiscardPolicy, java.util.concurrent.RejectedExecutionHandler
                public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                    DefaultUpnpServiceConfiguration.log.info("Thread pool rejected execution of " + runnable.getClass());
                    super.rejectedExecution(runnable, threadPoolExecutor);
                }
            });
        }

        public ClingExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedHandler) {
            super(0, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, 60L, TimeUnit.SECONDS, new SynchronousQueue(), threadFactory, rejectedHandler);
        }

        @Override // java.util.concurrent.ThreadPoolExecutor
        protected void afterExecute(Runnable runnable, Throwable throwable) {
            super.afterExecute(runnable, throwable);
            if (throwable != null) {
                DefaultUpnpServiceConfiguration.log.warning("Thread terminated " + runnable + " abruptly with exception: " + throwable);
                DefaultUpnpServiceConfiguration.log.warning("Root cause: " + Exceptions.unwrap(throwable));
            }
        }
    }

    public static class ClingThreadFactory implements ThreadFactory {
        protected final ThreadGroup group;
        protected final AtomicInteger threadNumber = new AtomicInteger(1);
        protected final String namePrefix = "cling-";

        public ClingThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }

        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, "cling-" + this.threadNumber.getAndIncrement(), 0L);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != 5) {
                t.setPriority(5);
            }
            return t;
        }
    }
}
