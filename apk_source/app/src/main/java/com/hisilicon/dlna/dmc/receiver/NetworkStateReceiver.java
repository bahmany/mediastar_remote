package com.hisilicon.dlna.dmc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServerUtil;
import java.net.NetworkInterface;
import org.teleal.cling.android.AndroidNetworkAddressFactory;
import org.teleal.cling.transport.SwitchableRouter;

/* loaded from: classes.dex */
public class NetworkStateReceiver extends BroadcastReceiver {
    private boolean m_disableWifiPending;
    private SwitchableRouter m_router;
    private RouterStateListener m_routerStateListener;
    protected int DISABLE_STATE_TIMEOUT = 10;
    private NetworkInterface m_interfaceCache = null;

    public interface RouterStateListener {
        void onNetworkChanged(NetworkInterface networkInterface);

        void onRouterDisabled();

        void onRouterEnabled();

        void onRouterError(String str);
    }

    public NetworkStateReceiver(SwitchableRouter router, RouterStateListener routerStateListener) {
        this.m_router = router;
        this.m_routerStateListener = routerStateListener;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.TETHER_STATE_CHANGED") || intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE") || intent.getAction().equals("android.net.wifi.WIFI_AP_STATE_CHANGED")) {
            NetworkInterface ni = AndroidNetworkAddressFactory.getWifiNetworkInterface((WifiManager) context.getSystemService("wifi"), (ConnectivityManager) context.getSystemService("connectivity"));
            HttpServerUtil.updateHostAddress(ni);
            if (ni == null) {
                this.m_router.disable();
                this.m_routerStateListener.onRouterDisabled();
                this.m_disableWifiPending = true;
                new Thread(new Runnable() { // from class: com.hisilicon.dlna.dmc.receiver.NetworkStateReceiver.1
                    @Override // java.lang.Runnable
                    public void run() throws InterruptedException {
                        for (int i = 0; i < NetworkStateReceiver.this.DISABLE_STATE_TIMEOUT; i++) {
                            try {
                                Thread.sleep(1000L);
                                if (NetworkStateReceiver.this.m_disableWifiPending) {
                                    if (i == NetworkStateReceiver.this.DISABLE_STATE_TIMEOUT - 1 && NetworkStateReceiver.this.m_routerStateListener != null) {
                                        NetworkStateReceiver.this.m_routerStateListener.onRouterError("no network found");
                                    }
                                } else {
                                    return;
                                }
                            } catch (InterruptedException e) {
                                return;
                            }
                        }
                    }
                }).start();
                return;
            }
            this.m_router.enable();
            this.m_routerStateListener.onRouterEnabled();
            if (this.m_interfaceCache == null || !ni.equals(this.m_interfaceCache)) {
                this.m_routerStateListener.onNetworkChanged(ni);
            }
            this.m_interfaceCache = ni;
            this.m_disableWifiPending = false;
        }
    }
}
