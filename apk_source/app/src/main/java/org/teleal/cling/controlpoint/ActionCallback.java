package org.teleal.cling.controlpoint;

import java.net.URL;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.control.IncomingActionResponseMessage;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.protocol.sync.SendingAction;

/* loaded from: classes.dex */
public abstract class ActionCallback implements Runnable {
    protected final ActionInvocation actionInvocation;
    protected ControlPoint controlPoint;

    public abstract void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str);

    public abstract void success(ActionInvocation actionInvocation);

    public static final class Default extends ActionCallback {
        public Default(ActionInvocation actionInvocation, ControlPoint controlPoint) {
            super(actionInvocation, controlPoint);
        }

        @Override // org.teleal.cling.controlpoint.ActionCallback
        public void success(ActionInvocation invocation) {
        }

        @Override // org.teleal.cling.controlpoint.ActionCallback
        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
        }
    }

    protected ActionCallback(ActionInvocation actionInvocation, ControlPoint controlPoint) {
        this.actionInvocation = actionInvocation;
        this.controlPoint = controlPoint;
    }

    protected ActionCallback(ActionInvocation actionInvocation) {
        this.actionInvocation = actionInvocation;
    }

    public ActionInvocation getActionInvocation() {
        return this.actionInvocation;
    }

    public synchronized ControlPoint getControlPoint() {
        return this.controlPoint;
    }

    public synchronized ActionCallback setControlPoint(ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
        return this;
    }

    @Override // java.lang.Runnable
    public void run() {
        Service service = this.actionInvocation.getAction().getService();
        if (service instanceof LocalService) {
            LocalService localService = (LocalService) service;
            localService.getExecutor(this.actionInvocation.getAction()).execute(this.actionInvocation);
            if (this.actionInvocation.getFailure() != null) {
                failure(this.actionInvocation, null);
                return;
            } else {
                success(this.actionInvocation);
                return;
            }
        }
        if (service instanceof RemoteService) {
            if (getControlPoint() == null) {
                throw new IllegalStateException("Callback must be executed through ControlPoint");
            }
            RemoteService remoteService = (RemoteService) service;
            URL controLURL = remoteService.getDevice().normalizeURI(remoteService.getControlURI());
            SendingAction prot = getControlPoint().getProtocolFactory().createSendingAction(this.actionInvocation, controLURL);
            prot.run();
            IncomingActionResponseMessage response = prot.getOutputMessage();
            if (response == null) {
                failure(this.actionInvocation, null);
            } else if (response.getOperation().isFailed()) {
                failure(this.actionInvocation, response.getOperation());
            } else {
                success(this.actionInvocation);
            }
        }
    }

    protected String createDefaultFailureMessage(ActionInvocation invocation, UpnpResponse operation) {
        String message = "Error: ";
        ActionException exception = invocation.getFailure();
        if (exception != null) {
            message = String.valueOf("Error: ") + exception.getMessage();
        }
        if (operation != null) {
            return String.valueOf(message) + " (HTTP response was: " + operation.getResponseDetails() + ")";
        }
        return message;
    }

    protected void failure(ActionInvocation invocation, UpnpResponse operation) {
        failure(invocation, operation, createDefaultFailureMessage(invocation, operation));
    }

    public String toString() {
        return "(ActionCallback) " + this.actionInvocation;
    }
}
