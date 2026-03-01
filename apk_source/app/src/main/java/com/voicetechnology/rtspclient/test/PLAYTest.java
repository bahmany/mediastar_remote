package com.voicetechnology.rtspclient.test;

import com.voicetechnology.rtspclient.concepts.Client;
import com.voicetechnology.rtspclient.concepts.Request;
import com.voicetechnology.rtspclient.concepts.Response;
import java.io.IOException;

/* loaded from: classes.dex */
public class PLAYTest extends SETUPandTEARDOWNTest {
    public static void main(String[] args) throws Throwable {
        new PLAYTest();
    }

    protected PLAYTest() throws Exception {
    }

    @Override // com.voicetechnology.rtspclient.test.SETUPandTEARDOWNTest, com.voicetechnology.rtspclient.concepts.ClientListener
    public void response(Client client, Request request, Response response) {
        try {
            super.response(client, request, response);
            if (request.getMethod() == Request.Method.PLAY && response.getStatusCode() == 200) {
                Thread.sleep(10000L);
                client.teardown();
            }
        } catch (Throwable t) {
            generalError(client, t);
        }
    }

    @Override // com.voicetechnology.rtspclient.test.SETUPandTEARDOWNTest
    protected void sessionSet(Client client) throws IOException {
        client.play();
    }
}
