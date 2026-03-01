package com.voicetechnology.rtspclient;

import com.voicetechnology.rtspclient.concepts.Content;
import com.voicetechnology.rtspclient.concepts.EntityMessage;
import com.voicetechnology.rtspclient.concepts.Message;
import com.voicetechnology.rtspclient.headers.ContentEncodingHeader;
import com.voicetechnology.rtspclient.headers.ContentLengthHeader;
import com.voicetechnology.rtspclient.headers.ContentTypeHeader;

/* loaded from: classes.dex */
public class RTSPEntityMessage implements EntityMessage {
    private Content content;
    private final Message message;

    public RTSPEntityMessage(Message message) {
        this.message = message;
    }

    public RTSPEntityMessage(Message message, Content body) {
        this(message);
        setContent(body);
    }

    @Override // com.voicetechnology.rtspclient.concepts.EntityMessage
    public Message getMessage() {
        return this.message;
    }

    @Override // com.voicetechnology.rtspclient.concepts.EntityMessage
    public byte[] getBytes() throws MissingHeaderException {
        this.message.getHeader("Content-Type");
        this.message.getHeader("Content-Length");
        return this.content.getBytes();
    }

    @Override // com.voicetechnology.rtspclient.concepts.EntityMessage
    public Content getContent() {
        return this.content;
    }

    @Override // com.voicetechnology.rtspclient.concepts.EntityMessage
    public void setContent(Content content) {
        if (content == null) {
            throw new NullPointerException();
        }
        this.content = content;
        this.message.addHeader(new ContentTypeHeader(content.getType()));
        if (content.getEncoding() != null) {
            this.message.addHeader(new ContentEncodingHeader(content.getEncoding()));
        }
        this.message.addHeader(new ContentLengthHeader(content.getBytes().length));
    }

    @Override // com.voicetechnology.rtspclient.concepts.EntityMessage
    public boolean isEntity() {
        return this.content != null;
    }
}
