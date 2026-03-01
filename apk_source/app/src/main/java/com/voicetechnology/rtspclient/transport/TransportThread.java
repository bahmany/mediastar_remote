package com.voicetechnology.rtspclient.transport;

import com.voicetechnology.rtspclient.concepts.TransportListener;
import java.io.IOException;

/* compiled from: PlainTCP.java */
/* loaded from: classes.dex */
class TransportThread extends Thread {
    private volatile SafeTransportListener listener;
    private final PlainTCP transport;

    public TransportThread(PlainTCP transport, TransportListener listener) {
        this.transport = transport;
        this.listener = new SafeTransportListener(listener);
    }

    public SafeTransportListener getListener() {
        return this.listener;
    }

    public void setListener(TransportListener listener) {
        new SafeTransportListener(listener);
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        this.listener.connected(this.transport);
        byte[] buffer = new byte[2048];
        while (this.transport.isConnected()) {
            try {
                int read = this.transport.receive(buffer);
                if (read == -1) {
                    this.transport.setConnected(false);
                    this.listener.remoteDisconnection(this.transport);
                } else {
                    this.listener.dataReceived(this.transport, buffer, read);
                }
            } catch (IOException e) {
                this.listener.error(this.transport, e);
            }
        }
    }
}
