package org.teleal.cling.support.avtransport.callback;

import java.util.logging.Logger;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;

/* loaded from: classes.dex */
public abstract class SetAVTransportURI extends ActionCallback {
    private static Logger log = Logger.getLogger(SetAVTransportURI.class.getName());

    public SetAVTransportURI(Service service, String uri) {
        this(new UnsignedIntegerFourBytes(0L), service, uri, null);
    }

    public SetAVTransportURI(Service service, String uri, String metadata) {
        this(new UnsignedIntegerFourBytes(0L), service, uri, metadata);
    }

    public SetAVTransportURI(UnsignedIntegerFourBytes instanceId, Service service, String uri) {
        this(instanceId, service, uri, null);
    }

    public SetAVTransportURI(UnsignedIntegerFourBytes instanceId, Service service, String uri, String metadata) throws InvalidValueException {
        super(new ActionInvocation(service.getAction("SetAVTransportURI")));
        log.fine("Creating SetAVTransportURI action for URI: " + uri);
        getActionInvocation().setInput("InstanceID", instanceId);
        getActionInvocation().setInput("CurrentURI", uri);
        getActionInvocation().setInput("CurrentURIMetaData", metadata);
    }

    @Override // org.teleal.cling.controlpoint.ActionCallback
    public void success(ActionInvocation invocation) {
        log.fine("Execution successful");
    }
}
