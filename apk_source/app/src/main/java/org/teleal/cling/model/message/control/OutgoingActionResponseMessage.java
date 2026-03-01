package org.teleal.cling.model.message.control;

import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.ContentTypeHeader;
import org.teleal.cling.model.message.header.EXTHeader;
import org.teleal.cling.model.message.header.ServerHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.QueryStateVariableAction;

/* loaded from: classes.dex */
public class OutgoingActionResponseMessage extends StreamResponseMessage implements ActionResponseMessage {
    private String actionNamespace;

    public OutgoingActionResponseMessage(Action action) {
        this(UpnpResponse.Status.OK, action);
    }

    public OutgoingActionResponseMessage(UpnpResponse.Status status) {
        this(status, null);
    }

    public OutgoingActionResponseMessage(UpnpResponse.Status status, Action action) {
        super(new UpnpResponse(status));
        if (action != null) {
            if (action instanceof QueryStateVariableAction) {
                this.actionNamespace = "urn:schemas-upnp-org:control-1-0";
            } else {
                this.actionNamespace = action.getService().getServiceType().toString();
            }
        }
        addHeaders();
    }

    protected void addHeaders() {
        getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8));
        getHeaders().add(UpnpHeader.Type.SERVER, new ServerHeader());
        getHeaders().add(UpnpHeader.Type.EXT, new EXTHeader());
    }

    @Override // org.teleal.cling.model.message.control.ActionMessage
    public String getActionNamespace() {
        return this.actionNamespace;
    }
}
