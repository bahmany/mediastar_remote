package com.voicetechnology.rtspclient.concepts;

/* loaded from: classes.dex */
public interface ClientListener {
    void generalError(Client client, Throwable th);

    void mediaDescriptor(Client client, String str);

    void requestFailed(Client client, Request request, Throwable th);

    void response(Client client, Request request, Response response);
}
