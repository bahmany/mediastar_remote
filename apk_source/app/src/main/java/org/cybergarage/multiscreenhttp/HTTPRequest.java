package org.cybergarage.multiscreenhttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import org.cybergarage.multiscreenutil.Debug;

/* loaded from: classes.dex */
public class HTTPRequest extends HTTPPacket {
    private static Logger logger = Logger.getLogger("org.cybergarage.http");
    private HTTPSocket httpSocket;
    private String method;
    private String requestHost;
    private int requestPort;
    private String uri;

    public HTTPRequest() {
        this.method = null;
        this.uri = null;
        this.requestHost = "";
        this.requestPort = -1;
        this.httpSocket = null;
    }

    public HTTPRequest(InputStream in) {
        super(in);
        this.method = null;
        this.uri = null;
        this.requestHost = "";
        this.requestPort = -1;
        this.httpSocket = null;
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
        ParameterList paramList = new ParameterList();
        String uri = getURI();
        if (uri != null) {
            uri.length();
            int paramIdx = uri.indexOf(63);
            if (paramIdx >= 0) {
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
        }
        return paramList;
    }

    public String getParameterValue(String name) {
        ParameterList paramList = getParameterList();
        return paramList.getValue(name);
    }

    @Override // org.cybergarage.multiscreenhttp.HTTPPacket
    public String getVersion() {
        return hasFirstLine() ? getFirstLineToken(2) : super.getVersion();
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

    private void setSocket(HTTPSocket value) {
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

    public String getFirstLineString() {
        return String.valueOf(getMethod()) + " " + getURI() + " HTTP/" + getVersion() + "\r\n";
    }

    public String getHeader() {
        StringBuffer str = new StringBuffer();
        str.append(getFirstLineString());
        String headerString = getHeaderString();
        str.append(headerString);
        return str.toString();
    }

    public void post(HTTPResponse httpRes) throws IOException {
        HTTPSocket httpSock = getSocket();
        httpSock.post(httpRes);
        httpSock.close();
    }

    public HTTPResponse post(String host, int port, int timeOut) throws IOException {
        HTTPResponse httpRes = new HTTPResponse();
        try {
            Socket sock = new Socket();
            sock.setSoTimeout(timeOut);
            long time = System.currentTimeMillis();
            sock.connect(new InetSocketAddress(host, port), timeOut);
            Debug.message("socket connect cost time" + (System.currentTimeMillis() - time));
            OutputStream out = sock.getOutputStream();
            PrintStream pout = new PrintStream(out, false, "UTF-8");
            pout.print(getHeader());
            pout.print("\r\n");
            String outString = getContentString();
            Debug.message("[" + outString + "]\n");
            pout.print(outString);
            pout.flush();
            InputStream in = sock.getInputStream();
            httpRes.set(in);
            in.close();
            out.close();
            sock.close();
        } catch (ConnectException e) {
            logger.warning("Exception posting HTTP request: NOT_FOUND" + e);
            httpRes.setStatusCode(500);
        } catch (SocketException e2) {
            logger.warning("Exception posting HTTP request: ISocketException" + e2);
            httpRes.setStatusCode(500);
        } catch (IOException e3) {
            logger.warning("Exception posting HTTP request: IIOException" + e3);
            httpRes.setStatusCode(500);
        }
        return httpRes;
    }

    public void set(HTTPRequest httpReq) {
        set((HTTPPacket) httpReq);
        setSocket(httpReq.getSocket());
    }

    public void returnResponse(int statusCode) throws IOException {
        HTTPResponse httpRes = new HTTPResponse();
        httpRes.setStatusCode(statusCode);
        httpRes.setContentLength(0L);
        post(httpRes);
    }

    public void returnOK() throws IOException {
        returnResponse(200);
    }

    public void returnBadRequest() throws IOException {
        returnResponse(400);
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
