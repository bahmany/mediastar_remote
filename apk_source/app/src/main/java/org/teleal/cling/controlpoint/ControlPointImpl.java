package org.teleal.cling.controlpoint;

import java.util.logging.Logger;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.model.message.header.MXHeader;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.protocol.ProtocolFactory;
import org.teleal.cling.registry.Registry;

/* loaded from: classes.dex */
public class ControlPointImpl implements ControlPoint {
    private static Logger log = Logger.getLogger(ControlPointImpl.class.getName());
    protected final UpnpServiceConfiguration configuration;
    protected final ProtocolFactory protocolFactory;
    protected final Registry registry;

    public ControlPointImpl(UpnpServiceConfiguration configuration, ProtocolFactory protocolFactory, Registry registry) {
        log.fine("Creating ControlPoint: " + getClass().getName());
        this.configuration = configuration;
        this.protocolFactory = protocolFactory;
        this.registry = registry;
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public UpnpServiceConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public Registry getRegistry() {
        return this.registry;
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public void search() {
        search(new STAllHeader(), MXHeader.DEFAULT_VALUE.intValue());
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public void search(UpnpHeader searchType) {
        search(searchType, MXHeader.DEFAULT_VALUE.intValue());
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public void search(int mxSeconds) {
        search(new STAllHeader(), mxSeconds);
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public void search(UpnpHeader searchType, int mxSeconds) {
        log.fine("Sending asynchronous search for: " + searchType.getString());
        getConfiguration().getAsyncProtocolExecutor().execute(getProtocolFactory().createSendingSearch(searchType, mxSeconds));
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public void execute(ActionCallback callback) {
        log.fine("Invoking action in background: " + callback);
        callback.setControlPoint(this);
        getConfiguration().getSyncProtocolExecutor().execute(callback);
    }

    @Override // org.teleal.cling.controlpoint.ControlPoint
    public void execute(SubscriptionCallback callback) {
        log.fine("Invoking subscription in background: " + callback);
        callback.setControlPoint(this);
        getConfiguration().getSyncProtocolExecutor().execute(callback);
    }
}
