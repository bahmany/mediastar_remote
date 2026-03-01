package org.apache.mina.proxy.handlers.http.basic;

import javax.security.sasl.SaslException;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.proxy.ProxyAuthException;
import org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler;
import org.apache.mina.proxy.handlers.http.HttpProxyRequest;
import org.apache.mina.proxy.handlers.http.HttpProxyResponse;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: classes.dex */
public class HttpNoAuthLogicHandler extends AbstractAuthLogicHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpNoAuthLogicHandler.class);

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    public HttpNoAuthLogicHandler(ProxyIoSession proxyIoSession) {
        super(proxyIoSession);
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    @Override // org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler
    public void doHandshake(IoFilter.NextFilter nextFilter) {
        logger.debug(" doHandshake()");
        writeRequest(nextFilter, (HttpProxyRequest) this.request);
        this.step++;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.security.sasl.SaslException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    @Override // org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler
    public void handleResponse(HttpProxyResponse response) throws SaslException {
        throw new ProxyAuthException("Received error response code (" + response.getStatusLine() + ").");
    }
}
