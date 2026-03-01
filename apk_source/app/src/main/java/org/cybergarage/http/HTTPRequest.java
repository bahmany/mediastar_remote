package org.cybergarage.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;
import org.cybergarage.util.Debug;

/* loaded from: classes.dex */
public class HTTPRequest extends HTTPPacket {
    private HTTPSocket httpSocket;
    private int mTimeoutConnect;
    private int mTimeoutRead;
    private String method;
    private Socket postSocket;
    private String requestHost;
    private int requestPort;
    private String uri;

    public HTTPRequest() {
        this.mTimeoutConnect = 2000;
        this.mTimeoutRead = 2000;
        this.method = null;
        this.uri = null;
        this.requestHost = "";
        this.requestPort = -1;
        this.httpSocket = null;
        this.postSocket = null;
        setVersion("1.0");
    }

    public HTTPRequest(InputStream in) {
        super(in);
        this.mTimeoutConnect = 2000;
        this.mTimeoutRead = 2000;
        this.method = null;
        this.uri = null;
        this.requestHost = "";
        this.requestPort = -1;
        this.httpSocket = null;
        this.postSocket = null;
    }

    public HTTPRequest(HTTPSocket httpSock) {
        this(httpSock.getInputStream());
        setSocket(httpSock);
    }

    public void setMethod(String value) {
        this.method = value;
    }

    public String getMethod() {
        return this.method != null ? this.method : getFirstLineToken(0);
    }

    public boolean isMethod(String method) {
        String headerMethod = getMethod();
        if (headerMethod == null) {
            return false;
        }
        return headerMethod.equalsIgnoreCase(method);
    }

    public boolean isGetRequest() {
        return isMethod("GET");
    }

    public boolean isPostRequest() {
        return isMethod("POST");
    }

    public boolean isHeadRequest() {
        return isMethod(HTTP.HEAD);
    }

    public boolean isSubscribeRequest() {
        return isMethod("SUBSCRIBE");
    }

    public boolean isUnsubscribeRequest() {
        return isMethod("UNSUBSCRIBE");
    }

    public boolean isNotifyRequest() {
        return isMethod("NOTIFY");
    }

    public void setURI(String value, boolean isCheckRelativeURL) {
        this.uri = value;
        if (isCheckRelativeURL) {
            this.uri = HTTP.toRelativeURL(this.uri);
        }
    }

    public void setURI(String value) {
        setURI(value, false);
    }

    public String getURI() {
        return this.uri != null ? this.uri : getFirstLineToken(1);
    }

    public ParameterList getParameterList() {
        int paramIdx;
        ParameterList paramList = new ParameterList();
        String uri = getURI();
        if (uri != null && (paramIdx = uri.indexOf(63)) >= 0) {
            while (paramIdx > 0) {
                int eqIdx = uri.indexOf(61, paramIdx + 1);
                String name = uri.substring(paramIdx + 1, eqIdx);
                int nextParamIdx = uri.indexOf(38, eqIdx + 1);
                String value = uri.substring(eqIdx + 1, nextParamIdx > 0 ? nextParamIdx : uri.length());
                Parameter param = new Parameter(name, value);
                paramList.add(param);
                paramIdx = nextParamIdx;
            }
        }
        return paramList;
    }

    public String getParameterValue(String name) {
        ParameterList paramList = getParameterList();
        return paramList.getValue(name);
    }

    public boolean isSOAPAction() {
        return hasHeader("SOAPACTION");
    }

    public void setRequestHost(String host) {
        this.requestHost = host;
    }

    public String getRequestHost() {
        return this.requestHost;
    }

    public void setRequestPort(int host) {
        this.requestPort = host;
    }

    public int getRequestPort() {
        return this.requestPort;
    }

    public void setSocket(HTTPSocket value) {
        this.httpSocket = value;
    }

    public HTTPSocket getSocket() {
        return this.httpSocket;
    }

    public String getLocalAddress() {
        return getSocket().getLocalAddress();
    }

    public int getLocalPort() {
        return getSocket().getLocalPort();
    }

    public void setTimeoutConnect(int timeout) {
        this.mTimeoutConnect = timeout;
    }

    public void setTimeoutRead(int timeout) {
        this.mTimeoutRead = timeout;
    }

    public boolean parseRequestLine(String lineStr) {
        StringTokenizer st = new StringTokenizer(lineStr, " ");
        if (!st.hasMoreTokens()) {
            return false;
        }
        setMethod(st.nextToken());
        if (!st.hasMoreTokens()) {
            return false;
        }
        setURI(st.nextToken());
        if (!st.hasMoreTokens()) {
            return false;
        }
        setVersion(st.nextToken());
        return true;
    }

    public String getHTTPVersion() {
        return hasFirstLine() ? getFirstLineToken(2) : "HTTP/" + super.getVersion();
    }

    public String getFirstLineString() {
        return String.valueOf(getMethod()) + " " + getURI() + " " + getHTTPVersion() + "\r\n";
    }

    public String getHeader() {
        StringBuffer str = new StringBuffer();
        str.append(getFirstLineString());
        String headerString = getHeaderString();
        str.append(headerString);
        return str.toString();
    }

    public boolean isKeepAlive() {
        if (isCloseConnection()) {
            return false;
        }
        if (isKeepAliveConnection()) {
            return true;
        }
        String httpVer = getHTTPVersion();
        boolean isHTTP10 = httpVer.indexOf("1.0") > 0;
        return !isHTTP10;
    }

    public boolean read() {
        return super.read(getSocket());
    }

    public boolean post(HTTPResponse httpRes) {
        HTTPSocket httpSock = getSocket();
        long offset = 0;
        long length = httpRes.getContentLength();
        if (hasContentRange()) {
            long firstPos = getContentRangeFirstPosition();
            long lastPos = getContentRangeLastPosition();
            if (lastPos <= 0) {
                lastPos = length - 1;
            }
            if (firstPos > length || lastPos > length) {
                return returnResponse(416);
            }
            httpRes.setContentRange(firstPos, lastPos, length);
            httpRes.setStatusCode(206);
            offset = firstPos;
            length = (lastPos - firstPos) + 1;
        }
        return httpSock.post(httpRes, offset, length, isHeadRequest());
    }

    public HTTPResponse post(String host, int port, boolean isKeepAlive) throws IOException {
        HTTPResponse httpRes = new HTTPResponse();
        setHost(host);
        setConnection(isKeepAlive ? HTTP.KEEP_ALIVE : HTTP.CLOSE);
        boolean isHeaderRequest = isHeadRequest();
        OutputStream out = null;
        InputStream in = null;
        try {
            try {
                if (this.postSocket == null) {
                    this.postSocket = new Socket();
                    this.postSocket.connect(new InetSocketAddress(host, port), this.mTimeoutConnect);
                    this.postSocket.setSoTimeout(this.mTimeoutRead);
                }
                out = this.postSocket.getOutputStream();
                PrintStream pout = new PrintStream(out);
                pout.print(getHeader());
                pout.print("\r\n");
                boolean isChunkedRequest = isChunked();
                String content = getContentString();
                int contentLength = content != null ? content.length() : 0;
                if (contentLength > 0) {
                    if (isChunkedRequest) {
                        String chunSizeBuf = Long.toHexString(contentLength);
                        pout.print(chunSizeBuf);
                        pout.print("\r\n");
                    }
                    pout.print(content);
                    if (isChunkedRequest) {
                        pout.print("\r\n");
                    }
                }
                if (isChunkedRequest) {
                    pout.print("0");
                    pout.print("\r\n");
                }
                pout.flush();
                in = this.postSocket.getInputStream();
                httpRes.set(in, isHeaderRequest);
                if (!isKeepAlive) {
                    try {
                        in.close();
                    } catch (Exception e) {
                    }
                    if (in != null) {
                        try {
                            out.close();
                        } catch (Exception e2) {
                        }
                    }
                    if (out != null) {
                        try {
                            this.postSocket.close();
                        } catch (Exception e3) {
                        }
                    }
                    this.postSocket = null;
                }
            } catch (SocketException e4) {
                httpRes.setStatusCode(500);
                Debug.warning(e4);
                if (!isKeepAlive) {
                    try {
                        in.close();
                    } catch (Exception e5) {
                    }
                    if (in != null) {
                        try {
                            out.close();
                        } catch (Exception e6) {
                        }
                    }
                    if (out != null) {
                        try {
                            this.postSocket.close();
                        } catch (Exception e7) {
                        }
                    }
                    this.postSocket = null;
                }
            } catch (IOException e8) {
                httpRes.setStatusCode(500);
                Debug.warning(e8);
                if (!isKeepAlive) {
                    try {
                        in.close();
                    } catch (Exception e9) {
                    }
                    if (in != null) {
                        try {
                            out.close();
                        } catch (Exception e10) {
                        }
                    }
                    if (out != null) {
                        try {
                            this.postSocket.close();
                        } catch (Exception e11) {
                        }
                    }
                    this.postSocket = null;
                }
            }
            return httpRes;
        } catch (Throwable th) {
            if (!isKeepAlive) {
                try {
                    in.close();
                } catch (Exception e12) {
                }
                if (in != null) {
                    try {
                        out.close();
                    } catch (Exception e13) {
                    }
                }
                if (out != null) {
                    try {
                        this.postSocket.close();
                    } catch (Exception e14) {
                    }
                }
                this.postSocket = null;
            }
            throw th;
        }
    }

    public HTTPResponse post(String host, int port) {
        return post(host, port, false);
    }

    public void set(HTTPRequest httpReq) {
        set((HTTPPacket) httpReq);
        setSocket(httpReq.getSocket());
    }

    public boolean returnResponse(int statusCode) {
        HTTPResponse httpRes = new HTTPResponse();
        httpRes.setStatusCode(statusCode);
        httpRes.setContentLength(0L);
        return post(httpRes);
    }

    public boolean returnOK() {
        return returnResponse(200);
    }

    public boolean returnBadRequest() {
        return returnResponse(400);
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(getHeader());
        str.append("\r\n");
        str.append(getContentString());
        return str.toString();
    }

    public void print() {
        System.out.println(toString());
    }
}
