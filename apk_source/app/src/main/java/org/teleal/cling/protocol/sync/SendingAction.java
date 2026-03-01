package org.teleal.cling.protocol.sync;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.control.IncomingActionResponseMessage;
import org.teleal.cling.model.message.control.OutgoingActionRequestMessage;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.protocol.SendingSync;
import org.teleal.cling.transport.spi.UnsupportedDataException;
import org.teleal.common.util.Exceptions;

/* loaded from: classes.dex */
public class SendingAction extends SendingSync<OutgoingActionRequestMessage, IncomingActionResponseMessage> {
    private static final Logger log = Logger.getLogger(SendingAction.class.getName());
    protected final ActionInvocation actionInvocation;

    public SendingAction(UpnpService upnpService, ActionInvocation actionInvocation, URL controlURL) {
        super(upnpService, new OutgoingActionRequestMessage(actionInvocation, controlURL));
        this.actionInvocation = actionInvocation;
    }

    @Override // org.teleal.cling.protocol.SendingSync
    public IncomingActionResponseMessage executeSync() {
        return invokeRemote(getInputMessage());
    }

    protected IncomingActionResponseMessage invokeRemote(OutgoingActionRequestMessage requestMessage) throws ActionException {
        IncomingActionResponseMessage responseMessage;
        Device device = this.actionInvocation.getAction().getService().getDevice();
        log.fine("Sending outgoing action call '" + this.actionInvocation.getAction().getName() + "' to remote service of: " + device);
        IncomingActionResponseMessage responseMessage2 = null;
        try {
            StreamResponseMessage streamResponse = sendRemoteRequest(requestMessage);
            if (streamResponse == null) {
                log.fine("No connection or no no response received, returning null");
                this.actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Connection error or no response received"));
                responseMessage = null;
            } else {
                responseMessage = new IncomingActionResponseMessage(streamResponse);
                try {
                    if (responseMessage.isFailedNonRecoverable()) {
                        log.fine("Response was a non-recoverable failure: " + responseMessage);
                        throw new ActionException(ErrorCode.ACTION_FAILED, "Non-recoverable remote execution failure: " + responseMessage.getOperation().getResponseDetails());
                    }
                    if (responseMessage.isFailedRecoverable()) {
                        handleResponseFailure(responseMessage);
                    } else {
                        handleResponse(responseMessage);
                    }
                    responseMessage2 = responseMessage;
                } catch (ActionException e) {
                    ex = e;
                    responseMessage2 = responseMessage;
                    log.fine("Remote action invocation failed, returning Internal Server Error message: " + ex.getMessage());
                    this.actionInvocation.setFailure(ex);
                    return (responseMessage2 == null || !responseMessage2.getOperation().isFailed()) ? new IncomingActionResponseMessage(new UpnpResponse(UpnpResponse.Status.INTERNAL_SERVER_ERROR)) : responseMessage2;
                }
            }
            return responseMessage;
        } catch (ActionException e2) {
            ex = e2;
        }
    }

    protected StreamResponseMessage sendRemoteRequest(OutgoingActionRequestMessage requestMessage) throws ActionException {
        try {
            log.fine("Writing SOAP request body of: " + requestMessage);
            getUpnpService().getConfiguration().getSoapActionProcessor().writeBody(requestMessage, this.actionInvocation);
            log.fine("Sending SOAP body of message as stream to remote device");
            return getUpnpService().getRouter().send(requestMessage);
        } catch (UnsupportedDataException ex) {
            log.fine("Error writing SOAP body: " + ex);
            log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex));
            throw new ActionException(ErrorCode.ACTION_FAILED, "Error writing request message. " + ex.getMessage());
        }
    }

    protected void handleResponse(IncomingActionResponseMessage responseMsg) throws ActionException {
        try {
            log.fine("Received response for outgoing call, reading SOAP response body: " + responseMsg);
            getUpnpService().getConfiguration().getSoapActionProcessor().readBody(responseMsg, this.actionInvocation);
        } catch (UnsupportedDataException ex) {
            log.fine("Error reading SOAP body: " + ex);
            log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex));
            throw new ActionException(ErrorCode.ACTION_FAILED, "Error reading response message. " + ex.getMessage());
        }
    }

    protected void handleResponseFailure(IncomingActionResponseMessage responseMsg) throws ActionException {
        try {
            log.fine("Received response with Internal Server Error, reading SOAP failure message");
            getUpnpService().getConfiguration().getSoapActionProcessor().readBody(responseMsg, this.actionInvocation);
        } catch (UnsupportedDataException ex) {
            log.fine("Error reading SOAP body: " + ex);
            log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex));
            throw new ActionException(ErrorCode.ACTION_FAILED, "Error reading response failure message. " + ex.getMessage());
        }
    }
}
