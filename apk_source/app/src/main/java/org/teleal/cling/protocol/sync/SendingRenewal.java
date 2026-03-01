package org.teleal.cling.protocol.sync;

import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.gena.IncomingSubscribeResponseMessage;
import org.teleal.cling.model.message.gena.OutgoingRenewalRequestMessage;
import org.teleal.cling.protocol.SendingSync;

/* loaded from: classes.dex */
public class SendingRenewal extends SendingSync<OutgoingRenewalRequestMessage, IncomingSubscribeResponseMessage> {
    private static final Logger log = Logger.getLogger(SendingRenewal.class.getName());
    protected final RemoteGENASubscription subscription;

    public SendingRenewal(UpnpService upnpService, RemoteGENASubscription subscription) {
        super(upnpService, new OutgoingRenewalRequestMessage(subscription));
        this.subscription = subscription;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.teleal.cling.protocol.SendingSync
    public IncomingSubscribeResponseMessage executeSync() {
        log.fine("Sending subscription renewal request: " + getInputMessage());
        StreamResponseMessage response = getUpnpService().getRouter().send(getInputMessage());
        if (response == null) {
            log.fine("Subscription renewal failed, no response received");
            getUpnpService().getRegistry().removeRemoteSubscription(this.subscription);
            getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingRenewal.1
                @Override // java.lang.Runnable
                public void run() {
                    SendingRenewal.this.subscription.end(CancelReason.RENEWAL_FAILED, null);
                }
            });
            return null;
        }
        final IncomingSubscribeResponseMessage responseMessage = new IncomingSubscribeResponseMessage(response);
        if (response.getOperation().isFailed()) {
            log.fine("Subscription renewal failed, response was: " + response);
            getUpnpService().getRegistry().removeRemoteSubscription(this.subscription);
            getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.SendingRenewal.2
                @Override // java.lang.Runnable
                public void run() {
                    SendingRenewal.this.subscription.end(CancelReason.RENEWAL_FAILED, responseMessage.getOperation());
                }
            });
            return responseMessage;
        }
        log.fine("Subscription renewed, updating in registry, response was: " + response);
        this.subscription.setActualSubscriptionDurationSeconds(responseMessage.getSubscriptionDurationSeconds());
        getUpnpService().getRegistry().updateRemoteSubscription(this.subscription);
        return responseMessage;
    }
}
