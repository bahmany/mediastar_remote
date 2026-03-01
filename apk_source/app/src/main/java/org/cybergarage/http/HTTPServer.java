package org.cybergarage.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.cybergarage.util.Debug;
import org.cybergarage.util.ListenerList;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class HTTPServer implements Runnable {
    public static final int DEFAULT_CONNECT_TIMEOUT = 2000;
    public static final int DEFAULT_PORT = 80;
    public static final int DEFAULT_TIMEOUT = 2000;
    public static final String NAME = "CyberHTTP";
    public static final String VERSION = "1.0";
    private ServerSocket serverSock;
    private InetAddress bindAddr = null;
    private int bindPort = 0;
    protected int timeout = 2000;
    private ListenerList httpRequestListenerList = new ListenerList();
    private Thread httpServerThread = null;

    public static String getName() {
        String osName = System.getProperty("os.name");
        String osVer = System.getProperty("os.version");
        return String.valueOf(osName) + ServiceReference.DELIMITER + osVer + " " + NAME + ServiceReference.DELIMITER + "1.0";
    }

    public HTTPServer() {
        this.serverSock = null;
        this.serverSock = null;
    }

    public ServerSocket getServerSock() {
        return this.serverSock;
    }

    public String getBindAddress() {
        return this.bindAddr == null ? "" : this.bindAddr.toString();
    }

    public int getBindPort() {
        return this.bindPort;
    }

    public synchronized int getTimeout() {
        return this.timeout;
    }

    public synchronized void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean open(InetAddress addr, int port) {
        if (this.serverSock != null) {
            return true;
        }
        try {
            this.serverSock = new ServerSocket(this.bindPort, 0, this.bindAddr);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean open(String addr, int port) {
        if (this.serverSock != null) {
            return true;
        }
        try {
            this.bindAddr = InetAddress.getByName(addr);
            this.bindPort = port;
            this.serverSock = new ServerSocket(this.bindPort, 0, this.bindAddr);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean close() throws IOException {
        if (this.serverSock == null) {
            return true;
        }
        try {
            this.serverSock.close();
            this.serverSock = null;
            this.bindAddr = null;
            this.bindPort = 0;
            return true;
        } catch (Exception e) {
            Debug.warning(e);
            return false;
        }
    }

    public Socket accept() throws IOException {
        if (this.serverSock == null) {
            return null;
        }
        try {
            Socket sock = this.serverSock.accept();
            sock.setSoTimeout(getTimeout());
            return sock;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isOpened() {
        return this.serverSock != null;
    }

    public void addRequestListener(HTTPRequestListener listener) {
        this.httpRequestListenerList.add(listener);
    }

    public void removeRequestListener(HTTPRequestListener listener) {
        this.httpRequestListenerList.remove(listener);
    }

    public void performRequestListener(HTTPRequest httpReq) {
        int listenerSize = this.httpRequestListenerList.size();
        for (int n = 0; n < listenerSize; n++) {
            HTTPRequestListener listener = (HTTPRequestListener) this.httpRequestListenerList.get(n);
            listener.httpRequestRecieved(httpReq);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        if (isOpened()) {
            Thread thisThread = Thread.currentThread();
            while (this.httpServerThread == thisThread) {
                Thread.yield();
                try {
                    Debug.message("accept ...");
                    Socket sock = accept();
                    if (sock != null) {
                        Debug.message("sock = " + sock.getRemoteSocketAddress());
                    }
                    HTTPServerThread httpServThread = new HTTPServerThread(this, sock);
                    httpServThread.start();
                    Debug.message("httpServThread ...");
                } catch (Exception e) {
                    Debug.warning(e);
                    return;
                }
            }
        }
    }

    public boolean start() {
        StringBuffer name = new StringBuffer("Cyber.HTTPServer/");
        name.append(this.serverSock.getLocalSocketAddress());
        this.httpServerThread = new Thread(this, name.toString());
        this.httpServerThread.start();
        return true;
    }

    public boolean stop() throws IOException {
        close();
        this.httpServerThread = null;
        return true;
    }
}
