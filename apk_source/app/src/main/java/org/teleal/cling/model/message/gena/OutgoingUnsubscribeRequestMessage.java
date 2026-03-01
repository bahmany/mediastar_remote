package org.teleal.cling.model.message.gena;

import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.header.SubscriptionIdHeader;
import org.teleal.cling.model.message.header.UpnpHeader;

/* loaded from: classes.dex */
public class OutgoingUnsubscribeRequestMessage extends StreamRequestMessage {
    public OutgoingUnsubscribeRequestMessage(RemoteGENASubscription subscription) {
        super(UpnpRequest.Method.UNSUBSCRIBE, subscription.getEventSubscriptionURL());
        getHeaders().add(UpnpHeader.Type.SID, new SubscriptionIdHeader(subscription.getSubscriptionId()));
    }
}
