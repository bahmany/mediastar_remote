package com.voicetechnology.rtspclient.concepts;

import com.voicetechnology.rtspclient.MissingHeaderException;
import com.voicetechnology.rtspclient.headers.ContentEncodingHeader;

/* loaded from: classes.dex */
public class Content {
    private byte[] content;
    private String encoding;
    private String type;

    public void setDescription(Message message) throws MissingHeaderException {
        this.type = message.getHeader("Content-Type").getRawValue();
        try {
            this.encoding = message.getHeader(ContentEncodingHeader.NAME).getRawValue();
        } catch (MissingHeaderException e) {
        }
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public byte[] getBytes() {
        return this.content;
    }

    public void setBytes(byte[] content) {
        this.content = content;
    }
}
