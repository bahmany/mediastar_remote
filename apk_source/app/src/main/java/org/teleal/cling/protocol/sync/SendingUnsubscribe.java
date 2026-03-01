package org.teleal.cling.protocol.sync;

import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.gena.OutgoingUnsubscribeRequestMessage;
import org.teleal.cling.protocol.SendingSync;

/* loaded from: classes.dex */
public class SendingUnsubscribe extends SendingSync<OutgoingUnsubscribeRequestMessage, StreamResponseMessage> {
    private static final Logger log = Logger.getLogger(SendingUnsubscribe.class.getName());
    protected final RemoteGENASubscription subscription;

    public SendingUnsubscribe(UpnpService upnpService, RemoteGENASubscription subscription) {
        super(upnpService, new OutgoingUnsubscribeRequestMessage(subscription));
        this.subscription = subscription;
    }

    @Override // org.teleal.cling.protocol.SendingSync
    protected StreamResponseMessage executeSync() {
        log.fine("Sending unsubscribe request: " + getInputMessage());
        final StreamResponseMessage response = getUpnpService().getRouter().send(getInputMessage());
        getUpnpService().getRegistry().removeRemoteSubscription(this.subscription);
        getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingUnsubscribe.1
            @Override // java.lang.Runnable
            public void run() {
                if (response == null) {
                    SendingUnsubscribe.log.fine("Unsubscribe failed, no response received");
                    SendingUnsubscribe.this.subscription.end(CancelReason.UNSUBSCRIBE_FAILED, null);
                } else if (response.getOperation().isFailed()) {
                    SendingUnsubscribe.log.fine("Unsubscribe failed, response was: " + response);
                    SendingUnsubscribe.this.subscription.end(CancelReason.UNSUBSCRIBE_FAILED, response.getOperation());
                } else {
                    SendingUnsubscribe.log.fine("Unsubscribe successful, response was: " + response);
                    SendingUnsubscribe.this.subscription.end(null, response.getOperation());
                }
            }
        });
        return response;
    }
}
