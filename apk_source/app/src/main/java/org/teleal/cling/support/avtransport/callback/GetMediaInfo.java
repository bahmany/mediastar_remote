package org.teleal.cling.support.avtransport.callback;

import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.model.MediaInfo;

/* loaded from: classes.dex */
public abstract class GetMediaInfo extends ActionCallback {
    private static Logger log = Logger.getLogger(GetMediaInfo.class.getName());

    public abstract void received(ActionInvocation actionInvocation, MediaInfo mediaInfo);

    public GetMediaInfo(Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }

    public GetMediaInfo(UnsignedIntegerFourBytes instanceId, Service service) throws InvalidValueException {
        super(new ActionInvocation(service.getAction("GetMediaInfo")));
        getActionInvocation().setInput("InstanceID", instanceId);
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation invocation) {
        MediaInfo mediaInfo = new MediaInfo(invocation.getOutputMap());
        received(invocation, mediaInfo);
    }
}
