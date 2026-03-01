package com.voicetechnology.rtspclient.headers;

/* loaded from: classes.dex */
public class ContentTypeHeader extends BaseStringHeader {
    public static final String NAME = "Content-Type";

    public ContentTypeHeader() {
        super("Content-Type");
    }

    public ContentTypeHeader(String header) {
        super("Content-Type", header);
    }
}
