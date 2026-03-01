package com.voicetechnology.rtspclient.messages;

import com.voicetechnology.rtspclient.MissingHeaderException;
import com.voicetechnology.rtspclient.RTSPRequest;
import com.voicetechnology.rtspclient.concepts.Client;
import com.voicetechnology.rtspclient.concepts.Response;
import com.voicetechnology.rtspclient.headers.SessionHeader;
import com.voicetechnology.rtspclient.headers.TransportHeader;
import java.net.URISyntaxException;

/* loaded from: classes.dex */
public class RTSPSetupRequest extends RTSPRequest {
    public RTSPSetupRequest() {
    }

    public RTSPSetupRequest(String line) throws URISyntaxException {
        super(line);
    }

    @Override // com.voicetechnology.rtspclient.RTSPMessage, com.voicetechnology.rtspclient.concepts.Message
    public byte[] getBytes() throws MissingHeaderException {
        getHeader(TransportHeader.NAME);
        return super.getBytes();
    }

    @Override // com.voicetechnology.rtspclient.RTSPRequest, com.voicetechnology.rtspclient.concepts.Request
    public void handleResponse(Client client, Response response) {
        super.handleResponse(client, response);
        try {
            if (response.getStatusCode() == 200) {
                client.setSession((SessionHeader) response.getHeader(SessionHeader.NAME));
            }
        } catch (MissingHeaderException e) {
            client.getClientListener().generalError(client, e);
        }
    }
}
