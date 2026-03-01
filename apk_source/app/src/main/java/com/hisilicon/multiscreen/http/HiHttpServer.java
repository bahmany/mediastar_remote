package com.hisilicon.multiscreen.http;

import com.hisilicon.multiscreen.protocol.utils.LogTool;
import java.io.IOException;
import org.cybergarage.http.HTTP;
import org.cybergarage.multiscreenhttp.HTTPRequest;
import org.cybergarage.multiscreenhttp.HTTPRequestListener;
import org.cybergarage.multiscreenhttp.HTTPResponse;
import org.cybergarage.multiscreenhttp.HTTPServerList;
import org.cybergarage.multiscreenutil.Debug;

/* loaded from: classes.dex */
public class HiHttpServer implements HTTPRequestListener {
    public static final int HTTP_SERVER_DEFAULT_PORT = 80;
    public static final int HTTP_SERVER_DEFAULT_TIME_OUT = 1000;
    public static final int MAX_USER_PORT = 49151;
    public static final int MIN_USER_PORT = 1024;
    private int mServerPort = 80;
    private int mSocketTimeOut = 1000;
    private HiHttpRecvMsgListener mHiHttpRecvMsgListener = null;
    private HTTPServerList mHttpServerList = null;

    public HiHttpServer(int port, HiHttpRecvMsgListener listener) throws HiHttpException {
        setServerPort(port);
        setHttpRecvMsgListener(listener);
    }

    public HiHttpServer() {
    }

    public void setServerPort(int port) throws HiHttpException {
        if (port < 1024 || port > 49151) {
            throw new HiHttpException("wrong port range!");
        }
        this.mServerPort = port;
    }

    public int getServerPort() {
        return this.mServerPort;
    }

    public void setHttpRecvMsgListener(HiHttpRecvMsgListener listener) {
        this.mHiHttpRecvMsgListener = listener;
    }

    public HiHttpRecvMsgListener getHttpRecvMsgListener() {
        return this.mHiHttpRecvMsgListener;
    }

    public void startServer() {
        if (this.mHttpServerList == null) {
            this.mHttpServerList = new HTTPServerList();
        }
        if (this.mHttpServerList.open(this.mServerPort, this.mSocketTimeOut)) {
            this.mHttpServerList.addRequestListener(this);
            this.mHttpServerList.start();
        } else {
            LogTool.e("Fail to open HttpServerList, it is BindException.");
        }
    }

    public void stopServer() throws IOException {
        if (this.mHttpServerList != null) {
            this.mHttpServerList.close();
            this.mHttpServerList.stop();
            this.mHttpServerList.clear();
            this.mHttpServerList = null;
        }
    }

    @Override // org.cybergarage.multiscreenhttp.HTTPRequestListener
    public void httpRequestReceived(HTTPRequest httpReq) throws IOException {
        if (this.mHiHttpRecvMsgListener == null) {
            throw new HiHttpException("HttpRecvMsgListener is null!");
        }
        String sendMsg = this.mHiHttpRecvMsgListener.onHttpMsgReceived(httpReq.getContent(), null);
        if (sendMsg == null) {
            throw new HiHttpException("http Response is null!");
        }
        HTTPResponse httpRes = new HTTPResponse();
        httpRes.setStatusCode(200);
        httpRes.setVersion("1.0");
        httpRes.setConnection(HTTP.CLOSE);
        httpRes.setContent(sendMsg);
        httpReq.post(httpRes);
    }

    public void setSocketTimeOut(int timeOut) {
        this.mSocketTimeOut = timeOut;
    }

    public int getSocketTimeOut() {
        return this.mSocketTimeOut;
    }

    public void debugOn() {
        Debug.on();
    }

    public void debugOff() {
        Debug.off();
    }
}
