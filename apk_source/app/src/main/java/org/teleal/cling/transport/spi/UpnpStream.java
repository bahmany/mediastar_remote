package org.teleal.cling.transport.spi;

import java.util.logging.Logger;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.protocol.ProtocolCreationException;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.protocol.ReceivingSync;
import org.teleal.common.util.Exceptions;

/* loaded from: classes.dex */
public abstract class UpnpStream implements Runnable {
    private static Logger log = Logger.getLogger(UpnpStream.class.getName());
    protected final ProtocolFactory protocolFactory;
    protected ReceivingSync syncProtocol;

    protected UpnpStream(ProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }

    public StreamResponseMessage process(StreamRequestMessage requestMsg) {
        log.fine("Processing stream request message: " + requestMsg);
        try {
            this.syncProtocol = getProtocolFactory().createReceivingSync(requestMsg);
            log.fine("Running protocol for synchronous message processing: " + this.syncProtocol);
            this.syncProtocol.run();
            StreamResponseMessage responseMsg = this.syncProtocol.getOutputMessage();
            if (responseMsg == null) {
                log.finer("Protocol did not return any response message");
                return null;
            }
            log.finer("Protocol returned response: " + responseMsg);
            return responseMsg;
        } catch (ProtocolCreationException ex) {
            log.warning("Processing stream request failed - " + Exceptions.unwrap(ex).toString());
            return new StreamResponseMessage(UpnpResponse.Status.NOT_IMPLEMENTED);
        }
    }

    protected void responseSent(StreamResponseMessage responseMessage) {
        if (this.syncProtocol != null) {
            this.syncProtocol.responseSent(responseMessage);
        }
    }

    protected void responseException(Throwable t) {
        if (this.syncProtocol != null) {
            this.syncProtocol.responseException(t);
        }
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }
}
