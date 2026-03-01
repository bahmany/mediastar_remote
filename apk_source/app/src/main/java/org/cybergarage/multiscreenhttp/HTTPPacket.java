package org.cybergarage.multiscreenhttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;
import org.cybergarage.multiscreennet.HostInterface;
import org.cybergarage.multiscreenutil.Debug;
import org.cybergarage.multiscreenutil.StringUtil;

/* loaded from: classes.dex */
public class HTTPPacket {
    public static final String ENCODE_CHARSET = "UTF-8";
    private static Logger logger = Logger.getLogger("org.cybergarage.http");
    private byte[] content;
    private InputStream contentInput;
    private String firstLine;
    private Vector httpHeaderList;
    private String version;

    public HTTPPacket() {
        this.firstLine = "";
        this.httpHeaderList = new Vector();
        this.content = new byte[0];
        this.contentInput = null;
        setVersion("1.0");
        setContentInputStream(null);
    }

    public HTTPPacket(HTTPPacket httpPacket) {
        this.firstLine = "";
        this.httpHeaderList = new Vector();
        this.content = new byte[0];
        this.contentInput = null;
        setVersion("1.0");
        set(httpPacket);
        setContentInputStream(null);
    }

    public HTTPPacket(InputStream in) throws IOException {
        this.firstLine = "";
        this.httpHeaderList = new Vector();
        this.content = new byte[0];
        this.contentInput = null;
        setVersion("1.0");
        set(in);
        setContentInputStream(null);
    }

    public void setVersion(String ver) {
        this.version = ver;
    }

    public String getVersion() {
        return this.version;
    }

    protected void set(InputStream inStream) throws IOException {
        String firstLineTmp;
        try {
            new InputStreamReader(inStream, "UTF-8");
            String firstLine = readLine(inStream);
            setFirstLine(firstLine);
            HTTPStatus httpStatus = new HTTPStatus(firstLine);
            int statCode = httpStatus.getStatusCode();
            if (statCode == 100) {
                String headerLine = readLine(inStream);
                while (headerLine != null && headerLine.length() > 0) {
                    HTTPHeader header = new HTTPHeader(headerLine);
                    if (header.hasName()) {
                        setHeader(header);
                    }
                    headerLine = readLine(inStream);
                }
                String actualFirstLine = readLine(inStream);
                if (actualFirstLine != null && actualFirstLine.length() > 0) {
                    setFirstLine(actualFirstLine);
                } else {
                    return;
                }
            }
            long conLen = 999999999;
            String headerLine2 = readLine(inStream);
            while (headerLine2 != null && headerLine2.length() > 0) {
                HTTPHeader header2 = new HTTPHeader(headerLine2);
                if (header2.hasName()) {
                    setHeader(header2);
                    if (header2.getName().equalsIgnoreCase(HTTP.CONTENT_LENGTH)) {
                        conLen = getContentLength();
                    }
                }
                headerLine2 = readLine(inStream);
            }
            if ((conLen != 999999999 || ((firstLineTmp = getFirstLine()) != null && firstLineTmp.startsWith("HTTP"))) && 0 < conLen) {
                int chunkSize = HTTP.getChunkSize();
                byte[] readBuf = new byte[chunkSize];
                long readCnt = 0;
                StringBuffer conBuf = new StringBuffer();
                while (readCnt < conLen) {
                    try {
                        int len = inStream.read(readBuf, 0, chunkSize);
                        if (len < 0) {
                            break;
                        }
                        String tmp = new String(readBuf, 0, len, "UTF-8");
                        conBuf.append(tmp);
                        readCnt += len;
                    } catch (Exception e) {
                        logger.warning("Exception reading HTTP response" + e);
                    }
                }
                String conStr = conBuf.toString();
                setContent(conStr.getBytes("UTF-8"));
            }
        } catch (Exception e2) {
            Debug.message("Exception");
        }
    }

    protected void set(HTTPSocket httpSock) throws IOException {
        set(httpSock.getInputStream());
    }

    protected void set(HTTPPacket httpPacket) {
        setFirstLine(httpPacket.getFirstLine());
        clearHeaders();
        int nHeaders = httpPacket.getNHeaders();
        for (int n = 0; n < nHeaders; n++) {
            HTTPHeader header = httpPacket.getHeader(n);
            addHeader(header);
        }
        setContent(httpPacket.getContent());
    }

    private String readLine(InputStream inStream) throws IOException {
        StringBuffer sb = new StringBuffer("");
        while (true) {
            try {
                int c = inStream.read();
                if (c == -1) {
                    return null;
                }
                if (c != 10) {
                    if (c != 13) {
                        sb.append((char) c);
                    }
                } else {
                    return sb.toString();
                }
            } catch (IOException e) {
                return null;
            }
        }
    }

    private void setFirstLine(String value) {
        this.firstLine = value;
    }

    protected String getFirstLine() {
        return this.firstLine;
    }

    protected String getFirstLineToken(int num) {
        StringTokenizer st = new StringTokenizer(this.firstLine, " ");
        String lastToken = "";
        if (st == null) {
            return null;
        }
        for (int n = 0; n <= num; n++) {
            if (!st.hasMoreTokens()) {
                return "";
            }
            lastToken = st.nextToken();
        }
        return lastToken;
    }

    public boolean hasFirstLine() {
        return this.firstLine.length() > 0;
    }

    public int getNHeaders() {
        return this.httpHeaderList.size();
    }

    public void addHeader(HTTPHeader header) {
        this.httpHeaderList.add(header);
    }

    public void addHeader(String name, String value) {
        HTTPHeader header = new HTTPHeader(name, value);
        this.httpHeaderList.add(header);
    }

    public HTTPHeader getHeader(int n) {
        return (HTTPHeader) this.httpHeaderList.get(n);
    }

    public HTTPHeader getHeader(String name) {
        int nHeaders = getNHeaders();
        for (int n = 0; n < nHeaders; n++) {
            HTTPHeader header = getHeader(n);
            String headerName = header.getName();
            if (headerName.equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }

    public void clearHeaders() {
        this.httpHeaderList.clear();
        this.httpHeaderList = new Vector();
    }

    public boolean hasHeader(String name) {
        return getHeader(name) != null;
    }

    public void setHeader(String name, String value) {
        HTTPHeader header = getHeader(name);
        if (header != null) {
            header.setValue(value);
        } else {
            addHeader(name, value);
        }
    }

    public void setHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    public void setHeader(String name, long value) {
        setHeader(name, Long.toString(value));
    }

    public void setHeader(HTTPHeader header) {
        setHeader(header.getName(), header.getValue());
    }

    public String getHeaderValue(String name) {
        HTTPHeader header = getHeader(name);
        return header == null ? "" : header.getValue();
    }

    public void setStringHeader(String name, String value, String startWidth, String endWidth) {
        String headerValue = value;
        if (!headerValue.startsWith(startWidth)) {
            headerValue = String.valueOf(startWidth) + headerValue;
        }
        if (!headerValue.endsWith(endWidth)) {
            headerValue = String.valueOf(headerValue) + endWidth;
        }
        setHeader(name, headerValue);
    }

    public void setStringHeader(String name, String value) {
        setStringHeader(name, value, "\"", "\"");
    }

    public String getStringHeaderValue(String name, String startWidth, String endWidth) {
        String headerValue = getHeaderValue(name);
        if (headerValue.startsWith(startWidth)) {
            headerValue = headerValue.substring(1, headerValue.length());
        }
        if (headerValue.endsWith(endWidth)) {
            return headerValue.substring(0, headerValue.length() - 1);
        }
        return headerValue;
    }

    public String getStringHeaderValue(String name) {
        return getStringHeaderValue(name, "\"", "\"");
    }

    public void setIntegerHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    public void setLongHeader(String name, long value) {
        setHeader(name, Long.toString(value));
    }

    public int getIntegerHeaderValue(String name) {
        HTTPHeader header = getHeader(name);
        if (header == null) {
            return 0;
        }
        return StringUtil.toInteger(header.getValue());
    }

    public long getLongHeaderValue(String name) {
        HTTPHeader header = getHeader(name);
        if (header == null) {
            return 0L;
        }
        return StringUtil.toLong(header.getValue());
    }

    public String getHeaderString() {
        StringBuffer str = new StringBuffer();
        int nHeaders = getNHeaders();
        for (int n = 0; n < nHeaders; n++) {
            HTTPHeader header = getHeader(n);
            str.append(String.valueOf(header.getName()) + ": " + header.getValue() + "\r\n");
        }
        return str.toString();
    }

    public void setContent(byte[] data) {
        this.content = data;
        setContentLength(data.length);
    }

    public void setContent(String data) {
        try {
            setContent(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.warning("Exception " + e);
        }
    }

    public byte[] getContent() {
        return this.content;
    }

    public String getContentString() {
        try {
            return new String(this.content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.warning("Exception " + e);
            return null;
        }
    }

    public boolean hasContent() {
        return this.content.length > 0;
    }

    public void setContentInputStream(InputStream in) {
        this.contentInput = in;
    }

    public InputStream getContentInputStream() {
        return this.contentInput;
    }

    public boolean hasContentInputStream() {
        return this.contentInput != null;
    }

    public void setContentType(String type) {
        setHeader(HTTP.CONTENT_TYPE, type);
    }

    public String getContentType() {
        return getHeaderValue(HTTP.CONTENT_TYPE);
    }

    public void setContentLength(long len) {
        setLongHeader(HTTP.CONTENT_LENGTH, len);
    }

    public long getContentLength() {
        return getLongHeaderValue(HTTP.CONTENT_LENGTH);
    }

    public void setConnection(String connection) {
        setHeader(HTTP.CONNECTION, connection);
    }

    public String getConnection() {
        return getHeaderValue(HTTP.CONNECTION);
    }

    public void setCacheControl(String directive) {
        setHeader(HTTP.CACHE_CONTROL, directive);
    }

    public void setCacheControl(String directive, int value) {
        String strVal = String.valueOf(directive) + "=" + Integer.toString(value);
        setHeader(HTTP.CACHE_CONTROL, strVal);
    }

    public void setCacheControl(int value) {
        setCacheControl("max-age", value);
    }

    public String getCacheControl() {
        return getHeaderValue(HTTP.CACHE_CONTROL);
    }

    public void setServer(String name) {
        setHeader(HTTP.SERVER, name);
    }

    public String getServer() {
        return getHeaderValue(HTTP.SERVER);
    }

    public void setHost(String host, int port) {
        String hostAddr = host;
        if (HostInterface.isIPv6Address(host)) {
            hostAddr = "[" + host + "]";
        }
        setHeader("HOST", String.valueOf(hostAddr) + ":" + Integer.toString(port));
    }

    public String getHost() {
        return getHeaderValue("HOST");
    }

    public void setDate(Calendar cal) {
        Date date = new Date(cal);
        setHeader(HTTP.DATE, date.getDateString());
    }

    public String getDate() {
        return getHeaderValue(HTTP.DATE);
    }
}
