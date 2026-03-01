package org.apache.mina.proxy.handlers.http;

import java.util.List;
import java.util.Map;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.proxy.handlers.ProxyRequest;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.apache.mina.proxy.utils.StringUtilities;
import org.cybergarage.http.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: classes.dex */
public abstract class AbstractAuthLogicHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractAuthLogicHandler.class);
    protected ProxyIoSession proxyIoSession;
    protected ProxyRequest request;
    protected int step = 0;

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    public abstract void doHandshake(IoFilter.NextFilter nextFilter);

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    public abstract void handleResponse(HttpProxyResponse httpProxyResponse);

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    protected AbstractAuthLogicHandler(ProxyIoSession proxyIoSession) {
        this.proxyIoSession = proxyIoSession;
        this.request = proxyIoSession.getRequest();
        if (this.request == null || !(this.request instanceof HttpProxyRequest)) {
            throw new IllegalArgumentException("request parameter should be a non null HttpProxyRequest instance");
        }
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    protected void writeRequest(IoFilter.NextFilter nextFilter, HttpProxyRequest request) {
        logger.debug("  sending HTTP request");
        ((AbstractHttpLogicHandler) this.proxyIoSession.getHandler()).writeRequest(nextFilter, request);
    }

    public static void addKeepAliveHeaders(Map<String, List<String>> headers) {
        StringUtilities.addValueToHeader(headers, HTTP.KEEP_ALIVE, HttpProxyConstants.DEFAULT_KEEP_ALIVE_TIME, true);
        StringUtilities.addValueToHeader(headers, "Proxy-Connection", "keep-Alive", true);
    }
}
