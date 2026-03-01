package org.teleal.cling.support.connectionmanager.callback;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.support.model.ConnectionInfo;
import org.teleal.cling.support.model.ProtocolInfo;

/* loaded from: classes.dex */
public abstract class PrepareForConnection extends ActionCallback {
    public abstract void received(ActionInvocation actionInvocation, int i, int i2, int i3);

    public PrepareForConnection(Service service, ProtocolInfo remoteProtocolInfo, ServiceReference peerConnectionManager, int peerConnectionID, ConnectionInfo.Direction direction) {
        this(service, null, remoteProtocolInfo, peerConnectionManager, peerConnectionID, direction);
    }

    public PrepareForConnection(Service service, ControlPoint controlPoint, ProtocolInfo remoteProtocolInfo, ServiceReference peerConnectionManager, int peerConnectionID, ConnectionInfo.Direction direction) throws InvalidValueException {
        super(new ActionInvocation(service.getAction("PrepareForConnection")), controlPoint);
        getActionInvocation().setInput("RemoteProtocolInfo", remoteProtocolInfo.toString());
        getActionInvocation().setInput("PeerConnectionManager", peerConnectionManager.toString());
        getActionInvocation().setInput("PeerConnectionID", Integer.valueOf(peerConnectionID));
        getActionInvocation().setInput("Direction", direction.toString());
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation invocation) {
        received(invocation, ((Integer) invocation.getOutput("ConnectionID").getValue()).intValue(), ((Integer) invocation.getOutput("RcsID").getValue()).intValue(), ((Integer) invocation.getOutput("AVTransportID").getValue()).intValue());
    }
}
