package com.voicetechnology.rtspclient.test;

import com.voicetechnology.rtspclient.RTSPClient;
import com.voicetechnology.rtspclient.concepts.Client;
import com.voicetechnology.rtspclient.concepts.ClientListener;
import com.voicetechnology.rtspclient.concepts.Request;
import com.voicetechnology.rtspclient.concepts.Response;
import com.voicetechnology.rtspclient.transport.PlainTCP;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class SETUPandTEARDOWNTest implements ClientListener {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$voicetechnology$rtspclient$concepts$Request$Method = null;
    private static final String TARGET_URI = "rtsp://192.168.0.100:8554/a.ts";
    private String controlURI;
    private int port;
    private final List<String> resourceList;

    static /* synthetic */ int[] $SWITCH_TABLE$com$voicetechnology$rtspclient$concepts$Request$Method() {
        int[] iArr = $SWITCH_TABLE$com$voicetechnology$rtspclient$concepts$Request$Method;
        if (iArr == null) {
            iArr = new int[Request.Method.valuesCustom().length];
            try {
                iArr[Request.Method.DESCRIBE.ordinal()] = 2;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Request.Method.OPTIONS.ordinal()] = 1;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Request.Method.PLAY.ordinal()] = 4;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[Request.Method.RECORD.ordinal()] = 5;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[Request.Method.SETUP.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[Request.Method.TEARDOWN.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            $SWITCH_TABLE$com$voicetechnology$rtspclient$concepts$Request$Method = iArr;
        }
        return iArr;
    }

    public static void main(String[] args) throws Throwable {
        new SETUPandTEARDOWNTest();
    }

    protected SETUPandTEARDOWNTest() throws Exception {
        RTSPClient client = new RTSPClient();
        client.setTransport(new PlainTCP());
        client.setClientListener(this);
        client.describe(new URI(TARGET_URI));
        this.resourceList = Collections.synchronizedList(new LinkedList());
        this.port = 2000;
    }

    @Override // com.voicetechnology.rtspclient.concepts.ClientListener
    public void requestFailed(Client client, Request request, Throwable cause) {
        System.out.println("Request failed \n" + request);
        cause.printStackTrace();
    }

    @Override // com.voicetechnology.rtspclient.concepts.ClientListener
    public void response(Client client, Request request, Response response) {
        try {
            System.out.println("Got response: \n" + response);
            System.out.println("for the request: \n" + request);
            if (response.getStatusCode() == 200) {
                switch ($SWITCH_TABLE$com$voicetechnology$rtspclient$concepts$Request$Method()[request.getMethod().ordinal()]) {
                    case 2:
                        System.out.println(this.resourceList);
                        if (this.resourceList.get(0).equals("*")) {
                            this.controlURI = request.getURI();
                            this.resourceList.remove(0);
                        }
                        if (this.resourceList.size() > 0) {
                            client.setup(new URI(this.controlURI), nextPort(), this.resourceList.remove(0));
                            break;
                        } else {
                            client.setup(new URI(this.controlURI), nextPort());
                            break;
                        }
                    case 3:
                        if (this.resourceList.size() > 0) {
                            client.setup(new URI(this.controlURI), nextPort(), this.resourceList.remove(0));
                            break;
                        } else {
                            sessionSet(client);
                            break;
                        }
                }
                return;
            }
            client.teardown();
        } catch (Throwable t) {
            generalError(client, t);
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.ClientListener
    public void generalError(Client client, Throwable error) {
        error.printStackTrace();
    }

    @Override // com.voicetechnology.rtspclient.concepts.ClientListener
    public void mediaDescriptor(Client client, String descriptor) {
        System.out.println("Session Descriptor\n" + descriptor);
        while (true) {
            int position = descriptor.indexOf("control:");
            if (position > -1) {
                descriptor = descriptor.substring("control:".length() + position);
                this.resourceList.add(descriptor.substring(0, descriptor.indexOf(13)));
            } else {
                return;
            }
        }
    }

    protected void sessionSet(Client client) throws IOException {
        client.teardown();
    }

    private int nextPort() {
        int i = this.port + 2;
        this.port = i;
        return i - 2;
    }
}
