package org.teleal.cling.protocol.sync;

import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.gena.IncomingEventRequestMessage;
import org.teleal.cling.model.message.gena.OutgoingEventResponseMessage;
import org.teleal.cling.model.resource.ServiceEventCallbackResource;
import org.teleal.cling.protocol.ReceivingSync;
import org.teleal.cling.transport.spi.UnsupportedDataException;

/* loaded from: classes.dex */
public class ReceivingEvent extends ReceivingSync<StreamRequestMessage, OutgoingEventResponseMessage> {
    private static final Logger log = Logger.getLogger(ReceivingEvent.class.getName());

    public ReceivingEvent(UpnpService upnpService, StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.teleal.cling.protocol.ReceivingSync
    public OutgoingEventResponseMessage executeSync() {
        OutgoingEventResponseMessage outgoingEventResponseMessage;
        if (!((StreamRequestMessage) getInputMessage()).isContentTypeTextUDA()) {
            log.warning("Received without or with invalid Content-Type: " + getInputMessage());
        }
        ServiceEventCallbackResource resource = (ServiceEventCallbackResource) getUpnpService().getRegistry().getResource(ServiceEventCallbackResource.class, ((StreamRequestMessage) getInputMessage()).getUri());
        if (resource == null) {
            log.fine("No local resource found: " + getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.NOT_FOUND));
        }
        final IncomingEventRequestMessage requestMessage = new IncomingEventRequestMessage((StreamRequestMessage) getInputMessage(), resource.getModel());
        if (requestMessage.getSubscrptionId() == null) {
            log.fine("Subscription ID missing in event request: " + getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }
        if (!requestMessage.hasValidNotificationHeaders()) {
            log.fine("Missing NT and/or NTS headers in event request: " + getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.BAD_REQUEST));
        }
        if (!requestMessage.hasValidNotificationHeaders()) {
            log.fine("Invalid NT and/or NTS headers in event request: " + getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }
        if (requestMessage.getSequence() == null) {
            log.fine("Sequence missing in event request: " + getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }
        try {
            getUpnpService().getConfiguration().getGenaEventProcessor().readBody(requestMessage);
            try {
                getUpnpService().getRegistry().lockRemoteSubscriptions();
                final RemoteGENASubscription subscription = getUpnpService().getRegistry().getRemoteSubscription(requestMessage.getSubscrptionId());
                if (subscription == null) {
                    log.severe("Invalid subscription ID, no active subscription: " + requestMessage);
                    outgoingEventResponseMessage = new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
                } else {
                    getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() { // from class: org.teleal.cling.protocol.sync.ReceivingEvent.1
                        @Override // java.lang.Runnable
                        public void run() {
                            ReceivingEvent.log.fine("Calling active subscription with event state variable values");
                            subscription.receive(requestMessage.getSequence(), requestMessage.getStateVariableValues());
                        }
                    });
                    getUpnpService().getRegistry().unlockRemoteSubscriptions();
                    outgoingEventResponseMessage = new OutgoingEventResponseMessage();
                }
                return outgoingEventResponseMessage;
            } finally {
                getUpnpService().getRegistry().unlockRemoteSubscriptions();
            }
        } catch (UnsupportedDataException ex) {
            log.fine("Can't read request body, " + ex);
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.INTERNAL_SERVER_ERROR));
        }
    }
}
