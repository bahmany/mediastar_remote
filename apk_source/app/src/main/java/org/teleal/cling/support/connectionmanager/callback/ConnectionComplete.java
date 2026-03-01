package org.teleal.cling.support.connectionmanager.callback;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;

/* loaded from: classes.dex */
public abstract class ConnectionComplete extends ActionCallback {
    public ConnectionComplete(Service service, int connectionID) {
        this(service, null, connectionID);
    }

    protected ConnectionComplete(Service service, ControlPoint controlPoint, int connectionID) throws InvalidValueException {
        super(new ActionInvocation(service.getAction("ConnectionComplete")), controlPoint);
        getActionInvocation().setInput("ConnectionID", Integer.valueOf(connectionID));
    }
}
