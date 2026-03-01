package org.teleal.cling.transport.impl;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Logger;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.transport.Router;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.StreamServer;

/* loaded from: classes.dex */
public class StreamServerImpl implements StreamServer<StreamServerConfigurationImpl> {
    private static Logger log = Logger.getLogger(StreamServer.class.getName());
    protected final StreamServerConfigurationImpl configuration;
    protected HttpServer server;

    public StreamServerImpl(StreamServerConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    @Override // org.teleal.cling.transport.spi.StreamServer
    public synchronized void init(InetAddress bindAddress, Router router) throws InitializationException {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(bindAddress, this.configuration.getListenPort());
            this.server = HttpServer.create(socketAddress, this.configuration.getTcpConnectionBacklog());
            this.server.createContext(ServiceReference.DELIMITER, new RequestHttpHandler(router));
            log.info("Created server (for receiving TCP streams) on: " + this.server.getAddress());
        } catch (Exception ex) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + ex.toString(), ex);
        }
    }

    @Override // org.teleal.cling.transport.spi.StreamServer
    public synchronized int getPort() {
        return this.server.getAddress().getPort();
    }

    @Override // org.teleal.cling.transport.spi.StreamServer
    public StreamServerConfigurationImpl getConfiguration() {
        return this.configuration;
    }

    @Override // java.lang.Runnable
    public synchronized void run() {
        log.fine("Starting StreamServer...");
        this.server.start();
    }

    @Override // org.teleal.cling.transport.spi.StreamServer
    public synchronized void stop() {
        log.fine("Stopping StreamServer...");
        if (this.server != null) {
            this.server.stop(1);
        }
    }

    static class RequestHttpHandler implements HttpHandler {
        private static Logger log = Logger.getLogger(RequestHttpHandler.class.getName());
        private final Router router;

        public RequestHttpHandler(Router router) {
            this.router = router;
        }

        public void handle(HttpExchange httpExchange) throws IOException {
            log.fine("Received HTTP exchange: " + httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI());
            this.router.received(new HttpExchangeUpnpStream(this.router.getProtocolFactory(), httpExchange));
        }
    }
}
