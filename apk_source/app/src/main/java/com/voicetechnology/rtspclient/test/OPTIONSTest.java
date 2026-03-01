package com.voicetechnology.rtspclient.test;

import com.voicetechnology.rtspclient.RTSPClient;
import com.voicetechnology.rtspclient.concepts.Client;
import com.voicetechnology.rtspclient.concepts.ClientListener;
import com.voicetechnology.rtspclient.concepts.Request;
import com.voicetechnology.rtspclient.concepts.Response;
import com.voicetechnology.rtspclient.transport.PlainTCP;
import java.net.URI;

/* loaded from: classes.dex */
public class OPTIONSTest implements ClientListener {
    public static void main(String[] args) throws Throwable {
        new OPTIONSTest();
    }

    private OPTIONSTest() throws Exception {
        RTSPClient client = new RTSPClient();
        client.setTransport(new PlainTCP());
        client.setClientListener(this);
        client.options("*", new URI("rtsp://rmv8.bbc.net.uk/1xtra/"));
    }

    @Override // com.voicetechnology.rtspclient.concepts.ClientListener
    public void requestFailed(Client client, Request request, Throwable cause) {
        System.out.println("Request failed \n" + request);
    }

    @Override // com.voicetechnology.rtspclient.concepts.ClientListener
    public void response(Client client, Request request, Response response) {
        System.out.println("Got response: \n" + response);
        System.out.println("for the request: \n" + request);
    }

    @Override // com.voicetechnology.rtspclient.concepts.ClientListener
    public void generalError(Client client, Throwable error) {
        error.printStackTrace();
    }

    @Override // com.voicetechnology.rtspclient.concepts.ClientListener
    public void mediaDescriptor(Client client, String descriptor) {
    }
}
