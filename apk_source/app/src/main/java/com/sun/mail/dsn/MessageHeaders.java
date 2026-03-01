package com.sun.mail.dsn;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

/* loaded from: classes.dex */
public class MessageHeaders extends MimeMessage {
    public MessageHeaders() throws MessagingException {
        super((Session) null);
        this.content = new byte[0];
    }

    public MessageHeaders(InputStream is) throws MessagingException {
        super((Session) null, is);
        this.content = new byte[0];
    }

    public MessageHeaders(InternetHeaders headers) throws MessagingException {
        super((Session) null);
        this.headers = headers;
        this.content = new byte[0];
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public int getSize() {
        return 0;
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    @Override // javax.mail.internet.MimeMessage
    protected InputStream getContentStream() {
        return new ByteArrayInputStream(this.content);
    }

    @Override // javax.mail.internet.MimeMessage, javax.mail.Part
    public void setDataHandler(DataHandler dh) throws MessagingException {
        throw new MessagingException("Can't set content for MessageHeaders");
    }
}
