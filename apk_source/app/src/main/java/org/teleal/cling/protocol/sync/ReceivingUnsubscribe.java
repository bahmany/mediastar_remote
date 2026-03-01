package org.teleal.cling.protocol.sync;

import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.gena.LocalGENASubscription;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.gena.IncomingUnsubscribeRequestMessage;
import org.teleal.cling.model.resource.ServiceEventSubscriptionResource;
import org.teleal.cling.protocol.ReceivingSync;

/* loaded from: classes.dex */
public class ReceivingUnsubscribe extends ReceivingSync<StreamRequestMessage, StreamResponseMessage> {
    private static final Logger log = Logger.getLogger(ReceivingUnsubscribe.class.getName());

    public ReceivingUnsubscribe(UpnpService upnpService, StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.teleal.cling.protocol.ReceivingSync
    protected StreamResponseMessage executeSync() {
        ServiceEventSubscriptionResource resource = (ServiceEventSubscriptionResource) getUpnpService().getRegistry().getResource(ServiceEventSubscriptionResource.class, ((StreamRequestMessage) getInputMessage()).getUri());
        if (resource == null) {
            log.fine("No local resource found: " + getInputMessage());
            return null;
        }
        log.fine("Found local event subscription matching relative request URI: " + ((StreamRequestMessage) getInputMessage()).getUri());
        IncomingUnsubscribeRequestMessage requestMessage = new IncomingUnsubscribeRequestMessage((StreamRequestMessage) getInputMessage(), resource.getModel());
        if (requestMessage.getSubscriptionId() != null && (requestMessage.hasNotificationHeader() || requestMessage.hasCallbackHeader())) {
            log.fine("Subscription ID and NT or Callback in unsubcribe request: " + getInputMessage());
            return new StreamResponseMessage(UpnpResponse.Status.BAD_REQUEST);
        }
        LocalGENASubscription subscription = getUpnpService().getRegistry().getLocalSubscription(requestMessage.getSubscriptionId());
        if (subscription == null) {
            log.fine("Invalid subscription ID for unsubscribe request: " + getInputMessage());
            return new StreamResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }
        log.fine("Unregistering subscription: " + subscription);
        if (getUpnpService().getRegistry().removeLocalSubscription(subscription)) {
            subscription.end(null);
        } else {
            log.fine("Subscription was already removed from registry");
        }
        return new StreamResponseMessage(UpnpResponse.Status.OK);
    }
}
