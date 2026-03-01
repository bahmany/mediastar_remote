package com.sun.mail.smtp;

import javax.mail.Session;
import javax.mail.URLName;

/* loaded from: classes.dex */
public class SMTPSSLTransport extends SMTPTransport {
    public SMTPSSLTransport(Session session, URLName urlname) {
        super(session, urlname, "smtps", 465, true);
    }
}
