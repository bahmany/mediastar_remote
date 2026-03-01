package com.voicetechnology.rtspclient.transport;

import com.voicetechnology.rtspclient.concepts.Message;
import com.voicetechnology.rtspclient.concepts.Transport;
import com.voicetechnology.rtspclient.concepts.TransportListener;

/* loaded from: classes.dex */
class SafeTransportListener implements TransportListener {
    private final TransportListener behaviour;

    public SafeTransportListener(TransportListener theBehaviour) {
        this.behaviour = theBehaviour;
    }

    @Override // com.voicetechnology.rtspclient.concepts.TransportListener
    public void connected(Transport t) {
        if (this.behaviour != null) {
            try {
                this.behaviour.connected(t);
            } catch (Throwable error) {
                this.behaviour.error(t, error);
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.TransportListener
    public void dataReceived(Transport t, byte[] data, int size) {
        if (this.behaviour != null) {
            try {
                this.behaviour.dataReceived(t, data, size);
            } catch (Throwable error) {
                this.behaviour.error(t, error);
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.TransportListener
    public void dataSent(Transport t) {
        if (this.behaviour != null) {
            try {
                this.behaviour.dataSent(t);
            } catch (Throwable error) {
                this.behaviour.error(t, error);
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.TransportListener
    public void error(Transport t, Throwable error) {
        if (this.behaviour != null) {
            this.behaviour.error(t, error);
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.TransportListener
    public void error(Transport t, Message message, Throwable error) {
        if (this.behaviour != null) {
            this.behaviour.error(t, message, error);
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.TransportListener
    public void remoteDisconnection(Transport t) {
        if (this.behaviour != null) {
            try {
                this.behaviour.remoteDisconnection(t);
            } catch (Throwable error) {
                this.behaviour.error(t, error);
            }
        }
    }
}
