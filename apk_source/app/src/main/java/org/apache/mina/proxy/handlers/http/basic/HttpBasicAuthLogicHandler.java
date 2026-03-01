package org.apache.mina.proxy.handlers.http.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.sasl.SaslException;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.proxy.ProxyAuthException;
import org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler;
import org.apache.mina.proxy.handlers.http.HttpProxyConstants;
import org.apache.mina.proxy.handlers.http.HttpProxyRequest;
import org.apache.mina.proxy.handlers.http.HttpProxyResponse;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.apache.mina.proxy.utils.StringUtilities;
import org.apache.mina.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: classes.dex */
public class HttpBasicAuthLogicHandler extends AbstractAuthLogicHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpBasicAuthLogicHandler.class);

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    public HttpBasicAuthLogicHandler(ProxyIoSession proxyIoSession) throws SaslException {
        super(proxyIoSession);
        ((HttpProxyRequest) this.request).checkRequiredProperties(HttpProxyConstants.USER_PROPERTY, HttpProxyConstants.PWD_PROPERTY);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.security.sasl.SaslException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    @Override // org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler
    public void doHandshake(IoFilter.NextFilter nextFilter) throws SaslException {
        logger.debug(" doHandshake()");
        if (this.step > 0) {
            throw new ProxyAuthException("Authentication request already sent");
        }
        HttpProxyRequest req = (HttpProxyRequest) this.request;
        Map<String, List<String>> headers = req.getHeaders() != null ? req.getHeaders() : new HashMap<>();
        String username = req.getProperties().get(HttpProxyConstants.USER_PROPERTY);
        String password = req.getProperties().get(HttpProxyConstants.PWD_PROPERTY);
        StringUtilities.addValueToHeader(headers, "Proxy-Authorization", "Basic " + createAuthorization(username, password), true);
        addKeepAliveHeaders(headers);
        req.setHeaders(headers);
        writeRequest(nextFilter, req);
        this.step++;
    }

    public static String createAuthorization(String username, String password) {
        return new String(Base64.encodeBase64((username + ":" + password).getBytes()));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.security.sasl.SaslException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    @Override // org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler
    public void handleResponse(HttpProxyResponse response) throws SaslException {
        if (response.getStatusCode() != 407) {
            throw new ProxyAuthException("Received error response code (" + response.getStatusLine() + ").");
        }
    }
}
