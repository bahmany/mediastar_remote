package com.voicetechnology.rtspclient.headers;

/* loaded from: classes.dex */
public class SessionHeader extends BaseStringHeader {
    public static final String NAME = "Session";

    public SessionHeader() {
        super(NAME);
    }

    public SessionHeader(String header) {
        super(NAME, header);
    }
}
