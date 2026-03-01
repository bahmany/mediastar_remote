package org.apache.mina.proxy.handlers.http;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionInitializer;
import org.apache.mina.proxy.AbstractProxyLogicHandler;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.apache.mina.proxy.utils.StringUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: classes.dex */
public abstract class AbstractHttpLogicHandler extends AbstractProxyLogicHandler {
    private int contentLength;
    private int entityBodyLimitPosition;
    private int entityBodyStartPosition;
    private boolean hasChunkedData;
    private HttpProxyResponse parsedResponse;
    private IoBuffer responseData;
    private boolean waitingChunkedData;
    private boolean waitingFooters;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpLogicHandler.class);
    private static final String DECODER = AbstractHttpLogicHandler.class.getName() + ".Decoder";
    private static final byte[] HTTP_DELIMITER = {13, 10, 13, 10};
    private static final byte[] CRLF_DELIMITER = {13, 10};

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    public abstract void handleResponse(HttpProxyResponse httpProxyResponse);

    public AbstractHttpLogicHandler(ProxyIoSession proxyIoSession) {
        super(proxyIoSession);
        this.responseData = null;
        this.parsedResponse = null;
        this.contentLength = -1;
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    /* JADX WARN: Code restructure failed: missing block: B:146:0x016a, code lost:
    
        if (r15.waitingFooters == false) goto L170;
     */
    /* JADX WARN: Code restructure failed: missing block: B:147:0x016c, code lost:
    
        r3.setDelimiter(org.apache.mina.proxy.handlers.http.AbstractHttpLogicHandler.CRLF_DELIMITER, false);
        r9 = r3.decodeFully(r17);
     */
    /* JADX WARN: Code restructure failed: missing block: B:148:0x0178, code lost:
    
        if (r9 == null) goto L171;
     */
    /* JADX WARN: Code restructure failed: missing block: B:150:0x017f, code lost:
    
        if (r9.remaining() != 2) goto L160;
     */
    /* JADX WARN: Code restructure failed: missing block: B:151:0x0181, code lost:
    
        r15.waitingFooters = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:160:0x0228, code lost:
    
        r6 = r9.getString(getProxyIoSession().getCharset().newDecoder());
        r5 = r6.split(":\\s?", 2);
        org.apache.mina.proxy.utils.StringUtilities.addValueToHeader(r15.parsedResponse.getHeaders(), r5[0], r5[1], false);
        r15.responseData.put(r9);
        r15.responseData.put(org.apache.mina.proxy.handlers.http.AbstractHttpLogicHandler.CRLF_DELIMITER);
     */
    /* JADX WARN: Removed duplicated region for block: B:120:0x00b7 A[Catch: Exception -> 0x0097, all -> 0x009f, TryCatch #0 {Exception -> 0x0097, blocks: (B:93:0x0026, B:95:0x002a, B:99:0x0038, B:101:0x0079, B:103:0x0083, B:115:0x00a2, B:117:0x00b0, B:155:0x01ee, B:105:0x008d, B:118:0x00b3, B:120:0x00b7, B:122:0x00bb, B:124:0x00c3, B:125:0x00d1, B:127:0x00e5, B:128:0x00f2, B:130:0x00f6, B:132:0x00fa, B:134:0x00fe, B:136:0x010c, B:138:0x0124, B:139:0x0129, B:141:0x014a, B:156:0x0200, B:142:0x0156, B:144:0x015a, B:157:0x020d, B:159:0x0215, B:145:0x0168, B:147:0x016c, B:149:0x017a, B:151:0x0181, B:160:0x0228, B:152:0x0184, B:154:0x01e9), top: B:164:0x0026, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:128:0x00f2 A[Catch: Exception -> 0x0097, all -> 0x009f, TryCatch #0 {Exception -> 0x0097, blocks: (B:93:0x0026, B:95:0x002a, B:99:0x0038, B:101:0x0079, B:103:0x0083, B:115:0x00a2, B:117:0x00b0, B:155:0x01ee, B:105:0x008d, B:118:0x00b3, B:120:0x00b7, B:122:0x00bb, B:124:0x00c3, B:125:0x00d1, B:127:0x00e5, B:128:0x00f2, B:130:0x00f6, B:132:0x00fa, B:134:0x00fe, B:136:0x010c, B:138:0x0124, B:139:0x0129, B:141:0x014a, B:156:0x0200, B:142:0x0156, B:144:0x015a, B:157:0x020d, B:159:0x0215, B:145:0x0168, B:147:0x016c, B:149:0x017a, B:151:0x0181, B:160:0x0228, B:152:0x0184, B:154:0x01e9), top: B:164:0x0026, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:130:0x00f6 A[Catch: Exception -> 0x0097, all -> 0x009f, LOOP:0: B:130:0x00f6->B:159:0x0215, LOOP_START, TryCatch #0 {Exception -> 0x0097, blocks: (B:93:0x0026, B:95:0x002a, B:99:0x0038, B:101:0x0079, B:103:0x0083, B:115:0x00a2, B:117:0x00b0, B:155:0x01ee, B:105:0x008d, B:118:0x00b3, B:120:0x00b7, B:122:0x00bb, B:124:0x00c3, B:125:0x00d1, B:127:0x00e5, B:128:0x00f2, B:130:0x00f6, B:132:0x00fa, B:134:0x00fe, B:136:0x010c, B:138:0x0124, B:139:0x0129, B:141:0x014a, B:156:0x0200, B:142:0x0156, B:144:0x015a, B:157:0x020d, B:159:0x0215, B:145:0x0168, B:147:0x016c, B:149:0x017a, B:151:0x0181, B:160:0x0228, B:152:0x0184, B:154:0x01e9), top: B:164:0x0026, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:152:0x0184 A[Catch: Exception -> 0x0097, all -> 0x009f, TryCatch #0 {Exception -> 0x0097, blocks: (B:93:0x0026, B:95:0x002a, B:99:0x0038, B:101:0x0079, B:103:0x0083, B:115:0x00a2, B:117:0x00b0, B:155:0x01ee, B:105:0x008d, B:118:0x00b3, B:120:0x00b7, B:122:0x00bb, B:124:0x00c3, B:125:0x00d1, B:127:0x00e5, B:128:0x00f2, B:130:0x00f6, B:132:0x00fa, B:134:0x00fe, B:136:0x010c, B:138:0x0124, B:139:0x0129, B:141:0x014a, B:156:0x0200, B:142:0x0156, B:144:0x015a, B:157:0x020d, B:159:0x0215, B:145:0x0168, B:147:0x016c, B:149:0x017a, B:151:0x0181, B:160:0x0228, B:152:0x0184, B:154:0x01e9), top: B:164:0x0026, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:157:0x020d A[Catch: Exception -> 0x0097, all -> 0x009f, TryCatch #0 {Exception -> 0x0097, blocks: (B:93:0x0026, B:95:0x002a, B:99:0x0038, B:101:0x0079, B:103:0x0083, B:115:0x00a2, B:117:0x00b0, B:155:0x01ee, B:105:0x008d, B:118:0x00b3, B:120:0x00b7, B:122:0x00bb, B:124:0x00c3, B:125:0x00d1, B:127:0x00e5, B:128:0x00f2, B:130:0x00f6, B:132:0x00fa, B:134:0x00fe, B:136:0x010c, B:138:0x0124, B:139:0x0129, B:141:0x014a, B:156:0x0200, B:142:0x0156, B:144:0x015a, B:157:0x020d, B:159:0x0215, B:145:0x0168, B:147:0x016c, B:149:0x017a, B:151:0x0181, B:160:0x0228, B:152:0x0184, B:154:0x01e9), top: B:164:0x0026, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:168:0x015a A[SYNTHETIC] */
    @Override // org.apache.mina.proxy.ProxyLogicHandler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized void messageReceived(org.apache.mina.core.filterchain.IoFilter.NextFilter r16, org.apache.mina.core.buffer.IoBuffer r17) {
        /*
            Method dump skipped, instructions count: 613
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.mina.proxy.handlers.http.AbstractHttpLogicHandler.messageReceived(org.apache.mina.core.filterchain.IoFilter$NextFilter, org.apache.mina.core.buffer.IoBuffer):void");
    }

    public void writeRequest(IoFilter.NextFilter nextFilter, HttpProxyRequest request) {
        ProxyIoSession proxyIoSession = getProxyIoSession();
        if (proxyIoSession.isReconnectionNeeded()) {
            reconnect(nextFilter, request);
        } else {
            writeRequest0(nextFilter, request);
        }
    }

    public void writeRequest0(IoFilter.NextFilter nextFilter, HttpProxyRequest request) {
        try {
            String data = request.toHttpString();
            IoBuffer buf = IoBuffer.wrap(data.getBytes(getProxyIoSession().getCharsetName()));
            LOGGER.debug("   write:\n{}", data.replace("\r", "\\r").replace("\n", "\\n\n"));
            writeData(nextFilter, buf);
        } catch (UnsupportedEncodingException ex) {
            closeSession("Unable to send HTTP request: ", ex);
        }
    }

    private void reconnect(IoFilter.NextFilter nextFilter, HttpProxyRequest request) {
        LOGGER.debug("Reconnecting to proxy ...");
        ProxyIoSession proxyIoSession = getProxyIoSession();
        proxyIoSession.getConnector().connect(new IoSessionInitializer<ConnectFuture>() { // from class: org.apache.mina.proxy.handlers.http.AbstractHttpLogicHandler.1
            final /* synthetic */ IoFilter.NextFilter val$nextFilter;
            final /* synthetic */ ProxyIoSession val$proxyIoSession;
            final /* synthetic */ HttpProxyRequest val$request;

            AnonymousClass1(ProxyIoSession proxyIoSession2, IoFilter.NextFilter nextFilter2, HttpProxyRequest request2) {
                proxyIoSession = proxyIoSession2;
                nextFilter = nextFilter2;
                httpProxyRequest = request2;
            }

            @Override // org.apache.mina.core.session.IoSessionInitializer
            public void initializeSession(IoSession session, ConnectFuture future) {
                AbstractHttpLogicHandler.LOGGER.debug("Initializing new session: {}", session);
                session.setAttribute(ProxyIoSession.PROXY_SESSION, proxyIoSession);
                proxyIoSession.setSession(session);
                AbstractHttpLogicHandler.LOGGER.debug("  setting up proxyIoSession: {}", proxyIoSession);
                future.addListener((IoFutureListener<?>) new IoFutureListener<ConnectFuture>() { // from class: org.apache.mina.proxy.handlers.http.AbstractHttpLogicHandler.1.1
                    C01611() {
                    }

                    @Override // org.apache.mina.core.future.IoFutureListener
                    public void operationComplete(ConnectFuture future2) {
                        proxyIoSession.setReconnectionNeeded(false);
                        AbstractHttpLogicHandler.this.writeRequest0(nextFilter, httpProxyRequest);
                    }
                });
            }

            /* renamed from: org.apache.mina.proxy.handlers.http.AbstractHttpLogicHandler$1$1 */
            class C01611 implements IoFutureListener<ConnectFuture> {
                C01611() {
                }

                @Override // org.apache.mina.core.future.IoFutureListener
                public void operationComplete(ConnectFuture future2) {
                    proxyIoSession.setReconnectionNeeded(false);
                    AbstractHttpLogicHandler.this.writeRequest0(nextFilter, httpProxyRequest);
                }
            }
        });
    }

    /* renamed from: org.apache.mina.proxy.handlers.http.AbstractHttpLogicHandler$1 */
    class AnonymousClass1 implements IoSessionInitializer<ConnectFuture> {
        final /* synthetic */ IoFilter.NextFilter val$nextFilter;
        final /* synthetic */ ProxyIoSession val$proxyIoSession;
        final /* synthetic */ HttpProxyRequest val$request;

        AnonymousClass1(ProxyIoSession proxyIoSession2, IoFilter.NextFilter nextFilter2, HttpProxyRequest request2) {
            proxyIoSession = proxyIoSession2;
            nextFilter = nextFilter2;
            httpProxyRequest = request2;
        }

        @Override // org.apache.mina.core.session.IoSessionInitializer
        public void initializeSession(IoSession session, ConnectFuture future) {
            AbstractHttpLogicHandler.LOGGER.debug("Initializing new session: {}", session);
            session.setAttribute(ProxyIoSession.PROXY_SESSION, proxyIoSession);
            proxyIoSession.setSession(session);
            AbstractHttpLogicHandler.LOGGER.debug("  setting up proxyIoSession: {}", proxyIoSession);
            future.addListener((IoFutureListener<?>) new IoFutureListener<ConnectFuture>() { // from class: org.apache.mina.proxy.handlers.http.AbstractHttpLogicHandler.1.1
                C01611() {
                }

                @Override // org.apache.mina.core.future.IoFutureListener
                public void operationComplete(ConnectFuture future2) {
                    proxyIoSession.setReconnectionNeeded(false);
                    AbstractHttpLogicHandler.this.writeRequest0(nextFilter, httpProxyRequest);
                }
            });
        }

        /* renamed from: org.apache.mina.proxy.handlers.http.AbstractHttpLogicHandler$1$1 */
        class C01611 implements IoFutureListener<ConnectFuture> {
            C01611() {
            }

            @Override // org.apache.mina.core.future.IoFutureListener
            public void operationComplete(ConnectFuture future2) {
                proxyIoSession.setReconnectionNeeded(false);
                AbstractHttpLogicHandler.this.writeRequest0(nextFilter, httpProxyRequest);
            }
        }
    }

    protected HttpProxyResponse decodeResponse(String response) throws Exception {
        LOGGER.debug("  parseResponse()");
        String[] responseLines = response.split("\r\n");
        String[] statusLine = responseLines[0].trim().split(" ", 2);
        if (statusLine.length < 2) {
            throw new Exception("Invalid response status line (" + statusLine + "). Response: " + response);
        }
        if (!statusLine[1].matches("^\\d\\d\\d")) {
            throw new Exception("Invalid response code (" + statusLine[1] + "). Response: " + response);
        }
        Map<String, List<String>> headers = new HashMap<>();
        for (int i = 1; i < responseLines.length; i++) {
            String[] args = responseLines[i].split(":\\s?", 2);
            StringUtilities.addValueToHeader(headers, args[0], args[1], false);
        }
        return new HttpProxyResponse(statusLine[0], statusLine[1], headers);
    }
}
