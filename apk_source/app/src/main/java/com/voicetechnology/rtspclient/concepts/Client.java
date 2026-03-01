package com.voicetechnology.rtspclient.concepts;

import com.voicetechnology.rtspclient.MissingHeaderException;
import com.voicetechnology.rtspclient.headers.SessionHeader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/* loaded from: classes.dex */
public interface Client {
    void describe(URI uri) throws IOException;

    ClientListener getClientListener();

    MessageFactory getMessageFactory();

    Transport getTransport();

    URI getURI();

    int nextCSeq();

    void options(String str, URI uri) throws URISyntaxException, IOException;

    void play() throws IOException;

    void play(String str) throws IOException;

    void record() throws IOException;

    void send(Message message) throws IOException, MissingHeaderException;

    void setClientListener(ClientListener clientListener);

    void setSession(SessionHeader sessionHeader);

    void setTransport(Transport transport);

    void setup(URI uri, int i) throws IOException;

    void setup(URI uri, int i, String str) throws IOException;

    void teardown();
}
