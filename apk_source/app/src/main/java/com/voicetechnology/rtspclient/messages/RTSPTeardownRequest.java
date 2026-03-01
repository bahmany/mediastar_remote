package com.voicetechnology.rtspclient.messages;

import com.voicetechnology.rtspclient.MissingHeaderException;
import com.voicetechnology.rtspclient.RTSPRequest;
import com.voicetechnology.rtspclient.concepts.Client;
import com.voicetechnology.rtspclient.concepts.Response;
import com.voicetechnology.rtspclient.headers.SessionHeader;
import java.net.URISyntaxException;

/* loaded from: classes.dex */
public class RTSPTeardownRequest extends RTSPRequest {
    public RTSPTeardownRequest() {
    }

    public RTSPTeardownRequest(String messageLine) throws URISyntaxException {
        super(messageLine);
    }

    @Override // com.voicetechnology.rtspclient.RTSPMessage, com.voicetechnology.rtspclient.concepts.Message
    public byte[] getBytes() throws MissingHeaderException {
        getHeader(SessionHeader.NAME);
        return super.getBytes();
    }

    @Override // com.voicetechnology.rtspclient.RTSPRequest, com.voicetechnology.rtspclient.concepts.Request
    public void handleResponse(Client client, Response response) {
        super.handleResponse(client, response);
        if (response.getStatusCode() == 200) {
            client.setSession(null);
        }
        client.getTransport().disconnect();
    }
}
