package org.teleal.cling.protocol.sync;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpHeaders;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.control.IncomingActionRequestMessage;
import org.teleal.cling.model.message.control.OutgoingActionResponseMessage;
import org.teleal.cling.model.message.header.ContentTypeHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.resource.ServiceControlResource;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.protocol.ReceivingSync;
import org.teleal.cling.transport.spi.UnsupportedDataException;
import org.teleal.common.util.Exceptions;

/* loaded from: classes.dex */
public class ReceivingAction extends ReceivingSync<StreamRequestMessage, StreamResponseMessage> {
    private static final Logger log = Logger.getLogger(ReceivingAction.class.getName());
    protected static final ThreadLocal<IncomingActionRequestMessage> requestThreadLocal = new ThreadLocal<>();
    protected static final ThreadLocal<UpnpHeaders> extraResponseHeadersThreadLocal = new ThreadLocal<>();

    public ReceivingAction(UpnpService upnpService, StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.teleal.cling.protocol.ReceivingSync
    protected StreamResponseMessage executeSync() {
        ActionInvocation invocation;
        OutgoingActionResponseMessage responseMessage;
        ContentTypeHeader contentTypeHeader = (ContentTypeHeader) ((StreamRequestMessage) getInputMessage()).getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class);
        if (contentTypeHeader != null && !contentTypeHeader.isUDACompliantXML()) {
            log.warning("Received invalid Content-Type '" + contentTypeHeader + "': " + getInputMessage());
            return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.UNSUPPORTED_MEDIA_TYPE));
        }
        if (contentTypeHeader == null) {
            log.warning("Received without Content-Type: " + getInputMessage());
        }
        ServiceControlResource resource = (ServiceControlResource) getUpnpService().getRegistry().getResource(ServiceControlResource.class, ((StreamRequestMessage) getInputMessage()).getUri());
        if (resource == null) {
            log.fine("No local resource found: " + getInputMessage());
            return null;
        }
        log.fine("Found local action resource matching relative request URI: " + ((StreamRequestMessage) getInputMessage()).getUri());
        OutgoingActionResponseMessage responseMessage2 = null;
        try {
            try {
                try {
                    IncomingActionRequestMessage requestMessage = new IncomingActionRequestMessage((StreamRequestMessage) getInputMessage(), resource.getModel());
                    requestThreadLocal.set(requestMessage);
                    extraResponseHeadersThreadLocal.set(new UpnpHeaders());
                    log.finer("Created incoming action request message: " + requestMessage);
                    invocation = new ActionInvocation(requestMessage.getAction());
                    log.fine("Reading body of request message");
                    getUpnpService().getConfiguration().getSoapActionProcessor().readBody(requestMessage, invocation);
                    log.fine("Executing on local service: " + invocation);
                    resource.getModel().getExecutor(invocation.getAction()).execute(invocation);
                    if (invocation.getFailure() == null) {
                        OutgoingActionResponseMessage responseMessage3 = new OutgoingActionResponseMessage(invocation.getAction());
                        responseMessage = responseMessage3;
                    } else {
                        OutgoingActionResponseMessage responseMessage4 = new OutgoingActionResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR, invocation.getAction());
                        responseMessage = responseMessage4;
                    }
                    if (responseMessage != null && extraResponseHeadersThreadLocal.get() != null) {
                        log.fine("Merging extra headers into action response message: " + extraResponseHeadersThreadLocal.get().size());
                        responseMessage.getHeaders().putAll((Map) extraResponseHeadersThreadLocal.get());
                    }
                    requestThreadLocal.set(null);
                    extraResponseHeadersThreadLocal.set(null);
                } catch (ActionException ex) {
                    log.finer("Error executing local action: " + ex);
                    invocation = new ActionInvocation(ex);
                    OutgoingActionResponseMessage responseMessage5 = new OutgoingActionResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
                    if (responseMessage5 != null && extraResponseHeadersThreadLocal.get() != null) {
                        log.fine("Merging extra headers into action response message: " + extraResponseHeadersThreadLocal.get().size());
                        responseMessage5.getHeaders().putAll((Map) extraResponseHeadersThreadLocal.get());
                    }
                    requestThreadLocal.set(null);
                    extraResponseHeadersThreadLocal.set(null);
                    responseMessage = responseMessage5;
                }
            } catch (UnsupportedDataException ex2) {
                if (log.isLoggable(Level.FINER)) {
                    log.log(Level.FINER, "Error reading action request XML body: " + ex2.toString(), Exceptions.unwrap(ex2));
                }
                invocation = new ActionInvocation(Exceptions.unwrap(ex2) instanceof ActionException ? (ActionException) Exceptions.unwrap(ex2) : new ActionException(ErrorCode.ACTION_FAILED, ex2.getMessage()));
                OutgoingActionResponseMessage responseMessage6 = new OutgoingActionResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
                if (responseMessage6 != null && extraResponseHeadersThreadLocal.get() != null) {
                    log.fine("Merging extra headers into action response message: " + extraResponseHeadersThreadLocal.get().size());
                    responseMessage6.getHeaders().putAll((Map) extraResponseHeadersThreadLocal.get());
                }
                requestThreadLocal.set(null);
                extraResponseHeadersThreadLocal.set(null);
                responseMessage = responseMessage6;
            }
            try {
                log.fine("Writing body of response message");
                getUpnpService().getConfiguration().getSoapActionProcessor().writeBody(responseMessage, invocation);
                log.fine("Returning finished response message: " + responseMessage);
                return responseMessage;
            } catch (UnsupportedDataException ex3) {
                log.warning("Failure writing body of response message, sending '500 Internal Server Error' without body");
                log.log(Level.WARNING, "Exception root cause: ", Exceptions.unwrap(ex3));
                return new StreamResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
            }
        } catch (Throwable th) {
            if (0 != 0 && extraResponseHeadersThreadLocal.get() != null) {
                log.fine("Merging extra headers into action response message: " + extraResponseHeadersThreadLocal.get().size());
                responseMessage2.getHeaders().putAll((Map) extraResponseHeadersThreadLocal.get());
            }
            requestThreadLocal.set(null);
            extraResponseHeadersThreadLocal.set(null);
            throw th;
        }
    }

    public static IncomingActionRequestMessage getRequestMessage() {
        return requestThreadLocal.get();
    }

    public static UpnpHeaders getExtraResponseHeaders() {
        return extraResponseHeadersThreadLocal.get();
    }
}
