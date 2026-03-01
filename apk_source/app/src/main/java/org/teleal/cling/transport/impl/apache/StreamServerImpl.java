package org.teleal.cling.transport.impl.apache;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;
import org.apache.http.HttpRequestFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.StreamServer;
import org.teleal.cling.transport.spi.UpnpStream;

/* loaded from: classes.dex */
public class StreamServerImpl implements StreamServer<StreamServerConfigurationImpl> {
    private static final Logger log = Logger.getLogger(StreamServer.class.getName());
    protected final StreamServerConfigurationImpl configuration;
    protected Router router;
    protected ServerSocket serverSocket;
    protected HttpParams globalParams = new BasicHttpParams();
    private volatile boolean stopped = false;

    public StreamServerImpl(StreamServerConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    @Override // org.teleal.cling.transport.spi.StreamServer
    public StreamServerConfigurationImpl getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.transport.spi.StreamServer
    public synchronized void init(InetAddress bindAddress, Router router) throws InitializationException {
        try {
            this.router = router;
            this.serverSocket = new ServerSocket(this.configuration.getListenPort(), this.configuration.getTcpConnectionBacklog(), bindAddress);
            log.info("Created socket (for receiving TCP streams) on: " + this.serverSocket.getLocalSocketAddress());
            this.globalParams.setIntParameter("http.socket.timeout", this.configuration.getDataWaitTimeoutSeconds() * 1000).setIntParameter("http.socket.buffer-size", this.configuration.getBufferSizeKilobytes() * 1024).setBooleanParameter("http.connection.stalecheck", this.configuration.isStaleConnectionCheck()).setBooleanParameter("http.tcp.nodelay", this.configuration.isTcpNoDelay());
        } catch (Exception ex) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + ex.toString(), ex);
        }
    }

    @Override // org.teleal.cling.transport.spi.StreamServer
    public synchronized int getPort() {
        return this.serverSocket.getLocalPort();
    }

    @Override // org.teleal.cling.transport.spi.StreamServer
    public synchronized void stop() {
        this.stopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException ex) {
            log.fine("Exception closing streaming server socket: " + ex);
        }
    }

    @Override // java.lang.Runnable
    public void run() throws IOException {
        log.fine("Entering blocking receiving loop, listening for HTTP stream requests on: " + this.serverSocket.getLocalSocketAddress());
        while (!this.stopped) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                DefaultHttpServerConnection httpServerConnection = new DefaultHttpServerConnection() { // from class: org.teleal.cling.transport.impl.apache.StreamServerImpl.1
                    @Override // org.apache.http.impl.AbstractHttpServerConnection
                    protected HttpRequestFactory createHttpRequestFactory() {
                        return new UpnpHttpRequestFactory();
                    }
                };
                log.fine("Incoming connection from: " + clientSocket.getInetAddress());
                httpServerConnection.bind(clientSocket, this.globalParams);
                UpnpStream connectionStream = new HttpServerConnectionUpnpStream(this.router.getProtocolFactory(), httpServerConnection, this.globalParams);
                this.router.received(connectionStream);
            } catch (InterruptedIOException ex) {
                log.fine("I/O has been interrupted, stopping receiving loop, bytes transfered: " + ex.bytesTransferred);
            } catch (SocketException ex2) {
                if (!this.stopped) {
                    log.fine("Exception using server socket: " + ex2.getMessage());
                }
            } catch (IOException ex3) {
                log.fine("Exception initializing receiving loop: " + ex3.getMessage());
            }
        }
        try {
            log.fine("Receiving loop stopped");
            if (!this.serverSocket.isClosed()) {
                log.fine("Closing streaming server socket");
                this.serverSocket.close();
            }
        } catch (Exception ex4) {
            log.info("Exception closing streaming server socket: " + ex4.getMessage());
        }
    }
}
