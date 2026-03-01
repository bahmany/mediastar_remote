package org.cybergarage.multiscreenhttp;

import java.io.IOException;
import java.net.SocketException;
import java.util.Vector;
import org.cybergarage.multiscreennet.HostInterface;

/* loaded from: classes.dex */
public class HTTPServerList extends Vector {
    public void addRequestListener(HTTPRequestListener listener) {
        int nServers = size();
        for (int n = 0; n < nServers; n++) {
            HTTPServer server = getHTTPServer(n);
            server.addRequestListener(listener);
        }
    }

    public HTTPServer getHTTPServer(int n) {
        return (HTTPServer) get(n);
    }

    public String getBindAddress(int n) {
        return getHTTPServer(n).getBindAddress();
    }

    public String getHostAddress(int n) {
        return getHTTPServer(n).getHostAddress();
    }

    public int getBindPort(int n) {
        return getHTTPServer(n).getBindPort();
    }

    public void close() throws IOException {
        int nServers = size();
        for (int n = 0; n < nServers; n++) {
            HTTPServer server = getHTTPServer(n);
            server.close();
        }
    }

    public boolean open(int port, int timeOut) throws SocketException {
        int nHostAddrs = HostInterface.getNHostAddresses();
        clear();
        for (int n = 0; n < nHostAddrs; n++) {
            String bindAddr = HostInterface.getHostAddress(n);
            HTTPServer httpServer = new HTTPServer();
            httpServer.setClientSocketTimeOut(timeOut);
            if (httpServer.open(bindAddr, port)) {
                add(httpServer);
            }
        }
        if (size() > 0) {
            return true;
        }
        return false;
    }

    public void start() {
        int nServers = size();
        for (int n = 0; n < nServers; n++) {
            HTTPServer server = getHTTPServer(n);
            server.start();
        }
    }

    public void stop() {
        int nServers = size();
        for (int n = 0; n < nServers; n++) {
            HTTPServer server = getHTTPServer(n);
            server.stop();
        }
    }
}
