package org.teleal.cling.protocol;

import java.net.URL;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.gena.LocalGENASubscription;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.NamedServiceType;
import org.teleal.cling.model.types.NotificationSubtype;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.protocol.async.ReceivingNotification;
import org.teleal.cling.protocol.async.ReceivingSearch;
import org.teleal.cling.protocol.async.ReceivingSearchResponse;
import org.teleal.cling.protocol.async.SendingNotificationAlive;
import org.teleal.cling.protocol.async.SendingNotificationByebye;
import org.teleal.cling.protocol.async.SendingSearch;
import org.teleal.cling.protocol.sync.ReceivingAction;
import org.teleal.cling.protocol.sync.ReceivingEvent;
import org.teleal.cling.protocol.sync.ReceivingRetrieval;
import org.teleal.cling.protocol.sync.ReceivingSubscribe;
import org.teleal.cling.protocol.sync.ReceivingUnsubscribe;
import org.teleal.cling.protocol.sync.SendingAction;
import org.teleal.cling.protocol.sync.SendingEvent;
import org.teleal.cling.protocol.sync.SendingRenewal;
import org.teleal.cling.protocol.sync.SendingSubscribe;
import org.teleal.cling.protocol.sync.SendingUnsubscribe;

/* loaded from: classes.dex */
public class ProtocolFactoryImpl implements ProtocolFactory {
    private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$model$message$UpnpRequest$Method;
    private static final Logger log = Logger.getLogger(ProtocolFactory.class.getName());
    protected final UpnpService upnpService;

    static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$model$message$UpnpRequest$Method() {
        int[] iArr = $SWITCH_TABLE$org$teleal$cling$model$message$UpnpRequest$Method;
        if (iArr == null) {
            iArr = new int[UpnpRequest.Method.valuesCustom().length];
            try {
                iArr[UpnpRequest.Method.GET.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[UpnpRequest.Method.MSEARCH.ordinal()] = 4;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[UpnpRequest.Method.NOTIFY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[UpnpRequest.Method.POST.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[UpnpRequest.Method.SUBSCRIBE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[UpnpRequest.Method.UNKNOWN.ordinal()] = 7;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[UpnpRequest.Method.UNSUBSCRIBE.ordinal()] = 6;
            } catch (NoSuchFieldError e7) {
            }
            $SWITCH_TABLE$org$teleal$cling$model$message$UpnpRequest$Method = iArr;
        }
        return iArr;
    }

    public ProtocolFactoryImpl(UpnpService upnpService) {
        log.fine("Creating ProtocolFactory: " + getClass().getName());
        this.upnpService = upnpService;
    }

    @Override // org.teleal.cling.protocol.ProtocolFactory
    public UpnpService getUpnpService() {
        return this.upnpService;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.teleal.cling.protocol.ProtocolFactory
    public ReceivingAsync createReceivingAsync(IncomingDatagramMessage message) throws ProtocolCreationException {
        log.fine("Creating protocol for incoming asynchronous: " + message);
        if (message.getOperation() instanceof UpnpRequest) {
            switch ($SWITCH_TABLE$org$teleal$cling$model$message$UpnpRequest$Method()[((UpnpRequest) message.getOperation()).getMethod().ordinal()]) {
                case 3:
                    if (isByeBye(message) || isSupportedServiceAdvertisement(message)) {
                        return new ReceivingNotification(getUpnpService(), message);
                    }
                    return null;
                case 4:
                    return new ReceivingSearch(getUpnpService(), message);
            }
        }
        if (message.getOperation() instanceof UpnpResponse) {
            return isSupportedServiceAdvertisement(message) ? new ReceivingSearchResponse(getUpnpService(), message) : null;
        }
        throw new ProtocolCreationException("Protocol for incoming datagram message not found: " + message);
    }

    protected boolean isByeBye(IncomingDatagramMessage message) {
        String ntsHeader = message.getHeaders().getFirstHeader(UpnpHeader.Type.NTS.getHttpName());
        return ntsHeader != null && ntsHeader.equals(NotificationSubtype.BYEBYE.getHeaderString());
    }

    protected boolean isSupportedServiceAdvertisement(IncomingDatagramMessage message) {
        ServiceType[] exclusiveServiceTypes = getUpnpService().getConfiguration().getExclusiveServiceTypes();
        if (exclusiveServiceTypes == null) {
            return false;
        }
        if (exclusiveServiceTypes.length == 0) {
            return true;
        }
        String usnHeader = message.getHeaders().getFirstHeader(UpnpHeader.Type.USN.getHttpName());
        if (usnHeader == null) {
            return false;
        }
        try {
            NamedServiceType nst = NamedServiceType.valueOf(usnHeader);
            for (ServiceType exclusiveServiceType : exclusiveServiceTypes) {
                if (nst.getServiceType().implementsVersion(exclusiveServiceType)) {
                    return true;
                }
            }
        } catch (InvalidValueException e) {
            log.finest("Not a named service type header value: " + usnHeader);
        }
        log.fine("Service advertisement not supported, dropping it: " + usnHeader);
        return false;
    }

    @Override // org.teleal.cling.protocol.ProtocolFactory
    public ReceivingSync createReceivingSync(StreamRequestMessage message) throws ProtocolCreationException {
        log.fine("Creating protocol for incoming synchronous: " + message);
        if (message.getOperation().getMethod().equals(UpnpRequest.Method.GET)) {
            return new ReceivingRetrieval(getUpnpService(), message);
        }
        if (getUpnpService().getConfiguration().getNamespace().isControlPath(message.getUri())) {
            if (message.getOperation().getMethod().equals(UpnpRequest.Method.POST)) {
                return new ReceivingAction(getUpnpService(), message);
            }
        } else if (getUpnpService().getConfiguration().getNamespace().isEventSubscriptionPath(message.getUri())) {
            if (message.getOperation().getMethod().equals(UpnpRequest.Method.SUBSCRIBE)) {
                return new ReceivingSubscribe(getUpnpService(), message);
            }
            if (message.getOperation().getMethod().equals(UpnpRequest.Method.UNSUBSCRIBE)) {
                return new ReceivingUnsubscribe(getUpnpService(), message);
            }
        } else if (getUpnpService().getConfiguration().getNamespace().isEventCallbackPath(message.getUri()) && message.getOperation().getMethod().equals(UpnpRequest.Method.NOTIFY)) {
            return new ReceivingEvent(getUpnpService(), message);
        }
        throw new ProtocolCreationException("Protocol for message type not found: " + message);
    }

    @Override // org.teleal.cling.protocol.ProtocolFactory
    public SendingNotificationAlive createSendingNotificationAlive(LocalDevice localDevice) {
        return new SendingNotificationAlive(getUpnpService(), localDevice);
    }

    @Override // org.teleal.cling.protocol.ProtocolFactory
    public SendingNotificationByebye createSendingNotificationByebye(LocalDevice localDevice) {
        return new SendingNotificationByebye(getUpnpService(), localDevice);
    }

    @Override // org.teleal.cling.protocol.ProtocolFactory
    public SendingSearch createSendingSearch(UpnpHeader searchTarget, int mxSeconds) {
        return new SendingSearch(getUpnpService(), searchTarget, mxSeconds);
    }

    @Override // org.teleal.cling.protocol.ProtocolFactory
    public SendingAction createSendingAction(ActionInvocation actionInvocation, URL controlURL) {
        return new SendingAction(getUpnpService(), actionInvocation, controlURL);
    }

    @Override // org.teleal.cling.protocol.ProtocolFactory
    public SendingSubscribe createSendingSubscribe(RemoteGENASubscription subscription) {
        return new SendingSubscribe(getUpnpService(), subscription);
    }

    @Override // org.teleal.cling.protocol.ProtocolFactory
    public SendingRenewal createSendingRenewal(RemoteGENASubscription subscription) {
        return new SendingRenewal(getUpnpService(), subscription);
    }

    @Override // org.teleal.cling.protocol.ProtocolFactory
    public SendingUnsubscribe createSendingUnsubscribe(RemoteGENASubscription subscription) {
        return new SendingUnsubscribe(getUpnpService(), subscription);
    }

    @Override // org.teleal.cling.protocol.ProtocolFactory
    public SendingEvent createSendingEvent(LocalGENASubscription subscription) {
        return new SendingEvent(getUpnpService(), subscription);
    }
}
