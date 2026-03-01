package com.voicetechnology.rtspclient.messages;

import com.voicetechnology.rtspclient.RTSPRequest;
import com.voicetechnology.rtspclient.concepts.Message;
import com.voicetechnology.rtspclient.concepts.Request;
import java.net.URI;
import java.net.URISyntaxException;

/* loaded from: classes.dex */
public class RTSPOptionsRequest extends RTSPRequest {
    public RTSPOptionsRequest() {
    }

    public RTSPOptionsRequest(String line) throws URISyntaxException {
        super(line);
    }

    @Override // com.voicetechnology.rtspclient.RTSPRequest, com.voicetechnology.rtspclient.concepts.Request
    public void setLine(String uri, Request.Method method) throws URISyntaxException {
        setMethod(method);
        setURI("*".equals(uri) ? uri : new URI(uri).toString());
        super.setLine(String.valueOf(method.toString()) + ' ' + uri + ' ' + Message.RTSP_VERSION_TOKEN);
    }
}
