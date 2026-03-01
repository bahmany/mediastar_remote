package org.cybergarage.multiscreenhttp;

import java.io.InputStream;

/* loaded from: classes.dex */
public class HTTPResponse extends HTTPPacket {
    private int statusCode;

    public HTTPResponse() {
        this.statusCode = 0;
        setContentType("text/html; charset=\"utf-8\"");
        setServer(HTTPServer.getName());
        setContent("");
    }

    public HTTPResponse(HTTPResponse httpRes) {
        this.statusCode = 0;
        set(httpRes);
        setStatusCode(httpRes.getStatusCode());
    }

    public HTTPResponse(InputStream in) {
        super(in);
        this.statusCode = 0;
    }

    public HTTPResponse(HTTPSocket httpSock) {
        this(httpSock.getInputStream());
    }

    public void setStatusCode(int code) {
        this.statusCode = code;
    }

    public int getStatusCode() {
        if (this.statusCode != 0) {
            return this.statusCode;
        }
        getFirstLine();
        HTTPStatus httpStatus = new HTTPStatus(getFirstLine());
        this.statusCode = httpStatus.getStatusCode();
        return this.statusCode;
    }

    public String getStatusLineString() {
        return "HTTP/" + getVersion() + " " + getStatusCode() + " " + HTTPStatus.code2String(this.statusCode) + "\r\n";
    }

    public String getHeader() {
        StringBuffer str = new StringBuffer();
        str.append(getStatusLineString());
        str.append(getHeaderString());
        return str.toString();
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(getStatusLineString());
        str.append(getHeaderString());
        str.append("\r\n");
        str.append(getContentString());
        return str.toString();
    }

    public void print() {
        System.out.println(toString());
    }
}
