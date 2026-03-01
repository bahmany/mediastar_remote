package org.teleal.cling.support.igd.callback;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.model.Connection;

/* loaded from: classes.dex */
public abstract class GetStatusInfo extends ActionCallback {
    protected abstract void success(Connection.StatusInfo statusInfo);

    public GetStatusInfo(Service service) {
        super(new ActionInvocation(service.getAction("GetStatusInfo")));
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation invocation) {
        try {
            Connection.Status status = Connection.Status.valueOf(invocation.getOutput("NewConnectionStatus").getValue().toString());
            Connection.Error lastError = Connection.Error.valueOf(invocation.getOutput("NewLastConnectionError").getValue().toString());
            success(new Connection.StatusInfo(status, (UnsignedIntegerFourBytes) invocation.getOutput("NewUptime").getValue(), lastError));
        } catch (Exception ex) {
            invocation.setFailure(new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Invalid status or last error string: " + ex, ex));
            failure(invocation, null);
        }
    }
}
