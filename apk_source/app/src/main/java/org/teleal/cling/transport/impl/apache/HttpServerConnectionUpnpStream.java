package org.teleal.cling.transport.impl.apache;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.ConnectionClosedException;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpHeaders;
import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.UpnpOperation;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.transport.spi.UnsupportedDataException;
import org.teleal.cling.transport.spi.UpnpStream;
import org.teleal.common.io.IO;
import org.teleal.common.util.Exceptions;

/* loaded from: classes.dex */
public class HttpServerConnectionUpnpStream extends UpnpStream {
    private static final Logger log = Logger.getLogger(UpnpStream.class.getName());
    protected final HttpServerConnection connection;
    protected final BasicHttpProcessor httpProcessor;
    protected final HttpService httpService;
    protected final HttpParams params;

    protected HttpServerConnectionUpnpStream(ProtocolFactory protocolFactory, HttpServerConnection connection, HttpParams params) {
        super(protocolFactory);
        this.httpProcessor = new BasicHttpProcessor();
        this.connection = connection;
        this.params = params;
        this.httpProcessor.addInterceptor(new ResponseDate());
        this.httpProcessor.addInterceptor(new ResponseContent());
        this.httpProcessor.addInterceptor(new ResponseConnControl());
        this.httpService = new UpnpHttpService(this.httpProcessor, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
        this.httpService.setParams(params);
    }

    public HttpServerConnection getConnection() {
        return this.connection;
    }

    @Override // java.lang.Runnable
    public void run() {
        while (!Thread.interrupted() && this.connection.isOpen()) {
            try {
                try {
                    try {
                        try {
                            try {
                                log.fine("Handling request on open connection...");
                                HttpContext context = new BasicHttpContext(null);
                                this.httpService.handleRequest(this.connection, context);
                            } catch (IOException ex) {
                                log.warning("I/O exception during HTTP request processing: " + ex.getMessage());
                                responseException(ex);
                                try {
                                    this.connection.shutdown();
                                    return;
                                } catch (IOException ex2) {
                                    log.fine("Error closing connection: " + ex2.getMessage());
                                    return;
                                }
                            }
                        } catch (SocketTimeoutException ex3) {
                            log.fine("Server-side closed socket (this is 'normal' behavior of Apache HTTP Core!): " + ex3.getMessage());
                            try {
                                this.connection.shutdown();
                                return;
                            } catch (IOException ex4) {
                                log.fine("Error closing connection: " + ex4.getMessage());
                                return;
                            }
                        }
                    } catch (HttpException ex5) {
                        throw new UnsupportedDataException("Request malformed: " + ex5.getMessage(), ex5);
                    }
                } catch (ConnectionClosedException ex6) {
                    log.fine("Client closed connection");
                    responseException(ex6);
                    try {
                        this.connection.shutdown();
                        return;
                    } catch (IOException ex7) {
                        log.fine("Error closing connection: " + ex7.getMessage());
                        return;
                    }
                }
            } finally {
                try {
                    this.connection.shutdown();
                } catch (IOException ex8) {
                    log.fine("Error closing connection: " + ex8.getMessage());
                }
            }
        }
    }

    protected class UpnpHttpService extends HttpService {
        public UpnpHttpService(HttpProcessor processor, ConnectionReuseStrategy reuse, HttpResponseFactory responseFactory) {
            super(processor, reuse, responseFactory);
        }

        @Override // org.apache.http.protocol.HttpService
        protected void doService(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext ctx) throws HttpException, IllegalStateException, IOException {
            HttpServerConnectionUpnpStream.log.fine("Processing HTTP request: " + httpRequest.getRequestLine().toString());
            String requestMethod = httpRequest.getRequestLine().getMethod();
            String requestURI = httpRequest.getRequestLine().getUri();
            try {
                StreamRequestMessage requestMessage = new StreamRequestMessage(UpnpRequest.Method.getByHttpName(requestMethod), URI.create(requestURI));
                if (((UpnpRequest) requestMessage.getOperation()).getMethod().equals(UpnpRequest.Method.UNKNOWN)) {
                    HttpServerConnectionUpnpStream.log.fine("Method not supported by UPnP stack: " + requestMethod);
                    throw new MethodNotSupportedException("Method not supported: " + requestMethod);
                }
                HttpServerConnectionUpnpStream.log.fine("Created new request message: " + requestMessage);
                int requestHttpMinorVersion = httpRequest.getProtocolVersion().getMinor();
                ((UpnpRequest) requestMessage.getOperation()).setHttpMinorVersion(requestHttpMinorVersion);
                requestMessage.setHeaders(new UpnpHeaders((Map<String, List<String>>) HeaderUtil.get(httpRequest)));
                if (httpRequest instanceof HttpEntityEnclosingRequest) {
                    HttpServerConnectionUpnpStream.log.fine("Request contains entity body, setting on UPnP message");
                    HttpEntityEnclosingRequest entityEnclosingHttpRequest = (HttpEntityEnclosingRequest) httpRequest;
                    InputStream is = null;
                    try {
                        is = entityEnclosingHttpRequest.getEntity().getContent();
                        byte[] bodyBytes = IO.readBytes(is);
                        if (bodyBytes.length > 0 && requestMessage.isContentTypeMissingOrText()) {
                            HttpServerConnectionUpnpStream.log.fine("Request contains textual entity body, converting then setting string on message");
                            requestMessage.setBodyCharacters(bodyBytes);
                        } else if (bodyBytes.length > 0) {
                            HttpServerConnectionUpnpStream.log.fine("Request contains binary entity body, setting bytes on message");
                            requestMessage.setBody(UpnpMessage.BodyType.BYTES, bodyBytes);
                        } else {
                            HttpServerConnectionUpnpStream.log.fine("Request did not contain entity body");
                        }
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                } else {
                    HttpServerConnectionUpnpStream.log.fine("Request did not contain entity body");
                }
                try {
                    StreamResponseMessage responseMsg = HttpServerConnectionUpnpStream.this.process(requestMessage);
                    if (responseMsg != null) {
                        HttpServerConnectionUpnpStream.log.fine("Sending HTTP response message: " + responseMsg);
                        httpResponse.setStatusLine(new BasicStatusLine(new ProtocolVersion("HTTP", 1, responseMsg.getOperation().getHttpMinorVersion()), responseMsg.getOperation().getStatusCode(), responseMsg.getOperation().getStatusMessage()));
                        HttpServerConnectionUpnpStream.log.fine("Response status line: " + httpResponse.getStatusLine());
                        httpResponse.setParams(getResponseParams(requestMessage.getOperation()));
                        HeaderUtil.add(httpResponse, responseMsg.getHeaders());
                        if (responseMsg.hasBody() && responseMsg.getBodyType().equals(UpnpMessage.BodyType.BYTES)) {
                            httpResponse.setEntity(new ByteArrayEntity(responseMsg.getBodyBytes()));
                        } else if (responseMsg.hasBody() && responseMsg.getBodyType().equals(UpnpMessage.BodyType.STRING)) {
                            StringEntity responseEntity = new StringEntity(responseMsg.getBodyString(), "UTF-8");
                            httpResponse.setEntity(responseEntity);
                        }
                    } else {
                        HttpServerConnectionUpnpStream.log.fine("Sending HTTP response: 404");
                        httpResponse.setStatusCode(404);
                    }
                    HttpServerConnectionUpnpStream.this.responseSent(responseMsg);
                } catch (RuntimeException ex) {
                    HttpServerConnectionUpnpStream.log.fine("Exception occured during UPnP stream processing: " + ex);
                    if (HttpServerConnectionUpnpStream.log.isLoggable(Level.FINE)) {
                        HttpServerConnectionUpnpStream.log.log(Level.FINE, "Cause: " + Exceptions.unwrap(ex), Exceptions.unwrap(ex));
                    }
                    HttpServerConnectionUpnpStream.log.fine("Sending HTTP response: 500");
                    httpResponse.setStatusCode(500);
                    HttpServerConnectionUpnpStream.this.responseException(ex);
                }
            } catch (IllegalArgumentException e) {
                String msg = "Invalid request URI: " + requestURI + ": " + e.getMessage();
                HttpServerConnectionUpnpStream.log.warning(msg);
                throw new HttpException(msg, e);
            }
        }

        protected HttpParams getResponseParams(UpnpOperation operation) {
            HttpParams localParams = new BasicHttpParams();
            return new DefaultedHttpParams(localParams, HttpServerConnectionUpnpStream.this.params);
        }
    }
}
