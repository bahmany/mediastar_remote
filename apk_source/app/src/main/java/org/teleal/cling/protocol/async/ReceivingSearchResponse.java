package org.teleal.cling.protocol.async;

import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.discovery.IncomingSearchResponse;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteDeviceIdentity;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.protocol.ReceivingAsync;
import org.teleal.cling.protocol.RetrieveRemoteDescriptors;

/* loaded from: classes.dex */
public class ReceivingSearchResponse extends ReceivingAsync<IncomingSearchResponse> {
    private static final Logger log = Logger.getLogger(ReceivingSearchResponse.class.getName());

    public ReceivingSearchResponse(UpnpService upnpService, IncomingDatagramMessage<UpnpResponse> inputMessage) {
        super(upnpService, new IncomingSearchResponse(inputMessage));
    }

    @Override // org.teleal.cling.protocol.ReceivingAsync
    protected void execute() throws IllegalAccessException, InstantiationException {
        if (!getInputMessage().isSearchResponseMessage()) {
            log.fine("Ignoring invalid search response message: " + getInputMessage());
            return;
        }
        UDN udn = getInputMessage().getRootDeviceUDN();
        if (udn == null) {
            log.fine("Ignoring search response message without UDN: " + getInputMessage());
            return;
        }
        RemoteDeviceIdentity rdIdentity = new RemoteDeviceIdentity(getInputMessage());
        log.fine("Received device search response: " + rdIdentity);
        if (getUpnpService().getRegistry().update(rdIdentity)) {
            log.fine("Remote device was already known: " + udn);
            return;
        }
        try {
            RemoteDevice rd = new RemoteDevice(rdIdentity);
            if (rdIdentity.getDescriptorURL() == null) {
                log.finer("Ignoring message without location URL header: " + getInputMessage());
            } else if (rdIdentity.getMaxAgeSeconds() == null) {
                log.finer("Ignoring message without max-age header: " + getInputMessage());
            } else {
                getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(new RetrieveRemoteDescriptors(getUpnpService(), rd));
            }
        } catch (ValidationException ex) {
            log.warning("Validation errors of device during discovery: " + rdIdentity);
            for (ValidationError validationError : ex.getErrors()) {
                log.warning(validationError.toString());
            }
        }
    }
}
