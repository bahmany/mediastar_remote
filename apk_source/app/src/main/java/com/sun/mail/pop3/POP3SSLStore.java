package com.sun.mail.pop3;

import javax.mail.Session;
import javax.mail.URLName;

/* loaded from: classes.dex */
public class POP3SSLStore extends POP3Store {
    public POP3SSLStore(Session session, URLName url) {
        super(session, url, "pop3s", 995, true);
    }
}
