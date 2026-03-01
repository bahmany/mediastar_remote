package com.voicetechnology.rtspclient;

import com.voicetechnology.rtspclient.concepts.Client;
import com.voicetechnology.rtspclient.concepts.Message;
import com.voicetechnology.rtspclient.concepts.Request;
import com.voicetechnology.rtspclient.concepts.Response;
import java.net.URI;
import java.net.URISyntaxException;
import org.cybergarage.http.HTTP;

/* loaded from: classes.dex */
public class RTSPRequest extends RTSPMessage implements Request {
    private Request.Method method;
    private String uri;

    public RTSPRequest() {
    }

    public RTSPRequest(String messageLine) throws URISyntaxException {
        String[] parts = messageLine.split(" ");
        setLine(parts[0], Request.Method.valueOf(parts[1]));
    }

    @Override // com.voicetechnology.rtspclient.concepts.Request
    public void setLine(String uri, Request.Method method) throws URISyntaxException {
        this.method = method;
        this.uri = new URI(uri).toString();
        super.setLine(String.valueOf(method.toString()) + ' ' + uri + ' ' + Message.RTSP_VERSION_TOKEN);
    }

    @Override // com.voicetechnology.rtspclient.concepts.Request
    public Request.Method getMethod() {
        return this.method;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Request
    public String getURI() {
        return this.uri;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Request
    public void handleResponse(Client client, Response response) {
        if (testForClose(client, this) || testForClose(client, response)) {
            client.getTransport().disconnect();
        }
    }

    protected void setURI(String uri) {
        this.uri = uri;
    }

    protected void setMethod(Request.Method method) {
        this.method = method;
    }

    private boolean testForClose(Client client, Message message) {
        try {
            return message.getHeader(HTTP.CONNECTION).getRawValue().equalsIgnoreCase(HTTP.CLOSE);
        } catch (MissingHeaderException e) {
            return false;
        } catch (Exception e2) {
            client.getClientListener().generalError(client, e2);
            return false;
        }
    }
}
