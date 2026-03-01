package com.hisilicon.dlna.dmc.processor.upnp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Process;
import com.hisilicon.dlna.dmc.processor.impl.DMSProcessorImpl;
import com.hisilicon.dlna.dmc.processor.impl.LocalDMRProcessorImpl;
import com.hisilicon.dlna.dmc.processor.impl.LocalDMSProcessorImpl;
import com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl;
import com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor;
import com.hisilicon.dlna.dmc.processor.interfaces.DMSProcessor;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.ContentNode;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.ContentTree;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.MediaInitException;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.MediaServer;
import com.hisilicon.dlna.dmc.receiver.NetworkStateReceiver;
import com.hisilicon.dlna.dmc.utility.GlobalCache;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.android.AndroidUpnpServiceConfiguration;
import org.teleal.cling.android.AndroidWifiSwitchableRouter;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.transport.Router;

/* loaded from: classes.dex */
public class CoreUpnpService extends Service {
    public static final String AIRSHARE_EXIT = "AIRSHARE_EXIT";
    public static final String PLAYLIST_CHANGED = "PLAYLIST_CHANGED";
    private static MediaServer mediaServer;
    private ConnectivityManager connectivityManager;
    private Device currentDMR;
    private Device currentDMS;
    private List<DMRProcessor.DMRProcessorListener> dmrListeners;
    private DMRProcessor dmrProcessor;
    private DMSProcessor dmsProcessor;
    private volatile boolean isInitialized;
    private NetworkStateReceiver networkReceiver;
    private RegistryListener registryListener;
    private PowerManager.WakeLock serviceWakeLock;
    private UpnpService upnpService;
    private CoreUpnpServiceListener upnpServiceListener;
    private WifiManager.WifiLock wifiLock;
    private WifiManager wifiManager;
    private static boolean mediaPrepared = false;
    private static boolean serverPrepared = false;
    private static boolean isBusySharingContent = false;
    private static boolean isAirShare = false;
    private UDN m_localDMS_UDN = null;
    private UDN m_localDMR_UDN = null;
    private CoreUpnpServiceBinder upnpBinder = new CoreUpnpServiceBinder();
    private BroadcastReceiver changedReceiver = new BroadcastReceiver() { // from class: com.hisilicon.dlna.dmc.processor.upnp.CoreUpnpService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CoreUpnpService.AIRSHARE_EXIT)) {
                CoreUpnpService.this.onDestroy();
                return;
            }
            ContentNode node = ContentTree.getNode(ContentTree.PLAYLIST_ID);
            if (node != null) {
                Container playlistContainer = node.getContainer();
                playlistContainer.getContainers().clear();
                playlistContainer.getItems().clear();
                playlistContainer.setChildCount(0);
                ContentTree.initPlaylist();
            }
        }
    };

    public interface CoreUpnpServiceListener {
        void onNetworkChanged(NetworkInterface networkInterface);

        void onRouterDisabled();

        void onRouterEnabled();

        void onRouterError(String str);
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.isInitialized = false;
        this.dmrListeners = new ArrayList();
        PowerManager mgr = (PowerManager) getSystemService("power");
        this.serviceWakeLock = mgr.newWakeLock(1, getClass().getCanonicalName());
        this.serviceWakeLock.acquire();
        this.wifiManager = (WifiManager) getSystemService("wifi");
        this.connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        initializedUpnpService();
    }

    private void initializedUpnpService() {
        try {
            this.upnpService = new UpnpServiceImpl(createConfiguration(this.wifiManager), new RegistryListener[0]) { // from class: com.hisilicon.dlna.dmc.processor.upnp.CoreUpnpService.2
                @Override // org.teleal.cling.UpnpServiceImpl
                protected Router createRouter(ProtocolFactory protocolFactory, Registry registry) {
                    AndroidWifiSwitchableRouter router = CoreUpnpService.this.createRouter(getConfiguration(), protocolFactory, CoreUpnpService.this.wifiManager, CoreUpnpService.this.connectivityManager);
                    CoreUpnpService.this.networkReceiver = new NetworkStateReceiver(router, new NetworkStateReceiver.RouterStateListener() { // from class: com.hisilicon.dlna.dmc.processor.upnp.CoreUpnpService.2.1
                        @Override // com.hisilicon.dlna.dmc.receiver.NetworkStateReceiver.RouterStateListener
                        public void onRouterError(String cause) {
                            if (CoreUpnpService.this.upnpServiceListener != null) {
                                CoreUpnpService.this.upnpServiceListener.onRouterError("No network found");
                            }
                        }

                        @Override // com.hisilicon.dlna.dmc.receiver.NetworkStateReceiver.RouterStateListener
                        public void onNetworkChanged(NetworkInterface ni) {
                            try {
                                CoreUpnpService.this.changedNetWork();
                            } catch (MediaInitException e) {
                                e.printStackTrace();
                            }
                            if (CoreUpnpService.this.upnpServiceListener != null) {
                                CoreUpnpService.this.upnpServiceListener.onNetworkChanged(ni);
                            }
                        }

                        @Override // com.hisilicon.dlna.dmc.receiver.NetworkStateReceiver.RouterStateListener
                        public void onRouterEnabled() {
                            if (CoreUpnpService.this.upnpServiceListener != null) {
                                CoreUpnpService.this.upnpServiceListener.onRouterEnabled();
                            }
                        }

                        @Override // com.hisilicon.dlna.dmc.receiver.NetworkStateReceiver.RouterStateListener
                        public void onRouterDisabled() {
                            if (CoreUpnpService.this.upnpServiceListener != null) {
                                CoreUpnpService.this.upnpServiceListener.onRouterDisabled();
                            }
                        }
                    });
                    if (!ModelUtil.ANDROID_EMULATOR) {
                        IntentFilter filter = new IntentFilter();
                        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                        filter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
                        CoreUpnpService.this.registerReceiver(CoreUpnpService.this.networkReceiver, filter);
                    }
                    return router;
                }
            };
            this.isInitialized = true;
        } catch (Exception e) {
            this.isInitialized = false;
        }
        if (this.isInitialized) {
            this.wifiLock = this.wifiManager.createWifiLock(3, "UpnpWifiLock");
            this.wifiLock.acquire();
            startLocalDMS();
            this.dmrListeners.clear();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(AIRSHARE_EXIT);
        filter.addAction(PLAYLIST_CHANGED);
        registerReceiver(this.changedReceiver, filter);
    }

    protected AndroidUpnpServiceConfiguration createConfiguration(WifiManager wifiManager) {
        return new AndroidUpnpServiceConfiguration(wifiManager, this.connectivityManager) { // from class: com.hisilicon.dlna.dmc.processor.upnp.CoreUpnpService.3
            @Override // org.teleal.cling.DefaultUpnpServiceConfiguration, org.teleal.cling.UpnpServiceConfiguration
            public ServiceType[] getExclusiveServiceTypes() {
                return new ServiceType[]{new UDAServiceType("AVTransport"), new UDAServiceType("ContentDirectory"), new UDAServiceType("RenderingControl")};
            }
        };
    }

    protected AndroidWifiSwitchableRouter createRouter(UpnpServiceConfiguration configuration, ProtocolFactory protocolFactory, WifiManager wifiManager, ConnectivityManager connectivityManager) {
        return new AndroidWifiSwitchableRouter(configuration, protocolFactory, wifiManager, connectivityManager);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changedNetWork() throws MediaInitException {
        if (isAirShare) {
            serverPrepared = false;
            ContentTree.clear();
            prepareShareContent();
        }
    }

    /* JADX WARN: Type inference failed for: r0v6, types: [com.hisilicon.dlna.dmc.processor.upnp.CoreUpnpService$4] */
    @Override // android.app.Service
    public void onDestroy() {
        try {
            this.serviceWakeLock.release();
            unregisterReceiver();
            if (this.dmsProcessor != null) {
                this.dmsProcessor.dispose();
            }
            if (this.dmrProcessor != null) {
                this.dmrProcessor.dispose();
            }
            releaseWifiLock();
            GlobalCache.clear();
            if (this.upnpService != null) {
                new AsyncTask<Void, Void, Void>() { // from class: com.hisilicon.dlna.dmc.processor.upnp.CoreUpnpService.4
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Void doInBackground(Void... params) {
                        try {
                            CoreUpnpService.this.upnpService.getRegistry().removeAllLocalDevices();
                            CoreUpnpService.this.upnpService.getRegistry().removeAllRemoteDevices();
                            CoreUpnpService.this.upnpService.getRegistry().removeListener(CoreUpnpService.this.registryListener);
                            CoreUpnpService.this.upnpService.shutdown();
                            CoreUpnpService.this.upnpService = null;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public void onPostExecute(Void result) {
                    }
                }.execute(new Void[0]);
            }
        } finally {
            Process.killProcess(Process.myPid());
        }
    }

    private void unregisterReceiver() {
        try {
            if (this.networkReceiver != null) {
                unregisterReceiver(this.networkReceiver);
            }
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(this.changedReceiver);
        } catch (Exception e2) {
        }
    }

    private void releaseWifiLock() {
        if (this.wifiLock != null) {
            try {
                this.wifiLock.release();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.upnpBinder;
    }

    public class CoreUpnpServiceBinder extends Binder {
        public CoreUpnpServiceBinder() {
        }

        public boolean isInitialized() {
            return CoreUpnpService.this.isInitialized;
        }

        public DMSProcessor getDMSProcessor() {
            return CoreUpnpService.this.dmsProcessor;
        }

        public DMRProcessor getDMRProcessor() {
            return CoreUpnpService.this.dmrProcessor;
        }

        public void setCurrentDMS(UDN uDN) {
            CoreUpnpService.this.m_localDMS_UDN = uDN;
            CoreUpnpService.this.dmsProcessor = null;
            CoreUpnpService.this.currentDMS = CoreUpnpService.this.upnpService.getRegistry().getDevice(uDN, true);
            if (!(CoreUpnpService.this.currentDMS instanceof RemoteDevice)) {
                if (!(CoreUpnpService.this.currentDMS instanceof LocalDevice)) {
                    CoreUpnpService.this.dmsProcessor = null;
                    return;
                } else {
                    CoreUpnpService.this.dmsProcessor = new LocalDMSProcessorImpl();
                    return;
                }
            }
            CoreUpnpService.this.dmsProcessor = new DMSProcessorImpl(CoreUpnpService.this.currentDMS, getControlPoint());
        }

        public void setLiveTV(UDN uDN) {
            Device liveTVDevice = CoreUpnpService.this.upnpService.getRegistry().getDevice(uDN, true);
            if (!(liveTVDevice instanceof RemoteDevice)) {
                CoreUpnpService.this.dmsProcessor = null;
            } else {
                CoreUpnpService.this.dmsProcessor = new DMSProcessorImpl(liveTVDevice, getControlPoint());
            }
        }

        public void stbOnline(UDN uDN) {
            CoreUpnpService.this.m_localDMR_UDN = uDN;
            CoreUpnpService.this.currentDMR = CoreUpnpService.this.upnpService.getRegistry().getDevice(uDN, true);
        }

        public void setCurrentDMR(UDN uDN) {
            CoreUpnpService.this.m_localDMR_UDN = uDN;
            if (CoreUpnpService.this.dmrProcessor != null) {
                CoreUpnpService.this.dmrProcessor.dispose();
            }
            if (uDN == null) {
                CoreUpnpService.this.dmrProcessor = new LocalDMRProcessorImpl(CoreUpnpService.this);
                synchronized (CoreUpnpService.this.dmrListeners) {
                    for (DMRProcessor.DMRProcessorListener listener : CoreUpnpService.this.dmrListeners) {
                        CoreUpnpService.this.dmrProcessor.addListener(listener);
                    }
                }
                return;
            }
            CoreUpnpService.this.currentDMR = CoreUpnpService.this.upnpService.getRegistry().getDevice(uDN, true);
            if (CoreUpnpService.this.dmrProcessor != null && (CoreUpnpService.this.currentDMR instanceof RemoteDevice)) {
                CoreUpnpService.this.dmrProcessor = new RemoteDMRProcessorImpl(CoreUpnpService.this.currentDMR, getControlPoint());
                synchronized (CoreUpnpService.this.dmrListeners) {
                    for (DMRProcessor.DMRProcessorListener listener2 : CoreUpnpService.this.dmrListeners) {
                        CoreUpnpService.this.dmrProcessor.addListener(listener2);
                    }
                }
                return;
            }
            CoreUpnpService.this.dmrProcessor = new LocalDMRProcessorImpl(CoreUpnpService.this);
            synchronized (CoreUpnpService.this.dmrListeners) {
                for (DMRProcessor.DMRProcessorListener listener3 : CoreUpnpService.this.dmrListeners) {
                    CoreUpnpService.this.dmrProcessor.addListener(listener3);
                }
            }
        }

        public Device getCurrentDMS() {
            return CoreUpnpService.this.currentDMS;
        }

        public Device getCurrentDMR() {
            return CoreUpnpService.this.currentDMR;
        }

        public UpnpService get() {
            return CoreUpnpService.this.upnpService;
        }

        public UpnpServiceConfiguration getConfiguration() {
            if (CoreUpnpService.this.upnpService != null) {
                return CoreUpnpService.this.upnpService.getConfiguration();
            }
            return null;
        }

        public Registry getRegistry() {
            if (CoreUpnpService.this.upnpService != null) {
                return CoreUpnpService.this.upnpService.getRegistry();
            }
            return null;
        }

        public ControlPoint getControlPoint() {
            if (CoreUpnpService.this.upnpService != null) {
                return CoreUpnpService.this.upnpService.getControlPoint();
            }
            return null;
        }

        public void setProcessor(CoreUpnpServiceListener listener) {
            CoreUpnpService.this.upnpServiceListener = listener;
        }

        public void addRegistryListener(RegistryListener listener) {
            CoreUpnpService.this.registryListener = listener;
            CoreUpnpService.this.upnpService.getRegistry().addListener(listener);
        }

        public void setDMSExported(boolean value) {
        }

        public void addDMRListener(DMRProcessor.DMRProcessorListener listener) {
            synchronized (CoreUpnpService.this.dmrListeners) {
                if (!CoreUpnpService.this.dmrListeners.contains(listener)) {
                    CoreUpnpService.this.dmrListeners.add(listener);
                }
                if (CoreUpnpService.this.dmrProcessor != null) {
                    CoreUpnpService.this.dmrProcessor.addListener(listener);
                }
            }
        }

        public void removeDMRListener(DMRProcessor.DMRProcessorListener listener) {
            synchronized (CoreUpnpService.this.dmrListeners) {
                CoreUpnpService.this.dmrListeners.remove(listener);
                if (CoreUpnpService.this.dmrProcessor != null) {
                    CoreUpnpService.this.dmrProcessor.removeListener(listener);
                }
            }
        }

        public void updateOrCreateShareContent() throws MediaInitException {
            CoreUpnpService.this.prepareShareContent();
            CoreUpnpService.isAirShare = true;
        }

        public void updateOrCreateShareAMContent() throws MediaInitException {
            CoreUpnpService.this.preareShareAMContent();
            CoreUpnpService.isAirShare = true;
        }

        public void stopLoading() {
            ContentTree.stopLoading();
        }
    }

    private void startLocalDMS() {
        try {
            mediaServer = new MediaServer(this);
            Registry localRegistry = this.upnpService.getRegistry();
            LocalDevice localLocalDevice = mediaServer.getDevice();
            localRegistry.addDevice(localLocalDevice);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void prepareShareContent() throws MediaInitException {
        if (!isBusySharingContent) {
            prepareMediaServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void preareShareAMContent() throws MediaInitException {
        if (!isBusySharingContent) {
            prepareAMMediaServer();
        }
    }

    class RefreshContentThread extends Thread {
        RefreshContentThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                if (CoreUpnpService.isBusySharingContent) {
                    return;
                }
                CoreUpnpService.this.prepareMediaServer();
            } catch (MediaInitException e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void prepareMediaServer() throws MediaInitException {
        isBusySharingContent = true;
        try {
            try {
                try {
                    if (serverPrepared) {
                        isBusySharingContent = false;
                    } else {
                        ContentTree.clear();
                        System.out.println("Come into initPlaylist--><");
                        ContentTree.initPlaylist();
                        if (mediaPrepared) {
                            System.out.println("Come into media not Prepared--><");
                            ContentTree.createPhotosAlbumsDir();
                            ContentTree.createVideosAlbumsDir();
                            ContentTree.createAudiosAlbumsDir();
                        } else {
                            System.out.println("Come into mediaPrepared--><");
                            ContentTree.initAllMedia();
                            mediaPrepared = true;
                        }
                        serverPrepared = true;
                        isBusySharingContent = false;
                    }
                } catch (MediaInitException ex) {
                    ex.printStackTrace();
                    throw new MediaInitException(ex);
                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
                isBusySharingContent = false;
            }
        } catch (Throwable th) {
            isBusySharingContent = false;
            throw th;
        }
    }

    private synchronized void prepareAMMediaServer() throws MediaInitException {
        isBusySharingContent = true;
        try {
            try {
                System.out.println("The serverPrepared is:" + serverPrepared);
                if (serverPrepared) {
                    isBusySharingContent = false;
                } else {
                    ContentTree.clear();
                    System.out.println("Come into mediaPrepared1--><");
                    ContentTree.initPlaylist();
                    if (mediaPrepared) {
                        System.out.println("Come into prepareAMMediaServer--><");
                        ContentTree.createPhotosAlbumsDir();
                        ContentTree.createVideosAlbumsDir();
                        ContentTree.createAudiosAlbumsDir();
                    } else {
                        System.out.println("Come into mediaPrepared2--><");
                        ContentTree.initAudioAndMusic();
                        mediaPrepared = true;
                    }
                    System.out.println("Come into prepareAMMediaServer end--><");
                    serverPrepared = true;
                    isBusySharingContent = false;
                }
            } catch (MediaInitException ex) {
                ex.printStackTrace();
                throw new MediaInitException(ex);
            } catch (Exception ex2) {
                ex2.printStackTrace();
                isBusySharingContent = false;
            }
        } catch (Throwable th) {
            isBusySharingContent = false;
            throw th;
        }
    }
}
