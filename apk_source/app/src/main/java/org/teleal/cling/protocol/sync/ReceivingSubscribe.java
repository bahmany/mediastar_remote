package org.teleal.cling.protocol.sync;

import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.LocalGENASubscription;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.gena.IncomingSubscribeRequestMessage;
import org.teleal.cling.model.message.gena.OutgoingSubscribeResponseMessage;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.resource.ServiceEventSubscriptionResource;
import org.teleal.cling.protocol.ReceivingSync;
import org.teleal.common.util.Exceptions;

/* loaded from: classes.dex */
public class ReceivingSubscribe extends ReceivingSync<StreamRequestMessage, OutgoingSubscribeResponseMessage> {
    private static final Logger log = Logger.getLogger(ReceivingSubscribe.class.getName());
    protected LocalGENASubscription subscription;

    public ReceivingSubscribe(UpnpService upnpService, StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.teleal.cling.protocol.ReceivingSync
    public OutgoingSubscribeResponseMessage executeSync() {
        ServiceEventSubscriptionResource resource = (ServiceEventSubscriptionResource) getUpnpService().getRegistry().getResource(ServiceEventSubscriptionResource.class, ((StreamRequestMessage) getInputMessage()).getUri());
        if (resource == null) {
            log.fine("No local resource found: " + getInputMessage());
            return null;
        }
        log.fine("Found local event subscription matching relative request URI: " + ((StreamRequestMessage) getInputMessage()).getUri());
        IncomingSubscribeRequestMessage requestMessage = new IncomingSubscribeRequestMessage((StreamRequestMessage) getInputMessage(), resource.getModel());
        if (requestMessage.getSubscriptionId() != null && (requestMessage.hasNotificationHeader() || requestMessage.getCallbackURLs() != null)) {
            log.fine("Subscription ID and NT or Callback in subscribe request: " + getInputMessage());
            return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.BAD_REQUEST);
        }
        if (requestMessage.getSubscriptionId() != null) {
            return processRenewal(resource.getModel(), requestMessage);
        }
        if (requestMessage.hasNotificationHeader() && requestMessage.getCallbackURLs() != null) {
            return processNewSubscription(resource.getModel(), requestMessage);
        }
        log.fine("No subscription ID, no NT or Callback, neither subscription or renewal: " + getInputMessage());
        return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
    }

    protected OutgoingSubscribeResponseMessage processRenewal(LocalService service, IncomingSubscribeRequestMessage requestMessage) {
        this.subscription = getUpnpService().getRegistry().getLocalSubscription(requestMessage.getSubscriptionId());
        if (this.subscription == null) {
            log.fine("Invalid subscription ID for renewal request: " + getInputMessage());
            return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }
        log.fine("Renewing subscription: " + this.subscription);
        this.subscription.setSubscriptionDuration(requestMessage.getRequestedTimeoutSeconds());
        if (getUpnpService().getRegistry().updateLocalSubscription(this.subscription)) {
            return new OutgoingSubscribeResponseMessage(this.subscription);
        }
        log.fine("Subscription went away before it could be renewed: " + getInputMessage());
        return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
    }

    protected OutgoingSubscribeResponseMessage processNewSubscription(LocalService service, IncomingSubscribeRequestMessage requestMessage) {
        if (requestMessage.getCallbackURLs() == null) {
            log.fine("Missing or invalid Callback URLs in subscribe request: " + getInputMessage());
            return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }
        if (!requestMessage.hasNotificationHeader()) {
            log.fine("Missing or invalid NT header in subscribe request: " + getInputMessage());
            return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }
        Integer timeoutSeconds = requestMessage.getRequestedTimeoutSeconds();
        try {
            this.subscription = new LocalGENASubscription(service, timeoutSeconds, requestMessage.getCallbackURLs()) { // from class: org.teleal.cling.protocol.sync.ReceivingSubscribe.1
                @Override // org.teleal.cling.model.gena.GENASubscription
                public void established() {
                }

                @Override // org.teleal.cling.model.gena.LocalGENASubscription
                public void ended(CancelReason reason) {
                }

                @Override // org.teleal.cling.model.gena.GENASubscription
                public void eventReceived() {
                    ReceivingSubscribe.this.getUpnpService().getConfiguration().getSyncProtocolExecutor().execute(ReceivingSubscribe.this.getUpnpService().getProtocolFactory().createSendingEvent(this));
                }
            };
            log.fine("Adding subscription to registry: " + this.subscription);
            getUpnpService().getRegistry().addLocalSubscription(this.subscription);
            log.fine("Returning subscription response, waiting to send initial event");
            return new OutgoingSubscribeResponseMessage(this.subscription);
        } catch (Exception ex) {
            log.warning("Couldn't create local subscription to service: " + Exceptions.unwrap(ex));
            return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override // org.teleal.cling.protocol.ReceivingSync
    public void responseSent(StreamResponseMessage responseMessage) {
        if (this.subscription != null) {
            if (responseMessage != null && !responseMessage.getOperation().isFailed() && this.subscription.getCurrentSequence().getValue().longValue() == 0) {
                log.fine("Establishing subscription");
                this.subscription.registerOnService();
                this.subscription.establish();
                log.fine("Response to subscription sent successfully, now sending initial event asynchronously");
                getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(getUpnpService().getProtocolFactory().createSendingEvent(this.subscription));
                return;
            }
            if (this.subscription.getCurrentSequence().getValue().longValue() == 0) {
                log.fine("Subscription request's response aborted, not sending initial event");
                if (responseMessage == null) {
                    log.fine("Reason: No response at all from subscriber");
                } else {
                    log.fine("Reason: " + responseMessage.getOperation());
                }
                log.fine("Removing subscription from registry: " + this.subscription);
                getUpnpService().getRegistry().removeLocalSubscription(this.subscription);
            }
        }
    }

    @Override // org.teleal.cling.protocol.ReceivingSync
    public void responseException(Throwable t) {
        if (this.subscription != null) {
            log.fine("Response could not be send to subscriber, removing local GENA subscription: " + this.subscription);
            getUpnpService().getRegistry().removeLocalSubscription(this.subscription);
        }
    }
}
