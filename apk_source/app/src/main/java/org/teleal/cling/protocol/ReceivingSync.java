package org.teleal.cling.protocol;

import org.teleal.cling.UpnpService;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;

/* loaded from: classes.dex */
public abstract class ReceivingSync<IN extends StreamRequestMessage, OUT extends StreamResponseMessage> extends ReceivingAsync<IN> {
    protected OUT outputMessage;

    protected abstract OUT executeSync();

    protected ReceivingSync(UpnpService upnpService, IN inputMessage) {
        super(upnpService, inputMessage);
    }

    public OUT getOutputMessage() {
        return this.outputMessage;
    }

    @Override // org.teleal.cling.protocol.ReceivingAsync
    protected final void execute() {
        this.outputMessage = (OUT) executeSync();
    }

    public void responseSent(StreamResponseMessage responseMessage) {
    }

    public void responseException(Throwable t) {
    }

    @Override // org.teleal.cling.protocol.ReceivingAsync
    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }
}
