package org.cybergarage.multiscreenhttp;

import java.io.IOException;
import java.net.Socket;

/* loaded from: classes.dex */
public class HTTPServerThread extends Thread {
    private HTTPServer httpServer;
    private int mTimeOut;
    private Socket sock;

    public HTTPServerThread(HTTPServer httpServer, Socket sock, int timeOut) {
        this.mTimeOut = 1000;
        this.httpServer = httpServer;
        this.sock = sock;
        this.mTimeOut = timeOut;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() throws IOException {
        HTTPSocket httpSock = new HTTPSocket(this.sock, this.mTimeOut);
        if (httpSock.open()) {
            HTTPRequest httpReq = new HTTPRequest(httpSock);
            this.httpServer.performRequestListener(httpReq);
            httpSock.close();
        }
    }
}
