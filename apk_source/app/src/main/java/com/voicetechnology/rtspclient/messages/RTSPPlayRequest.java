package com.voicetechnology.rtspclient.messages;

import com.voicetechnology.rtspclient.MissingHeaderException;
import com.voicetechnology.rtspclient.RTSPRequest;
import com.voicetechnology.rtspclient.headers.SessionHeader;
import java.net.URISyntaxException;

/* loaded from: classes.dex */
public class RTSPPlayRequest extends RTSPRequest {
    public RTSPPlayRequest() {
    }

    public RTSPPlayRequest(String messageLine) throws URISyntaxException {
        super(messageLine);
    }

    @Override // com.voicetechnology.rtspclient.RTSPMessage, com.voicetechnology.rtspclient.concepts.Message
    public byte[] getBytes() throws MissingHeaderException {
        getHeader(SessionHeader.NAME);
        return super.getBytes();
    }
}
