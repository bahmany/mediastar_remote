package org.teleal.cling.transport.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import master.flame.danmaku.danmaku.parser.IDataSource;
import org.apache.mina.proxy.handlers.http.HttpProxyConstants;
import org.cybergarage.http.HTTP;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpHeaders;
import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.transport.spi.InitializationException;
import org.teleal.cling.transport.spi.StreamClient;
import org.teleal.common.http.Headers;
import org.teleal.common.io.IO;
import org.teleal.common.util.URIUtil;
import sun.net.www.protocol.http.Handler;

/* loaded from: classes.dex */
public class StreamClientImpl implements StreamClient {
    static final String HACK_STREAM_HANDLER_SYSTEM_PROPERTY = "hackStreamHandlerProperty";
    private static final Logger log = Logger.getLogger(StreamClient.class.getName());
    protected final StreamClientConfigurationImpl configuration;

    public StreamClientImpl(StreamClientConfigurationImpl configuration) throws InitializationException {
        this.configuration = configuration;
        log.fine("Using persistent HTTP stream client connections: " + configuration.isUsePersistentConnections());
        System.setProperty("http.keepAlive", Boolean.toString(configuration.isUsePersistentConnections()));
        if (System.getProperty(HACK_STREAM_HANDLER_SYSTEM_PROPERTY) == null) {
            log.fine("Setting custom static URLStreamHandlerFactory to work around Sun JDK bugs");
            URLStreamHandlerFactory shf = new URLStreamHandlerFactory() { // from class: org.teleal.cling.transport.impl.StreamClientImpl.1
                @Override // java.net.URLStreamHandlerFactory
                public URLStreamHandler createURLStreamHandler(String protocol) {
                    StreamClientImpl.log.fine("Creating new URLStreamHandler for protocol: " + protocol);
                    if (IDataSource.SCHEME_HTTP_TAG.equals(protocol)) {
                        return new Handler() { // from class: org.teleal.cling.transport.impl.StreamClientImpl.1.1
                            protected URLConnection openConnection(URL u) throws IOException {
                                return openConnection(u, null);
                            }

                            protected URLConnection openConnection(URL u, Proxy p) throws IOException {
                                return new UpnpURLConnection(u, this);
                            }
                        };
                    }
                    return null;
                }
            };
            try {
                URL.setURLStreamHandlerFactory(shf);
                System.setProperty(HACK_STREAM_HANDLER_SYSTEM_PROPERTY, "alreadyWorkedAroundTheEvilJDK");
            } catch (Throwable th) {
                throw new InitializationException("URLStreamHandlerFactory already set for this JVM. Can't use bundled default client based on JDK's HTTPURLConnection. Consider alternatives, e.g. org.teleal.cling.transport.impl.apache.StreamClientImpl.");
            }
        }
    }

    @Override // org.teleal.cling.transport.spi.StreamClient
    public StreamClientConfigurationImpl getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.transport.spi.StreamClient
    public StreamResponseMessage sendRequest(StreamRequestMessage requestMessage) {
        StreamResponseMessage streamResponseMessageCreateResponse;
        UpnpRequest requestOperation = requestMessage.getOperation();
        log.fine("Preparing HTTP request message with method '" + requestOperation.getHttpMethodName() + "': " + requestMessage);
        URL url = URIUtil.toURL(requestOperation.getURI());
        HttpURLConnection urlConnection = null;
        try {
            try {
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod(requestOperation.getHttpMethodName());
                    urlConnection.setReadTimeout(this.configuration.getDataReadTimeoutSeconds() * 1000);
                    urlConnection.setConnectTimeout(this.configuration.getConnectionTimeoutSeconds() * 1000);
                    applyRequestProperties(urlConnection, requestMessage);
                    applyRequestBody(urlConnection, requestMessage);
                    log.fine("Sending HTTP request: " + requestMessage);
                    InputStream inputStream = urlConnection.getInputStream();
                    streamResponseMessageCreateResponse = createResponse(urlConnection, inputStream);
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                } catch (ProtocolException ex) {
                    log.fine("Unrecoverable HTTP protocol exception: " + ex);
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    streamResponseMessageCreateResponse = null;
                } catch (Exception ex2) {
                    log.info("Unrecoverable exception occured, no error response possible: " + ex2);
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    streamResponseMessageCreateResponse = null;
                }
            } catch (IOException ex3) {
                if (urlConnection == null) {
                    log.info("Could not open URL connection: " + ex3.getMessage());
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    streamResponseMessageCreateResponse = null;
                } else {
                    log.fine("Exception occured, trying to read the error stream");
                    try {
                        InputStream inputStream2 = urlConnection.getErrorStream();
                        streamResponseMessageCreateResponse = createResponse(urlConnection, inputStream2);
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    } catch (Exception errorEx) {
                        log.fine("Could not read error stream: " + errorEx);
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        streamResponseMessageCreateResponse = null;
                    }
                }
            }
            return streamResponseMessageCreateResponse;
        } catch (Throwable th) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            throw th;
        }
    }

    @Override // org.teleal.cling.transport.spi.StreamClient
    public void stop() {
    }

    protected void applyRequestProperties(HttpURLConnection urlConnection, StreamRequestMessage requestMessage) {
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setRequestProperty("User-Agent", getConfiguration().getUserAgentValue(requestMessage.getUdaMajorVersion(), requestMessage.getUdaMinorVersion()));
        applyHeaders(urlConnection, requestMessage.getHeaders());
    }

    protected void applyHeaders(HttpURLConnection urlConnection, Headers headers) {
        log.fine("Writing headers on HttpURLConnection: " + headers.size());
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String v : entry.getValue()) {
                String headerName = entry.getKey();
                log.fine("Setting header '" + headerName + "': " + v);
                urlConnection.setRequestProperty(headerName, v);
            }
        }
    }

    protected void applyRequestBody(HttpURLConnection urlConnection, StreamRequestMessage requestMessage) throws IOException {
        if (requestMessage.hasBody()) {
            urlConnection.setDoOutput(true);
            if (requestMessage.getBodyType().equals(UpnpMessage.BodyType.STRING)) {
                IO.writeUTF8(urlConnection.getOutputStream(), requestMessage.getBodyString());
            } else if (requestMessage.getBodyType().equals(UpnpMessage.BodyType.BYTES)) {
                IO.writeBytes(urlConnection.getOutputStream(), requestMessage.getBodyBytes());
            }
            urlConnection.getOutputStream().flush();
            return;
        }
        urlConnection.setDoOutput(false);
    }

    protected StreamResponseMessage createResponse(HttpURLConnection urlConnection, InputStream inputStream) throws Exception {
        if (urlConnection.getResponseCode() == -1) {
            log.fine("Did not receive valid HTTP response");
            return null;
        }
        UpnpResponse responseOperation = new UpnpResponse(urlConnection.getResponseCode(), urlConnection.getResponseMessage());
        log.fine("Received response: " + responseOperation);
        StreamResponseMessage responseMessage = new StreamResponseMessage(responseOperation);
        responseMessage.setHeaders(new UpnpHeaders(urlConnection.getHeaderFields()));
        byte[] bodyBytes = null;
        if (inputStream != null) {
            try {
                bodyBytes = IO.readBytes(inputStream);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        if (bodyBytes != null && bodyBytes.length > 0 && responseMessage.isContentTypeMissingOrText()) {
            log.fine("Response contains textual entity body, converting then setting string on message");
            responseMessage.setBodyCharacters(bodyBytes);
        } else if (bodyBytes != null && bodyBytes.length > 0) {
            log.fine("Response contains binary entity body, setting bytes on message");
            responseMessage.setBody(UpnpMessage.BodyType.BYTES, bodyBytes);
        } else {
            log.fine("Response did not contain entity body");
        }
        log.fine("Response message complete: " + responseMessage);
        return responseMessage;
    }

    static class UpnpURLConnection extends sun.net.www.protocol.http.HttpURLConnection {
        private static final String[] methods = {"GET", "POST", HTTP.HEAD, "OPTIONS", HttpProxyConstants.PUT, "DELETE", "SUBSCRIBE", "UNSUBSCRIBE", "NOTIFY"};

        protected UpnpURLConnection(URL u, Handler handler) throws IOException {
            super(u, handler);
        }

        public UpnpURLConnection(URL u, String host, int port) throws IOException {
            super(u, host, port);
        }

        public synchronized OutputStream getOutputStream() throws IOException {
            OutputStream os;
            String savedMethod = this.method;
            if (this.method.equals(HttpProxyConstants.PUT) || this.method.equals("POST") || this.method.equals("NOTIFY")) {
                this.method = HttpProxyConstants.PUT;
            } else {
                this.method = "GET";
            }
            os = super.getOutputStream();
            this.method = savedMethod;
            return os;
        }

        public void setRequestMethod(String method) throws ProtocolException {
            if (this.connected) {
                throw new ProtocolException("Cannot reset method once connected");
            }
            for (String m : methods) {
                if (m.equals(method)) {
                    this.method = method;
                    return;
                }
            }
            throw new ProtocolException("Invalid UPnP HTTP method: " + method);
        }
    }
}
