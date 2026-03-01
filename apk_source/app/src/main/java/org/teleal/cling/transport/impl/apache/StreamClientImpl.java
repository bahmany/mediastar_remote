package org.teleal.cling.transport.impl.apache;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import master.flame.danmaku.danmaku.parser.IDataSource;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.cybergarage.xml.XML;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpHeaders;
import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.StreamClient;
import org.teleal.common.util.Exceptions;

/* loaded from: classes.dex */
public class StreamClientImpl implements StreamClient<StreamClientConfigurationImpl> {
    private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$model$message$UpnpRequest$Method;
    private static final Logger log = Logger.getLogger(StreamClient.class.getName());
    protected final ThreadSafeClientConnManager clientConnectionManager;
    protected final StreamClientConfigurationImpl configuration;
    protected final HttpParams globalParams = new BasicHttpParams();
    protected final DefaultHttpClient httpClient;

    static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$model$message$UpnpRequest$Method() {
        int[] iArr = $SWITCH_TABLE$org$teleal$cling$model$message$UpnpRequest$Method;
        if (iArr == null) {
            iArr = new int[UpnpRequest.Method.valuesCustom().length];
            try {
                iArr[UpnpRequest.Method.GET.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[UpnpRequest.Method.MSEARCH.ordinal()] = 4;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[UpnpRequest.Method.NOTIFY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[UpnpRequest.Method.POST.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[UpnpRequest.Method.SUBSCRIBE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[UpnpRequest.Method.UNKNOWN.ordinal()] = 7;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[UpnpRequest.Method.UNSUBSCRIBE.ordinal()] = 6;
            } catch (NoSuchFieldError e7) {
            }
            $SWITCH_TABLE$org$teleal$cling$model$message$UpnpRequest$Method = iArr;
        }
        return iArr;
    }

    public StreamClientImpl(StreamClientConfigurationImpl configuration) throws InitializationException {
        this.configuration = configuration;
        ConnManagerParams.setMaxTotalConnections(this.globalParams, getConfiguration().getMaxTotalConnections());
        HttpConnectionParams.setConnectionTimeout(this.globalParams, getConfiguration().getConnectionTimeoutSeconds() * 1000);
        HttpConnectionParams.setSoTimeout(this.globalParams, getConfiguration().getDataReadTimeoutSeconds() * 1000);
        HttpProtocolParams.setContentCharset(this.globalParams, getConfiguration().getContentCharset());
        if (getConfiguration().getSocketBufferSize() != -1) {
            HttpConnectionParams.setSocketBufferSize(this.globalParams, getConfiguration().getSocketBufferSize());
        }
        HttpConnectionParams.setStaleCheckingEnabled(this.globalParams, getConfiguration().getStaleCheckingEnabled());
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme(IDataSource.SCHEME_HTTP_TAG, PlainSocketFactory.getSocketFactory(), 80));
        this.clientConnectionManager = new ThreadSafeClientConnManager(this.globalParams, registry);
        this.httpClient = new DefaultHttpClient(this.clientConnectionManager, this.globalParams);
        if (getConfiguration().getRequestRetryCount() != -1) {
            this.httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(getConfiguration().getRequestRetryCount(), false));
        }
    }

    @Override // org.teleal.cling.transport.spi.StreamClient
    public StreamClientConfigurationImpl getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.transport.spi.StreamClient
    public StreamResponseMessage sendRequest(StreamRequestMessage requestMessage) {
        UpnpRequest requestOperation = requestMessage.getOperation();
        log.fine("Preparing HTTP request message with method '" + requestOperation.getHttpMethodName() + "': " + requestMessage);
        try {
            HttpUriRequest httpRequest = createHttpRequest(requestMessage, requestOperation);
            httpRequest.setParams(getRequestParams(requestMessage));
            HeaderUtil.add(httpRequest, requestMessage.getHeaders());
            log.fine("Sending HTTP request: " + httpRequest.getURI());
            return (StreamResponseMessage) this.httpClient.execute(httpRequest, createResponseHandler());
        } catch (ClientProtocolException ex) {
            log.warning("HTTP protocol exception executing request: " + requestMessage);
            log.warning("Cause: " + Exceptions.unwrap(ex));
            return null;
        } catch (IOException ex2) {
            log.fine("Client connection was aborted: " + ex2.getMessage());
            return null;
        } catch (MethodNotSupportedException ex3) {
            log.warning("Request aborted: " + ex3.toString());
            return null;
        }
    }

    @Override // org.teleal.cling.transport.spi.StreamClient
    public void stop() {
        log.fine("Shutting down HTTP client connection manager/pool");
        this.clientConnectionManager.shutdown();
    }

    protected HttpUriRequest createHttpRequest(UpnpMessage upnpMessage, UpnpRequest upnpRequestOperation) throws MethodNotSupportedException {
        switch ($SWITCH_TABLE$org$teleal$cling$model$message$UpnpRequest$Method()[upnpRequestOperation.getMethod().ordinal()]) {
            case 1:
                return new HttpGet(upnpRequestOperation.getURI());
            case 2:
                HttpEntityEnclosingRequest post = new HttpPost(upnpRequestOperation.getURI());
                post.setEntity(createHttpRequestEntity(upnpMessage));
                return (HttpUriRequest) post;
            case 3:
                HttpEntityEnclosingRequest notify = new HttpPost(upnpRequestOperation.getURI()) { // from class: org.teleal.cling.transport.impl.apache.StreamClientImpl.3
                    @Override // org.apache.http.client.methods.HttpPost, org.apache.http.client.methods.HttpRequestBase, org.apache.http.client.methods.HttpUriRequest
                    public String getMethod() {
                        return UpnpRequest.Method.NOTIFY.getHttpName();
                    }
                };
                notify.setEntity(createHttpRequestEntity(upnpMessage));
                return (HttpUriRequest) notify;
            case 4:
            default:
                throw new MethodNotSupportedException(upnpRequestOperation.getHttpMethodName());
            case 5:
                return new HttpGet(upnpRequestOperation.getURI()) { // from class: org.teleal.cling.transport.impl.apache.StreamClientImpl.1
                    @Override // org.apache.http.client.methods.HttpGet, org.apache.http.client.methods.HttpRequestBase, org.apache.http.client.methods.HttpUriRequest
                    public String getMethod() {
                        return UpnpRequest.Method.SUBSCRIBE.getHttpName();
                    }
                };
            case 6:
                return new HttpGet(upnpRequestOperation.getURI()) { // from class: org.teleal.cling.transport.impl.apache.StreamClientImpl.2
                    @Override // org.apache.http.client.methods.HttpGet, org.apache.http.client.methods.HttpRequestBase, org.apache.http.client.methods.HttpUriRequest
                    public String getMethod() {
                        return UpnpRequest.Method.UNSUBSCRIBE.getHttpName();
                    }
                };
        }
    }

    protected HttpEntity createHttpRequestEntity(UpnpMessage upnpMessage) {
        if (upnpMessage.getBodyType().equals(UpnpMessage.BodyType.BYTES)) {
            log.fine("Preparing HTTP request entity as byte[]");
            return new ByteArrayEntity(upnpMessage.getBodyBytes());
        }
        log.fine("Preparing HTTP request entity as string");
        try {
            String charset = upnpMessage.getContentTypeCharset();
            String bodyString = upnpMessage.getBodyString();
            if (charset == null) {
                charset = "UTF-8";
            }
            return new StringEntity(bodyString, charset);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected ResponseHandler<StreamResponseMessage> createResponseHandler() {
        return new ResponseHandler<StreamResponseMessage>() { // from class: org.teleal.cling.transport.impl.apache.StreamClientImpl.4
            @Override // org.apache.http.client.ResponseHandler
            public StreamResponseMessage handleResponse(HttpResponse httpResponse) throws IOException {
                StatusLine statusLine = httpResponse.getStatusLine();
                StreamClientImpl.log.fine("Received HTTP response: " + statusLine);
                UpnpResponse responseOperation = new UpnpResponse(statusLine.getStatusCode(), statusLine.getReasonPhrase());
                StreamResponseMessage responseMessage = new StreamResponseMessage(responseOperation);
                responseMessage.setHeaders(new UpnpHeaders((Map<String, List<String>>) HeaderUtil.get(httpResponse)));
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null && entity.getContentLength() != 0) {
                    if (responseMessage.isContentTypeMissingOrText()) {
                        StreamClientImpl.log.fine("HTTP response message contains text entity");
                        responseMessage.setBody(UpnpMessage.BodyType.STRING, EntityUtils.toString(entity, XML.CHARSET_UTF8));
                    } else {
                        StreamClientImpl.log.fine("HTTP response message contains binary entity");
                        responseMessage.setBody(UpnpMessage.BodyType.BYTES, EntityUtils.toByteArray(entity));
                    }
                }
                return responseMessage;
            }
        };
    }

    protected HttpParams getRequestParams(StreamRequestMessage requestMessage) {
        HttpParams localParams = new BasicHttpParams();
        localParams.setParameter("http.protocol.version", requestMessage.getOperation().getHttpMinorVersion() == 0 ? HttpVersion.HTTP_1_0 : HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(localParams, getConfiguration().getUserAgentValue(requestMessage.getUdaMajorVersion(), requestMessage.getUdaMinorVersion()));
        return new DefaultedHttpParams(localParams, this.globalParams);
    }
}
