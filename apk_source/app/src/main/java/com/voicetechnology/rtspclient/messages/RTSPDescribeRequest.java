package com.voicetechnology.rtspclient.messages;

import com.voicetechnology.rtspclient.MissingHeaderException;
import com.voicetechnology.rtspclient.RTSPRequest;
import com.voicetechnology.rtspclient.concepts.Client;
import com.voicetechnology.rtspclient.concepts.Response;
import java.net.URISyntaxException;

/* loaded from: classes.dex */
public class RTSPDescribeRequest extends RTSPRequest {
    public RTSPDescribeRequest() {
    }

    public RTSPDescribeRequest(String messageLine) throws URISyntaxException {
        super(messageLine);
    }

    @Override // com.voicetechnology.rtspclient.RTSPMessage, com.voicetechnology.rtspclient.concepts.Message
    public byte[] getBytes() throws MissingHeaderException {
        getHeader("Accept");
        return super.getBytes();
    }

    @Override // com.voicetechnology.rtspclient.RTSPRequest, com.voicetechnology.rtspclient.concepts.Request
    public void handleResponse(Client client, Response response) {
        super.handleResponse(client, response);
        try {
            client.getClientListener().mediaDescriptor(client, new String(response.getEntityMessage().getContent().getBytes()));
        } catch (Exception e) {
            client.getClientListener().generalError(client, e);
        }
    }
}
