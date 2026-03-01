package org.teleal.cling.support.renderingcontrol.callback;

import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.model.Channel;

/* loaded from: classes.dex */
public abstract class GetMute extends ActionCallback {
    private static Logger log = Logger.getLogger(GetMute.class.getName());

    public abstract void received(ActionInvocation actionInvocation, boolean z);

    public GetMute(Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }

    public GetMute(UnsignedIntegerFourBytes instanceId, Service service) throws InvalidValueException {
        super(new ActionInvocation(service.getAction("GetMute")));
        getActionInvocation().setInput("InstanceID", instanceId);
        getActionInvocation().setInput("Channel", Channel.Master.toString());
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation invocation) {
        boolean currentMute = ((Boolean) invocation.getOutput("CurrentMute").getValue()).booleanValue();
        received(invocation, currentMute);
    }
}
