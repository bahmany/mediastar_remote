package com.voicetechnology.rtspclient.concepts;

import com.voicetechnology.rtspclient.MissingHeaderException;
import java.io.IOException;
import java.net.URI;

/* loaded from: classes.dex */
public interface Transport {
    void connect(URI uri) throws IOException;

    void disconnect();

    boolean isConnected();

    void sendMessage(Message message) throws IOException, MissingHeaderException;

    void setTransportListener(TransportListener transportListener);

    void setUserData(Object obj);
}
