package org.teleal.cling.android;

import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.NetworkAddressFactory;

/* loaded from: classes.dex */
public class AndroidNetworkAddressFactory implements NetworkAddressFactory {
    private static final Logger log = Logger.getLogger(NetworkAddressFactory.class.getName());
    protected List<InetAddress> bindAddresses = new ArrayList();
    protected NetworkInterface wifiInterface;

    public AndroidNetworkAddressFactory(WifiManager wifiManager, ConnectivityManager connectivityManager) throws InitializationException {
        this.wifiInterface = getWifiNetworkInterface(wifiManager, connectivityManager);
        if (this.wifiInterface == null) {
            throw new InitializationException("Could not discover WiFi network interface");
        }
        log.info("Discovered WiFi network interface: " + this.wifiInterface.getDisplayName());
        discoverBindAddresses();
    }

    protected void discoverBindAddresses() throws InitializationException {
        try {
            log.finer("Discovering addresses of interface: " + this.wifiInterface.getDisplayName());
            for (InetAddress inetAddress : getInetAddresses(this.wifiInterface)) {
                if (inetAddress == null) {
                    log.warning("Network has a null address: " + this.wifiInterface.getDisplayName());
                } else if (isUsableAddress(inetAddress)) {
                    log.fine("Discovered usable network interface address: " + inetAddress.getHostAddress());
                    this.bindAddresses.add(inetAddress);
                } else {
                    log.finer("Ignoring non-usable network interface address: " + inetAddress.getHostAddress());
                }
            }
        } catch (Exception ex) {
            throw new InitializationException("Could not not analyze local network interfaces: " + ex, ex);
        }
    }

    protected boolean isUsableAddress(InetAddress address) {
        if (address instanceof Inet4Address) {
            return true;
        }
        log.finer("Skipping unsupported non-IPv4 address: " + address);
        return false;
    }

    protected List<InetAddress> getInetAddresses(NetworkInterface networkInterface) {
        return Collections.list(networkInterface.getInetAddresses());
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress getMulticastGroup() {
        try {
            return InetAddress.getByName("239.255.255.250");
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public int getMulticastPort() {
        return 1900;
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public int getStreamListenPort() {
        return 0;
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public NetworkInterface[] getNetworkInterfaces() {
        return new NetworkInterface[]{this.wifiInterface};
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress[] getBindAddresses() {
        return (InetAddress[]) this.bindAddresses.toArray(new InetAddress[this.bindAddresses.size()]);
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public byte[] getHardwareAddress(InetAddress inetAddress) {
        return null;
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress getBroadcastAddress(InetAddress inetAddress) {
        return null;
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress getLocalAddress(NetworkInterface networkInterface, boolean isIPv6, InetAddress remoteAddress) {
        for (InetAddress localAddress : getInetAddresses(networkInterface)) {
            if ((isIPv6 && (localAddress instanceof Inet6Address)) || (!isIPv6 && (localAddress instanceof Inet4Address))) {
                return localAddress;
            }
        }
        throw new IllegalStateException("Can't find any IPv4 or IPv6 address on interface: " + networkInterface.getDisplayName());
    }

    public static NetworkInterface getWifiNetworkInterface(WifiManager manager, ConnectivityManager connectivityManager) {
        return ModelUtil.ANDROID_EMULATOR ? getEmulatorWifiNetworkInterface(manager, connectivityManager) : getRealWifiNetworkInterface(manager, connectivityManager);
    }

    public static NetworkInterface getEmulatorWifiNetworkInterface(WifiManager manager, ConnectivityManager connectivityManager) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface iface : interfaces) {
                List<InetAddress> addresses = Collections.list(iface.getInetAddresses());
                for (InetAddress address : addresses) {
                    if (!address.isLoopbackAddress()) {
                        return iface;
                    }
                }
            }
            return null;
        } catch (Exception ex) {
            throw new InitializationException("Could not find emulator's network interface: " + ex, ex);
        }
    }

    public static NetworkInterface getRealWifiNetworkInterface(WifiManager wifiManager, ConnectivityManager connectivityManager) throws SocketException {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (wifiManager != null && wifiManager.isWifiEnabled() && connectivityManager.getNetworkInfo(1).isConnected()) {
                int wifiIP = wifiManager.getConnectionInfo().getIpAddress();
                int reverseWifiIP = Integer.reverseBytes(wifiIP);
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iface = interfaces.nextElement();
                    Enumeration inetAddresses = iface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        int byteArrayToInt = byteArrayToInt(inetAddresses.nextElement().getAddress(), 0);
                        if (byteArrayToInt == wifiIP || byteArrayToInt == reverseWifiIP) {
                            return iface;
                        }
                    }
                }
            }
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface2 = interfaces.nextElement();
                Enumeration inetAddresses2 = iface2.getInetAddresses();
                while (inetAddresses2.hasMoreElements()) {
                    InetAddress nextElement = inetAddresses2.nextElement();
                    String hostAddress = nextElement.getHostAddress();
                    if (!nextElement.isLoopbackAddress() && hostAddress.matches("\\d+.\\d+.\\d+.\\d+")) {
                        return iface2;
                    }
                }
            }
            return null;
        } catch (SocketException e) {
            log.info("No network interfaces available");
            return null;
        }
    }

    static int byteArrayToInt(byte[] arr, int offset) {
        if (arr == null || arr.length - offset < 4) {
            return -1;
        }
        int r0 = (arr[offset] & 255) << 24;
        int r1 = (arr[offset + 1] & 255) << 16;
        int r2 = (arr[offset + 2] & 255) << 8;
        int r3 = arr[offset + 3] & 255;
        return r0 + r1 + r2 + r3;
    }
}
