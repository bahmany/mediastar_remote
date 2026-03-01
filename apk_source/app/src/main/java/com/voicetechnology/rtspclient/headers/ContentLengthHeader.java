package com.voicetechnology.rtspclient.headers;

/* loaded from: classes.dex */
public class ContentLengthHeader extends BaseIntegerHeader {
    public static final String NAME = "Content-Length";

    public ContentLengthHeader() {
        super("Content-Length");
    }

    public ContentLengthHeader(int value) {
        super("Content-Length", value);
    }

    public ContentLengthHeader(String header) {
        super("Content-Length", header);
    }
}
