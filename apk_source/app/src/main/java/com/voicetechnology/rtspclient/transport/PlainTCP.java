package com.voicetechnology.rtspclient.transport;

import com.voicetechnology.rtspclient.MissingHeaderException;
import com.voicetechnology.rtspclient.concepts.Message;
import com.voicetechnology.rtspclient.concepts.Transport;
import com.voicetechnology.rtspclient.concepts.TransportListener;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;

/* loaded from: classes.dex */
public class PlainTCP implements Transport {
    private volatile boolean connected;
    private Socket socket;
    private TransportThread thread;
    private TransportListener transportListener;

    @Override // com.voicetechnology.rtspclient.concepts.Transport
    public void connect(URI to) throws IOException {
        if (this.connected) {
            throw new IllegalStateException("Socket is still open. Close it first");
        }
        int port = to.getPort();
        if (port == -1) {
            port = 554;
        }
        this.socket = new Socket(to.getHost(), port);
        setConnected(true);
        this.thread = new TransportThread(this, this.transportListener);
        this.thread.start();
    }

    @Override // com.voicetechnology.rtspclient.concepts.Transport
    public void disconnect() throws IOException {
        setConnected(false);
        try {
            this.socket.close();
        } catch (IOException e) {
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.Transport
    public boolean isConnected() {
        return this.connected;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Transport
    public synchronized void sendMessage(Message message) throws IOException, MissingHeaderException {
        this.socket.getOutputStream().write(message.getBytes());
        this.thread.getListener().dataSent(this);
    }

    @Override // com.voicetechnology.rtspclient.concepts.Transport
    public void setTransportListener(TransportListener listener) {
        this.transportListener = listener;
        if (this.thread != null) {
            this.thread.setListener(listener);
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.Transport
    public void setUserData(Object data) {
    }

    int receive(byte[] data) throws IOException {
        return this.socket.getInputStream().read(data);
    }

    void setConnected(boolean connected) {
        this.connected = connected;
    }
}
