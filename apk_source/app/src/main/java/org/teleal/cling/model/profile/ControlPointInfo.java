package org.teleal.cling.model.profile;

import org.teleal.cling.model.message.UpnpHeaders;

/* loaded from: classes.dex */
public class ControlPointInfo {
    UpnpHeaders headers;

    public ControlPointInfo() {
        this(new UpnpHeaders());
    }

    public ControlPointInfo(UpnpHeaders headers) {
        this.headers = headers;
    }

    public UpnpHeaders getHeaders() {
        return this.headers;
    }
}
