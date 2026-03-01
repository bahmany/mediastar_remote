package com.voicetechnology.rtspclient;

import com.voicetechnology.rtspclient.concepts.Client;
import com.voicetechnology.rtspclient.concepts.ClientListener;
import com.voicetechnology.rtspclient.concepts.Header;
import com.voicetechnology.rtspclient.concepts.Message;
import com.voicetechnology.rtspclient.concepts.MessageBuffer;
import com.voicetechnology.rtspclient.concepts.MessageFactory;
import com.voicetechnology.rtspclient.concepts.Request;
import com.voicetechnology.rtspclient.concepts.Response;
import com.voicetechnology.rtspclient.concepts.Transport;
import com.voicetechnology.rtspclient.concepts.TransportListener;
import com.voicetechnology.rtspclient.headers.SessionHeader;
import com.voicetechnology.rtspclient.headers.TransportHeader;
import com.voicetechnology.rtspclient.messages.RTSPOptionsRequest;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.cybergarage.http.HTTP;

/* loaded from: classes.dex */
public class RTSPClient implements Client, TransportListener {
    private ClientListener clientListener;
    private SessionHeader session;
    private Transport transport;
    private URI uri;
    private MessageFactory messageFactory = new RTSPMessageFactory();
    private volatile int cseq = 0;
    private Map<Integer, Request> outstanding = new HashMap();
    private MessageBuffer messageBuffer = new MessageBuffer();

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public Transport getTransport() {
        return this.transport;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public void setSession(SessionHeader session) {
        this.session = session;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public MessageFactory getMessageFactory() {
        return this.messageFactory;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public URI getURI() {
        return this.uri;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public void options(String uri, URI endpoint) throws URISyntaxException, IOException {
        try {
            RTSPOptionsRequest message = (RTSPOptionsRequest) this.messageFactory.outgoingRequest(uri, Request.Method.OPTIONS, nextCSeq(), this.session);
            if (!getTransport().isConnected()) {
                message.addHeader(new Header(HTTP.CONNECTION, HTTP.CLOSE));
            }
            send(message, endpoint);
        } catch (MissingHeaderException e) {
            if (this.clientListener != null) {
                this.clientListener.generalError(this, e);
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public void play() throws IOException {
        try {
            send(this.messageFactory.outgoingRequest(this.uri.toString(), Request.Method.PLAY, nextCSeq(), this.session));
        } catch (Exception e) {
            if (this.clientListener != null) {
                this.clientListener.generalError(this, e);
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public void play(String Url) throws IOException {
        try {
            send(this.messageFactory.outgoingRequest(Url, Request.Method.PLAY, nextCSeq(), this.session));
        } catch (Exception e) {
            if (this.clientListener != null) {
                this.clientListener.generalError(this, e);
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public void record() throws IOException {
        throw new UnsupportedOperationException("Recording is not supported in current version.");
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public void setClientListener(ClientListener listener) {
        this.clientListener = listener;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public ClientListener getClientListener() {
        return this.clientListener;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public void setTransport(Transport transport) {
        this.transport = transport;
        transport.setTransportListener(this);
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public void describe(URI uri) throws IOException {
        this.uri = uri;
        try {
            send(this.messageFactory.outgoingRequest(uri.toString(), Request.Method.DESCRIBE, nextCSeq(), new Header("Accept", "application/sdp")));
        } catch (Exception e) {
            if (this.clientListener != null) {
                this.clientListener.generalError(this, e);
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public void setup(URI uri, int localPort) throws IOException {
        this.uri = uri;
        try {
            String portParam = "client_port=" + localPort + "-" + (localPort + 1);
            send(getSetup(uri.toString(), localPort, new TransportHeader(TransportHeader.LowerTransport.DEFAULT, "unicast", portParam), this.session));
        } catch (Exception e) {
            if (this.clientListener != null) {
                this.clientListener.generalError(this, e);
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public void setup(URI uri, int localPort, String resource) throws IOException {
        this.uri = uri;
        try {
            String portParam = "client_port=" + localPort + "-" + (localPort + 1);
            String finalURI = uri.toString();
            if (resource != null && !resource.equals("*")) {
                finalURI = String.valueOf(finalURI) + '/' + resource;
            }
            send(getSetup(finalURI, localPort, new TransportHeader(TransportHeader.LowerTransport.DEFAULT, "unicast", portParam), this.session));
        } catch (Exception e) {
            if (this.clientListener != null) {
                this.clientListener.generalError(this, e);
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public void teardown() {
        if (this.session != null) {
            try {
                send(this.messageFactory.outgoingRequest(this.uri.toString(), Request.Method.TEARDOWN, nextCSeq(), this.session, new Header(HTTP.CONNECTION, HTTP.CLOSE)));
            } catch (Exception e) {
                if (this.clientListener != null) {
                    this.clientListener.generalError(this, e);
                }
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.TransportListener
    public void connected(Transport t) throws Throwable {
    }

    @Override // com.voicetechnology.rtspclient.concepts.TransportListener
    public void dataReceived(Transport t, byte[] data, int size) throws Throwable {
        Request request;
        this.messageBuffer.addData(data, size);
        while (this.messageBuffer.getLength() > 0) {
            try {
                this.messageFactory.incomingMessage(this.messageBuffer);
                this.messageBuffer.discardData();
                Message message = this.messageBuffer.getMessage();
                if (message instanceof Request) {
                    send(this.messageFactory.outgoingResponse(405, "Method Not Allowed", message.getCSeq().getValue(), new Header[0]));
                } else {
                    synchronized (this.outstanding) {
                        request = this.outstanding.remove(Integer.valueOf(message.getCSeq().getValue()));
                    }
                    Response response = (Response) message;
                    request.handleResponse(this, response);
                    this.clientListener.response(this, request, response);
                }
            } catch (IncompleteMessageException e) {
                return;
            } catch (InvalidMessageException e2) {
                this.messageBuffer.discardData();
                if (this.clientListener != null) {
                    this.clientListener.generalError(this, e2.getCause());
                }
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.TransportListener
    public void dataSent(Transport t) throws Throwable {
    }

    @Override // com.voicetechnology.rtspclient.concepts.TransportListener
    public void error(Transport t, Throwable error) {
        this.clientListener.generalError(this, error);
    }

    @Override // com.voicetechnology.rtspclient.concepts.TransportListener
    public void error(Transport t, Message message, Throwable error) {
        this.clientListener.requestFailed(this, (Request) message, error);
    }

    @Override // com.voicetechnology.rtspclient.concepts.TransportListener
    public void remoteDisconnection(Transport t) throws Throwable {
        synchronized (this.outstanding) {
            for (Map.Entry<Integer, Request> request : this.outstanding.entrySet()) {
                this.clientListener.requestFailed(this, request.getValue(), new SocketException("Socket has been closed"));
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public int nextCSeq() {
        int i = this.cseq;
        this.cseq = i + 1;
        return i;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Client
    public void send(Message message) throws IOException, MissingHeaderException {
        send(message, this.uri);
    }

    private void send(Message message, URI endpoint) throws IOException, MissingHeaderException {
        if (!this.transport.isConnected()) {
            this.transport.connect(endpoint);
        }
        if (message instanceof Request) {
            Request request = (Request) message;
            synchronized (this.outstanding) {
                this.outstanding.put(Integer.valueOf(message.getCSeq().getValue()), request);
            }
            try {
                this.transport.sendMessage(message);
                return;
            } catch (IOException e) {
                this.clientListener.requestFailed(this, request, e);
                return;
            }
        }
        this.transport.sendMessage(message);
    }

    private Request getSetup(String uri, int localPort, Header... headers) throws URISyntaxException {
        return getMessageFactory().outgoingRequest(uri, Request.Method.SETUP, nextCSeq(), headers);
    }
}
