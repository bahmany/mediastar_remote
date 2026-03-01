package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;

/* loaded from: classes.dex */
public class Status {
    static final String[] standardItems = {"MESSAGES", "RECENT", "UNSEEN", "UIDNEXT", "UIDVALIDITY"};
    public String mbox;
    public int recent;
    public int total;
    public long uidnext;
    public long uidvalidity;
    public int unseen;

    public Status(Response r) throws ParsingException {
        this.mbox = null;
        this.total = -1;
        this.recent = -1;
        this.uidnext = -1L;
        this.uidvalidity = -1L;
        this.unseen = -1;
        this.mbox = r.readAtomString();
        r.skipSpaces();
        if (r.readByte() != 40) {
            throw new ParsingException("parse error in STATUS");
        }
        do {
            String attr = r.readAtom();
            if (attr.equalsIgnoreCase("MESSAGES")) {
                this.total = r.readNumber();
            } else if (attr.equalsIgnoreCase("RECENT")) {
                this.recent = r.readNumber();
            } else if (attr.equalsIgnoreCase("UIDNEXT")) {
                this.uidnext = r.readLong();
            } else if (attr.equalsIgnoreCase("UIDVALIDITY")) {
                this.uidvalidity = r.readLong();
            } else if (attr.equalsIgnoreCase("UNSEEN")) {
                this.unseen = r.readNumber();
            }
        } while (r.readByte() != 41);
    }

    public static void add(Status s1, Status s2) {
        if (s2.total != -1) {
            s1.total = s2.total;
        }
        if (s2.recent != -1) {
            s1.recent = s2.recent;
        }
        if (s2.uidnext != -1) {
            s1.uidnext = s2.uidnext;
        }
        if (s2.uidvalidity != -1) {
            s1.uidvalidity = s2.uidvalidity;
        }
        if (s2.unseen != -1) {
            s1.unseen = s2.unseen;
        }
    }
}
