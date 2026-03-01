package org.apache.mina.proxy.handlers.http.digest;

import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
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
public class HttpDigestAuthLogicHandler extends AbstractAuthLogicHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpDigestAuthLogicHandler.class);
    private static SecureRandom rnd;
    private HashMap<String, String> directives;
    private HttpProxyResponse response;

    static {
        try {
            rnd = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    public HttpDigestAuthLogicHandler(ProxyIoSession proxyIoSession) throws SaslException {
        super(proxyIoSession);
        this.directives = null;
        ((HttpProxyRequest) this.request).checkRequiredProperties(HttpProxyConstants.USER_PROPERTY, HttpProxyConstants.PWD_PROPERTY);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.security.sasl.SaslException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    @Override // org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler
    public void doHandshake(IoFilter.NextFilter nextFilter) throws SaslException {
        logger.debug(" doHandshake()");
        if (this.step > 0 && this.directives == null) {
            throw new ProxyAuthException("Authentication challenge not received");
        }
        HttpProxyRequest req = (HttpProxyRequest) this.request;
        Map<String, List<String>> headers = req.getHeaders() != null ? req.getHeaders() : new HashMap<>();
        if (this.step > 0) {
            logger.debug("  sending DIGEST challenge response");
            HashMap<String, String> map = new HashMap<>();
            map.put("username", req.getProperties().get(HttpProxyConstants.USER_PROPERTY));
            StringUtilities.copyDirective(this.directives, map, "realm");
            StringUtilities.copyDirective(this.directives, map, "uri");
            StringUtilities.copyDirective(this.directives, map, "opaque");
            StringUtilities.copyDirective(this.directives, map, "nonce");
            String algorithm = StringUtilities.copyDirective(this.directives, map, "algorithm");
            if (algorithm != null && !"md5".equalsIgnoreCase(algorithm) && !"md5-sess".equalsIgnoreCase(algorithm)) {
                throw new ProxyAuthException("Unknown algorithm required by server");
            }
            String qop = this.directives.get("qop");
            if (qop != null) {
                StringTokenizer st = new StringTokenizer(qop, ClientInfo.SEPARATOR_BETWEEN_VARS);
                String token = null;
                while (st.hasMoreTokens()) {
                    String tk = st.nextToken();
                    if ("auth".equalsIgnoreCase(token)) {
                        break;
                    }
                    int pos = Arrays.binarySearch(DigestUtilities.SUPPORTED_QOPS, tk);
                    if (pos > -1) {
                        token = tk;
                    }
                }
                if (token != null) {
                    map.put("qop", token);
                    byte[] nonce = new byte[8];
                    rnd.nextBytes(nonce);
                    try {
                        String cnonce = new String(Base64.encodeBase64(nonce), this.proxyIoSession.getCharsetName());
                        map.put("cnonce", cnonce);
                    } catch (UnsupportedEncodingException e) {
                        throw new ProxyAuthException("Unable to encode cnonce", e);
                    }
                } else {
                    throw new ProxyAuthException("No supported qop option available");
                }
            }
            map.put("nc", "00000001");
            map.put("uri", req.getHttpURI());
            try {
                map.put("response", DigestUtilities.computeResponseValue(this.proxyIoSession.getSession(), map, req.getHttpVerb().toUpperCase(), req.getProperties().get(HttpProxyConstants.PWD_PROPERTY), this.proxyIoSession.getCharsetName(), this.response.getBody()));
                StringBuilder sb = new StringBuilder("Digest ");
                boolean addSeparator = false;
                for (String key : map.keySet()) {
                    if (addSeparator) {
                        sb.append(", ");
                    } else {
                        addSeparator = true;
                    }
                    boolean quotedValue = ("qop".equals(key) || "nc".equals(key)) ? false : true;
                    sb.append(key);
                    if (quotedValue) {
                        sb.append("=\"").append(map.get(key)).append('\"');
                    } else {
                        sb.append('=').append(map.get(key));
                    }
                }
                StringUtilities.addValueToHeader(headers, "Proxy-Authorization", sb.toString(), true);
            } catch (Exception e2) {
                throw new ProxyAuthException("Digest response computing failed", e2);
            }
        }
        addKeepAliveHeaders(headers);
        req.setHeaders(headers);
        writeRequest(nextFilter, req);
        this.step++;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.security.sasl.SaslException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    @Override // org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler
    public void handleResponse(HttpProxyResponse response) throws SaslException {
        this.response = response;
        if (this.step == 0) {
            if (response.getStatusCode() != 401 && response.getStatusCode() != 407) {
                throw new ProxyAuthException("Received unexpected response code (" + response.getStatusLine() + ").");
            }
            List<String> values = response.getHeaders().get("Proxy-Authenticate");
            String challengeResponse = null;
            Iterator i$ = values.iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                String s = i$.next();
                if (s.startsWith("Digest")) {
                    challengeResponse = s;
                    break;
                }
            }
            if (challengeResponse == null) {
                throw new ProxyAuthException("Server doesn't support digest authentication method !");
            }
            try {
                this.directives = StringUtilities.parseDirectives(challengeResponse.substring(7).getBytes(this.proxyIoSession.getCharsetName()));
                this.step = 1;
                return;
            } catch (Exception e) {
                throw new ProxyAuthException("Parsing of server digest directives failed", e);
            }
        }
        throw new ProxyAuthException("Received unexpected response code (" + response.getStatusLine() + ").");
    }
}
