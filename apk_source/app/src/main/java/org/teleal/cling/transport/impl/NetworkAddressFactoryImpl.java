package org.teleal.cling.transport.impl;

import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.NetworkAddressFactory;
import org.teleal.common.util.OS;

/* loaded from: classes.dex */
public class NetworkAddressFactoryImpl implements NetworkAddressFactory {
    public static final int DEFAULT_TCP_HTTP_LISTEN_PORT = 0;
    private static Logger log = Logger.getLogger(NetworkAddressFactoryImpl.class.getName());
    protected List<InetAddress> bindAddresses;
    protected List<NetworkInterface> networkInterfaces;
    protected int streamListenPort;
    protected Set<String> useAddresses;
    protected Set<String> useInterfaces;

    public NetworkAddressFactoryImpl() throws InitializationException {
        this(0);
    }

    public NetworkAddressFactoryImpl(int streamListenPort) throws SocketException, InitializationException {
        this.useInterfaces = new HashSet();
        this.useAddresses = new HashSet();
        this.networkInterfaces = new ArrayList();
        this.bindAddresses = new ArrayList();
        String useInterfacesString = System.getProperty(NetworkAddressFactory.SYSTEM_PROPERTY_NET_IFACES);
        if (useInterfacesString != null) {
            String[] userInterfacesStrings = useInterfacesString.split(ClientInfo.SEPARATOR_BETWEEN_VARS);
            this.useInterfaces.addAll(Arrays.asList(userInterfacesStrings));
        }
        String useAddressesString = System.getProperty(NetworkAddressFactory.SYSTEM_PROPERTY_NET_ADDRESSES);
        if (useAddressesString != null) {
            String[] useAddressesStrings = useAddressesString.split(ClientInfo.SEPARATOR_BETWEEN_VARS);
            this.useAddresses.addAll(Arrays.asList(useAddressesStrings));
        }
        if (OS.checkForLinux()) {
            Properties props = System.getProperties();
            props.setProperty("java.net.preferIPv6Stack", "true");
            System.setProperties(props);
        }
        discoverNetworkInterfaces();
        discoverBindAddresses();
        if (this.networkInterfaces.size() == 0 || this.bindAddresses.size() == 0) {
            throw new InitializationException("Could not discover any bindable network interfaces and/or addresses");
        }
        this.streamListenPort = streamListenPort;
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
        return this.streamListenPort;
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public NetworkInterface[] getNetworkInterfaces() {
        return (NetworkInterface[]) this.networkInterfaces.toArray(new NetworkInterface[this.networkInterfaces.size()]);
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress[] getBindAddresses() {
        return (InetAddress[]) this.bindAddresses.toArray(new InetAddress[this.bindAddresses.size()]);
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public byte[] getHardwareAddress(InetAddress inetAddress) throws SocketException {
        try {
            NetworkInterface iface = NetworkInterface.getByInetAddress(inetAddress);
            if (iface != null) {
                return iface.getHardwareAddress();
            }
            return null;
        } catch (SocketException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress getBroadcastAddress(InetAddress inetAddress) {
        for (NetworkInterface iface : this.networkInterfaces) {
            for (InterfaceAddress interfaceAddress : getInterfaceAddresses(iface)) {
                if (interfaceAddress != null && interfaceAddress.getAddress().equals(inetAddress)) {
                    return interfaceAddress.getBroadcast();
                }
            }
        }
        return null;
    }

    @Override // org.teleal.cling.transport.spi.NetworkAddressFactory
    public InetAddress getLocalAddress(NetworkInterface networkInterface, boolean isIPv6, InetAddress remoteAddress) {
        InetAddress localIPInSubnet = getBindAddressInSubnetOf(remoteAddress);
        if (localIPInSubnet != null) {
            return localIPInSubnet;
        }
        log.finer("Could not find local bind address in same subnet as: " + remoteAddress.getHostAddress());
        for (InetAddress interfaceAddress : getInetAddresses(networkInterface)) {
            if (!isIPv6 || !(interfaceAddress instanceof Inet6Address)) {
                if (!isIPv6 && (interfaceAddress instanceof Inet4Address)) {
                    return interfaceAddress;
                }
            } else {
                return interfaceAddress;
            }
        }
        throw new IllegalStateException("Can't find any IPv4 or IPv6 address on interface: " + networkInterface.getDisplayName());
    }

    protected List<InterfaceAddress> getInterfaceAddresses(NetworkInterface networkInterface) {
        return networkInterface.getInterfaceAddresses();
    }

    protected List<InetAddress> getInetAddresses(NetworkInterface networkInterface) {
        return Collections.list(networkInterface.getInetAddresses());
    }

    protected InetAddress getBindAddressInSubnetOf(InetAddress inetAddress) {
        for (NetworkInterface iface : this.networkInterfaces) {
            for (InterfaceAddress ifaceAddress : getInterfaceAddresses(iface)) {
                if (this.bindAddresses.contains(ifaceAddress.getAddress()) && isInSubnet(inetAddress.getAddress(), ifaceAddress.getAddress().getAddress(), ifaceAddress.getNetworkPrefixLength())) {
                    return ifaceAddress.getAddress();
                }
            }
        }
        return null;
    }

    protected boolean isInSubnet(byte[] ip, byte[] network, short prefix) {
        if (ip.length != network.length || prefix / 8 > ip.length) {
            return false;
        }
        int i = 0;
        while (prefix >= 8 && i < ip.length) {
            if (ip[i] != network[i]) {
                return false;
            }
            i++;
            prefix = (short) (prefix - 8);
        }
        byte mask = (byte) (((1 << (8 - prefix)) - 1) ^ (-1));
        return (ip[i] & mask) == (network[i] & mask);
    }

    protected void discoverNetworkInterfaces() throws SocketException, InitializationException {
        try {
            Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            Iterator it = Collections.list(interfaceEnumeration).iterator();
            while (it.hasNext()) {
                NetworkInterface iface = (NetworkInterface) it.next();
                log.finer("Analyzing network interface: " + iface.getDisplayName());
                if (isUsableNetworkInterface(iface)) {
                    log.fine("Discovered usable network interface: " + iface.getDisplayName());
                    this.networkInterfaces.add(iface);
                } else {
                    log.finer("Ignoring non-usable network interface: " + iface.getDisplayName());
                }
            }
        } catch (Exception ex) {
            throw new InitializationException("Could not not analyze local network interfaces: " + ex, ex);
        }
    }

    protected boolean isUsableNetworkInterface(NetworkInterface iface) throws Exception {
        if (!iface.isUp()) {
            log.finer("Skipping network interface (down): " + iface.getDisplayName());
            return false;
        }
        if (getInetAddresses(iface).size() == 0) {
            log.finer("Skipping network interface without bound IP addresses: " + iface.getDisplayName());
            return false;
        }
        if (iface.getName().toLowerCase().startsWith("vmnet") || iface.getDisplayName().toLowerCase().contains("vmnet")) {
            log.finer("Skipping network interface (VMWare): " + iface.getDisplayName());
            return false;
        }
        if (iface.getName().toLowerCase().startsWith("vnic")) {
            log.finer("Skipping network interface (Parallels): " + iface.getDisplayName());
            return false;
        }
        if (iface.getName().toLowerCase().startsWith("ppp")) {
            log.finer("Skipping network interface (PPP): " + iface.getDisplayName());
            return false;
        }
        if (!iface.supportsMulticast()) {
            log.finer("Skipping network interface (no multicast support): " + iface.getDisplayName());
            return false;
        }
        if (iface.isLoopback()) {
            log.finer("Skipping network interface (ignoring loopback): " + iface.getDisplayName());
            return false;
        }
        if (this.useInterfaces.size() > 0 && !this.useInterfaces.contains(iface.getName())) {
            log.finer("Skipping unwanted network interface (-Dorg.teleal.cling.network.useInterfaces): " + iface.getName());
            return false;
        }
        return true;
    }

    protected void discoverBindAddresses() throws InitializationException {
        try {
            Iterator<NetworkInterface> it = this.networkInterfaces.iterator();
            while (it.hasNext()) {
                NetworkInterface networkInterface = it.next();
                log.finer("Discovering addresses of interface: " + networkInterface.getDisplayName());
                int usableAddresses = 0;
                for (InetAddress inetAddress : getInetAddresses(networkInterface)) {
                    if (inetAddress == null) {
                        log.warning("Network has a null address: " + networkInterface.getDisplayName());
                    } else if (isUsableAddress(networkInterface, inetAddress)) {
                        log.fine("Discovered usable network interface address: " + inetAddress.getHostAddress());
                        usableAddresses++;
                        this.bindAddresses.add(inetAddress);
                    } else {
                        log.finer("Ignoring non-usable network interface address: " + inetAddress.getHostAddress());
                    }
                }
                if (usableAddresses == 0) {
                    log.finer("Network interface has no usable addresses, removing: " + networkInterface.getDisplayName());
                    it.remove();
                }
            }
        } catch (Exception ex) {
            throw new InitializationException("Could not not analyze local network interfaces: " + ex, ex);
        }
    }

    protected boolean isUsableAddress(NetworkInterface networkInterface, InetAddress address) {
        if (!(address instanceof Inet4Address)) {
            log.finer("Skipping unsupported non-IPv4 address: " + address);
            return false;
        }
        if (address.isLoopbackAddress()) {
            log.finer("Skipping loopback address: " + address);
            return false;
        }
        if (this.useAddresses.size() > 0 && !this.useAddresses.contains(address.getHostAddress())) {
            log.finer("Skipping unwanted address: " + address);
            return false;
        }
        return true;
    }

    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        System.out.printf("Parent Info:%s\n", netint.getParent());
        System.out.printf("Display name: %s\n", netint.getDisplayName());
        System.out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        Iterator it = Collections.list(inetAddresses).iterator();
        while (it.hasNext()) {
            InetAddress inetAddress = (InetAddress) it.next();
            System.out.printf("InetAddress: %s\n", inetAddress);
        }
        List<InterfaceAddress> ias = netint.getInterfaceAddresses();
        for (InterfaceAddress ia : ias) {
            System.out.println(" Interface Address");
            System.out.println("  Address: " + ia.getAddress());
            System.out.println("  Broadcast: " + ia.getBroadcast());
            System.out.println("  Prefix length: " + ((int) ia.getNetworkPrefixLength()));
        }
        Enumeration<NetworkInterface> subIfs = netint.getSubInterfaces();
        Iterator it2 = Collections.list(subIfs).iterator();
        while (it2.hasNext()) {
            NetworkInterface subIf = (NetworkInterface) it2.next();
            System.out.printf("\tSub Interface Display name: %s\n", subIf.getDisplayName());
            System.out.printf("\tSub Interface Name: %s\n", subIf.getName());
        }
        System.out.printf("Up? %s\n", Boolean.valueOf(netint.isUp()));
        System.out.printf("Loopback? %s\n", Boolean.valueOf(netint.isLoopback()));
        System.out.printf("PointToPoint? %s\n", Boolean.valueOf(netint.isPointToPoint()));
        System.out.printf("Supports multicast? %s\n", Boolean.valueOf(netint.supportsMulticast()));
        System.out.printf("Virtual? %s\n", Boolean.valueOf(netint.isVirtual()));
        System.out.printf("Hardware address: %s\n", Arrays.toString(netint.getHardwareAddress()));
        System.out.printf("MTU: %s\n", Integer.valueOf(netint.getMTU()));
        System.out.printf("\n", new Object[0]);
    }
}
