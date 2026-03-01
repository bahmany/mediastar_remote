package org.teleal.cling.support.connectionmanager.callback;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.support.model.ConnectionInfo;
import org.teleal.cling.support.model.ProtocolInfo;

/* loaded from: classes.dex */
public abstract class GetCurrentConnectionInfo extends ActionCallback {
    public abstract void received(ActionInvocation actionInvocation, ConnectionInfo connectionInfo);

    public GetCurrentConnectionInfo(Service service, int connectionID) {
        this(service, null, connectionID);
    }

    protected GetCurrentConnectionInfo(Service service, ControlPoint controlPoint, int connectionID) throws InvalidValueException {
        super(new ActionInvocation(service.getAction("GetCurrentConnectionInfo")), controlPoint);
        getActionInvocation().setInput("ConnectionID", Integer.valueOf(connectionID));
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation invocation) {
        try {
            ConnectionInfo info = new ConnectionInfo(((Integer) invocation.getInput("ConnectionID").getValue()).intValue(), ((Integer) invocation.getOutput("RcsID").getValue()).intValue(), ((Integer) invocation.getOutput("AVTransportID").getValue()).intValue(), new ProtocolInfo(invocation.getOutput("ProtocolInfo").toString()), new ServiceReference(invocation.getOutput("PeerConnectionManager").toString()), ((Integer) invocation.getOutput("PeerConnectionID").getValue()).intValue(), ConnectionInfo.Direction.valueOf(invocation.getOutput("Direction").toString()), ConnectionInfo.Status.valueOf(invocation.getOutput("Status").toString()));
            received(invocation, info);
        } catch (Exception ex) {
            invocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't parse ConnectionInfo response: " + ex, ex));
            failure(invocation, null);
        }
    }
}
