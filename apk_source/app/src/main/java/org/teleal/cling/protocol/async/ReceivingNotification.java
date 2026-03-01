package org.teleal.cling.protocol.async;

import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.discovery.IncomingNotificationRequest;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteDeviceIdentity;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.protocol.ReceivingAsync;
import org.teleal.cling.protocol.RetrieveRemoteDescriptors;

/* loaded from: classes.dex */
public class ReceivingNotification extends ReceivingAsync<IncomingNotificationRequest> {
    private static final Logger log = Logger.getLogger(ReceivingNotification.class.getName());

    public ReceivingNotification(UpnpService upnpService, IncomingDatagramMessage<UpnpRequest> inputMessage) {
        super(upnpService, new IncomingNotificationRequest(inputMessage));
    }

    @Override // org.teleal.cling.protocol.ReceivingAsync
    protected void execute() throws IllegalAccessException, InstantiationException {
        UDN udn = getInputMessage().getUDN();
        if (udn == null) {
            log.fine("Ignoring notification message without UDN: " + getInputMessage());
            return;
        }
        RemoteDeviceIdentity rdIdentity = new RemoteDeviceIdentity(getInputMessage());
        log.fine("Received device notification: " + rdIdentity);
        try {
            RemoteDevice rd = new RemoteDevice(rdIdentity);
            if (getInputMessage().isAliveMessage()) {
                log.fine("Received device ALIVE advertisement, descriptor location is: " + rdIdentity.getDescriptorURL());
                if (rdIdentity.getDescriptorURL() == null) {
                    log.finer("Ignoring message without location URL header: " + getInputMessage());
                    return;
                }
                if (rdIdentity.getMaxAgeSeconds() == null) {
                    log.finer("Ignoring message without max-age header: " + getInputMessage());
                    return;
                } else if (getUpnpService().getRegistry().update(rdIdentity)) {
                    log.finer("Remote device was already known: " + udn);
                    return;
                } else {
                    getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(new RetrieveRemoteDescriptors(getUpnpService(), rd));
                    return;
                }
            }
            if (getInputMessage().isByeByeMessage()) {
                log.fine("Received device BYEBYE advertisement");
                boolean removed = getUpnpService().getRegistry().removeDevice(rd);
                if (removed) {
                    log.fine("Removed remote device from registry: " + rd);
                    return;
                }
                return;
            }
            log.finer("Ignoring unknown notification message: " + getInputMessage());
        } catch (ValidationException ex) {
            log.warning("Validation errors of device during discovery: " + rdIdentity);
            for (ValidationError validationError : ex.getErrors()) {
                log.warning(validationError.toString());
            }
        }
    }
}
