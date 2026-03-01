package org.teleal.cling.protocol.sync;

import java.net.URL;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.gena.LocalGENASubscription;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.gena.OutgoingEventRequestMessage;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.protocol.SendingSync;
import org.teleal.cling.transport.spi.UnsupportedDataException;

/* loaded from: classes.dex */
public class SendingEvent extends SendingSync<OutgoingEventRequestMessage, StreamResponseMessage> {
    private static final Logger log = Logger.getLogger(SendingEvent.class.getName());
    protected final UnsignedIntegerFourBytes currentSequence;
    protected final OutgoingEventRequestMessage[] requestMessages;
    protected final String subscriptionId;

    public SendingEvent(UpnpService upnpService, LocalGENASubscription subscription) throws UnsupportedDataException {
        super(upnpService, null);
        this.subscriptionId = subscription.getSubscriptionId();
        this.requestMessages = new OutgoingEventRequestMessage[subscription.getCallbackURLs().size()];
        int i = 0;
        for (URL url : subscription.getCallbackURLs()) {
            this.requestMessages[i] = new OutgoingEventRequestMessage(subscription, url);
            getUpnpService().getConfiguration().getGenaEventProcessor().writeBody(this.requestMessages[i]);
            i++;
        }
        this.currentSequence = subscription.getCurrentSequence();
        subscription.incrementSequence();
    }

    @Override // org.teleal.cling.protocol.SendingSync
    protected StreamResponseMessage executeSync() {
        log.fine("Sending event for subscription: " + this.subscriptionId);
        StreamResponseMessage lastResponse = null;
        for (OutgoingEventRequestMessage requestMessage : this.requestMessages) {
            if (this.currentSequence.getValue().longValue() == 0) {
                log.fine("Sending initial event message to callback URL: " + requestMessage.getUri());
            } else {
                log.fine("Sending event message '" + this.currentSequence + "' to callback URL: " + requestMessage.getUri());
            }
            lastResponse = getUpnpService().getRouter().send(requestMessage);
            log.fine("Received event callback response: " + lastResponse);
        }
        return lastResponse;
    }
}
