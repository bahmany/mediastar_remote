package org.cybergarage.multiscreenhttp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.cybergarage.multiscreenutil.Debug;
import org.cybergarage.multiscreenutil.ListenerList;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class HTTPServer implements Runnable {
    public static final int DEFAULT_PORT = 80;
    public static final String NAME = "HiMultiScreenHTTP";
    public static final String VERSION = "1.0";
    private ServerSocket serverSock;
    private int mTimeOut = 1000;
    private InetAddress bindAddr = null;
    private int bindPort = 0;
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

    public String getHostAddress() {
        return this.bindAddr == null ? "" : this.bindAddr.getHostAddress();
    }

    public boolean open(String addr, int port) {
        if (this.serverSock != null) {
            return true;
        }
        try {
            this.bindAddr = InetAddress.getByName(addr);
            this.bindPort = port;
            this.serverSock = new ServerSocket(port);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void close() throws IOException {
        if (this.serverSock != null) {
            try {
                this.serverSock.close();
                this.serverSock = null;
                this.bindAddr = null;
                this.bindPort = 0;
            } catch (Exception e) {
                Debug.message("close Excption");
            }
        }
    }

    public Socket accept() {
        if (this.serverSock == null) {
            return null;
        }
        try {
            return this.serverSock.accept();
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
            if (listener != null) {
                listener.httpRequestReceived(httpReq);
            }
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
                    HTTPServerThread httpServThread = new HTTPServerThread(this, sock, getClientSocketTimeOut());
                    httpServThread.start();
                    Debug.message("httpServThread ...");
                } catch (Exception e) {
                    Debug.warning(e);
                    return;
                }
            }
        }
    }

    public void start() {
        this.httpServerThread = new Thread(this);
        this.httpServerThread.start();
    }

    public void stop() {
        this.httpServerThread = null;
    }

    public void setClientSocketTimeOut(int time) {
        this.mTimeOut = time;
    }

    public int getClientSocketTimeOut() {
        return this.mTimeOut;
    }
}
