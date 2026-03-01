package org.teleal.cling.support.avtransport.callback;

import java.util.Map;
import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionArgumentValue;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.model.TransportInfo;

/* loaded from: classes.dex */
public abstract class GetTransportInfo extends ActionCallback {
    private static Logger log = Logger.getLogger(GetTransportInfo.class.getName());

    public abstract void received(ActionInvocation actionInvocation, TransportInfo transportInfo);

    public GetTransportInfo(Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }

    public GetTransportInfo(UnsignedIntegerFourBytes instanceId, Service service) throws InvalidValueException {
        super(new ActionInvocation(service.getAction("GetTransportInfo")));
        getActionInvocation().setInput("InstanceID", instanceId);
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation invocation) {
        TransportInfo transportInfo = new TransportInfo((Map<String, ActionArgumentValue>) invocation.getOutputMap());
        received(invocation, transportInfo);
    }
}
