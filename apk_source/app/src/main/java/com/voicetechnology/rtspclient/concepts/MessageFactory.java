package com.voicetechnology.rtspclient.concepts;

import com.voicetechnology.rtspclient.InvalidMessageException;
import com.voicetechnology.rtspclient.concepts.Request;
import java.net.URISyntaxException;

/* loaded from: classes.dex */
public interface MessageFactory {
    void incomingMessage(MessageBuffer messageBuffer) throws InvalidMessageException;

    Request outgoingRequest(Content content, String str, Request.Method method, int i, Header... headerArr) throws URISyntaxException;

    Request outgoingRequest(String str, Request.Method method, int i, Header... headerArr) throws URISyntaxException;

    Response outgoingResponse(int i, String str, int i2, Header... headerArr);

    Response outgoingResponse(Content content, int i, String str, int i2, Header... headerArr);
}
