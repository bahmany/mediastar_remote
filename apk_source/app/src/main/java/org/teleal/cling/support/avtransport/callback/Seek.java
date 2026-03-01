package org.teleal.cling.support.avtransport.callback;

import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.model.SeekMode;

/* loaded from: classes.dex */
public abstract class Seek extends ActionCallback {
    private static Logger log = Logger.getLogger(Seek.class.getName());

    public Seek(Service service, String relativeTimeTarget) {
        this(new UnsignedIntegerFourBytes(0L), service, SeekMode.REL_TIME, relativeTimeTarget);
    }

    public Seek(UnsignedIntegerFourBytes instanceId, Service service, String relativeTimeTarget) {
        this(instanceId, service, SeekMode.REL_TIME, relativeTimeTarget);
    }

    public Seek(Service service, SeekMode mode, String target) {
        this(new UnsignedIntegerFourBytes(0L), service, mode, target);
    }

    public Seek(UnsignedIntegerFourBytes instanceId, Service service, SeekMode mode, String target) throws InvalidValueException {
        super(new ActionInvocation(service.getAction("Seek")));
        getActionInvocation().setInput("InstanceID", instanceId);
        getActionInvocation().setInput("Unit", mode.name());
        getActionInvocation().setInput("Target", target);
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation invocation) {
        log.fine("Execution successful");
    }
}
