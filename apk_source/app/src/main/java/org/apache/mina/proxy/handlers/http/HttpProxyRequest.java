package org.apache.mina.proxy.handlers.http;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import javax.security.sasl.SaslException;
import org.apache.mina.proxy.ProxyAuthException;
import org.apache.mina.proxy.handlers.ProxyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: classes.dex */
public class HttpProxyRequest extends ProxyRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpProxyRequest.class);
    private Map<String, List<String>> headers;
    private String host;
    private final String httpURI;
    private final String httpVerb;
    private String httpVersion;
    private transient Map<String, String> properties;

    public HttpProxyRequest(InetSocketAddress endpointAddress) {
        this(endpointAddress, HttpProxyConstants.HTTP_1_0, (Map<String, List<String>>) null);
    }

    public HttpProxyRequest(InetSocketAddress endpointAddress, String httpVersion) {
        this(endpointAddress, httpVersion, (Map<String, List<String>>) null);
    }

    public HttpProxyRequest(InetSocketAddress endpointAddress, String httpVersion, Map<String, List<String>> headers) {
        this.httpVerb = HttpProxyConstants.CONNECT;
        if (!endpointAddress.isUnresolved()) {
            this.httpURI = endpointAddress.getHostName() + ":" + endpointAddress.getPort();
        } else {
            this.httpURI = endpointAddress.getAddress().getHostAddress() + ":" + endpointAddress.getPort();
        }
        this.httpVersion = httpVersion;
        this.headers = headers;
    }

    public HttpProxyRequest(String httpURI) {
        this("GET", httpURI, HttpProxyConstants.HTTP_1_0, null);
    }

    public HttpProxyRequest(String httpURI, String httpVersion) {
        this("GET", httpURI, httpVersion, null);
    }

    public HttpProxyRequest(String httpVerb, String httpURI, String httpVersion) {
        this(httpVerb, httpURI, httpVersion, null);
    }

    public HttpProxyRequest(String httpVerb, String httpURI, String httpVersion, Map<String, List<String>> headers) {
        this.httpVerb = httpVerb;
        this.httpURI = httpURI;
        this.httpVersion = httpVersion;
        this.headers = headers;
    }

    public final String getHttpVerb() {
        return this.httpVerb;
    }

    public String getHttpVersion() {
        return this.httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0034 A[Catch: all -> 0x0041, TRY_ENTER, TRY_LEAVE, TryCatch #1 {, blocks: (B:3:0x0001, B:5:0x0005, B:7:0x000b, B:9:0x0015, B:10:0x001f, B:12:0x0023, B:14:0x0027, B:19:0x0039, B:15:0x0034), top: B:26:0x0001, inners: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final synchronized java.lang.String getHost() {
        /*
            r3 = this;
            monitor-enter(r3)
            java.lang.String r1 = r3.host     // Catch: java.lang.Throwable -> L41
            if (r1 != 0) goto L34
            java.net.InetSocketAddress r1 = r3.getEndpointAddress()     // Catch: java.lang.Throwable -> L41
            if (r1 == 0) goto L1f
            java.net.InetSocketAddress r1 = r3.getEndpointAddress()     // Catch: java.lang.Throwable -> L41
            boolean r1 = r1.isUnresolved()     // Catch: java.lang.Throwable -> L41
            if (r1 != 0) goto L1f
            java.net.InetSocketAddress r1 = r3.getEndpointAddress()     // Catch: java.lang.Throwable -> L41
            java.lang.String r1 = r1.getHostName()     // Catch: java.lang.Throwable -> L41
            r3.host = r1     // Catch: java.lang.Throwable -> L41
        L1f:
            java.lang.String r1 = r3.host     // Catch: java.lang.Throwable -> L41
            if (r1 != 0) goto L34
            java.lang.String r1 = r3.httpURI     // Catch: java.lang.Throwable -> L41
            if (r1 == 0) goto L34
            java.net.URL r1 = new java.net.URL     // Catch: java.net.MalformedURLException -> L38 java.lang.Throwable -> L41
            java.lang.String r2 = r3.httpURI     // Catch: java.net.MalformedURLException -> L38 java.lang.Throwable -> L41
            r1.<init>(r2)     // Catch: java.net.MalformedURLException -> L38 java.lang.Throwable -> L41
            java.lang.String r1 = r1.getHost()     // Catch: java.net.MalformedURLException -> L38 java.lang.Throwable -> L41
            r3.host = r1     // Catch: java.net.MalformedURLException -> L38 java.lang.Throwable -> L41
        L34:
            java.lang.String r1 = r3.host     // Catch: java.lang.Throwable -> L41
            monitor-exit(r3)
            return r1
        L38:
            r0 = move-exception
            org.slf4j.Logger r1 = org.apache.mina.proxy.handlers.http.HttpProxyRequest.logger     // Catch: java.lang.Throwable -> L41
            java.lang.String r2 = "Malformed URL"
            r1.debug(r2, r0)     // Catch: java.lang.Throwable -> L41
            goto L34
        L41:
            r1 = move-exception
            monitor-exit(r3)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.mina.proxy.handlers.http.HttpProxyRequest.getHost():java.lang.String");
    }

    public final String getHttpURI() {
        return this.httpURI;
    }

    public final Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public final void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.security.sasl.SaslException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    public void checkRequiredProperties(String... propNames) throws SaslException {
        StringBuilder sb = new StringBuilder();
        for (String propertyName : propNames) {
            if (this.properties.get(propertyName) == null) {
                sb.append(propertyName).append(' ');
            }
        }
        if (sb.length() > 0) {
            sb.append("property(ies) missing in request");
            throw new ProxyAuthException(sb.toString());
        }
    }

    public String toHttpString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getHttpVerb()).append(' ').append(getHttpURI()).append(' ').append(getHttpVersion()).append("\r\n");
        boolean hostHeaderFound = false;
        if (getHeaders() != null) {
            for (Map.Entry<String, List<String>> header : getHeaders().entrySet()) {
                if (!hostHeaderFound) {
                    hostHeaderFound = header.getKey().equalsIgnoreCase("host");
                }
                for (String value : header.getValue()) {
                    sb.append(header.getKey()).append(": ").append(value).append("\r\n");
                }
            }
            if (!hostHeaderFound && getHttpVersion() == HttpProxyConstants.HTTP_1_1) {
                sb.append("Host: ").append(getHost()).append("\r\n");
            }
        }
        sb.append("\r\n");
        return sb.toString();
    }
}
