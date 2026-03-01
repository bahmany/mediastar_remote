package org.teleal.cling.model.message;

import java.net.InetAddress;
import org.teleal.cling.model.message.UpnpMessage;
import org.teleal.cling.model.message.UpnpOperation;

/* loaded from: classes.dex */
public abstract class OutgoingDatagramMessage<O extends UpnpOperation> extends UpnpMessage<O> {
    private InetAddress destinationAddress;
    private int destinationPort;

    protected OutgoingDatagramMessage(O operation, InetAddress destinationAddress, int destinationPort) {
        super(operation);
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
    }

    protected OutgoingDatagramMessage(O operation, UpnpMessage.BodyType bodyType, Object body, InetAddress destinationAddress, int destinationPort) {
        super(operation, bodyType, body);
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
    }

    public InetAddress getDestinationAddress() {
        return this.destinationAddress;
    }

    public int getDestinationPort() {
        return this.destinationPort;
    }
}
