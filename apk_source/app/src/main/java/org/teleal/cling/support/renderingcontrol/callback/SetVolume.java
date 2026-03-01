package org.teleal.cling.support.renderingcontrol.callback;

import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytes;
import org.teleal.cling.support.model.Channel;

/* loaded from: classes.dex */
public abstract class SetVolume extends ActionCallback {
    private static Logger log = Logger.getLogger(SetVolume.class.getName());

    public SetVolume(Service service, long newVolume) {
        this(new UnsignedIntegerFourBytes(0L), service, newVolume);
    }

    public SetVolume(UnsignedIntegerFourBytes instanceId, Service service, long newVolume) throws InvalidValueException {
        super(new ActionInvocation(service.getAction("SetVolume")));
        getActionInvocation().setInput("InstanceID", instanceId);
        getActionInvocation().setInput("Channel", Channel.Master.toString());
        getActionInvocation().setInput("DesiredVolume", new UnsignedIntegerTwoBytes(newVolume));
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation invocation) {
        log.fine("Executed successfully");
    }
}
