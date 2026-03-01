package org.teleal.cling.transport.impl;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.logging.Logger;
import org.teleal.cling.model.message.OutgoingDatagramMessage;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.spi.DatagramIO;
import org.teleal.cling.transport.spi.DatagramProcessor;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.UnsupportedDataException;

/* loaded from: classes.dex */
public class DatagramIOImpl implements DatagramIO<DatagramIOConfigurationImpl> {
    private static Logger log = Logger.getLogger(DatagramIO.class.getName());
    protected final DatagramIOConfigurationImpl configuration;
    protected DatagramProcessor datagramProcessor;
    protected InetSocketAddress localAddress;
    protected Router router;
    protected MulticastSocket socket;

    public DatagramIOImpl(DatagramIOConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    @Override // org.teleal.cling.transport.spi.DatagramIO
    public DatagramIOConfigurationImpl getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.transport.spi.DatagramIO
    public synchronized void init(InetAddress bindAddress, Router router, DatagramProcessor datagramProcessor) throws InitializationException {
        this.router = router;
        this.datagramProcessor = datagramProcessor;
        try {
            log.info("Creating bound socket (for datagram input/output) on: " + bindAddress);
            this.localAddress = new InetSocketAddress(bindAddress, 0);
            this.socket = new MulticastSocket(this.localAddress);
            this.socket.setTimeToLive(this.configuration.getTimeToLive());
            this.socket.setReceiveBufferSize(32768);
        } catch (Exception ex) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + ex);
        }
    }

    @Override // org.teleal.cling.transport.spi.DatagramIO
    public synchronized void stop() {
        if (this.socket != null && !this.socket.isClosed()) {
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
                log.fine("UDP datagram received from: " + datagram.getAddress().getHostAddress() + ":" + datagram.getPort() + " on: " + this.localAddress);
                this.router.received(this.datagramProcessor.read(this.localAddress.getAddress(), datagram));
            } catch (SocketException e) {
                log.fine("Socket closed");
                try {
                    if (!this.socket.isClosed()) {
                        log.fine("Closing unicast socket");
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

    @Override // org.teleal.cling.transport.spi.DatagramIO
    public synchronized void send(OutgoingDatagramMessage message) {
        log.fine("Sending message from address: " + this.localAddress);
        DatagramPacket packet = this.datagramProcessor.write(message);
        log.fine("Sending UDP datagram packet to: " + message.getDestinationAddress() + ":" + message.getDestinationPort());
        send(packet);
    }

    @Override // org.teleal.cling.transport.spi.DatagramIO
    public synchronized void send(DatagramPacket datagram) {
        log.fine("Sending message from address: " + this.localAddress);
        try {
            try {
                this.socket.send(datagram);
            } catch (SocketException e) {
                log.fine("Socket closed, aborting datagram send to: " + datagram.getAddress());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } catch (RuntimeException ex2) {
            throw ex2;
        }
    }
}
