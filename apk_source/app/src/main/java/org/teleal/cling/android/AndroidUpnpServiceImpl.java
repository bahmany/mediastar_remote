package org.teleal.cling.android;

import android.app.Service;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.registry.RegistryListener;

/* loaded from: classes.dex */
public abstract class AndroidUpnpServiceImpl extends Service {
    protected volatile boolean isInitialized = false;
    private PowerManager.WakeLock serviceWakeLock;
    protected volatile UpnpService upnpService;

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        PowerManager mgr = (PowerManager) getSystemService("power");
        this.serviceWakeLock = mgr.newWakeLock(1, getClass().getCanonicalName());
        this.serviceWakeLock.acquire();
        initializedUpnpService();
    }

    protected final void initializedUpnpService() {
        this.isInitialized = false;
        WifiManager wifiManager = (WifiManager) getSystemService("wifi");
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        try {
            this.upnpService = new UpnpServiceImpl(createConfiguration(wifiManager, connectivityManager), new RegistryListener[0]);
            this.isInitialized = true;
        } catch (Exception ex) {
            this.isInitialized = false;
            ex.printStackTrace();
        }
    }

    protected AndroidUpnpServiceConfiguration createConfiguration(WifiManager wifiManager, ConnectivityManager connectivityManager) {
        return new AndroidUpnpServiceConfiguration(wifiManager, connectivityManager);
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.serviceWakeLock.release();
        this.upnpService.shutdown();
    }
}
