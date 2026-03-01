package org.teleal.cling.android;

import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.teleal.cling.DefaultUpnpServiceConfiguration;
import org.teleal.cling.binding.xml.DeviceDescriptorBinder;
import org.teleal.cling.binding.xml.ServiceDescriptorBinder;
import org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl;
import org.teleal.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl;
import org.teleal.cling.transport.impl.apache.StreamClientConfigurationImpl;
import org.teleal.cling.transport.impl.apache.StreamClientImpl;
import org.teleal.cling.transport.impl.apache.StreamServerConfigurationImpl;
import org.teleal.cling.transport.impl.apache.StreamServerImpl;
import org.teleal.cling.transport.spi.NetworkAddressFactory;
import org.teleal.cling.transport.spi.StreamClient;
import org.teleal.cling.transport.spi.StreamServer;

/* loaded from: classes.dex */
public class AndroidUpnpServiceConfiguration extends DefaultUpnpServiceConfiguration {
    private static final Logger log = Logger.getLogger(AndroidUpnpServiceConfiguration.class.getName());
    protected final ConnectivityManager connectivityManager;
    protected final WifiManager wifiManager;

    public AndroidUpnpServiceConfiguration(WifiManager wifiManager, ConnectivityManager connectivityManager) {
        this(wifiManager, connectivityManager, 0);
    }

    public AndroidUpnpServiceConfiguration(WifiManager wifiManager, ConnectivityManager connectivityManager, int streamListenPort) {
        super(streamListenPort, false);
        this.wifiManager = wifiManager;
        this.connectivityManager = connectivityManager;
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
    }

    @Override // org.teleal.cling.DefaultUpnpServiceConfiguration
    protected NetworkAddressFactory createNetworkAddressFactory(int streamListenPort) {
        return new AndroidNetworkAddressFactory(this.wifiManager, this.connectivityManager);
    }

    @Override // org.teleal.cling.DefaultUpnpServiceConfiguration, org.teleal.cling.UpnpServiceConfiguration
    public StreamServer createStreamServer(NetworkAddressFactory networkAddressFactory) {
        StreamServerConfigurationImpl streamServerConfiguration = new StreamServerConfigurationImpl(networkAddressFactory.getStreamListenPort());
        return new StreamServerImpl(streamServerConfiguration);
    }

    @Override // org.teleal.cling.DefaultUpnpServiceConfiguration, org.teleal.cling.UpnpServiceConfiguration
    public StreamClient createStreamClient() {
        return new StreamClientImpl(new StreamClientConfigurationImpl() { // from class: org.teleal.cling.android.AndroidUpnpServiceConfiguration.1
            @Override // org.teleal.cling.transport.impl.apache.StreamClientConfigurationImpl
            public int getConnectionTimeoutSeconds() {
                return 2;
            }

            @Override // org.teleal.cling.transport.impl.apache.StreamClientConfigurationImpl
            public int getDataReadTimeoutSeconds() {
                return 5;
            }

            @Override // org.teleal.cling.transport.impl.apache.StreamClientConfigurationImpl
            public boolean getStaleCheckingEnabled() {
                return false;
            }

            @Override // org.teleal.cling.transport.impl.apache.StreamClientConfigurationImpl
            public int getRequestRetryCount() {
                return 1;
            }
        });
    }

    @Override // org.teleal.cling.DefaultUpnpServiceConfiguration
    protected DeviceDescriptorBinder createDeviceDescriptorBinderUDA10() {
        return new UDA10DeviceDescriptorBinderSAXImpl();
    }

    @Override // org.teleal.cling.DefaultUpnpServiceConfiguration
    protected ServiceDescriptorBinder createServiceDescriptorBinderUDA10() {
        return new UDA10ServiceDescriptorBinderSAXImpl();
    }

    @Override // org.teleal.cling.DefaultUpnpServiceConfiguration, org.teleal.cling.UpnpServiceConfiguration
    public int getRegistryMaintenanceIntervalMillis() {
        return 3000;
    }

    @Override // org.teleal.cling.DefaultUpnpServiceConfiguration
    protected Executor createDefaultExecutor() {
        ThreadPoolExecutor defaultExecutor = new ThreadPoolExecutor(8, 16, 5L, TimeUnit.SECONDS, new ArrayBlockingQueue(512)) { // from class: org.teleal.cling.android.AndroidUpnpServiceConfiguration.2
            @Override // java.util.concurrent.ThreadPoolExecutor
            protected void beforeExecute(Thread thread, Runnable runnable) {
                super.beforeExecute(thread, runnable);
                thread.setName("Thread " + thread.getId() + " (Active: " + getActiveCount() + ")");
            }
        };
        defaultExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy() { // from class: org.teleal.cling.android.AndroidUpnpServiceConfiguration.3
            @Override // java.util.concurrent.ThreadPoolExecutor.DiscardPolicy, java.util.concurrent.RejectedExecutionHandler
            public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                AndroidUpnpServiceConfiguration.log.warning("Thread pool saturated, discarding execution of '" + runnable.getClass() + "', consider raising the maximum pool or queue size");
                super.rejectedExecution(runnable, threadPoolExecutor);
            }
        });
        return defaultExecutor;
    }
}
