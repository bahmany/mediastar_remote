package org.teleal.cling.support.avtransport.callback;

import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;

/* loaded from: classes.dex */
public abstract class Stop extends ActionCallback {
    private static Logger log = Logger.getLogger(Stop.class.getName());

    public Stop(Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }

    public Stop(UnsignedIntegerFourBytes instanceId, Service service) throws InvalidValueException {
        super(new ActionInvocation(service.getAction("Stop")));
        getActionInvocation().setInput("InstanceID", instanceId);
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation invocation) {
        log.fine("Execution successful");
    }
}
