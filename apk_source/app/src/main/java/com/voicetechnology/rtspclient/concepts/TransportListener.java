package com.voicetechnology.rtspclient.concepts;

/* loaded from: classes.dex */
public interface TransportListener {
    void connected(Transport transport) throws Throwable;

    void dataReceived(Transport transport, byte[] bArr, int i) throws Throwable;

    void dataSent(Transport transport) throws Throwable;

    void error(Transport transport, Message message, Throwable th);

    void error(Transport transport, Throwable th);

    void remoteDisconnection(Transport transport) throws Throwable;
}
