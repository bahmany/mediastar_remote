package com.hisilicon.multiscreen.http;

import java.io.IOException;
import org.cybergarage.http.HTTP;
import org.cybergarage.multiscreenhttp.HTTPRequest;
import org.cybergarage.multiscreenhttp.HTTPResponse;
import org.cybergarage.multiscreenutil.Debug;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class HiHttpClient {
    private String mHostIp;
    private int mPort;
    private int mTimeout;

    public HiHttpClient() {
        this.mHostIp = null;
        this.mPort = 80;
        this.mTimeout = 2000;
    }

    public HiHttpClient(String hostIp, int port, int timeOut) {
        this.mHostIp = null;
        this.mPort = 80;
        this.mTimeout = 2000;
        this.mHostIp = hostIp;
        this.mPort = port;
        this.mTimeout = timeOut;
    }

    public void setHostIp(String hostIp) {
        this.mHostIp = hostIp;
    }

    public String getHostIp() {
        return this.mHostIp;
    }

    public void setPort(int port) {
        this.mPort = port;
    }

    public int getPort() {
        return this.mPort;
    }

    public void setTimeOut(int timeOut) {
        this.mTimeout = timeOut;
    }

    public int getTimeOut() {
        return this.mTimeout;
    }

    public HiHttpResponse sendRequest(String host, int port, int timeOut, byte[] msg) throws IOException {
        if (msg == null || msg.length == 0) {
            return getWrongEmptyResponse(404);
        }
        if (host == null) {
            return getWrongEmptyResponse(500);
        }
        HTTPRequest request = new HTTPRequest();
        request.setMethod("POST");
        request.setURI(ServiceReference.DELIMITER);
        request.setVersion("1.0");
        request.setConnection(HTTP.CLOSE);
        request.setContent(msg);
        HTTPResponse retHttpResponse = request.post(host, port, timeOut);
        HiHttpResponse retHiResponse = new HiHttpResponse(retHttpResponse.getStatusCode(), retHttpResponse.getContent());
        retHiResponse.setResponseMessage(retHttpResponse.getContentString());
        return retHiResponse;
    }

    public HiHttpResponse sendRequest(byte[] msg) throws IOException {
        if (msg == null || msg.length == 0) {
            return getWrongEmptyResponse(404);
        }
        if (this.mHostIp == null) {
            return getWrongEmptyResponse(500);
        }
        HTTPRequest request = new HTTPRequest();
        request.setMethod("POST");
        request.setURI(ServiceReference.DELIMITER);
        request.setVersion("1.0");
        request.setConnection(HTTP.CLOSE);
        request.setContent(msg);
        HTTPResponse retHttpResponse = request.post(this.mHostIp, this.mPort, this.mTimeout);
        HiHttpResponse retHiResponse = new HiHttpResponse(retHttpResponse.getStatusCode(), retHttpResponse.getContent());
        retHiResponse.setResponseMessage(retHttpResponse.getContentString());
        return retHiResponse;
    }

    public HiHttpResponse sendRequest(String msg) throws IOException {
        if (msg == null) {
            return getWrongEmptyResponse(404);
        }
        if (this.mHostIp == null) {
            return getWrongEmptyResponse(500);
        }
        HTTPRequest request = new HTTPRequest();
        request.setMethod("POST");
        request.setURI(ServiceReference.DELIMITER);
        request.setVersion("1.0");
        request.setConnection(HTTP.CLOSE);
        request.setContent(msg);
        HTTPResponse retHttpResponse = request.post(this.mHostIp, this.mPort, this.mTimeout);
        HiHttpResponse retHiResponse = new HiHttpResponse(retHttpResponse.getStatusCode(), retHttpResponse.getContent());
        retHiResponse.setResponseMessage(retHttpResponse.getContentString());
        return retHiResponse;
    }

    private HiHttpResponse getWrongEmptyResponse(int statusCode) {
        HiHttpResponse retHiResponse = new HiHttpResponse(statusCode, (String) null);
        return retHiResponse;
    }

    public void debugOn() {
        Debug.on();
    }

    public void debugOff() {
        Debug.off();
    }
}
