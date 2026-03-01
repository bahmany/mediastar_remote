package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
import java.util.Vector;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/* compiled from: ENVELOPE.java */
/* loaded from: classes.dex */
class IMAPAddress extends InternetAddress {
    private static final long serialVersionUID = -3835822029483122232L;
    private boolean group;
    private InternetAddress[] grouplist;
    private String groupname;

    IMAPAddress(Response r) throws ParsingException {
        this.group = false;
        r.skipSpaces();
        if (r.readByte() != 40) {
            throw new ParsingException("ADDRESS parse error");
        }
        this.encodedPersonal = r.readString();
        r.readString();
        String mb = r.readString();
        String host = r.readString();
        if (r.readByte() != 41) {
            throw new ParsingException("ADDRESS parse error");
        }
        if (host == null) {
            this.group = true;
            this.groupname = mb;
            if (this.groupname != null) {
                StringBuffer sb = new StringBuffer();
                sb.append(this.groupname).append(':');
                Vector v = new Vector();
                while (r.peekByte() != 41) {
                    IMAPAddress a = new IMAPAddress(r);
                    if (a.isEndOfGroup()) {
                        break;
                    }
                    if (v.size() != 0) {
                        sb.append(',');
                    }
                    sb.append(a.toString());
                    v.addElement(a);
                }
                sb.append(';');
                this.address = sb.toString();
                this.grouplist = new IMAPAddress[v.size()];
                v.copyInto(this.grouplist);
                return;
            }
            return;
        }
        if (mb == null || mb.length() == 0) {
            this.address = host;
        } else if (host.length() == 0) {
            this.address = mb;
        } else {
            this.address = String.valueOf(mb) + "@" + host;
        }
    }

    boolean isEndOfGroup() {
        return this.group && this.groupname == null;
    }

    @Override // javax.mail.internet.InternetAddress
    public boolean isGroup() {
        return this.group;
    }

    @Override // javax.mail.internet.InternetAddress
    public InternetAddress[] getGroup(boolean strict) throws AddressException {
        if (this.grouplist == null) {
            return null;
        }
        return (InternetAddress[]) this.grouplist.clone();
    }
}
