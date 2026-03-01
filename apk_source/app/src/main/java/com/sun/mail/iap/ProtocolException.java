package com.sun.mail.iap;

/* loaded from: classes.dex */
public class ProtocolException extends Exception {
    private static final long serialVersionUID = -4360500807971797439L;
    protected transient Response response;

    public ProtocolException() {
        this.response = null;
    }

    public ProtocolException(String s) {
        super(s);
        this.response = null;
    }

    public ProtocolException(Response r) {
        super(r.toString());
        this.response = null;
        this.response = r;
    }

    public Response getResponse() {
        return this.response;
    }
}
