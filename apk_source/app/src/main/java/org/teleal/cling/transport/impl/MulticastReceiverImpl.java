package org.teleal.cling.transport.impl;

import java.net.DatagramPacket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.logging.Logger;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.spi.DatagramProcessor;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.MulticastReceiver;
import org.teleal.cling.transport.spi.UnsupportedDataException;

/* loaded from: classes.dex */
public class MulticastReceiverImpl implements MulticastReceiver<MulticastReceiverConfigurationImpl> {
    private static Logger log = Logger.getLogger(MulticastReceiver.class.getName());
    protected final MulticastReceiverConfigurationImpl configuration;
    protected DatagramProcessor datagramProcessor;
    protected InetSocketAddress multicastAddress;
    protected NetworkInterface multicastInterface;
    protected Router router;
    private MulticastSocket socket;

    public MulticastReceiverImpl(MulticastReceiverConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    @Override // org.teleal.cling.transport.spi.MulticastReceiver
    public MulticastReceiverConfigurationImpl getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.transport.spi.MulticastReceiver
    public synchronized void init(NetworkInterface networkInterface, Router router, DatagramProcessor datagramProcessor) throws InitializationException {
        this.router = router;
        this.datagramProcessor = datagramProcessor;
        this.multicastInterface = networkInterface;
        try {
            log.info("Creating wildcard socket (for receiving multicast datagrams) on port: " + this.configuration.getPort());
            this.multicastAddress = new InetSocketAddress(this.configuration.getGroup(), this.configuration.getPort());
            this.socket = new MulticastSocket(this.configuration.getPort());
            this.socket.setReuseAddress(true);
            this.socket.setReceiveBufferSize(32768);
            log.info("Joining multicast group: " + this.multicastAddress + " on network interface: " + this.multicastInterface.getDisplayName());
            this.socket.joinGroup(this.multicastAddress, this.multicastInterface);
        } catch (Exception ex) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + ex);
        }
    }

    @Override // org.teleal.cling.transport.spi.MulticastReceiver
    public synchronized void stop() {
        if (this.socket != null && !this.socket.isClosed()) {
            try {
                log.fine("Leaving multicast group");
                this.socket.leaveGroup(this.multicastAddress, this.multicastInterface);
            } catch (Exception ex) {
                log.fine("Could not leave multicast group: " + ex);
            }
            this.socket.close();
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        log.fine("Entering blocking receiving loop, listening for UDP datagrams on: " + this.socket.getLocalAddress());
        while (true) {
            try {
                byte[] buf = new byte[getConfiguration().getMaxDatagramBytes()];
                DatagramPacket datagram = new DatagramPacket(buf, buf.length);
                this.socket.receive(datagram);
                InetAddress receivedOnLocalAddress = this.router.getNetworkAddressFactory().getLocalAddress(this.multicastInterface, this.multicastAddress.getAddress() instanceof Inet6Address, datagram.getAddress());
                log.fine("UDP datagram received from: " + datagram.getAddress().getHostAddress() + ":" + datagram.getPort() + " on local interface: " + this.multicastInterface.getDisplayName() + " and address: " + receivedOnLocalAddress.getHostAddress());
                this.router.received(this.datagramProcessor.read(receivedOnLocalAddress, datagram));
            } catch (SocketException e) {
                log.fine("Socket closed");
                try {
                    if (!this.socket.isClosed()) {
                        log.fine("Closing multicast socket");
                        this.socket.close();
                        return;
                    }
                    return;
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } catch (UnsupportedDataException ex2) {
                log.info("Could not read datagram: " + ex2.getMessage());
            } catch (Exception ex3) {
                throw new RuntimeException(ex3);
            }
        }
    }
}
