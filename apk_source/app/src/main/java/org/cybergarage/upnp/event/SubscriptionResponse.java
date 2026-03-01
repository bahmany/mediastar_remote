package org.cybergarage.upnp.event;

import org.cybergarage.http.HTTPResponse;
import org.cybergarage.upnp.UPnP;

/* loaded from: classes.dex */
public class SubscriptionResponse extends HTTPResponse {
    public SubscriptionResponse() {
        setServer(UPnP.getServerName());
    }

    public SubscriptionResponse(HTTPResponse httpRes) {
        super(httpRes);
    }

    public void setResponse(int code) {
        setStatusCode(code);
        setContentLength(0L);
    }

    public void setErrorResponse(int code) {
        setStatusCode(code);
        setContentLength(0L);
    }

    public void setSID(String id) {
        setHeader("SID", Subscription.toSIDHeaderString(id));
    }

    public String getSID() {
        return Subscription.getSID(getHeaderValue("SID"));
    }

    public void setTimeout(long value) {
        setHeader("TIMEOUT", Subscription.toTimeoutHeaderString(value));
    }

    public long getTimeout() {
        return Subscription.getTimeout(getHeaderValue("TIMEOUT"));
    }
}
