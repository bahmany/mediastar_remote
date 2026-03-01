package com.voicetechnology.rtspclient;

import com.voicetechnology.rtspclient.concepts.EntityMessage;
import com.voicetechnology.rtspclient.concepts.Header;
import com.voicetechnology.rtspclient.concepts.Message;
import com.voicetechnology.rtspclient.headers.CSeqHeader;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class RTSPMessage implements Message {
    private CSeqHeader cseq;
    private EntityMessage entity;
    private List<Header> headers = new ArrayList();
    private String line;

    @Override // com.voicetechnology.rtspclient.concepts.Message
    public byte[] getBytes() throws MissingHeaderException {
        getHeader(CSeqHeader.NAME);
        addHeader(new Header("User-Agent", "RTSPClientLib/Java"));
        byte[] message = toString().getBytes();
        if (getEntityMessage() != null) {
            byte[] body = this.entity.getBytes();
            byte[] full = new byte[message.length + body.length];
            System.arraycopy(message, 0, full, 0, message.length);
            System.arraycopy(body, 0, full, message.length, body.length);
            return full;
        }
        return message;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Message
    public Header getHeader(final String name) throws MissingHeaderException {
        int index = this.headers.indexOf(new Object() { // from class: com.voicetechnology.rtspclient.RTSPMessage.1
            public boolean equals(Object obj) {
                return name.equalsIgnoreCase(((Header) obj).getName());
            }
        });
        if (index == -1) {
            throw new MissingHeaderException(name);
        }
        return this.headers.get(index);
    }

    @Override // com.voicetechnology.rtspclient.concepts.Message
    public Header[] getHeaders() {
        return (Header[]) this.headers.toArray(new Header[this.headers.size()]);
    }

    @Override // com.voicetechnology.rtspclient.concepts.Message
    public CSeqHeader getCSeq() {
        return this.cseq;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Message
    public String getLine() {
        return this.line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Message
    public void addHeader(Header header) {
        if (header != null) {
            if (header instanceof CSeqHeader) {
                this.cseq = (CSeqHeader) header;
            }
            int index = this.headers.indexOf(header);
            if (index > -1) {
                this.headers.remove(index);
            } else {
                index = this.headers.size();
            }
            this.headers.add(index, header);
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.Message
    public EntityMessage getEntityMessage() {
        return this.entity;
    }

    @Override // com.voicetechnology.rtspclient.concepts.Message
    public Message setEntityMessage(EntityMessage entity) {
        this.entity = entity;
        return this;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getLine()).append("\r\n");
        for (Header header : this.headers) {
            buffer.append(header).append("\r\n");
        }
        buffer.append("\r\n");
        return buffer.toString();
    }
}
