package org.teleal.cling.support.avtransport.impl.state;

import org.teleal.cling.support.model.AVTransport;
import org.teleal.cling.support.model.TransportAction;

/* loaded from: classes.dex */
public abstract class AbstractState<T extends AVTransport> {
    private T transport;

    public abstract TransportAction[] getCurrentTransportActions();

    public AbstractState(T transport) {
        this.transport = transport;
    }

    public T getTransport() {
        return this.transport;
    }
}
